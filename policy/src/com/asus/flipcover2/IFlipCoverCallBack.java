package com.asus.flipcover2;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IFlipCoverCallBack extends IInterface {

    public static abstract class Stub extends Binder implements IFlipCoverCallBack {
        private static final String DESCRIPTOR = "com.asus.flipcover2.IFlipCoverCallBack";
        static final int TRANSACTION_disableNonSecureKeyguard = 7;
        static final int TRANSACTION_execCmd = 8;
        static final int TRANSACTION_goToSleepAfterTimeStamp = 2;
        static final int TRANSACTION_goToSleepAtTime = 1;
        static final int TRANSACTION_powerOffDevice = 5;
        static final int TRANSACTION_restartDevice = 6;
        static final int TRANSACTION_userActivity = 4;
        static final int TRANSACTION_wakeUp = 3;

        private static class Proxy implements IFlipCoverCallBack {
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

            public void goToSleepAtTime(long atTimeMillis) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(atTimeMillis);
                    this.mRemote.transact(Stub.TRANSACTION_goToSleepAtTime, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void goToSleepAfterTimeStamp(long timeStampMillis) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(timeStampMillis);
                    this.mRemote.transact(Stub.TRANSACTION_goToSleepAfterTimeStamp, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void wakeUp(long time) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(time);
                    this.mRemote.transact(Stub.TRANSACTION_wakeUp, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void userActivity(long when, boolean noChangeLights) throws RemoteException {
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(when);
                    if (noChangeLights) {
                        i = Stub.TRANSACTION_goToSleepAtTime;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_userActivity, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void powerOffDevice() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_powerOffDevice, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void restartDevice() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_restartDevice, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean disableNonSecureKeyguard() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_disableNonSecureKeyguard, _data, _reply, 0);
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

            public Bundle execCmd(int cmdId, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Bundle _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cmdId);
                    if (options != null) {
                        _data.writeInt(Stub.TRANSACTION_goToSleepAtTime);
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

        public static IFlipCoverCallBack asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IFlipCoverCallBack)) {
                return new Proxy(obj);
            }
            return (IFlipCoverCallBack) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int i = 0;
            switch (code) {
                case TRANSACTION_goToSleepAtTime /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    goToSleepAtTime(data.readLong());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_goToSleepAfterTimeStamp /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    goToSleepAfterTimeStamp(data.readLong());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_wakeUp /*3*/:
                    data.enforceInterface(DESCRIPTOR);
                    wakeUp(data.readLong());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_userActivity /*4*/:
                    boolean _arg1;
                    data.enforceInterface(DESCRIPTOR);
                    long _arg0 = data.readLong();
                    if (data.readInt() != 0) {
                        _arg1 = true;
                    } else {
                        _arg1 = false;
                    }
                    userActivity(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_powerOffDevice /*5*/:
                    data.enforceInterface(DESCRIPTOR);
                    powerOffDevice();
                    reply.writeNoException();
                    return true;
                case TRANSACTION_restartDevice /*6*/:
                    data.enforceInterface(DESCRIPTOR);
                    restartDevice();
                    reply.writeNoException();
                    return true;
                case TRANSACTION_disableNonSecureKeyguard /*7*/:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result = disableNonSecureKeyguard();
                    reply.writeNoException();
                    if (_result) {
                        i = TRANSACTION_goToSleepAtTime;
                    }
                    reply.writeInt(i);
                    return true;
                case TRANSACTION_execCmd /*8*/:
                    Bundle _arg12;
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg12 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    Bundle _result2 = execCmd(_arg02, _arg12);
                    reply.writeNoException();
                    if (_result2 != null) {
                        reply.writeInt(TRANSACTION_goToSleepAtTime);
                        _result2.writeToParcel(reply, TRANSACTION_goToSleepAtTime);
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

    boolean disableNonSecureKeyguard() throws RemoteException;

    Bundle execCmd(int i, Bundle bundle) throws RemoteException;

    void goToSleepAfterTimeStamp(long j) throws RemoteException;

    void goToSleepAtTime(long j) throws RemoteException;

    void powerOffDevice() throws RemoteException;

    void restartDevice() throws RemoteException;

    void userActivity(long j, boolean z) throws RemoteException;

    void wakeUp(long j) throws RemoteException;
}
