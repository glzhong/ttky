package com.tiantiankuyin.component.activity.online;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmsc.cmmusic.common.CMMusicCallback;
import com.cmsc.cmmusic.common.ExclusiveManagerInterface;
import com.cmsc.cmmusic.common.data.OrderResult;
import com.tiantiankuyin.bean.DownloadBean;
import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.component.service.DownloadFile;
import com.tiantiankuyin.component.service.DownloadFile.DownloadType;
import com.tiantiankuyin.component.service.DownloadService;
import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.SharedPreferencesManager;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.CommonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

public class CategoryActivityTen extends Activity {
	 
	public static final String DOWNLOADURL = "http://120.76.235.119/selected/";//http://120.76.46.35/ttky/
	TextView tv = null;
	ListView lv = null;
	ImageButton btn_back = null;
	Button btn_selectAll = null;
	Button btn_inverseSelect = null;
	Button btn_calcel = null;
	Button btn_download = null;

	JsonArray ja = new JsonArray();
	ArrayList<DownloadBean> listStr = null;
	private List<HashMap<String, Object>> list = null;
	private MyAdapter adapter;
	private View rootView;
	private SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(); 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView = LayoutInflater.from(this).inflate(R.layout.main10, null);
		setContentView(rootView);
		CommonUtils.setTitle(rootView, "酷音10元专属", true, false);
		tv = (TextView) this.findViewById(R.id.tv);
		lv = (ListView) this.findViewById(R.id.lv);
		btn_back = (ImageButton) this.findViewById(R.id.back_btn);
		btn_selectAll = (Button) this.findViewById(R.id.selectall);
		btn_inverseSelect = (Button) this.findViewById(R.id.inverseselect);
		btn_calcel = (Button) this.findViewById(R.id.cancel);
		btn_download = (Button) this.findViewById(R.id.download);
		try {
			showCheckBoxListView();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		btn_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				back();
			}
		});

		// 全选
		btn_selectAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				listStr = new ArrayList<DownloadBean>();
				for (int i = 0; i < list.size(); i++) {
					MyAdapter.isSelected.put(i, true);
					listStr.add((DownloadBean) data.get(i));
				}
				adapter.notifyDataSetChanged();// 注意这一句必须加上，否则checkbox无法正常更新状态
				tv.setText("已选中" + listStr.size() + "首歌");
			}
		});

		// 反选
		btn_inverseSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i = 0; i < list.size(); i++) {
					if (MyAdapter.isSelected.get(i) == false) {
						MyAdapter.isSelected.put(i, true);
						listStr.add((DownloadBean) data.get(i));
					} else {
						MyAdapter.isSelected.put(i, false);
						listStr.remove((DownloadBean) data.get(i));
					}
				}
				adapter.notifyDataSetChanged();
				tv.setText("已选中" + listStr.size() + "首歌");
			}

		});

		// 取消已选
		btn_calcel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i = 0; i < list.size(); i++) {
					if (MyAdapter.isSelected.get(i) == true) {
						MyAdapter.isSelected.put(i, false);
						listStr.remove((DownloadBean) data.get(i));
					}
				}
				adapter.notifyDataSetChanged();
				tv.setText("已选中" + listStr.size() + "首歌");
				// if (lv.getVisibility() == 8) {
				// lv.setVisibility(View.VISIBLE);
				// } else {
				// lv.setVisibility(View.GONE);
				// }

			}

		});
		btn_download.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				for (int i = 0; i < listStr.size(); i++) {
					System.out.println("选中内容 = " + listStr.get(i).getSongName() +"listStr size="+listStr.size());
				}
				 //download("");
				if(listStr.size()>10){
					Toast.makeText(CategoryActivityTen.this, "最多可以选中10首歌曲", Toast.LENGTH_LONG).show();
				}else if(listStr.size()==0){
					Toast.makeText(CategoryActivityTen.this, "请选中你喜欢的歌曲", Toast.LENGTH_LONG).show();
				}else{
					if(mSharedPreferencesManager.readIntPreferences("ten")==1){
						download("");
				}else{
//					showNormalDialog();
				  	ExclusiveManagerInterface.exclusiveOnce(
							CategoryActivityTen.this.getParent(),
							"632555Z01000100001", "1234",
							new CMMusicCallback<OrderResult>() {
								@Override
								public void operationResult(OrderResult result) {
									System.out.println("OrderResult exclusiveOnce=" + result);
									if (result != null) {
										if (result.getResCode().equals("000000")) {
											System.out.println("OrderResult =" + result.getResMsg());
											mSharedPreferencesManager.writeIntPreferences("ten", 1);
											Toast.makeText(CategoryActivityTen.this.getParent(), "支付成功，可以选择任意歌曲下载", Toast.LENGTH_LONG).show();
											//download("");
										}else{
											Toast.makeText(CategoryActivityTen.this.getParent(), "支付失败", Toast.LENGTH_LONG).show();
										}
									}

									}
								});
					}
					
				}
				
//				 download("");
			}

			
		});
	}
	private void download(String string) {
		String path = Constant.SdcardPath.SONG_SAVEPATH;
		progressDialog = new ProgressDialog(CategoryActivityTen.this
				.getParent());
		final DownloadTask task = new DownloadTask(
				CategoryActivityTen.this.getParent());
		task.execute();
		progressDialog.setTitle("正在下载，请稍候");// 设置Title
		progressDialog.setMessage("Loading...");
		progressDialog
				.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setIndeterminate(false);
		progressDialog.setMax(100);
		progressDialog.setCancelable(true);
		progressDialog.show();

	}
	ProgressDialog progressDialog;

	private class DownloadTask extends AsyncTask<DownloadBean, Integer, String> {
		private Activity context;
		int taskCount;
		int map = 0;

		public DownloadTask(Activity context) {
			this.context = context;
		}

		@Override
		protected String doInBackground(DownloadBean... arg02) {
			taskCount = listStr.size();
			for (int i = 0; i < listStr.size(); i++) {
				System.out.println(" progress = doInBackground()  getSongName =" + listStr.get(i).getSongName());
				String url;
				try {
					url = DOWNLOADURL+"/"+URLEncoder.encode(listStr.get(i).getFile(), "utf-8");
				
				downloadImage(url, Constant.SdcardPath.SONG_SAVEPATH + "/"
						+ listStr.get(i).getFile());
				System.out.println(" progress = doInBackground()  dir =" + Constant.SdcardPath.SONG_SAVEPATH + "/"
						+ listStr.get(i).getFile());
			 
				DownloadFile file = new DownloadFile(++TianlApp.i + "",
						listStr.get(i).getGid(), url, listStr.get(i).getFile(),
						listStr.get(i).getSongName(), listStr.get(i).getSingerName());
				file.setCreateTime(System.currentTimeMillis()); //设置创建文件时间，用于文件的排序
				file.setFileType(DownloadType.DOWNLOAD_TYPE_MUSIC);
				file.setDownloadListener(TianlApp.newInstance());
				file.setSongName(listStr.get(i).getSongName());
				file.setFileName(listStr.get(i).getFile());
				file.setGid(listStr.get(i).getGid());
				file.setFileID(listStr.get(i).getFileID());
				DownloadService.newInstance().binder.startDownloadTask(file,
						true);
				System.out.println(" progress = doInBackground() getFileID=" + listStr.get(i).getFileID() +" getGid ="
						+listStr.get(i).getGid() +" getFile="+listStr.get(i).getFile());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				map++;
			}
			return map + "";
		}

		 

		@Override
		protected void onProgressUpdate(Integer... progress) {
			progressDialog.setProgress(progress[0]);
			progressDialog.setMessage("Loading " + map + "/" + taskCount);

		}

		@Override
		protected void onPostExecute(String rowItems) {
			System.out.println(" progress = onPostExecute()");
			Toast.makeText(context, "下载完成", Toast.LENGTH_LONG).show();
			progressDialog.dismiss();
		}

		/**
		 * ����
		 * 
		 * @param urlString
		 * @return
		 */
		private int downloadImage(String urlString, String path) {
			System.out.println(" progress = downloadImage() urlString ="
					+ urlString);
		 
			int count = 0;
			URL url;

			File f = new File(path);
			if (f.exists()) {
				f.delete();
			}
			InputStream in = null;
			OutputStream out = null;

			try {
				 
				url = new URL(urlString);
				URLConnection conn = url.openConnection();
				int lengthOfFile = conn.getContentLength();

				// in = new BufferedInputStream(url.openStream());
				// ByteArrayOutputStream dataStream = new
				// ByteArrayOutputStream();
				// out = new BufferedOutputStream(dataStream);
				in = conn.getInputStream();
				out = new FileOutputStream(path);
				byte[] data = new byte[1024];
				long total = 0L;
				while ((count = in.read(data)) != -1) {
					total += count;
					publishProgress((int) ((total * 100) / lengthOfFile));
					 out.write(data, 0, count);
				}
				out.flush();
				in.close();
				System.out.println(" progress = downloadImage() path =" + path);
				// BitmapFactory.Options options = new BitmapFactory.Options();
				// options.inSampleSize = 1;
				//
				// byte[] bytes = dataStream.toByteArray();
				// bitmap = BitmapFactory.decodeByteArray(bytes, 0,
				// bytes.length);
			} catch (Exception e) {
				System.out.println(" progress = downloadImage() exception =" + e.toString());
				e.printStackTrace();
				return 0;
			} finally {
				try {
					if (null != out)
						out.flush();
					if (null != in)
						in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			return 1;
		}

	}

	// 显示带有checkbox的listview
	static List<DownloadBean> data = null;

	public void showCheckBoxListView() throws JSONException {
		list = new ArrayList<HashMap<String, Object>>();
		if (data != null)
			data.clear();
		data = getmusics(getFromAssets("music10.txt", CategoryActivityTen.this));

		for (int i = 0; i < data.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			DownloadBean db = (DownloadBean) data.get(i);

			map.put("item_tv", db.getSongName());
			map.put("item_cb", false);
			map.put("musicArtist", db.getSingerName());
			list.add(map);
			adapter = new MyAdapter(
					this,
					list,
					R.layout.item_list,
					new String[] { "item_tv", "item_cb", "musicArtist" },
					new int[] { R.id.musicName, R.id.item_cb, R.id.musicArtist });
			lv.setAdapter(adapter);
			listStr = new ArrayList<DownloadBean>();
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long arg3) {
					ViewHolder holder = (ViewHolder) view.getTag();
					holder.cb.toggle();// 在每次获取点击的item时改变checkbox的状态
					MyAdapter.isSelected.put(position, holder.cb.isChecked()); // 同时修改map的值保存状态
					if (holder.cb.isChecked() == true) {
						listStr.add((DownloadBean) data.get(position));
					} else {
						listStr.remove((DownloadBean) data.get(position));
					}
					tv.setText("已选中" + listStr.size() + "首歌");

				}

			});
		}
	}

	private List<DownloadBean> getmusics(String fromAssets) {
		List<DownloadBean> ls = new ArrayList<DownloadBean>();
		try {
			JSONArray ja = new JSONArray(fromAssets);
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = (JSONObject) ja.get(i);
				Gson gson = new Gson();
				System.out.println("jo =" + jo.toString());
				DownloadBean db = gson.fromJson(jo.toString(),
						DownloadBean.class);
				ls.add(db);
			}

			return ls;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ls;
	}

	protected void dialog() {
		AlertDialog.Builder builder = new Builder(
				CategoryActivityTen.this.getParent());
		builder.setMessage("支付后下载选中歌曲");

		builder.setTitle("提示");

		builder.setPositiveButton("确认", new AlertDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ExclusiveManagerInterface.exclusiveOnce(
				// CategoryActivity.this.getParent(), "632555Z01000100002",
				// null, new CMMusicCallback<Result>() {
				// @Override
				// public void operationResult(Result result) {
				// if (null != result) {
				// new AlertDialog.Builder(
				// CategoryActivity.this.getParent())
				// .setTitle("exclusiveOnce")
				// .setMessage(
				// result.toString())
				// .setPositiveButton("确认",
				// null).show();
				// }
				//
				// Log.d("test =", "ret is " + result);
				// }
				// });
			
				dialog.dismiss();

			}
		});

		builder.setNegativeButton("取消", new AlertDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();
	}

	public static class MyAdapter extends BaseAdapter {
		public static HashMap<Integer, Boolean> isSelected;
		private Context context = null;
		private LayoutInflater inflater = null;
		private List<HashMap<String, Object>> list = null;
		private String keyString[] = null;
		private String itemString = null; // 记录每个item中textview的值
		private String itemart = null;
		private int idValue[] = null;// id值

		public MyAdapter(Context context, List<HashMap<String, Object>> list,
				int resource, String[] from, int[] to) {
			this.context = context;
			this.list = list;
			keyString = new String[from.length];
			idValue = new int[to.length];
			System.arraycopy(from, 0, keyString, 0, from.length);
			System.arraycopy(to, 0, idValue, 0, to.length);
			inflater = LayoutInflater.from(context);
			init();
		}

		// 初始化 设置所有checkbox都为未选择
		public void init() {
			isSelected = new HashMap<Integer, Boolean>();
			for (int i = 0; i < list.size(); i++) {
				isSelected.put(i, false);
			}
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int position, View view, ViewGroup arg2) {
			ViewHolder holder = null;
			if (holder == null) {
				holder = new ViewHolder();
				if (view == null) {
					view = inflater.inflate(R.layout.item_list, null);
				}
				holder.pid = (TextView) view.findViewById(R.id.pid);
				holder.tv = (TextView) view.findViewById(R.id.musicName);
				holder.cb = (CheckBox) view.findViewById(R.id.item_cb);
				holder.viewBtn = (Button) view.findViewById(R.id.item_bt);
				holder.musicArtist = (TextView) view
						.findViewById(R.id.musicArtist);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			HashMap<String, Object> map = list.get(position);
			if (map != null) {
				itemString = (String) map.get(keyString[0]);
				itemart = (String) map.get(keyString[2]);
				holder.tv.setText(itemString);
				holder.musicArtist.setText(itemart);
			}
			holder.pid.setText(position + 1 + "");
			holder.viewBtn.setTag(position);
			// 给Button添加单击事件 添加Button之后ListView将失去焦点 需要的直接把Button的焦点去掉
			holder.viewBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// showInfo(position);
//					Toast.makeText(context,
//							"position =" + data.get(position).getSongName(),
//							Toast.LENGTH_LONG).show();
					MusicInfo ms = new MusicInfo();
					ms.setNetUrl(DOWNLOADURL+"/"+data.get(position).getFile());
					System.out.println("DOWNLOADURL ="+DOWNLOADURL+"/"+data.get(position).getFile());
					ms.setTitle(data.get(position).getSongName());
					ms.setArtist(data.get(position).getSingerName());
					PlayLogicManager.newInstance().playNet(ms);
				}
			});
			holder.cb.setChecked(isSelected.get(position));
			return view;
		}

	}

	@Override
	public void onBackPressed() {
		back();
	}

	public void back() {
		/** 本地首页Action */

		Intent intent = new Intent(IntentAction.INTENT_ONLINE_ACTIVITY);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, "OnlineActivity");
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent,
				level - 1 == 0 ? 1 : level - 1);
	}

	public String getFromAssets(String fileName, Context ctx) {
		try {
			InputStreamReader inputReader = new InputStreamReader(ctx
					.getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			String Result = "";
			while ((line = bufReader.readLine()) != null)
				Result += line;

			return Result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}