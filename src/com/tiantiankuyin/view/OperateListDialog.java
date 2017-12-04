/**
 * 
 */
package com.tiantiankuyin.view;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tiantiankuyin.bean.SongListInfo;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.view.OperateDialog.OprateItem;
import com.tiantiankuyin.R;

/**
 * 歌曲条目操作弹出框
 * @author Erica
 * @note 实现   OperatDialog cr = new OperatDialog(this, R.style.easouDialog,String[],OnItemClickListener);
			  cr.setMusicInfo(musicInfo);
			  cr.show();
 */
public class OperateListDialog extends Dialog {

	/** 界面操作对象 */
	private Context context;
	/** dialog里面的内容 */
	private ListView listView;
	/** 操作队列显示列表 */
	private List<OprateItem> datas = null;
	/** 操作监听  */
	private OnItemClickListener listener;
	/** 屏幕高*/
	private int screen_Height;
	/** 标题*/
	private String title;
	
	public OperateListDialog(Context context, List<OprateItem> _data, OnItemClickListener _listener) {
		super(context);
		this.context = context;
		datas = _data;
		listener = _listener;
		setCanceledOnTouchOutside(true);
		screen_Height = Env.getScreenHeight();
	}

	public OperateListDialog(Context context, int theme, List<OprateItem> _data, OnItemClickListener _listener) {
		super(context, theme);
		this.context = context;
		datas = _data;
		listener = _listener;
		screen_Height = Env.getScreenHeight();
		setCanceledOnTouchOutside(true);
	}
	
	public void setDatas(List<OprateItem> _data){
		datas = _data;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.easou_dialog);
		((TextView)findViewById(R.id.titleTxt)).setText(title);
		listView = (ListView) findViewById(R.id.operate_lv);
		listView.setVisibility(View.VISIBLE);
		// 隐藏对话框正文内容
		findViewById(R.id.normal_container).setVisibility(View.GONE);
		findViewById(R.id.okContainer).setVisibility(View.GONE);
		findViewById(R.id.checklist_container).setVisibility(View.GONE);
		findViewById(R.id.okAndCancelContainer).setVisibility(View.GONE);
		findViewById(R.id.okAndMoreAndCancelContainer).setVisibility(View.GONE);
		
		OperateListAdapter operateAdapter = new OperateListAdapter(context,datas);
		
		listView.setAdapter(operateAdapter);	
		listView.setOnItemClickListener(listener);
		//重定义界面布局
		Window dialogWindow = getWindow();
		WindowManager m = ((Activity) context).getWindowManager();
		Display d = m.getDefaultDisplay();
		WindowManager.LayoutParams p = dialogWindow.getAttributes();
		
		p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.8
		dialogWindow.setAttributes(p);
		
	}
	
	public void setTag(SongListInfo songListInfo){
		listView.setTag(songListInfo);
	}
	/** 列表操作类对象适配器
	 * @author Erica
	 *  */
	public class OperateListAdapter extends BaseAdapter{

		private Context mContext;
		private List<OprateItem> datas;		
		public OperateListAdapter(Context context,List<OprateItem> datas)
		{
			mContext=context;
			this.datas = datas;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return datas.size();
		}

		@Override
		public OprateItem getItem(int position) {
			// TODO Auto-generated method stub
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ItemView itemView = null;
			if(convertView == null)
			{
				
				convertView = LayoutInflater.from(mContext).inflate(R.layout.oprate_listitem, null);
				convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,screen_Height/10));//重点行
				itemView = new ItemView();
				itemView.txtMenu = (TextView)convertView.findViewById(R.id.menuitem);
				itemView.imageMenu =  (ImageView)convertView.findViewById(R.id.menuitemimage);
				convertView.setTag(itemView);
			}
			else
			{
				itemView = (ItemView)convertView.getTag();
			}
			
			itemView.imageMenu.setBackgroundDrawable(datas.get(position).getShowImage());
			itemView.txtMenu.setText(datas.get(position).getShowText());
			
			
			return convertView;
		}
		
		private class ItemView 
		{
			private ImageView imageMenu;
			private TextView txtMenu;
		}
		
	}
}
