/**
 * 
 */
package com.tiantiankuyin.view;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tiantiankuyin.component.activity.WebViewActivity;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 分享操作弹出列表框
 * 
 * @author Erica
 */
public class EasouShareDialog extends Dialog {

	/** 界面操作对象 */
	private Context context;
	/** dialog里面的内容 */
	private ListView listView;
	/** 分享操作适配器 */
	AppAdapter adapter = null;
	/** 分享字符串 */
	private String content;
	private String old_content ;
	/** 分享流文件 */
	private String localUrl;
	/** 无新浪执行分享 */
	private boolean isSinaWebShare;
	/** 无腾讯执行分享 */
	private boolean isTencentWebShare;
	List<ResolveInfo> shareList;
	private boolean isNetData;

	public EasouShareDialog(Context context) {
		super(context);
		this.context = context;
		getAppList();
	}

	/**
	 * 对话框构造函数
	 * 
	 * @author Erica
	 * @param context
	 *            Context 界面操作对象
	 * @param theme
	 *            int 界面style
	 * @param _content
	 *            String 分享字符串
	 * */
	public EasouShareDialog(Context context, int theme, String _content,String localUrl) {
		super(context, theme);
		this.context = context;
		content = _content;
		old_content = _content;
		if(content!=null&&content.length()>0){
			content = context.getResources().getString(R.string.dialog_share_content)+content+context.getResources().getString(R.string.dialog_share_content_end)+" http://t.cn/zlvcRX7";
		}else{
			content = context.getResources().getString(R.string.dialog_share_content_null)+" http://t.cn/zlvcRX7";
		}
		if(localUrl!=null){
			this.localUrl = localUrl;
		}
		getAppList();
		setCanceledOnTouchOutside(true);
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.easou_dialog);
		listView = (ListView) findViewById(R.id.operate_lv);
		listView.setVisibility(View.VISIBLE);
		// 隐藏对话框正文内容
		findViewById(R.id.normal_container).setVisibility(View.GONE);
		findViewById(R.id.okContainer).setVisibility(View.GONE);
		findViewById(R.id.checklist_container).setVisibility(View.GONE);
		findViewById(R.id.okAndCancelContainer).setVisibility(View.GONE);
		findViewById(R.id.okAndMoreAndCancelContainer).setVisibility(View.GONE);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 执行分享intent
				dismiss();

				if(isSinaWebShare && position==0){
					String q=old_content;
					String url="http://t.sina.cn/dpool/ttt/extShare.php?";
					String rt = "";
					String ru = "";
					try {
						rt=URLEncoder.encode("宜搜MP3搜索_"+q,"UTF-8");
						ru=URLEncoder.encode("http://t.cn/zlvcRX7","UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					String request_url = url+"rt="+rt+"&ru="+ru+"&wm="+4016;
					Intent adIntent = new Intent(
							IntentAction.INTENT_ONLINE_WEBVIEW_ACTIVITY);
					adIntent.putExtra(WebViewActivity.URL, request_url);
					context.startActivity(adIntent);
					return;
				}

				if(isTencentWebShare && position == 1){
					String q=content;
					String url="http://ti.3g.qq.com/open/s?aid=share&sbid=easou&";
					String msg = "";
					String return_str= "";
					try {
							msg = URLEncoder.encode(q,"UTF-8");
//							return_str = URLEncoder.encode("mp3.easou.com","UTF-8");
							return_str = "http%3A%2F%2Fmp3.easou.com%2Fs.e%3FactType%3D1%26q%3D%25E5%25B0%258F%25E6%2583%2585%25E6%25AD%258C%2B%25E8%258B%258F%25E6%2589%2593%25E7%25BB%25BF%26esid%3DnhDOh5xa4FY389Lxa7%26l%3D216.3%26wver%3Dc";
						} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					String request_url = url+"msg="+msg+"&return="+return_str;
//					System.out.println(request_url);
					Intent adIntent = new Intent(
							IntentAction.INTENT_ONLINE_WEBVIEW_ACTIVITY);
					adIntent.putExtra(WebViewActivity.URL,request_url);
					context.startActivity(adIntent);
					return;
					
				}
			
				ResolveInfo launchable = adapter.getItem(position);
				ActivityInfo activity = launchable.activityInfo;
				ComponentName name = new ComponentName(
						activity.applicationInfo.packageName, activity.name);
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setComponent(name);
				i.setType("*/*");			
				if(activity.applicationInfo.packageName.contains("tencent.WBlog")){
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				}else{					
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					i.addCategory(Intent.CATEGORY_LAUNCHER);
				}
				
				try{
					if(activity.applicationInfo.packageName.contains("android.bluetooth")||activity.applicationInfo.name.contains("OppLauncherActivity"))
					{
						if(localUrl!=null){
							File f = new File(localUrl);
							i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
						}else{
							return;
						}
						 
					}
				}catch(Exception e){
					
				}
				i.putExtra(Intent.EXTRA_SUBJECT,TianlApp.newInstance().getResources().getString(R.string.welcome));  
				i.putExtra(Intent.EXTRA_TEXT, content);

				context.startActivity(i);
			}
		});
		
		//设置标题
        ((TextView)findViewById(R.id.titleTxt)).setText(R.string.dialog_share);
        
		// 重新设置界面布局
		Window dialogWindow = getWindow();
		WindowManager m = ((Activity) context).getWindowManager();
		Display d = m.getDefaultDisplay();
		WindowManager.LayoutParams p = dialogWindow.getAttributes();
		p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.8
		if(shareList.size()>5){
			p.height = (int)(d.getHeight()*0.5);
		}
		dialogWindow.setAttributes(p);
	}

	/**
	 * 分享操作弹出框列表适配类
	 * 
	 * @author Erica
	 * */
	public class AppAdapter extends ArrayAdapter<ResolveInfo> {
		private PackageManager pm = null;

		AppAdapter(PackageManager pm, List<ResolveInfo> apps) {
			super(context, R.layout.musiclist_item, apps);
			this.pm = pm;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = newView(parent);
			}

			bindView(position, convertView);

			return (convertView);
		}

		private View newView(ViewGroup parent) {
			return (LayoutInflater.from(context).inflate(
					R.layout.music_share_item, parent, false));
		}

		private void bindView(int position, View row) {
			TextView label = (TextView) row.findViewById(R.id.label);
			ImageView icon = (ImageView) row.findViewById(R.id.icon);

			
			try{
				if(getItem(position).loadLabel(pm)!=null){
					label.setText(getItem(position).loadLabel(pm));
				}
				
				if(getItem(position).loadIcon(pm)!=null){
					icon.setImageDrawable(getItem(position).loadIcon(pm));
				}
			}catch(Exception e){
				label.setText(TianlApp.newInstance().getResources().getString(getItem(position).labelRes));
				icon.setImageDrawable(TianlApp.newInstance().getResources().getDrawable(getItem(position).icon));
				
			}
		}
	}
	
	public boolean isNetData() {
		return isNetData;
	}

	public void setNetData(boolean isNetData) {
		this.isNetData = isNetData;
	}

	/**
	 * 获取过滤后的应用分享列表
	 * 
	 * @author Erica
	 * */
	public void getAppList() {
		PackageManager pm = context.getPackageManager();
		boolean isHavaSina = false;
		int sina = 0;
		boolean isHaveTencent = false;
		int tencent = 0;
//		boolean isHaveRenRen = false;
//		int renren = 0;
//		boolean isHaveQzone = false;
//		int qzone = 0;
//		boolean isHaveDiandian = false;
//		int diandian = 0;
		boolean isHaveWeixin = false;
		int weixin = 0;
		boolean isBlueTooth = false;
		int bluetooth = 0;
		List<ResolveInfo> launchables = getShareTargets();
		int totalsize = launchables.size();
		Collections
				.sort(launchables, new ResolveInfo.DisplayNameComparator(pm));
		if (totalsize > 0) {
			for (int i = 0; i < launchables.size(); i++) {
//				System.out.println(launchables.get(i).activityInfo.name);
//				com.android.bluetooth.opp.BluetoothOppLauncherActivity
				

				if (launchables.get(i).activityInfo.name.contains("sina.")&&launchables.get(i).activityInfo.name.contains("weibo")) {
					isHavaSina = true;
					sina = i;
					continue;
				}
				if (launchables.get(i).activityInfo.name
						.contains("tencent.WBlog")) {
					isHaveTencent = true;
					tencent = i;
					continue;
				}

//				if (launchables.get(i).activityInfo.name.contains("renren")) {
//					isHaveRenRen = true;
//					renren = i;
//					continue;
//				}
//				if (launchables.get(i).activityInfo.name.contains("qzone")) {
//					isHaveQzone = true;
//					qzone = i;
//					continue;
//				}
//				if (launchables.get(i).activityInfo.name.contains("diandian")) {
//					isHaveDiandian = true;
//					diandian = i;
//					continue;
//				}
				if (launchables.get(i).activityInfo.name.contains("tencent.mm")) {
					isHaveWeixin = true;
					weixin = i;
					continue;
				}
				if (launchables.get(i).activityInfo.name.contains("android.bluetooth")||launchables.get(i).activityInfo.name.contains("OppLauncherActivity")) {
					isBlueTooth = true;
					bluetooth = i;
					continue;
				}

			}
		}
		List<ResolveInfo> tempList = new ArrayList<ResolveInfo>();
		for (ResolveInfo info : launchables) {
			tempList.add(info);
		}

		shareList = new ArrayList<ResolveInfo>();
		if (isHavaSina) {
			shareList.add(tempList.get(sina));
			launchables.remove(tempList.get(sina));
		}else{
			isSinaWebShare = true;
			ResolveInfo sina_rol = new ResolveInfo();
			sina_rol.icon = R.drawable.sina_icon;
			sina_rol.labelRes = R.string.dialog_share_title_sina;
			shareList.add(sina_rol);
			launchables.remove(tempList.get(sina));
		}
		if (isHaveTencent) {
			shareList.add(tempList.get(tencent));
			launchables.remove(tempList.get(tencent));
		}else{
			isTencentWebShare = true;
			ResolveInfo tencent_rol = new ResolveInfo();
			tencent_rol.icon = R.drawable.tencent_icon;
			tencent_rol.labelRes = R.string.dialog_share_title_tencent;
			shareList.add(tencent_rol);
			launchables.remove(tempList.get(tencent));
		}
		if (isHaveWeixin) {
			shareList.add(tempList.get(weixin));
			launchables.remove(tempList.get(weixin));
		}
		if (isBlueTooth&&localUrl!=null) {
			shareList.add(tempList.get(bluetooth));
			launchables.remove(tempList.get(bluetooth));
		}
		
//		if (isHaveRenRen) {
//			shareList.add(tempList.get(renren));
//			launchables.remove(tempList.get(renren));
//		}
//		if (isHaveQzone) {
//			shareList.add(tempList.get(qzone));
//			launchables.remove(tempList.get(qzone));
//		}
//		if (isHaveDiandian) {
//			shareList.add(tempList.get(diandian));
//			launchables.remove(tempList.get(diandian));
//		}

//		for (ResolveInfo info : launchables) {
//			shareList.add(info);
//		}
		/** 设置适配器 */
		adapter = new AppAdapter(pm, shareList);
	}

	/**
	 * 获取分享列表
	 * 
	 * @return List<ResolveInfo>
	 * */
	public List<ResolveInfo> getShareTargets() {
		List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();
		Intent intent = new Intent(Intent.ACTION_SEND, null);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		PackageManager pm = context.getPackageManager();
		mApps = pm.queryIntentActivities(intent,
				PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

		return mApps;
	}
}
