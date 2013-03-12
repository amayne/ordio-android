/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/andrew/Documents/Android/Ordio/Ordio/src/com/ordio/player/backend/IPlaybackService.aidl
 */
package com.ordio.player.backend;
public interface IPlaybackService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.ordio.player.backend.IPlaybackService
{
private static final java.lang.String DESCRIPTOR = "com.ordio.player.backend.IPlaybackService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.ordio.player.backend.IPlaybackService interface,
 * generating a proxy if needed.
 */
public static com.ordio.player.backend.IPlaybackService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.ordio.player.backend.IPlaybackService))) {
return ((com.ordio.player.backend.IPlaybackService)iin);
}
return new com.ordio.player.backend.IPlaybackService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_PlayPlayback:
{
data.enforceInterface(DESCRIPTOR);
this.PlayPlayback();
reply.writeNoException();
return true;
}
case TRANSACTION_PausePlayback:
{
data.enforceInterface(DESCRIPTOR);
this.PausePlayback();
reply.writeNoException();
return true;
}
case TRANSACTION_StopPlayback:
{
data.enforceInterface(DESCRIPTOR);
this.StopPlayback();
reply.writeNoException();
return true;
}
case TRANSACTION_LoopPlayback:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.LoopPlayback(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_PlaySong:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.PlaySong(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setEQ:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
double _arg1;
_arg1 = data.readDouble();
this.setEQ(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setVolume:
{
data.enforceInterface(DESCRIPTOR);
double _arg0;
_arg0 = data.readDouble();
double _arg1;
_arg1 = data.readDouble();
this.setVolume(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setPlaybackRate:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setPlaybackRate(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getPositionInMs:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getPositionInMs();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_resetEQ:
{
data.enforceInterface(DESCRIPTOR);
this.resetEQ();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.ordio.player.backend.IPlaybackService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void PlayPlayback() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_PlayPlayback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void PausePlayback() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_PausePlayback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void StopPlayback() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_StopPlayback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void LoopPlayback(int ms, boolean continuousPlay) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(ms);
_data.writeInt(((continuousPlay)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_LoopPlayback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void PlaySong(java.lang.String filename) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(filename);
mRemote.transact(Stub.TRANSACTION_PlaySong, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setEQ(int channel, double vol) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(channel);
_data.writeDouble(vol);
mRemote.transact(Stub.TRANSACTION_setEQ, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setVolume(double left, double right) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeDouble(left);
_data.writeDouble(right);
mRemote.transact(Stub.TRANSACTION_setVolume, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setPlaybackRate(int speed) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(speed);
mRemote.transact(Stub.TRANSACTION_setPlaybackRate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getPositionInMs() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPositionInMs, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void resetEQ() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_resetEQ, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_PlayPlayback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_PausePlayback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_StopPlayback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_LoopPlayback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_PlaySong = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_setEQ = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_setVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_setPlaybackRate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getPositionInMs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_resetEQ = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
}
public void PlayPlayback() throws android.os.RemoteException;
public void PausePlayback() throws android.os.RemoteException;
public void StopPlayback() throws android.os.RemoteException;
public void LoopPlayback(int ms, boolean continuousPlay) throws android.os.RemoteException;
public void PlaySong(java.lang.String filename) throws android.os.RemoteException;
public void setEQ(int channel, double vol) throws android.os.RemoteException;
public void setVolume(double left, double right) throws android.os.RemoteException;
public void setPlaybackRate(int speed) throws android.os.RemoteException;
public int getPositionInMs() throws android.os.RemoteException;
public void resetEQ() throws android.os.RemoteException;
}
