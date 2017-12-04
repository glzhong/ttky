package com.tiantiankuyin.database.bll;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.tiantiankuyin.bean.CategoryVO;
import com.tiantiankuyin.bean.HotSaleVO;
import com.tiantiankuyin.bean.OlAlbumList;
import com.tiantiankuyin.bean.OlAlbumVO;
import com.tiantiankuyin.bean.OlDownloadVO;
import com.tiantiankuyin.bean.OlLrcItem;
import com.tiantiankuyin.bean.OlRecommondSong;
import com.tiantiankuyin.bean.OlSingerVO;
import com.tiantiankuyin.bean.OlSongVO;
import com.tiantiankuyin.bean.OmnibusTipList;
import com.tiantiankuyin.bean.RecommendBanner;
import com.tiantiankuyin.bean.SingerInfoVO;
import com.tiantiankuyin.bean.SingerListBean;
import com.tiantiankuyin.bean.Update;
import com.tiantiankuyin.net.EasouHttpException;
import com.tiantiankuyin.net.EasouHttpRequest;
import com.tiantiankuyin.net.EasouHttpRequest.HttpRequestMethod;
import com.tiantiankuyin.net.EasouHttpResponse;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.LrcUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 作用：管理非本地音乐操作，1.提供对非本地音乐数据的获取
 * 
 * @author Erica
 */

public class OnlineMusicManager {
	private static OnlineMusicManager mOnlineMusicManager = new OnlineMusicManager();

	private OnlineMusicManager() {
	};

	public static OnlineMusicManager getInstence() {
		return mOnlineMusicManager;
	}

	/**
	 * 异步获取分类列表数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void getCategorys(final Context context,
			final OnDataPreparedListener<List<CategoryVO>> l, final String _url) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, false) {
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if (response != null) {
						byte[] results = response.getResultData();
						if (results == null) {
							l.callback(context, null);
							return;
						}
						String json = new String(results);
						// 解析
						if (json != null && json.length() > 0) {
							Gson gson = new Gson();
							Type collectionType = new TypeToken<Collection<CategoryVO>>() {
							}.getType();
							List<CategoryVO> netCategorys = gson.fromJson(json,
									collectionType);
							l.callback(context, netCategorys);
						}
					} else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 异步获取榜单列表数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void getHotSale(final Context context,
			final OnDataPreparedListener<HotSaleVO> l, final String _url) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, false) {
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if (response != null) {
						byte[] results = response.getResultData();
						if (results == null) {
							l.callback(context, null);
							return;
						}
						String json = new String(results);
						System.out.print("========================================================json"+json);
						// 解析
						if (json != null && json.length() > 0) {
							Gson gson = new Gson();
							HotSaleVO hotSaleVO = gson.fromJson(json,
									HotSaleVO.class);
							l.callback(context, hotSaleVO);
						}
					} else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 异步获取精选集列表数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void getOmnibusData(final Context context,
			final OnDataPreparedListener<OlAlbumVO> l, final String _url) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, false) {
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if (response != null) {
						byte[] results = response.getResultData();
						if (results == null) {
							l.callback(context, null);
							return;
						}
						String json = new String(results);
						// 解析
						if (json != null && json.length() > 0) {
							Gson gson = new Gson();
							OlAlbumVO temp_mOlAlbumVO = gson.fromJson(json,
									OlAlbumVO.class);
							l.callback(context, temp_mOlAlbumVO);
						}
					} else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 异步获取精选集标签数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void getOmnibusTipData(final Context context,
			final OnDataPreparedListener<OmnibusTipList> l, final String _url) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, false) {
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if (response != null) {
						byte[] results = response.getResultData();
						if (results == null) {
							l.callback(context, null);
							return;
						}
						String json = new String(results);
						// 解析
						if (json != null && json.length() > 0) {
							Gson gson = new Gson();
							OmnibusTipList temp_mOmnibusTipList = gson
									.fromJson(json, OmnibusTipList.class);
							l.callback(context, temp_mOmnibusTipList);
						}
					} else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 异步获取标签精选集数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void getSearchOmnibusData(final Context context,
			final OnDataPreparedListener<OlAlbumVO> l, final String _url) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, false) {
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if (response != null) {
						byte[] results = response.getResultData();
						if (results == null) {
							l.callback(context, null);
							return;
						}
						String json = new String(results);
						// 解析
						if (json != null && json.length() > 0) {
							Gson gson = new Gson();
							OlAlbumVO temp_mOlAlbumVO = gson.fromJson(json,
									OlAlbumVO.class);
							l.callback(context, temp_mOlAlbumVO);
						}
					} else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 异步获取精选集详细数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void getOmnibusDetailData(final Context context,
			final OnDataPreparedListener<OlAlbumList> l, final String _url) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, false) {
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if (response != null) {
						byte[] results = response.getResultData();
						if (results == null) {
							l.callback(context, null);
							return;
						}
						String json = new String(results);
						// 解析
						if (json != null && json.length() > 0) {
							Gson gson = new Gson();
							OlAlbumList temp_mOlAlbumList = gson.fromJson(json, OlAlbumList.class);
							l.callback(context, temp_mOlAlbumList);
						}
					} else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 异步获取推荐数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void getRecommondData(final Context context,
			final OnDataPreparedListener<OlRecommondSong> l, final String _url,final String serviceId) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, false) {
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if (response != null) {
						byte[] results = response.getResultData();
						if (results == null) {
							l.callback(context, null);
							return;
						}
						String json = new String(results);
						// 解析
						if (json != null && json.length() > 0) {
							OlRecommondSong bean = new OlRecommondSong();
							try {
								JSONArray jsonArray = new JSONArray(json);
								int jsonLength = jsonArray.length();
								List<OlSongVO> dataList = new ArrayList<OlSongVO>();
								for(int i=0; i<jsonLength;++i){
									OlSongVO olSongVO = new OlSongVO();
									JSONObject jsonObject = jsonArray.getJSONObject(i);
									olSongVO.setSinger(jsonObject.getString("singer"));
									olSongVO.setSong(jsonObject.getString("songName"));
									olSongVO.setSongCode(jsonObject.getString("songCode"));
									olSongVO.setServiceId(serviceId);
									dataList.add(olSongVO);
								}
								bean.setDataList(dataList);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}							
//							Gson gson = new Gson();
//							OlRecommondSong bean = gson.fromJson(json,
//									OlRecommondSong.class);
							l.callback(context, bean);
						}
					} else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 异步获取推荐banner数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void getBannerData(final Context context,
			final OnDataPreparedListener<RecommendBanner> l, final String _url) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, false) {
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if (response != null) {
						byte[] results = response.getResultData();
						if (results == null) {
							l.callback(context, null);
							return;
						}
						String json = new String(results);
						// 解析
						if (json != null && json.length() > 0) {
							Gson gson = new Gson();
							RecommendBanner bean = gson.fromJson(json,
									RecommendBanner.class);
							l.callback(context, bean);
						}
					} else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 异步获取线上歌曲数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void getSongListData(final Context context,
			final OnDataPreparedListener<OlSingerVO> l, final String _url) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, false) {
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if (response != null) {
						byte[] results = response.getResultData();
						if (results == null) {
							l.callback(context, null);
							return;
						}
						String json = new String(results);
						// 解析
						if (json != null && json.length() > 0) {
							Gson gson = new Gson();
							OlSingerVO olSingerVO=gson.fromJson(json, OlSingerVO.class);
							l.callback(context, olSingerVO);
						}
					} else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 异步获取歌手信息数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void getSingerData(final Context context,
			final OnDataPreparedListener<SingerInfoVO> l, final String _url) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, false) {
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if (response != null) {
						byte[] results = response.getResultData();
						if (results == null) {
							l.callback(context, null);
							return;
						}
						String json = new String(results);
						// 解析
						if (json != null && json.length() > 0) {
							Gson gson = new Gson();
							SingerInfoVO temp_mSingerInfoVO = gson.fromJson(json,
									SingerInfoVO.class);
							l.callback(context, temp_mSingerInfoVO);
						}
					} else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 异步获取歌手列表数据，返回值可能为null
	 * 
	 * @return
	 */
	public void getSingerListData(final Context context,
			final OnDataPreparedListener<SingerListBean> l, final String _url) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, false) {
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if (response != null) {
						byte[] results = response.getResultData();
						if (results == null) {
							l.callback(context, null);
							return;
						}
						String json = new String(results);
						// 解析
						if (json != null && json.length() > 0) {
							Gson gson = new Gson();
							SingerListBean temp_mSingerListBean = gson.fromJson(json,
									SingerListBean.class);
							l.callback(context, temp_mSingerListBean);
						}
					} else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取意见反馈响应数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void sendFeedBackByNet(final Context context,
			final OnDataPreparedListener<EasouHttpResponse> l, final String _url) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, false) {
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if (response != null) {
						l.callback(context, response);
					} else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取搜索结果数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void getLenvo(final Context context,
			final OnDataPreparedListener<List<String>> l, final String _url) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, false) {
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if (response != null) {
						byte[] results = response.getResultData();
						if (results == null) {
							l.callback(context, null);
							return;
						}
						String json = new String(results);
						// 解析
						if (json != null && json.length() > 0) {
							List<String> keys=new ArrayList<String>();
							try {
								JSONObject jsonObject = new JSONObject(json);
								JSONArray jarray = jsonObject.getJSONArray("dataList");
								for (int i = 0; i < jarray.length(); i++) {
									JSONObject keyObj = (JSONObject) jarray.opt(i);
									if (keyObj == null) {
										break;
									}
									String key = keyObj.getString("key");
									keys.add(key);
								}
								l.callback(context, keys);
							} catch (JSONException e) {
//								e.printStackTrace();
							}
						}
					} else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取搜索热词结果数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void getHotkeys(final Context context,
			final OnDataPreparedListener<List<String>> l, final String _url) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, false) {
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if (response != null) {
						byte[] results = response.getResultData();
						if (results == null) {
							l.callback(context, null);
							return;
						}
						String json = new String(results);
						// 解析
						if (json != null && json.length() > 0) {
							List<String> keys=new ArrayList<String>();
							try {
								JSONObject jsonObject = new JSONObject(json);
								JSONArray  jsonArray=jsonObject.getJSONArray("dataList");
								for(int i=0;i<jsonArray.length();i++){
									JSONObject obj= (JSONObject)jsonArray.get(i);
									String key=obj.getString("title");
									keys.add(key);
								}
								l.callback(context, keys);
							} catch (JSONException e) {
								l.callback(context, null);
							}
						}
					} else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 请求更新，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void getUpdate(final Context context,
			final OnDataPreparedListener<Update> l, final String _url) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, false) {
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if (response != null) {
						Update updateBean = CommonUtils.buildXmlUploadInf(response.getResultData());
						l.callback(context, updateBean);
					} else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 异步获取线上歌曲信息数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void getSongUrlData(final Context context,
			final OnDataPreparedListener<String> l, final String _url,final boolean isGzip) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, isGzip) {
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if (response != null) {
						byte[] resultData = response.getResultData();
						if(resultData == null)
							return;
						String url = new String(resultData);
						l.callback(context, url);
					} else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 异步获取线上歌词数据，返回值可能为null
	 * @author Perry
	 * @return
	 */
	public void getLrcData(final Context context,
			final OnDataPreparedListener<String> l, final String _url,final boolean isGzip) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, isGzip) {
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if (response != null) {
						byte[] resultData = response.getResultData();
						if(resultData == null)
							return;
						String encode = LrcUtils.getEncode(resultData);
						String url;
						try {
							url = new String (resultData, encode);
						} catch (UnsupportedEncodingException e) {
							url = new String (resultData);
						}
						l.callback(context, url);
					} else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 异步获取歌曲下载列表数据，返回值可能为null
	 * @author Perry
	 * @return
	 */
	public void getDownloadUrlData(final Context context,
			final OnDataPreparedListener<String> l, final String _url) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, true) {

				String downloadUrl = null;
				
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if(downloadUrl != null) {
						l.callback(context, downloadUrl);
					}else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}

				@Override
				protected EasouHttpResponse doInBackground(Object... params)
						throws EasouHttpException {
					EasouHttpResponse response = super.doInBackground(params);
					if(response != null) {
						byte[] results = response.getResultData();
						if(results != null){
							String json = new String(results);
							// 解析
							if (json != null && json.length() > 0) {
								Gson gson = new Gson();
								final OlDownloadVO temp_mOlDownloadVO = gson
										.fromJson(json, OlDownloadVO.class);
								downloadUrl = PlayLogicManager.newInstance()
										.checkDownloadUrls(temp_mOlDownloadVO);
							}
						}
					}
					return response;
				}
			};
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 异步获取线上歌词列表数据，并对其进行乱码编码验证。
	 * 返回值一个最合适的歌词下载地址，可能为null
	 * @author Perry
	 * @return
	 */
	public void getLrcUrlFromListData(final Context context,
			final OnDataPreparedListener<String> l, final String _url,final boolean isGzip) {
		try {
			new EasouHttpRequest(context, _url,
					HttpRequestMethod.GET, null, isGzip) {
				
				String lrcUrl = null;
				
				@Override
				protected void onFinish(EasouHttpResponse response) {
					if(lrcUrl != null) {
						l.callback(context, lrcUrl);
					}else {
						l.callback(context, null);
					}
				}

				@Override
				protected void onError(EasouHttpException exception) {
					l.callback(context, null);
				}

				@Override
				protected EasouHttpResponse doInBackground(Object... params)
						throws EasouHttpException {

					EasouHttpResponse response = super.doInBackground(params);
					if(response != null) {
						byte[] results = response.getResultData();
						if(results != null){
							String json = new String(results);
							// 解析
							if (json != null && json.length() > 0) {
								Gson gson = new Gson();
								try {
									List<OlLrcItem> list = gson.fromJson(
											json, new TypeToken<List<OlLrcItem>>() {
											}.getType());
									lrcUrl = LrcUtils.checkLrcUrl(list);
								} catch (Exception e) {
									// TODO: handle exception
									lrcUrl = null;
								}
								//TODO 增加验证LRC集合地址的代码。
								
							}
						}
					}
					return response;
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
