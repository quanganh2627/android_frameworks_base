package com.asus.flipcover2;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IFlipCoverService extends IInterface {

    public static abstract class Stub extends Binder implements IFlipCoverService {
        private static final String DESCRIPTOR = "com.asus.flipcover2.IFlipCoverService";
        static final int TRANSACTION_execCmd = 12;
        static final int TRANSACTION_hideFlipCover = 4;
        static final int TRANSACTION_isFlashLightOn = 8;
        static final int TRANSACTION_isFlashLightSelected = 9;
        static final int TRANSACTION_isFlipCoverShowing = 7;
        static final int TRANSACTION_onBootCompleted = 2;
        static final int TRANSACTION_onScreenTurnedOff = 5;
        static final int TRANSACTION_onScreenTurnedOn = 6;
        static final int TRANSACTION_onSystemReady = 1;
        static final int TRANSACTION_showDeviceOptionView = 11;
        static final int TRANSACTION_showFlipCover = 3;
        static final int TRANSACTION_turnOffFlashLight = 10;

        private static class Proxy implements IFlipCoverService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void onSystemReady(Bundle options, IFlipCoverCallBack callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (options != null) {
                        _data.writeInt(Stub.TRANSACTION_onSystemReady);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_onSystemReady, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onBootCompleted(Bundle options, IFlipCoverCallBack callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (options != null) {
                        _data.writeInt(Stub.TRANSACTION_onSystemReady);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_onBootCompleted, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void showFlipCover(Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (options != null) {
                        _data.writeInt(Stub.TRANSACTION_onSystemReady);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_showFlipCover, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void hideFlipCover(Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (options != null) {
                        _data.writeInt(Stub.TRANSACTION_onSystemReady);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_hideFlipCover, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onScreenTurnedOff(int reason, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(reason);
                    if (options != null) {
                        _data.writeInt(Stub.TRANSACTION_onSystemReady);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_onScreenTurnedOff, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onScreenTurnedOn(Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (options != null) {
                        _data.writeInt(Stub.TRANSACTION_onSystemReady);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_onScreenTurnedOn, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isFlipCoverShowing() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_isFlipCoverShowing, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isFlashLightOn() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_isFlashLightOn, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isFlashLightSelected() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_isFlashLightSelected, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void turnOffFlashLight(Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (options != null) {
                        _data.writeInt(Stub.TRANSACTION_onSystemReady);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_turnOffFlashLight, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void showDeviceOptionView(Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (options != null) {
                        _data.writeInt(Stub.TRANSACTION_onSystemReady);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_showDeviceOptionView, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Bundle execCmd(int cmdId, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Bundle _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cmdId);
                    if (options != null) {
                        _data.writeInt(Stub.TRANSACTION_onSystemReady);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_execCmd, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bundle) Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IFlipCoverService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IFlipCoverService)) {
                return new Proxy(obj);
            }
            return (IFlipCoverService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int i = 0;
            Bundle _arg0;
            int _arg02;
            Bundle _arg1;
            boolean _result;
            switch (code) {
                case TRANSACTION_onSystemReady /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    onSystemReady(_arg0, com.asus.flipcover2.IFlipCoverCallBack.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_onBootCompleted /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    onBootCompleted(_arg0, com.asus.flipcover2.IFlipCoverCallBack.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_showFlipCover /*3*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    showFlipCover(_arg0);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_hideFlipCover /*4*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    hideFlipCover(_arg0);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_onScreenTurnedOff /*5*/:
                    data.enforceInterface(DESCRIPTOR);
                    _arg02 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    onScreenTurnedOff(_arg02, _arg1);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_onScreenTurnedOn /*6*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    onScreenTurnedOn(_arg0);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_isFlipCoverShowing /*7*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = isFlipCoverShowing();
                    reply.writeNoException();
                    if (_result) {
                        i = TRANSACTION_onSystemReady;
                    }
                    reply.writeInt(i);
                    return true;
                case TRANSACTION_isFlashLightOn /*8*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = isFlashLightOn();
                    reply.writeNoException();
                    if (_result) {
                        i = TRANSACTION_onSystemReady;
                    }
                    reply.writeInt(i);
                    return true;
                case TRANSACTION_isFlashLightSelected /*9*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = isFlashLightSelected();
                    reply.writeNoException();
                    if (_result) {
                        i = TRANSACTION_onSystemReady;
                    }
                    reply.writeInt(i);
                    return true;
                case TRANSACTION_turnOffFlashLight /*10*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    turnOffFlashLight(_arg0);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_showDeviceOptionView /*11*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    showDeviceOptionView(_arg0);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_execCmd /*12*/:
                    data.enforceInterface(DESCRIPTOR);
                    _arg02 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    Bundle _result2 = execCmd(_arg02, _arg1);
                    reply.writeNoException();
                    if (_result2 != null) {
                        reply.writeInt(TRANSACTION_onSystemReady);
                        _result2.writeToParcel(reply, TRANSACTION_onSystemReady);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    Bundle execCmd(int i, Bundle bundle) throws RemoteException;

    void hideFlipCover(Bundle bundle) throws RemoteException;

    boolean isFlashLightOn() throws RemoteException;

    boolean isFlashLightSelected() throws RemoteException;

    boolean isFlipCoverShowing() throws RemoteException;

    void onBootCompleted(Bundle bundle, IFlipCoverCallBack iFlipCoverCallBack) throws RemoteException;

    void onScreenTurnedOff(int i, Bundle bundle) throws RemoteException;

    void onScreenTurnedOn(Bundle bundle) throws RemoteException;

    void onSystemReady(Bundle bundle, IFlipCoverCallBack iFlipCoverCallBack) throws RemoteException;

    void showDeviceOptionView(Bundle bundle) throws RemoteException;

    void showFlipCover(Bundle bundle) throws RemoteException;

    void turnOffFlashLight(Bundle bundle) throws RemoteException;
}
