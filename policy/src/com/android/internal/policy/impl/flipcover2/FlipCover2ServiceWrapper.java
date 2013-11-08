package com.android.internal.policy.impl.flipcover2;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.asus.flipcover2.IFlipCoverCallBack;
import com.asus.flipcover2.IFlipCoverService;

public class FlipCover2ServiceWrapper implements IFlipCoverService {
    private String TAG = "FlipCover2ServiceWrapper";
    private IFlipCoverService mService;

    public FlipCover2ServiceWrapper(IFlipCoverService service) {
        this.mService = service;
    }

    public IBinder asBinder() {
        return this.mService.asBinder();
    }

    public void onSystemReady(Bundle options, IFlipCoverCallBack callback) {
        try {
            if (this.mService != null) {
                this.mService.onSystemReady(options, callback);
            }
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void onBootCompleted(Bundle options, IFlipCoverCallBack callback) {
        try {
            if (this.mService != null) {
                this.mService.onBootCompleted(options, callback);
            }
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void showFlipCover(Bundle options) {
        try {
            if (this.mService != null) {
                this.mService.showFlipCover(options);
            }
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void hideFlipCover(Bundle options) {
        try {
            if (this.mService != null) {
                this.mService.hideFlipCover(options);
            }
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void onScreenTurnedOff(int reason, Bundle options) {
        try {
            if (this.mService != null) {
                this.mService.onScreenTurnedOff(reason, options);
            }
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void onScreenTurnedOn(Bundle options) {
        try {
            if (this.mService != null) {
                this.mService.onScreenTurnedOn(options);
            }
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public boolean isFlashLightOn() {
        try {
            if (this.mService != null) {
                return this.mService.isFlashLightOn();
            }
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
        return false;
    }

    public void turnOffFlashLight(Bundle options) {
        try {
            if (this.mService != null) {
                this.mService.turnOffFlashLight(options);
            }
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void showDeviceOptionView(Bundle options) {
        try {
            if (this.mService != null) {
                this.mService.showDeviceOptionView(options);
            }
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public boolean isFlipCoverShowing() {
        try {
            if (this.mService != null) {
                return this.mService.isFlipCoverShowing();
            }
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
        return false;
    }

    public boolean isFlashLightSelected() {
        try {
            if (this.mService != null) {
                return this.mService.isFlashLightSelected();
            }
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
        return false;
    }

    public Bundle execCmd(int cmdId, Bundle options) {
        try {
            if (this.mService != null) {
                return this.mService.execCmd(cmdId, options);
            }
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
        return null;
    }
}
