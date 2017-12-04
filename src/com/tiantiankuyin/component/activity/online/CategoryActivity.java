package com.tiantiankuyin.component.activity.online;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.CommonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

public class CategoryActivity extends Activity {
	public static final String ACTIVITY_ID = "CategoryActivity";
	TextView tv = null;
	ListView lv = null;
	Button btn_selectAll = null;
	Button btn_inverseSelect = null;
	Button btn_calcel = null;
	Button btn_download = null;
 
    JsonArray ja = new JsonArray();
	ArrayList<DownloadBean> listStr = null;
	private List<HashMap<String, Object>> list = null;
	private MyAdapter adapter;
    Button tv1,tv2,tv3;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainlist);
		tv1 = (Button) this.findViewById(R.id.tv1);
		tv2 = (Button) this.findViewById(R.id.tv2);
		tv3 = (Button) this.findViewById(R.id.tv3);
		tv1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
			 
	            Intent intent = new Intent(IntentAction.INTENT_MUSCI_LIST_CategoryActivityTrth);
	    		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, "CategoryActivityTrth");
	            int level = TianlApp.newInstance().getPageLevel();
	    		BaseActivity.newInstance().showActivity(intent, level+1);
			}
		});
		tv2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				    Intent intent = new Intent(IntentAction.INTENT_MUSCI_LIST_CategoryActivityTen);
		    		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, "CategoryActivityTen");
		            int level = TianlApp.newInstance().getPageLevel();
		    		BaseActivity.newInstance().showActivity(intent, level+1);
				
			}
		});
	   tv3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				    Intent intent = new Intent(IntentAction.INTENT_MUSCI_LIST_CategoryActivityTwenty);
		    		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, "CategoryActivityTwenty");
		            int level = TianlApp.newInstance().getPageLevel();
		    		BaseActivity.newInstance().showActivity(intent, level+1);
				
			}
		});
	}
	ProgressDialog progressDialog;
	private class DownloadTask extends AsyncTask<String, Integer, String> {
		private Activity context;
		int taskCount;
		 int map=0;
		public DownloadTask(Activity context) {
			this.context = context;
		}
		@Override
		protected String doInBackground(String... urls) {
			
			taskCount = urls.length;
            
			for (String url : urls) {
				 URL mUrl = null;
				try {
					mUrl = new URL(url);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				 String fileName = new File(mUrl.getFile()).getName();
				 downloadImage(url,Constant.SdcardPath.SONG_SAVEPATH +"/"+fileName);
				 
					DownloadFile file = new DownloadFile(++TianlApp.i + "","000212122112",url, CommonUtils.getFileNameByUrl(url),
							"����","�ű̳�");
					file.setCreateTime(System.currentTimeMillis()); // ���ô����ļ�ʱ�䣬�����ļ�������
					file.setFileType(DownloadType.DOWNLOAD_TYPE_MUSIC);
					file.setDownloadListener(TianlApp.newInstance());
					file.setSongName("����");
					file.setFileName("����" +CommonUtils.getSuffixByUrl(url));
					file.setGid("000212122112");
					file.setFileID("000212122112");
					DownloadService.newInstance().binder.startDownloadTask(file, true);
				 System.out.println(" progress = doInBackground()" + map);
				 map++;
			}
			
			return map+"";
		}
		@Override
		protected void onProgressUpdate(Integer... progress) {
			//System.out.println(" progress = onProgressUpdate"+progress[0]);
			progressDialog.setProgress(progress[0]);
			progressDialog.setMessage("Loading " + map + "/" + taskCount);

		}

		@Override
		protected void onPostExecute(String rowItems) {
			System.out.println(" progress = onPostExecute()" );
			//Toast.makeText(context, "�������", Toast.LENGTH_LONG).show();
			progressDialog.dismiss();
		}

		/**
		 * ���� 
		 * 
		 * @param urlString
		 * @return
		 */
		private int downloadImage(String urlString,String path) {
			System.out.println(" progress = downloadImage() urlString =" + urlString );
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

//				in = new BufferedInputStream(url.openStream());
//				ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
//				out = new BufferedOutputStream(dataStream);
				in = conn.getInputStream();
				out = new FileOutputStream(path);   
				byte[] data = new byte[1024];
				long total = 0L;
				while ((count = in.read(data)) != -1) {
					total += count;
					publishProgress((int) ((total * 100) / lengthOfFile));
					//out.write(data, 0, count);
				}
				out.flush();
				in.close();
				System.out.println(" progress = downloadImage() path =" +  path);
//				BitmapFactory.Options options = new BitmapFactory.Options();
//				options.inSampleSize = 1;
//
//				byte[] bytes = dataStream.toByteArray();
//				bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}finally{
				try {
					if(null!=out)
					out.flush();
					if(null!=in)
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}

			return 1;
		}

	}
	
	// ��ʾ����checkbox��listview

	static List<DownloadBean> data = null;
	public void showCheckBoxListView() throws JSONException {
		list = new ArrayList<HashMap<String, Object>>();
        if(data!=null)
        	data.clear();
		data = getmusics(getFromAssets("music.txt", CategoryActivity.this));
	 
		for (int i = 0; i < data.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			DownloadBean db= (DownloadBean) data.get(i);
			 
			map.put("item_tv", db.getSongName());
			map.put("item_cb", false);
			map.put("musicArtist", db.getSingerName());
			list.add(map);
			adapter = new MyAdapter(this, list, R.layout.item_list,
					new String[] { "item_tv", "item_cb","musicArtist" }, new int[] {
							R.id.musicName, R.id.item_cb ,R.id.musicArtist});
			lv.setAdapter(adapter);
			listStr = new ArrayList<DownloadBean>();
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long arg3) {
					ViewHolder holder = (ViewHolder) view.getTag();
					holder.cb.toggle();// ��ÿ�λ�ȡ�����itemʱ�ı�checkbox��״̬
					MyAdapter.isSelected.put(position, holder.cb.isChecked()); // ͬʱ�޸�map��ֵ����״̬
					if (holder.cb.isChecked() == true) {
						listStr.add((DownloadBean)data.get(position));
					} else {
						listStr.remove((DownloadBean)data.get(position));
					}
					tv.setText("��ѡ��" + listStr.size() + "��");

				}

			});
		}
	}

	private List<DownloadBean> getmusics(String fromAssets) {
		 List<DownloadBean> ls=new ArrayList<DownloadBean>();
		 try {
			JSONArray ja= new JSONArray(fromAssets);
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo=(JSONObject) ja.get(i);
				Gson gson = new Gson(); 
				System.out.println("jo ="+jo.toString());
				DownloadBean db = gson.fromJson(jo.toString(), DownloadBean.class); 
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
				CategoryActivity.this.getParent());
		builder.setMessage("ȷ���˳���");

		builder.setTitle("��ʾ");

		builder.setPositiveButton("ȷ��", new AlertDialog.OnClickListener() {

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
				// .setPositiveButton("ȷ��",
				// null).show();
				// }
				//
				// Log.d("test =", "ret is " + result);
				// }
				// });
				ExclusiveManagerInterface.exclusiveOnce(
						CategoryActivity.this.getParent(),
						"632555Z01000100002", "test",
						new CMMusicCallback<OrderResult>() {
							@Override
							public void operationResult(OrderResult result) {
								if (result.getResCode().equals("00000")) {

								}

							}
						});
				dialog.dismiss();

			}
		});

		builder.setNegativeButton("ȡ��", new AlertDialog.OnClickListener() {

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
		private String itemString = null; // ��¼ÿ��item��textview��ֵ
		private String itemart = null; 
		private int idValue[] = null;// idֵ

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

		// ��ʼ�� ��������checkbox��Ϊδѡ��
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
				holder.musicArtist =(TextView) view.findViewById(R.id.musicArtist);
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
			// ��Button��ӵ����¼� ���Button֮��ListView��ʧȥ���� ��Ҫ��ֱ�Ӱ�Button�Ľ���ȥ��
			holder.viewBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// showInfo(position);
					Toast.makeText(context, "position =" + data.get(position).getSongName(),
							Toast.LENGTH_LONG).show();
					MusicInfo ms = new MusicInfo();
					ms.setNetUrl("http://mp3.13400.com:99/20150628/49.mp3");
					ms.setTitle(data.get(position).getSongName());
					ms.setArtist(data.get(position).getSingerName());
					PlayLogicManager.newInstance().playNet(ms);
				}
			});
			holder.cb.setChecked(isSelected.get(position));
			return view;
		}

	}
    public String getFromAssets(String fileName,Context ctx){ 
        try { 
            InputStreamReader inputReader = new InputStreamReader(ctx.getResources().getAssets().open(fileName)); 
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null)
                Result += line;
            
            return Result;
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return null;
} 

}