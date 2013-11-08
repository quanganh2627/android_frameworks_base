package com.intel.cws.cwsservicemanagerclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.util.Xml;
import com.intel.cws.cwsservicemanager.CsmException;
import com.intel.cws.cwsservicemanager.CsmUtil;
import com.intel.cws.cwsservicemanager.ICsmModemMgrListener.Stub;
import com.intel.cws.cwsservicemanager.ICwsServiceMgr;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class CsmClient {
    private static final String ASYNC_PARAM = "async";
    private static final String CLIENT_TAG = "Client";
    private static final String CONFIG_FILE = "/system/vendor/etc/CsmConfig.xml";
    private static final String CONFIG_TAG = "CsmConfig";
    public static final int CSM_CLIENT_BIND = 1;
    public static final int CSM_CLIENT_BIND_AND_START = 2;
    private static final byte CSM_CLIENT_MODEM_DEAD = (byte) 4;
    private static final byte CSM_CLIENT_MODEM_DOWN = (byte) 2;
    private static final byte CSM_CLIENT_MODEM_UP = (byte) 1;
    private static final int CSM_CLIENT_MSG_ON_BIND = 1;
    private static final int CSM_CLIENT_MSG_RESTART = 2;
    public static final int CSM_CLIENT_NO_ACTION = 0;
    private static final byte CSM_CLIENT_NO_MODEM = (byte) 8;
    protected static final int CSM_CLIENT_NO_TIMEOUT = 0;
    public static final boolean CSM_CLIENT_REBIND = true;
    public static final int CSM_CLIENT_STOP_NO_UNBIND = 0;
    public static final int CSM_CLIENT_STOP_REBIND = 1;
    public static final int CSM_CLIENT_STOP_UNBIND = 2;
    public static final boolean CSM_CLIENT_UNBIND = false;
    protected static final byte CSM_ID_BT = (byte) 4;
    protected static final byte CSM_ID_GPS = (byte) 2;
    protected static final byte CSM_ID_NFC = (byte) 8;
    protected static final byte CSM_ID_WIFI = (byte) 1;
    protected static final byte CSM_ID_WIFI_OFFLOAD = (byte) 32;
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final String MODULE_TAG = "Module";
    private static final String NO_UNBIND_PARAM = "no_unbind";
    private static final String REBIND_PARAM = "rebind";
    private static final String START_TAG = "Start";
    private static final String STOP_TAG = "Stop";
    private static final String SYNC_PARAM = "sync";
    private static final String TAG = "CsmClient";
    private static final String TIMEOUT_TAG = "Timeout_ms";
    private static final String UNBIND_PARAM = "unbind";
    Byte mClientId;
    Context mContext;
    protected ICwsServiceMgr mCwsServiceMgr;
    protected Handler mHandler;
    private boolean mIsBound;
    private boolean mIsLockRequested;
    private boolean mIsPlatformShuttingDown;
    private boolean mIsStartSync;
    private final Stub mModemCallbacks;
    private byte mModemStatus;
    private BroadcastReceiver mShutdownReceiver;
    private BroadcastReceiver mSimStatusReceiver;
    private int mStopMode;
    private int mTimeout;
    private Semaphore waitOnModemUp;

    private class CsmXmlParser {
        private String ns;

        private CsmXmlParser() {
            this.ns = null;
        }

        public void parse(InputStream in) throws XmlPullParserException, IOException {
            if (CsmClient.DEBUG) {
                Log.d(CsmClient.TAG, "parse");
            }
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", CsmClient.DEBUG);
                parser.setInput(in, null);
                parser.nextTag();
                readConfig(parser);
            } finally {
                in.close();
            }
        }

        private void readConfig(XmlPullParser parser) throws XmlPullParserException, IOException {
            List ModuleList = new ArrayList();
            if (CsmClient.DEBUG) {
                Log.d(CsmClient.TAG, "readConfig");
            }
            parser.require(CsmClient.CSM_CLIENT_STOP_UNBIND, this.ns, CsmClient.CONFIG_TAG);
            while (parser.next() != 3) {
                if (parser.getEventType() == CsmClient.CSM_CLIENT_STOP_UNBIND) {
                    if (parser.getName().equals(CsmClient.MODULE_TAG)) {
                        if (CsmClient.DEBUG) {
                            Log.d(CsmClient.TAG, "readConfig - Module");
                        }
                        readModule(parser);
                    } else {
                        if (CsmClient.DEBUG) {
                            Log.d(CsmClient.TAG, "readConfig - skip");
                        }
                        CsmUtil.skip(parser);
                    }
                }
            }
            if (CsmClient.DEBUG) {
                Log.d(CsmClient.TAG, "readConfig - end");
            }
        }

        private void readModule(XmlPullParser parser) throws XmlPullParserException, IOException {
            parser.require(CsmClient.CSM_CLIENT_STOP_UNBIND, this.ns, CsmClient.MODULE_TAG);
            String client = null;
            String start = null;
            String timeout = null;
            String stop = null;
            if (CsmClient.DEBUG) {
                Log.d(CsmClient.TAG, "readModule");
            }
            while (parser.next() != 3) {
                if (parser.getEventType() == CsmClient.CSM_CLIENT_STOP_UNBIND) {
                    String elementName = parser.getName();
                    if (elementName.equals(CsmClient.CLIENT_TAG)) {
                        client = CsmUtil.readTag(parser, CsmClient.CLIENT_TAG, this.ns);
                        if (CsmClient.DEBUG) {
                            Log.d(CsmClient.TAG, "readModule - Client:" + client);
                        }
                    } else if (elementName.equals(CsmClient.START_TAG)) {
                        start = CsmUtil.readTag(parser, CsmClient.START_TAG, this.ns);
                        if (CsmClient.DEBUG) {
                            Log.d(CsmClient.TAG, "readModule - Start:" + start);
                        }
                    } else if (elementName.equals(CsmClient.TIMEOUT_TAG)) {
                        timeout = CsmUtil.readTag(parser, CsmClient.TIMEOUT_TAG, this.ns);
                        if (CsmClient.DEBUG) {
                            Log.d(CsmClient.TAG, "readModule - Timeout_ms:" + timeout);
                        }
                    } else if (elementName.equals(CsmClient.STOP_TAG)) {
                        stop = CsmUtil.readTag(parser, CsmClient.STOP_TAG, this.ns);
                        if (CsmClient.DEBUG) {
                            Log.d(CsmClient.TAG, "readModule - Stop:" + stop);
                        }
                    } else {
                        CsmUtil.skip(parser);
                    }
                }
            }
            if (client != null && CsmUtil.checkClient(client, CsmClient.this.mClientId.byteValue())) {
                if (start != null) {
                    if (start.equals(CsmClient.SYNC_PARAM)) {
                        CsmClient.this.mIsStartSync = CsmClient.CSM_CLIENT_REBIND;
                        if (timeout != null) {
                            CsmClient.this.mTimeout = Integer.parseInt(timeout);
                        }
                    } else if (start.equals(CsmClient.ASYNC_PARAM)) {
                        CsmClient.this.mIsStartSync = CsmClient.DEBUG;
                        CsmClient.this.mTimeout = CsmClient.CSM_CLIENT_STOP_NO_UNBIND;
                    }
                }
                if (stop == null) {
                    return;
                }
                if (stop.equals(CsmClient.NO_UNBIND_PARAM)) {
                    CsmClient.this.mStopMode = CsmClient.CSM_CLIENT_STOP_NO_UNBIND;
                } else if (stop.equals(CsmClient.UNBIND_PARAM)) {
                    CsmClient.this.mStopMode = CsmClient.CSM_CLIENT_STOP_UNBIND;
                } else if (stop.equals(CsmClient.REBIND_PARAM)) {
                    CsmClient.this.mStopMode = CsmClient.CSM_CLIENT_STOP_REBIND;
                }
            }
        }
    }

    public CsmClient(Context context, byte ClientId) throws CsmException {
        this(context, ClientId, CSM_CLIENT_STOP_NO_UNBIND);
    }

    public CsmClient(Context context, byte ClientId, int start) throws CsmException {
        this(context, ClientId, start, CSM_CLIENT_STOP_NO_UNBIND);
    }

    public CsmClient(Context context, byte ClientId, int start, int timeout) throws CsmException {
        this.mIsBound = DEBUG;
        this.mIsLockRequested = DEBUG;
        this.mIsPlatformShuttingDown = DEBUG;
        this.waitOnModemUp = new Semaphore(CSM_CLIENT_STOP_NO_UNBIND, CSM_CLIENT_REBIND);
        this.mModemStatus = CSM_ID_GPS;
        this.mIsStartSync = CSM_CLIENT_REBIND;
        this.mTimeout = 60000;
        this.mStopMode = CSM_CLIENT_STOP_NO_UNBIND;
        this.mSimStatusReceiver = null;
        this.mShutdownReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.intent.action.ACTION_SHUTDOWN")) {
                    CsmClient.this.mIsPlatformShuttingDown = CsmClient.CSM_CLIENT_REBIND;
                }
            }
        };
        this.mModemCallbacks = new Stub() {
            public void mmgrClbkModemAvailable() {
                if (CsmClient.DEBUG) {
                    Log.d(CsmClient.TAG, "[" + CsmClient.this.mClientId.toString() + "]" + " Modem is available message received");
                }
                byte previousModemStatus = CsmClient.this.mModemStatus;
                CsmClient.this.mModemStatus = CsmClient.CSM_ID_WIFI;
                CsmClient.this.waitOnModemUp.release();
                if (previousModemStatus != CsmClient.CSM_ID_WIFI) {
                    CsmClient.this.csmClientModemAvailable();
                }
            }

            public void mmgrClbkModemUnavailable(int reason) {
                if (CsmClient.DEBUG) {
                    Log.d(CsmClient.TAG, "[" + CsmClient.this.mClientId.toString() + "]" + " Modem is unavailable - reason: " + reason);
                }
                byte previousModemStatus = CsmClient.this.mModemStatus;
                if (CsmClient.this.waitOnModemUp.availablePermits() > 0) {
                    CsmClient.this.waitOnModemUp.drainPermits();
                }
                if (reason != 4 && reason != 8) {
                    CsmClient.this.mModemStatus = CsmClient.CSM_ID_GPS;
                } else if (reason == 8) {
                    CsmClient.this.mModemStatus = CsmClient.CSM_ID_NFC;
                } else {
                    CsmClient.this.mModemStatus = CsmClient.CSM_ID_BT;
                }
                if (previousModemStatus == CsmClient.CSM_ID_WIFI && reason != 0) {
                    CsmClient.this.csmClientModemUnavailable();
                }
            }
        };
        this.mClientId = new Byte(ClientId);
        this.mContext = context;
        try {
            new CsmXmlParser().parse(new FileInputStream(new File(CONFIG_FILE)));
            if (DEBUG) {
                Log.d(TAG, "[" + this.mClientId.toString() + "] mIsStartSync: " + this.mIsStartSync);
            }
            if (DEBUG) {
                Log.d(TAG, "[" + this.mClientId.toString() + "] mTimeout: " + this.mTimeout);
            }
            if (DEBUG) {
                Log.d(TAG, "[" + this.mClientId.toString() + "] mStopMode: " + this.mStopMode);
            }
        } catch (Exception ex) {
            Log.e(TAG, "[" + this.mClientId.toString() + "] Configuration Exception: " + ex);
        }
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    switch (msg.what) {
                        case CsmClient.CSM_CLIENT_STOP_REBIND /*1*/:
                            CsmClient.this.csmAddCallbacks();
                            try {
                                if (CsmClient.this.mCwsServiceMgr == null) {
                                    Log.e(CsmClient.TAG, "[" + CsmClient.this.mClientId.toString() + "] null mCwsServiceMgr");
                                } else if (CsmClient.this.mIsLockRequested) {
                                    CsmClient.this.mCwsServiceMgr.csmStart(CsmClient.this.mClientId.byteValue());
                                }
                            } catch (RemoteException e) {
                                Log.e(CsmClient.TAG, "[" + CsmClient.this.mClientId.toString() + "] csmStart failed.", e);
                            }
                            CsmClient.this.mIsBound = CsmClient.CSM_CLIENT_REBIND;
                            return;
                        case CsmClient.CSM_CLIENT_STOP_UNBIND /*2*/:
                            Log.v(CsmClient.TAG, "[" + CsmClient.this.mClientId.toString() + "] Establishing CSM connexion");
                            if (CsmClient.this.mIsBound) {
                                CsmClient.this.csmClientModemUnavailable();
                                CsmClient.this.csmClientStop(CsmClient.CSM_CLIENT_STOP_REBIND);
                                return;
                            } else if (CsmClient.this.mIsLockRequested) {
                                CsmClient.this.csmStartModem();
                                return;
                            } else {
                                CsmClient.this.csmClientBind();
                                return;
                            }
                        default:
                            return;
                    }
                } catch (CsmException csmEx) {
                    Log.e(CsmClient.TAG, "[" + CsmClient.this.mClientId.toString() + "] handleMessage CsmException:" + csmEx);
                }
                Log.e(CsmClient.TAG, "[" + CsmClient.this.mClientId.toString() + "] handleMessage CsmException:" + csmEx);
            }
        };
        this.mContext.registerReceiver(this.mShutdownReceiver, new IntentFilter("android.intent.action.ACTION_SHUTDOWN"));
        if (this.mContext.getPackageManager().queryIntentServices(new Intent(ICwsServiceMgr.class.getName()), CSM_CLIENT_STOP_NO_UNBIND).size() <= 0) {
            Log.i(TAG, "[" + this.mClientId.toString() + "] There is no CSM. Re-initialising mModemStatus.");
            this.mModemStatus = CSM_ID_NFC;
        }
        switch (start) {
            case CSM_CLIENT_STOP_NO_UNBIND /*0*/:
                return;
            case CSM_CLIENT_STOP_REBIND /*1*/:
                csmClientBind();
                return;
            case CSM_CLIENT_STOP_UNBIND /*2*/:
                start(timeout);
                return;
            default:
                throw new CsmException("Unsupported value for start in CsmClient.", 4);
        }
    }

    public Byte getClientId() {
        return this.mClientId;
    }

    public ICwsServiceMgr getService() {
        return this.mCwsServiceMgr;
    }

    public boolean csmStartModem() throws CsmException {
        if (this.mIsStartSync) {
            if (DEBUG) {
                Log.d(TAG, "[" + this.mClientId.toString() + "] csmStartModem - Sync");
            }
            return start(this.mTimeout);
        }
        if (DEBUG) {
            Log.d(TAG, "[" + this.mClientId.toString() + "] csmStartModem - Async");
        }
        return start(CSM_CLIENT_STOP_NO_UNBIND);
    }

    private boolean start(int timeout) throws CsmException {
        if ((this.mModemStatus & 8) != 0) {
            Log.w(TAG, "Calling start while there is no modem.");
            throw new CsmException("No modem.", CSM_CLIENT_STOP_REBIND);
        } else if ((this.mModemStatus & 4) != 0) {
            Log.w(TAG, "Calling start while modem is DEAD.");
            throw new CsmException("Modem is dead.", CSM_CLIENT_STOP_UNBIND);
        } else {
            Log.v(TAG, "[" + this.mClientId.toString() + "] Start called.");
            if (this.mIsPlatformShuttingDown) {
                if (DEBUG) {
                    Log.d(TAG, "[" + this.mClientId.toString() + "]" + " start called during platform shutdown.");
                }
                throw new CsmException("Shutdown ongoing", 3);
            }
            this.mIsLockRequested = CSM_CLIENT_REBIND;
            if (this.mIsBound) {
                try {
                    if (this.mCwsServiceMgr == null) {
                        Log.e(TAG, "[" + this.mClientId.toString() + "] mCwsServiceMgr is null");
                        throw new CsmException("null mCwsServiceMgr", 8);
                    }
                    this.mCwsServiceMgr.csmStart(this.mClientId.byteValue());
                    if (DEBUG) {
                        Log.d(TAG, "[" + this.mClientId.toString() + "] csmStart called.");
                    }
                } catch (RemoteException e) {
                    Log.e(TAG, "[" + this.mClientId.toString() + "] csmStart failed." + e);
                    throw new CsmException("Unable to call csmStart." + e.getMessage(), 3);
                }
            }
            csmClientBind();
            if (timeout > 0 && (this.mModemStatus & CSM_CLIENT_STOP_REBIND) == 0) {
                try {
                    if (!this.waitOnModemUp.tryAcquire(new Integer(timeout).longValue(), TimeUnit.MILLISECONDS)) {
                        csmClientUnbind(DEBUG);
                        if (DEBUG) {
                            Log.d(TAG, "[" + this.mClientId.toString() + "] Modem lock failed");
                        }
                        csmClientBind();
                        throw new CsmException("Modem lock failed.", 3);
                    }
                } catch (InterruptedException e2) {
                    Log.e(TAG, "[" + this.mClientId.toString() + "] Unable to wait on semaphore");
                    csmClientUnbind(DEBUG);
                    csmClientBind();
                    throw new CsmException("Unable to wait on semaphore.", 3);
                }
            }
            return CSM_CLIENT_REBIND;
        }
    }

    private void csmClientBind() {
        if ((this.mModemStatus & 8) == 0) {
            Log.v(TAG, "[" + this.mClientId.toString() + "] csmClientBind called.");
            this.mCwsServiceMgr = ICwsServiceMgr.Stub.asInterface(ServiceManager.getService("cws_service_manager"));
            if (this.mCwsServiceMgr == null) {
                Log.e(TAG, "mCwsServiceMgr is null");
                return;
            }
            if (DEBUG) {
                Log.d(TAG, "mCwsServiceMgr is not null");
            }
            this.mIsBound = CSM_CLIENT_REBIND;
            this.mHandler.sendEmptyMessage(CSM_CLIENT_STOP_REBIND);
            return;
        }
        Log.v(TAG, "[" + this.mClientId.toString() + "] csmClientBind called while there is no modem.");
    }

    private void csmClientUnbind(boolean auto_bind) throws CsmException {
        try {
            if (this.mCwsServiceMgr == null) {
                Log.e(TAG, "[" + this.mClientId.toString() + "] mCwsServiceMgr is null");
                throw new CsmException("null mCwsServiceMgr", 8);
            }
            this.mCwsServiceMgr.csmUnregisterCallback(CSM_CLIENT_STOP_NO_UNBIND, this.mModemCallbacks);
            this.mCwsServiceMgr = null;
            this.mIsBound = DEBUG;
            if (!this.mIsPlatformShuttingDown && auto_bind) {
                csmClientBind();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "[" + this.mClientId.toString() + "] csmUnregisterCallback failed");
        }
    }

    private void csmClientStop(int unbind) throws CsmException {
        Log.v(TAG, "[" + this.mClientId.toString() + "] csmStop called");
        if (this.mIsBound) {
            try {
                if (this.mCwsServiceMgr == null) {
                    Log.e(TAG, "[" + this.mClientId.toString() + "] mCwsServiceMgr is null");
                    throw new CsmException("null mCwsServiceMgr", 8);
                }
                this.mCwsServiceMgr.csmStop(this.mClientId.byteValue());
                switch (unbind) {
                    case CSM_CLIENT_STOP_NO_UNBIND /*0*/:
                        return;
                    case CSM_CLIENT_STOP_REBIND /*1*/:
                        csmClientUnbind(CSM_CLIENT_REBIND);
                        return;
                    case CSM_CLIENT_STOP_UNBIND /*2*/:
                        csmClientUnbind(DEBUG);
                        return;
                    default:
                        throw new CsmException("Unsupported value for csmClientStop.", 4);
                }
            } catch (RemoteException e) {
                Log.e(TAG, "[" + this.mClientId.toString() + "] csmStop failed");
                if (unbind < CSM_CLIENT_STOP_REBIND) {
                    unbind = CSM_CLIENT_STOP_REBIND;
                }
            }
        }
    }

    public void csmStop() {
        this.mIsLockRequested = DEBUG;
        try {
            csmClientStop(this.mStopMode);
        } catch (CsmException e) {
            Log.e(TAG, "[" + this.mClientId.toString() + "] Unexpected exception.", e);
        }
    }

    public String atSendCmd(String Cmd) throws CsmException {
        String response = null;
        try {
            if (this.mCwsServiceMgr == null) {
                Log.e(TAG, "[" + this.mClientId.toString() + "] mCwsServiceMgr is null");
                throw new CsmException("null mCwsServiceMgr", 8);
            }
            response = this.mCwsServiceMgr.atSendCmd(Cmd);
            return response;
        } catch (RemoteException e) {
            this.mHandler.sendEmptyMessage(CSM_CLIENT_STOP_UNBIND);
            Log.e(TAG, "[" + this.mClientId.toString() + "] atSendCmd failed. Restarting CsmClient.");
        }
    }

    public void csmClientModemAvailable() {
    }

    public void csmClientModemUnavailable() {
    }

    protected void csmAddCallbacks() throws CsmException {
        try {
            if (this.mCwsServiceMgr == null) {
                Log.e(TAG, "[" + this.mClientId.toString() + "] mCwsServiceMgr is null");
                throw new CsmException("null mCwsServiceMgr", 8);
            }
            this.mCwsServiceMgr.csmRegisterCallback(CSM_CLIENT_STOP_NO_UNBIND, this.mModemCallbacks);
            Log.v(TAG, "[" + this.mClientId.toString() + "] Callbacks registered.");
        } catch (RemoteException e) {
            Log.e(TAG, "[" + this.mClientId.toString() + "] Registering callback failed");
        }
    }

    public void csmActivateSimStatusReceiver() {
        if (DEBUG) {
            Log.d(TAG, "[" + this.mClientId.toString() + "] Activating SIM status receiver.");
        }
        this.mSimStatusReceiver = new BroadcastReceiver() {
            public void onReceive(Context c, Intent receivedIntent) {
                String action = receivedIntent.getAction();
                if (CsmClient.DEBUG) {
                    Log.d(CsmClient.TAG, "[" + CsmClient.this.mClientId.toString() + "] mSimStatusReceiver - action: " + action);
                }
                if (action.equals("android.intent.action.SIM_STATE_CHANGED")) {
                    String currentSimState = receivedIntent.getStringExtra("ss");
                    if (currentSimState == null) {
                        Log.e(CsmClient.TAG, "[" + CsmClient.this.mClientId.toString() + "] mSimStatusReceiver" + " - not possible to get the string");
                        return;
                    }
                    if (CsmClient.DEBUG) {
                        Log.d(CsmClient.TAG, "[" + CsmClient.this.mClientId.toString() + "] mSimStatusReceiver - " + "currentSimState: " + currentSimState);
                    }
                    if ("ABSENT".equals(currentSimState)) {
                        CsmClient.this.onSimAbsent();
                    } else if ("LOADED".equals(currentSimState)) {
                        CsmClient.this.onSimLoaded();
                    }
                } else if (CsmClient.DEBUG) {
                    Log.d(CsmClient.TAG, "[" + CsmClient.this.mClientId.toString() + "] mSimStatusReceiver" + " - wrong action received");
                }
            }
        };
        this.mContext.registerReceiver(this.mSimStatusReceiver, new IntentFilter("android.intent.action.SIM_STATE_CHANGED"));
    }

    public void onSimLoaded() {
    }

    public void onSimAbsent() {
    }
}
