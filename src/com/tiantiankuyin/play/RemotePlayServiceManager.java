package com.tiantiankuyin.play;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.tiantiankuyin.component.service.IRemotePlayService;
import com.tiantiankuyin.component.service.IRemotePlayerListener;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.utils.Lg;
 

public class RemotePlayServiceManager {
	private final String TAG = "RemotePlayServiceManager";
	private final byte[] SERVICE_VISIT_LOCK = new byte[0];
	private boolean mIsBound = false;
	private Runnable mOnServiceConnected;
	private ServiceConnection mServiceConnection;
	private IRemotePlayService mRemotePlayService;

	public void stop() {
		if (mIsBound) {
			try {
				mRemotePlayService.stop();
			} catch (RemoteException e) {
				Lg.d("stop()：音乐服务发生异常", e);
			}
		} else {
			Lg.e("stop()：音乐播放服务连接失败");
		}
	}

	public void startPlayNetMusic(String url, String songName, String fileID,
			String gid, String singerName) {
		if (mIsBound) {
			try {
				mRemotePlayService.startPlayNetMusic(url, songName, fileID,
						gid, singerName);
			} catch (RemoteException e) {
				Lg.d("startPlayNetMusic():音乐服务发生异常", e);
			}
		} else {
			Lg.e("startPlayNetMusic():音乐播放服务连接失败");
		}
	}

	public void startPlayMusic(String musicPath) {
		if (mIsBound) {
			try {
				mRemotePlayService.startPlayMusic(musicPath);
			} catch (RemoteException e) {
				Lg.d("startPlayMusic():音乐服务发生异常", e);
			}
		} else {
			Lg.e("startPlayMusic():音乐播放服务连接失败");
		}
	}

	public void seekTo(int milliseconds) {
		if (mIsBound) {
			try {
				mRemotePlayService.seekTo(milliseconds);
			} catch (RemoteException e) {
				Lg.d("seekTo():音乐服务发生异常", e);
			}
		} else {
			Lg.e("seekTo():音乐播放服务连接失败");
		}
	}

	public void play() {
		if (mIsBound) {
			try {
				mRemotePlayService.play();
			} catch (RemoteException e) {
				Lg.d("play():音乐服务发生异常", e);
			}
		} else {
			Lg.e("play():音乐播放服务连接失败");
		}
	}

	public void pause() {
		if (mIsBound) {
			try {
				mRemotePlayService.pause();
			} catch (RemoteException e) {
				Lg.d("pause():音乐服务发生异常", e);
			}
		} else {
			Lg.e("pause():音乐播放服务连接失败");
		}
	}

	public boolean isPrepared() {
		if (mIsBound) {
			try {
				return mRemotePlayService.isPrepared();
			} catch (RemoteException e) {
				Lg.d("isPrepared():音乐服务发生异常", e);
			}
		} else {
			Lg.e("isPrepared():音乐播放服务连接失败");
		}
		return false;
	}

	public boolean isPlaying() {
		if (mIsBound) {
			try {
				return mRemotePlayService.isPlaying();
			} catch (RemoteException e) {
				Lg.d("isPlaying():音乐服务发生异常", e);
			}
		} else {
			Lg.e("isPlaying():音乐播放服务连接失败");
		}
		return false;
	}

	public int getDuration() {
		if (mIsBound) {
			try {
				return mRemotePlayService.getDuration();
			} catch (RemoteException e) {
				Lg.d("getDuration():音乐服务发生异常", e);
			}
		} else {
			Lg.e("getDuration():音乐播放服务连接失败");
		}
		return 0;
	}

	public int getCurrentPosition() {
		if (mIsBound) {
			try {
				return mRemotePlayService.getCurrentPosition();
			} catch (RemoteException e) {
				Lg.d("getCurrentPosition():音乐服务发生异常", e);
			}
		} else {
			Lg.e("getCurrentPosition():音乐播放服务连接失败");
		}
		return 0;
	}

	public void deleteObservers() {
		if (mIsBound) {
			try {
				mRemotePlayService.deleteObservers();
			} catch (RemoteException e) {
				Lg.d("deleteObservers():音乐服务发生异常", e);
			}
		} else {
			Lg.e("deleteObservers():音乐播放服务连接失败");
		}
	}

	public void deleteObserver(String observerName) {
		if (mIsBound) {
			try {
				mRemotePlayService.deleteObserver(observerName);
			} catch (RemoteException e) {
				Lg.d("deleteObserver():音乐服务发生异常", e);
			}
		} else {
			Lg.e("deleteObserver():音乐播放服务连接失败");
		}
	}

	public void addObserver(String observerName,IRemotePlayerListener l) {
		if (mIsBound) {
			try {
				Lg.e(observerName);
				mRemotePlayService.addObserver(observerName,l);
			} catch (RemoteException e) {
				Lg.d("addObserver():音乐服务发生异常", e);
			}
		} else {
			Lg.e("addObserver():音乐播放服务连接失败");
		}
	}

	public void bind(Context context, final Runnable onServiceConnected) {
		synchronized (SERVICE_VISIT_LOCK) {
			if (!mIsBound) {
				mOnServiceConnected = onServiceConnected;
				mServiceConnection = new ServiceConnection() {

					public void onServiceConnected(ComponentName name,
							IBinder service) {
						synchronized (SERVICE_VISIT_LOCK) {
							mRemotePlayService = IRemotePlayService.Stub
									.asInterface(service);
							mIsBound = true;
//							Log.e(TAG, "RemotePlayServiceManager.startup()");

							if (mOnServiceConnected != null) {
								mOnServiceConnected.run();
								mOnServiceConnected = null;
							}
						}
					}

					public void onServiceDisconnected(ComponentName name) {
						synchronized (SERVICE_VISIT_LOCK) {
							mIsBound = false;
						}

					}

				};
				context.bindService(
						new Intent(IntentAction.INTENT_PLAY_SERVICE),
						mServiceConnection, Context.BIND_AUTO_CREATE);
//				Log.v(TAG, "startup():音乐播放服务绑定成功");
			} else {
//				Log.w(TAG, "startup():已与音乐播放服务建立连接，不能重复连接");
			}
		}
	}

	/** 取消 绑定 音乐播放服务 */
	public void unbind(Context context) {
		synchronized (SERVICE_VISIT_LOCK) {
			try {
				if (mIsBound) {
					context.unbindService(this.mServiceConnection);
					mRemotePlayService = null;
					mServiceConnection = null;
					mIsBound = false;
//					Log.e(TAG, "RemotePlayServiceManager.shutdown()" + mIsBound);
				}
			} catch (IllegalArgumentException e) {
//				Log.w(TAG, "unbind 服务失败，可能服务已经结束", e);
			}
		}
	}
}
