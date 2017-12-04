package com.tiantiankuyin.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.tiantiankuyin.bean.CategoryVO;
import com.tiantiankuyin.bean.RecordBean;
import com.tiantiankuyin.component.activity.online.CategorySubListActivity;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.view.MyGridView;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

public class CategoryExpandableListAdapter extends BaseExpandableListAdapter {
	private List<CategoryVO> categoryVOs;
	private Context context;

	public CategoryExpandableListAdapter(List<CategoryVO> categoryVOs,
			Context context) {
		this.context = context;
		this.categoryVOs = categoryVOs;
	}

	@Override
	public int getGroupCount() {
		return categoryVOs.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return categoryVOs.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return categoryVOs.get(groupPosition).getDataList().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TitleViewHolder holder;
		if (convertView == null) {
			holder = new TitleViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.online_similar_expandlistview_item_tilte_bg, null);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (TitleViewHolder) convertView.getTag();
		}
		holder.title.setText(categoryVOs.get(groupPosition).getDesc());
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ExpandViewHolder holder;
		if(convertView==null){
			holder=new ExpandViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.category_expandlistview_item, null);
			holder.gridView= (MyGridView) convertView.findViewById(R.id.gridView);
			convertView.setTag(holder);
		}else{
			holder=(ExpandViewHolder)convertView.getTag();
		}
		GridViewCategoryAdapter adapter=new GridViewCategoryAdapter(categoryVOs.get(groupPosition).getDataList(),context);
		holder.gridView.setAdapter(adapter);
		holder.gridView.setOnItemClickListener(itemclickListener);
		adapter.setGridView(holder.gridView);
		return convertView;
	}
	private AdapterView.OnItemClickListener itemclickListener=new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			TextView textName=(TextView)view.findViewById(R.id.category_name);//获取分类ID
			String nameTag=textName.getTag().toString();
			int typeId=Integer.parseInt(nameTag.split("_")[0]);
			String imageUrl=nameTag.split("_")[1];
			/*ImageView category_img = (ImageView) view
					.findViewById(R.id.category_img);*/
			/*String imgUrl=null;
			if(category_img!=null){
				imgUrl=category_img.getTag().toString();
			}*/
			recordCount(typeId,textName.getText().toString(),imageUrl);//记录次数
			showCategorySubListActivity(textName.getText().toString(),typeId);
		}
	};
	
	/**
	 * 记录分类被查看的次数 放入xml里面 按照 (id,name,imgurl,count)_(id,name,imgurl,count)_(id,name,imgurl,count)_(id,name,imgurl,count)格式
	 */
	private  void recordCount(int typeId,String name,String imgUrl){
		String oldStr=SPHelper.newInstance().getCategoryTimes();
		if(oldStr==null){//如果以前没记录
			String value=getDataFormatString(typeId,name,imgUrl,1);
			SPHelper.newInstance().setCategoryTimes(value);
		}else{
			List<RecordBean> beans=new ArrayList<RecordBean>();
			String[] oldValues=oldStr.split("_");
			for(String s:oldValues){
				String sDelte=s.substring(1, s.length()-1);
				String[] categoryStr=sDelte.split(",");
				RecordBean bean=new RecordBean(Integer.parseInt(categoryStr[0]),categoryStr[1],categoryStr[2],Integer.parseInt(categoryStr[3]));
				beans.add(bean);
			}
			boolean isSame=false;// 记录是否点击重复的type
			for(RecordBean bean:beans){
				if(bean.id==typeId){
					bean.count=bean.count+1;//点击同一个 数量+1;
					isSame=true;
					break;
				}
			}
			if(!isSame){//如果点击是新的 type 要加入一个新的对象了
				RecordBean bean=new RecordBean(typeId,name, imgUrl,1);
				beans.add(bean);
			}
			StringBuffer sb=new StringBuffer();
			// 保存数据到xml里面去
			for(RecordBean bean:beans){
				sb.append(getDataFormatString(bean.id,bean.name,bean.imgurl,bean.count)).append("_");
			}
			sb.delete(sb.length()-1, sb.length());//删除最后一个下划线
			SPHelper.newInstance().setCategoryTimes(sb.toString());
		}
	}
	/**
	 * 传入一串数据 返回 (id,name,imgurl,count) 这个数据
	 * @param typeId
	 * @param name
	 * @param imgUrl
	 * @return
	 */
	private String getDataFormatString(int typeId,String name,String imgUrl,int count){
		StringBuffer sb=new StringBuffer();
		sb.append("(").append(typeId).append(",").append(name).append(",").append(imgUrl).append(",").append(count).append(")");
		return sb.toString();
	}
	
	private void showCategorySubListActivity(String title,int typeId){
		Intent intent = new Intent(IntentAction.INTENT_ONLINE_CATEGORY_SUBLIST_ACTIVITY);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, "CategorySubListActivity");
		if (TianlApp.activityBundles != null) {
			Bundle bundle = new Bundle();
			bundle.putString(CategorySubListActivity.TITLE,title);
			bundle.putInt(CategorySubListActivity.TYPEID, typeId);
			bundle.putString(CategorySubListActivity.KEY_BACK_ACTION,
					IntentAction.INTENT_ONLINE_ACTIVITY);
			bundle.putString(CategorySubListActivity.KEY_ACTIVITY_NAME,
					"OnlineActivity");
			TianlApp.activityBundles.put(IntentAction.INTENT_ONLINE_CATEGORY_SUBLIST_ACTIVITY,
					bundle);
		}
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent, level + 1);
	}
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	class TitleViewHolder {
		TextView title;
	}
	class ExpandViewHolder {
		MyGridView gridView;
	}
}
