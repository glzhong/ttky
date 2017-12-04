/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\music\\����\\�������\\ttky0912\\ttky\\src\\com\\tiantiankuyin\\component\\service\\IRemotePlayerListener.aidl
 */
package com.tiantiankuyin.component.service;
public interface IRemotePlayerListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.tiantiankuyin.component.service.IRemotePlayerListener
{
private static final java.lang.String DESCRIPTOR = "com.tiantiankuyin.component.service.IRemotePlayerListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.tiantiankuyin.component.service.IRemotePlayerListener interface,
 * generating a proxy if needed.
 */
public static com.tiantiankuyin.component.service.IRemotePlayerListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.tiantiankuyin.component.service.IRemotePlayerListener))) {
return ((com.tiantiankuyin.component.service.IRemotePlayerListener)iin);
}
return new com.tiantiankuyin.component.service.IRemotePlayerListener.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
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
case TRANSACTION_onError:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.onError(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onPreparing:
{
data.enforceInterface(DESCRIPTOR);
this.onPreparing();
reply.writeNoException();
return true;
}
case TRANSACTION_onPrepared:
{
data.enforceInterface(DESCRIPTOR);
this.onPrepared();
reply.writeNoException();
return true;
}
case TRANSACTION_onStartBuffer:
{
data.enforceInterface(DESCRIPTOR);
this.onStartBuffer();
reply.writeNoException();
return true;
}
case TRANSACTION_onBufferingUpdate:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onBufferingUpdate(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onStartPlay:
{
data.enforceInterface(DESCRIPTOR);
this.onStartPlay();
reply.writeNoException();
return true;
}
case TRANSACTION_onMusicPause:
{
data.enforceInterface(DESCRIPTOR);
this.onMusicPause();
reply.writeNoException();
return true;
}
case TRANSACTION_onProgressChanged:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onProgressChanged(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onCompletion:
{
data.enforceInterface(DESCRIPTOR);
this.onCompletion();
reply.writeNoException();
return true;
}
case TRANSACTION_onMusicStop:
{
data.enforceInterface(DESCRIPTOR);
this.onMusicStop();
reply.writeNoException();
return true;
}
case TRANSACTION_onBufferComplete:
{
data.enforceInterface(DESCRIPTOR);
this.onBufferComplete();
reply.writeNoException();
return true;
}
case TRANSACTION_onBuffer:
{
data.enforceInterface(DESCRIPTOR);
this.onBuffer();
reply.writeNoException();
return true;
}
case TRANSACTION_onCacheUpdate:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
this.onCacheUpdate(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.tiantiankuyin.component.service.IRemotePlayerListener
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
	 * 播放器错误
	 * 
	 * @param what
	 *            分为MEDIA_ERROR_UNKNOWN和MEDIA_ERROR_SERVER_DIED
	 * @param extra
	 *            错误编码
	 */
@Override public void onError(int what, int extra) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(what);
_data.writeInt(extra);
mRemote.transact(Stub.TRANSACTION_onError, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 当播放器准备完毕
	 */
@Override public void onPreparing() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onPreparing, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 当播放器准备完毕
	 */
@Override public void onPrepared() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onPrepared, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 播放器开始缓冲
	 */
@Override public void onStartBuffer() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onStartBuffer, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 播放在线多媒体缓冲进度
	 * 
	 * @param percent
	 */
@Override public void onBufferingUpdate(int percent) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(percent);
mRemote.transact(Stub.TRANSACTION_onBufferingUpdate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 播放器开始播放
	 */
@Override public void onStartPlay() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onStartPlay, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 当暂停播放
	 */
@Override public void onMusicPause() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onMusicPause, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 当播放进度发生改变
	 * 
	 * @param currentMilliseconds
	 *            当前播放到的毫秒数
	 */
@Override public void onProgressChanged(int currentMilliseconds) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(currentMilliseconds);
mRemote.transact(Stub.TRANSACTION_onProgressChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 当播放器已经播放完某个媒体文件
	 */
@Override public void onCompletion() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onCompletion, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 当停止播放音乐
	 */
@Override public void onMusicStop() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onMusicStop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 通知观察者正在缓冲（此缓冲不是缓冲完全部歌曲，是正在与服务器交互，可用于等待提示）
	 */
@Override public void onBufferComplete() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onBufferComplete, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 通知观察者缓冲完成（此缓冲不是缓冲完全部歌曲，是请求完成可以播放）
	 */
@Override public void onBuffer() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onBuffer, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 通知观察者缓存进度
	 * 
	 * @param currentCache
	 */
@Override public void onCacheUpdate(long currentCache) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(currentCache);
mRemote.transact(Stub.TRANSACTION_onCacheUpdate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onPreparing = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onPrepared = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onStartBuffer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_onBufferingUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_onStartPlay = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_onMusicPause = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_onProgressChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_onCompletion = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_onMusicStop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_onBufferComplete = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_onBuffer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_onCacheUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
}
/**
	 * 播放器错误
	 * 
	 * @param what
	 *            分为MEDIA_ERROR_UNKNOWN和MEDIA_ERROR_SERVER_DIED
	 * @param extra
	 *            错误编码
	 */
public void onError(int what, int extra) throws android.os.RemoteException;
/**
	 * 当播放器准备完毕
	 */
public void onPreparing() throws android.os.RemoteException;
/**
	 * 当播放器准备完毕
	 */
public void onPrepared() throws android.os.RemoteException;
/**
	 * 播放器开始缓冲
	 */
public void onStartBuffer() throws android.os.RemoteException;
/**
	 * 播放在线多媒体缓冲进度
	 * 
	 * @param percent
	 */
public void onBufferingUpdate(int percent) throws android.os.RemoteException;
/**
	 * 播放器开始播放
	 */
public void onStartPlay() throws android.os.RemoteException;
/**
	 * 当暂停播放
	 */
public void onMusicPause() throws android.os.RemoteException;
/**
	 * 当播放进度发生改变
	 * 
	 * @param currentMilliseconds
	 *            当前播放到的毫秒数
	 */
public void onProgressChanged(int currentMilliseconds) throws android.os.RemoteException;
/**
	 * 当播放器已经播放完某个媒体文件
	 */
public void onCompletion() throws android.os.RemoteException;
/**
	 * 当停止播放音乐
	 */
public void onMusicStop() throws android.os.RemoteException;
/**
	 * 通知观察者正在缓冲（此缓冲不是缓冲完全部歌曲，是正在与服务器交互，可用于等待提示）
	 */
public void onBufferComplete() throws android.os.RemoteException;
/**
	 * 通知观察者缓冲完成（此缓冲不是缓冲完全部歌曲，是请求完成可以播放）
	 */
public void onBuffer() throws android.os.RemoteException;
/**
	 * 通知观察者缓存进度
	 * 
	 * @param currentCache
	 */
public void onCacheUpdate(long currentCache) throws android.os.RemoteException;
}
