/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\music\\����\\�������\\ttky0912\\ttky\\src\\com\\tiantiankuyin\\component\\service\\IDownload.aidl
 */
package com.tiantiankuyin.component.service;
public interface IDownload extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.tiantiankuyin.component.service.IDownload
{
private static final java.lang.String DESCRIPTOR = "com.tiantiankuyin.component.service.IDownload";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.tiantiankuyin.component.service.IDownload interface,
 * generating a proxy if needed.
 */
public static com.tiantiankuyin.component.service.IDownload asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.tiantiankuyin.component.service.IDownload))) {
return ((com.tiantiankuyin.component.service.IDownload)iin);
}
return new com.tiantiankuyin.component.service.IDownload.Stub.Proxy(obj);
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
case TRANSACTION_startDownloadTask:
{
data.enforceInterface(DESCRIPTOR);
com.tiantiankuyin.component.service.DownloadFile _arg0;
if ((0!=data.readInt())) {
_arg0 = com.tiantiankuyin.component.service.DownloadFile.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _arg1;
_arg1 = (0!=data.readInt());
this.startDownloadTask(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_pauseDownloadTask:
{
data.enforceInterface(DESCRIPTOR);
com.tiantiankuyin.component.service.DownloadFile _arg0;
if ((0!=data.readInt())) {
_arg0 = com.tiantiankuyin.component.service.DownloadFile.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.pauseDownloadTask(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_deleteDownloadTask:
{
data.enforceInterface(DESCRIPTOR);
com.tiantiankuyin.component.service.DownloadFile _arg0;
if ((0!=data.readInt())) {
_arg0 = com.tiantiankuyin.component.service.DownloadFile.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.deleteDownloadTask(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_startAllDownloadTask:
{
data.enforceInterface(DESCRIPTOR);
this.startAllDownloadTask();
reply.writeNoException();
return true;
}
case TRANSACTION_startAllNormalDownloadTask:
{
data.enforceInterface(DESCRIPTOR);
this.startAllNormalDownloadTask();
reply.writeNoException();
return true;
}
case TRANSACTION_startALlWifiDownloadTask:
{
data.enforceInterface(DESCRIPTOR);
this.startALlWifiDownloadTask();
reply.writeNoException();
return true;
}
case TRANSACTION_pauseAllDownloadTask:
{
data.enforceInterface(DESCRIPTOR);
this.pauseAllDownloadTask();
reply.writeNoException();
return true;
}
case TRANSACTION_pauseAllNormalDownloadTask:
{
data.enforceInterface(DESCRIPTOR);
this.pauseAllNormalDownloadTask();
reply.writeNoException();
return true;
}
case TRANSACTION_pauseAllWifiDownloadTask:
{
data.enforceInterface(DESCRIPTOR);
this.pauseAllWifiDownloadTask();
reply.writeNoException();
return true;
}
case TRANSACTION_deleteAllWifiDownloadTask:
{
data.enforceInterface(DESCRIPTOR);
this.deleteAllWifiDownloadTask();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.tiantiankuyin.component.service.IDownload
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
	 * 开始某个下载任务
	 * 
	 * @param file
	 * @param isNewFileTask
	 *            是否是一个新文件任务
	 */
@Override public void startDownloadTask(com.tiantiankuyin.component.service.DownloadFile file, boolean isNewFileTask) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((file!=null)) {
_data.writeInt(1);
file.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeInt(((isNewFileTask)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_startDownloadTask, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 暂停某个下载任务
	 * 
	 * @param file
	 */
@Override public void pauseDownloadTask(com.tiantiankuyin.component.service.DownloadFile file) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((file!=null)) {
_data.writeInt(1);
file.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_pauseDownloadTask, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 删除某个下载任务
	 * 
	 * @param file
	 */
@Override public void deleteDownloadTask(com.tiantiankuyin.component.service.DownloadFile file) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((file!=null)) {
_data.writeInt(1);
file.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_deleteDownloadTask, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 开始全部下载任务
	 */
@Override public void startAllDownloadTask() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startAllDownloadTask, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 开始全部普通下载任务
	 */
@Override public void startAllNormalDownloadTask() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startAllNormalDownloadTask, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 开始全部预约下载任务
	 */
@Override public void startALlWifiDownloadTask() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startALlWifiDownloadTask, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 暂停全部下载任务
	 */
@Override public void pauseAllDownloadTask() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_pauseAllDownloadTask, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 暂停全部普通下载任务
	 */
@Override public void pauseAllNormalDownloadTask() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_pauseAllNormalDownloadTask, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 暂停全部预约下载任务
	 */
@Override public void pauseAllWifiDownloadTask() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_pauseAllWifiDownloadTask, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 删除所有已下载任务
	 */
@Override public void deleteAllWifiDownloadTask() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_deleteAllWifiDownloadTask, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_startDownloadTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_pauseDownloadTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_deleteDownloadTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_startAllDownloadTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_startAllNormalDownloadTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_startALlWifiDownloadTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_pauseAllDownloadTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_pauseAllNormalDownloadTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_pauseAllWifiDownloadTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_deleteAllWifiDownloadTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
}
/**
	 * 开始某个下载任务
	 * 
	 * @param file
	 * @param isNewFileTask
	 *            是否是一个新文件任务
	 */
public void startDownloadTask(com.tiantiankuyin.component.service.DownloadFile file, boolean isNewFileTask) throws android.os.RemoteException;
/**
	 * 暂停某个下载任务
	 * 
	 * @param file
	 */
public void pauseDownloadTask(com.tiantiankuyin.component.service.DownloadFile file) throws android.os.RemoteException;
/**
	 * 删除某个下载任务
	 * 
	 * @param file
	 */
public void deleteDownloadTask(com.tiantiankuyin.component.service.DownloadFile file) throws android.os.RemoteException;
/**
	 * 开始全部下载任务
	 */
public void startAllDownloadTask() throws android.os.RemoteException;
/**
	 * 开始全部普通下载任务
	 */
public void startAllNormalDownloadTask() throws android.os.RemoteException;
/**
	 * 开始全部预约下载任务
	 */
public void startALlWifiDownloadTask() throws android.os.RemoteException;
/**
	 * 暂停全部下载任务
	 */
public void pauseAllDownloadTask() throws android.os.RemoteException;
/**
	 * 暂停全部普通下载任务
	 */
public void pauseAllNormalDownloadTask() throws android.os.RemoteException;
/**
	 * 暂停全部预约下载任务
	 */
public void pauseAllWifiDownloadTask() throws android.os.RemoteException;
/**
	 * 删除所有已下载任务
	 */
public void deleteAllWifiDownloadTask() throws android.os.RemoteException;
}
