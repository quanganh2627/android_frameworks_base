package com.android.internal.policy.impl.flipcover2;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.policy.impl.PhoneWindowManager;
import com.asus.flipcover2.IFlipCoverCallBack;
import com.asus.flipcover2.IFlipCoverService.Stub;

public class FlipCover2ServiceDelegate {
    private static final int BIND_TYPE_COVER1 = 1;
    private static final int BIND_TYPE_COVER2 = 2;
    private static final int BIND_TYPE_NONE = 0;
    private static final boolean DEBUG = true;
    private static final String FLIPCOVER_CLASS = "com.asus.flipcover2.CoverService";
    private static final String FLIPCOVER_PACKAGE = "com.asus.flipcover2";
    private static final int MSG_REBIND = 1;
    private static final String PKG_SCHEME = "package";
    private static final String PKG_SCKEME_COVER1 = "package:com.asus.flipcover";
    private static final String PKG_SCKEME_COVER2 = "package:com.asus.flipcover2";
    private static final String PROPERTY_BINDTYPE = "persist.asus.flipcover.bindtype";
    private static final String TAG = "FlipCover2ServiceDelegate";
    private static final String VIEWFLIPCOVER_CLASS = "com.asus.flipcover.ViewFlipCoverService";
    private static final String VIEWFLIPCOVER_PACKAGE = "com.asus.flipcover";
    boolean isBootCompleted = false;
    boolean isCoverShowing = false;
    boolean isScreenOn = false;
    boolean isSystemReady = false;
    private boolean isTranscoverSetting1 = false;
    private int lastBindType = BIND_TYPE_NONE;
    Context mContext = null;
    protected FlipCover2ServiceWrapper mFlipCover2Service;
    private FlipCoverCallBack mFlipCoverCallBack = null;
    private final ServiceConnection mFlipCoverConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            FlipCover2ServiceDelegate.this.log("*** FlipCover connected (yay!)");
            try {
                FlipCover2ServiceDelegate.this.mFlipCover2Service = new FlipCover2ServiceWrapper(Stub.asInterface(service));
                if (FlipCover2ServiceDelegate.this.isSystemReady) {
                    FlipCover2ServiceDelegate.this.onSystemReady();
                }
                if (FlipCover2ServiceDelegate.this.isBootCompleted) {
                    FlipCover2ServiceDelegate.this.onBootCompleted();
                    if (FlipCover2ServiceDelegate.this.phoneWindowManager.isCoverClosed()) {
                        FlipCover2ServiceDelegate.this.showFlipCover();
                        if (FlipCover2ServiceDelegate.this.isScreenOn) {
                            FlipCover2ServiceDelegate.this.mFlipCover2Service.onScreenTurnedOn(FlipCover2ServiceDelegate.this.phoneWindowManager.getFlipCover2Options());
                            FlipCover2ServiceDelegate.this.log("onScreenTurnedOn invoked");
                            return;
                        }
                        return;
                    }
                    FlipCover2ServiceDelegate.this.hideFlipCover();
                }
            } catch (Exception e) {
                FlipCover2ServiceDelegate.this.log("onServiceConnected exception");
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            FlipCover2ServiceDelegate.this.log("*** FlipCover disconnected (boo!)");
            FlipCover2ServiceDelegate.this.resetValue();
        }
    };
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FlipCover2ServiceDelegate.MSG_REBIND /*1*/:
                    synchronized (FlipCover2ServiceDelegate.this.mLock) {
                        try {
                            FlipCover2ServiceDelegate.this.hideFlipCover();
                            FlipCover2ServiceDelegate.this.log("UNBIND in handler");
                            FlipCover2ServiceDelegate.this.mContext.unbindService(FlipCover2ServiceDelegate.this.mFlipCoverConnection);
                        } catch (Exception e) {
                            FlipCover2ServiceDelegate.this.log("unbindService exception in handler.");
                            e.printStackTrace();
                        }
                        FlipCover2ServiceDelegate.this.resetValue();
                        FlipCover2ServiceDelegate.this.log("REBIND in handler");
                        FlipCover2ServiceDelegate.this.lastBindType = FlipCover2ServiceDelegate.BIND_TYPE_NONE;
                        FlipCover2ServiceDelegate.this.bindService();
                    }
                    return;
                default:
                    return;
            }
        }
    };
    private final Object mLock = new Object();
    PhoneWindowManager phoneWindowManager = null;
    int screenOffReason;

    private final class FlipCoverCallBack extends IFlipCoverCallBack.Stub {
        private PhoneWindowManager mPhoneWindowManager = null;

        public FlipCoverCallBack(PhoneWindowManager phoneWindowManager) {
            this.mPhoneWindowManager = phoneWindowManager;
        }

        public void goToSleepAtTime(long atTimeMillis) throws RemoteException {
            this.mPhoneWindowManager.goToSleepForFlipCoverAtTime(atTimeMillis);
            FlipCover2ServiceDelegate.this.log("callback: goToSleepForFlipCoverAtTime");
        }

        public void goToSleepAfterTimeStamp(long timeStampMillis) throws RemoteException {
            this.mPhoneWindowManager.goToSleepForFlipCoverAfterTimeStamp(timeStampMillis);
            FlipCover2ServiceDelegate.this.log("callback: goToSleepForFlipCoverAfterTimeStamp");
        }

        public void wakeUp(long time) throws RemoteException {
            this.mPhoneWindowManager.wakeUpForFlipCover();
            FlipCover2ServiceDelegate.this.log("callback: wakeUp");
        }

        public void userActivity(long when, boolean noChangeLights) throws RemoteException {
            this.mPhoneWindowManager.userActivityForFlipCover();
            FlipCover2ServiceDelegate.this.log("callback: userActivity");
        }

        public void powerOffDevice() throws RemoteException {
            this.mPhoneWindowManager.powerOffForFlipCover();
            FlipCover2ServiceDelegate.this.log("callback: powerOffDevice");
        }

        public void restartDevice() throws RemoteException {
            this.mPhoneWindowManager.rebootForFlipCover();
            FlipCover2ServiceDelegate.this.log("callback: restartDevice");
        }

        public boolean disableNonSecureKeyguard() throws RemoteException {
            FlipCover2ServiceDelegate.this.log("callback: disableNonSecureKeyguard");
            return false;
        }

        public Bundle execCmd(int cmdId, Bundle options) throws RemoteException {
            FlipCover2ServiceDelegate.this.log("callback: execCmd");
            return null;
        }
    }

    void registerReceiver() {
        try {
            IntentFilter mIntentFilter = new IntentFilter();
            mIntentFilter.addDataScheme(PKG_SCHEME);
            mIntentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
            this.mContext.registerReceiver(new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String dataStr = intent.getDataString();
                    boolean isCover2 = FlipCover2ServiceDelegate.PKG_SCKEME_COVER2.equalsIgnoreCase(dataStr);
                    boolean isCover1 = FlipCover2ServiceDelegate.PKG_SCKEME_COVER1.equalsIgnoreCase(dataStr);
                    if (isCover1 || isCover2) {
                        boolean toRebind;
                        FlipCover2ServiceDelegate.this.log("action:" + intent.getAction());
                        FlipCover2ServiceDelegate.this.log("dataStr:" + dataStr);
                        switch (FlipCover2ServiceDelegate.this.getBindType(FlipCover2ServiceDelegate.this.mContext)) {
                            case FlipCover2ServiceDelegate.MSG_REBIND /*1*/:
                                if (FlipCover2ServiceDelegate.this.lastBindType != FlipCover2ServiceDelegate.MSG_REBIND) {
                                    toRebind = FlipCover2ServiceDelegate.DEBUG;
                                    break;
                                } else {
                                    toRebind = isCover1;
                                    break;
                                }
                            case FlipCover2ServiceDelegate.BIND_TYPE_COVER2 /*2*/:
                                if (FlipCover2ServiceDelegate.this.lastBindType != FlipCover2ServiceDelegate.BIND_TYPE_COVER2) {
                                    toRebind = FlipCover2ServiceDelegate.DEBUG;
                                    break;
                                } else {
                                    toRebind = isCover2;
                                    break;
                                }
                            default:
                                if (FlipCover2ServiceDelegate.this.lastBindType == 0) {
                                    toRebind = false;
                                    break;
                                } else {
                                    toRebind = FlipCover2ServiceDelegate.DEBUG;
                                    break;
                                }
                        }
                        if (toRebind) {
                            FlipCover2ServiceDelegate.this.sendRebindMsg();
                        }
                    }
                }
            }, mIntentFilter);
        } catch (Exception e) {
            log("register receiver ACTION_PACKAGE_REPLACED exception.");
            e.printStackTrace();
        }
        try {
            this.mContext.registerReceiver(new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    FlipCover2ServiceDelegate.this.log("action:" + intent.getAction());
                    if (FlipCover2ServiceDelegate.this.isSystemReady && FlipCover2ServiceDelegate.this.isBootCompleted) {
                        FlipCover2ServiceDelegate.this.sendRebindMsg();
                    }
                }
            }, new IntentFilter("android.intent.action.USER_SWITCHED"));
        } catch (Exception e2) {
            log("register receiver ACTION_USER_SWITCHED exception.");
            e2.printStackTrace();
        }
    }

    private void sendRebindMsg() {
        this.mHandler.removeMessages(MSG_REBIND);
        this.mHandler.sendEmptyMessage(MSG_REBIND);
        log("MSG_REBIND");
    }

    public FlipCover2ServiceDelegate(Context context, PhoneWindowManager phoneWindowManager, boolean isTranscoverSetting1) {
        this.phoneWindowManager = phoneWindowManager;
        this.mFlipCoverCallBack = new FlipCoverCallBack(phoneWindowManager);
        this.mContext = context;
        this.isTranscoverSetting1 = isTranscoverSetting1;
        registerReceiver();
    }

    boolean bindService(String packageName, String className) {
        try {
            Intent intent = new Intent();
            intent.setClassName(packageName, className);
            if (this.mContext.bindServiceAsUser(intent, this.mFlipCoverConnection, MSG_REBIND, UserHandle.CURRENT)) {
                log("*** FlipCover started " + packageName);
                return DEBUG;
            }
            log("*** FlipCover: can't bind to " + packageName);
            return false;
        } catch (Exception e) {
            log("bindService exception.");
            e.printStackTrace();
        }
    }

    void resetValue() {
        this.mFlipCover2Service = null;
        this.isCoverShowing = false;
    }

    public void bindService() {
        String packageName;
        String className;
        int targetBindType = getBindType(this.mContext);
        log("targetBindType:" + targetBindType);
        switch (targetBindType) {
            case MSG_REBIND /*1*/:
                packageName = VIEWFLIPCOVER_PACKAGE;
                className = VIEWFLIPCOVER_CLASS;
                break;
            case BIND_TYPE_COVER2 /*2*/:
                packageName = FLIPCOVER_PACKAGE;
                className = FLIPCOVER_CLASS;
                break;
            default:
                log("No cover app was choosed to show");
                return;
        }
        if (bindService(packageName, className)) {
            this.lastBindType = targetBindType;
        }
    }

    private final int getBindType(Context context) {
        if (this.isTranscoverSetting1) {
            return MSG_REBIND;
        }
        return BIND_TYPE_COVER2;
    }

    void log(String msg) {
        Log.v(TAG, msg);
    }

    public void onSystemReady() {
        if (this.mFlipCover2Service != null) {
            this.mFlipCover2Service.onSystemReady(this.phoneWindowManager.getFlipCover2Options(), this.mFlipCoverCallBack);
            log("onSystemReady invoked");
        } else {
            log("onSystemReady not invoked");
        }
        this.isSystemReady = DEBUG;
    }

    public void onBootCompleted() {
        if (this.mFlipCover2Service != null) {
            this.mFlipCover2Service.onBootCompleted(this.phoneWindowManager.getFlipCover2Options(), this.mFlipCoverCallBack);
            log("onBootCompleted invoked");
        } else {
            log("onBootCompleted not invoked");
        }
        this.isBootCompleted = DEBUG;
    }

    public void showFlipCover() {
        showFlipCover(false);
    }

    public void showFlipCover(boolean forNotifyLidSwitchChanged) {
        if (this.isCoverShowing || this.mFlipCover2Service == null) {
            log("showFlipCover not invoked");
            return;
        }
        this.isCoverShowing = DEBUG;
        this.mFlipCover2Service.showFlipCover(this.phoneWindowManager.getFlipCover2Options(forNotifyLidSwitchChanged));
        log("showFlipCover invoked");
    }

    public void hideFlipCover() {
        hideFlipCover(false);
    }

    public void hideFlipCover(boolean forNotifyLidSwitchChanged) {
        if (!this.isCoverShowing || this.mFlipCover2Service == null) {
            log("hideFlipCover not invoked");
            return;
        }
        this.isCoverShowing = false;
        this.mFlipCover2Service.hideFlipCover(this.phoneWindowManager.getFlipCover2Options(forNotifyLidSwitchChanged));
        log("hideFlipCover invoked");
    }

    public void onScreenTurnedOff(int reason) {
        if (this.mFlipCover2Service != null) {
            this.mFlipCover2Service.onScreenTurnedOff(reason, this.phoneWindowManager.getFlipCover2Options());
            log("onScreenTurnedOff invoked");
        } else {
            log("onScreenTurnedOff not invoked");
        }
        this.isScreenOn = false;
        this.screenOffReason = reason;
    }

    public void onScreenTurnedOn() {
        if (this.mFlipCover2Service != null) {
            this.mFlipCover2Service.onScreenTurnedOn(this.phoneWindowManager.getFlipCover2Options());
            log("onScreenTurnedOn invoked");
        } else {
            log("onScreenTurnedOn not invoked");
        }
        this.isScreenOn = DEBUG;
    }

    public boolean isFlashLightOn() {
        if (this.mFlipCover2Service != null) {
            log("isFlashLightOn invoked");
            return this.mFlipCover2Service.isFlashLightOn();
        }
        log("isFlashLightOn not invoked");
        return false;
    }

    public void turnOffFlashLight() {
        if (this.mFlipCover2Service != null) {
            this.mFlipCover2Service.turnOffFlashLight(this.phoneWindowManager.getFlipCover2Options());
            log("turnOffFlashLight invoked");
            return;
        }
        log("turnOffFlashLight not invoked");
    }

    public void showDeviceOptionView() {
        if (this.mFlipCover2Service != null) {
            this.mFlipCover2Service.showDeviceOptionView(this.phoneWindowManager.getFlipCover2Options());
            log("showDeviceOptionView invoked");
            return;
        }
        log("showDeviceOptionView not invoked");
    }

    public boolean isFlipCoverShowing() {
        if (this.mFlipCover2Service != null) {
            log("isFlipCoverShowing invoked");
            return this.mFlipCover2Service.isFlipCoverShowing();
        }
        log("isFlipCoverShowing not invoked");
        return false;
    }

    public boolean isFlashLightSelected() {
        if (this.mFlipCover2Service != null) {
            log("isFlashLightSelected invoked");
            return this.mFlipCover2Service.isFlashLightSelected();
        }
        log("isFlashLightSelected not invoked");
        return false;
    }

    public Bundle execCmd(int cmdId, Bundle options) {
        if (this.mFlipCover2Service != null) {
            log("execCmd invoked, cmdId:" + cmdId);
            return this.mFlipCover2Service.execCmd(cmdId, options);
        }
        log("execCmd not invoked, cmdId:" + cmdId);
        return null;
    }
}
