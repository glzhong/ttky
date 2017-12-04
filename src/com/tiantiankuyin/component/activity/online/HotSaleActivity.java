package com.tiantiankuyin.component.activity.online;
//package com.haige.demo.component.activity.online;
//
//import java.io.IOException;
//import java.util.List;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.haige.demo.Easou;
//import com.haige.demo.R;
//import com.haige.demo.adapter.HotSaleAdapter;
//import com.haige.demo.bean.HotSaleItem;
//import com.haige.demo.bean.HotSaleVO;
//import com.haige.demo.component.activity.BaseActivity;
//import com.haige.demo.database.bll.OnDataPreparedListener;
//import com.haige.demo.database.bll.OnlineMusicManager;
//import com.haige.demo.net.NetCache;
//import com.haige.demo.para.IntentAction;
//import com.haige.demo.utils.CommonUtils;
//import com.haige.demo.utils.Lg;
//import com.haige.demo.view.AnimImageView;
//
///**
// * 在线模块-榜单
// * @author andrew
// */
//public class HotSaleActivity extends Activity {
//
//	public static final String ACTIVITY_ID = "HotSaleActivity";
//	private ListView hotsaleList;
//	private HotSaleAdapter adapter;
//	/** 显示动画的组件 */
//	private AnimImageView imgDance;
//	private HotSaleVO before;
//	/** 界面精选集标签按钮 */
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.online_hotsale);
//		if (Easou.newInstance().activityList != null
//				&& !Easou.newInstance().activityList.contains(this)) {
//			Easou.newInstance().activityList.add(this); // 将该Activity实例加入到Activity管理集合中
//		}
//		init();
//		initData();
//	}
//
//	private boolean compatorHotSale(HotSaleVO befor, HotSaleVO after) {
//		if (befor == null || after == null) {
//			return false;
//		}
//		if (befor.getCount() != after.getCount()) {
//			return false;
//		}
//		int count = 0;
//		for (HotSaleItem hotSaleItem : befor.getAdList()) {
//			for (HotSaleItem item : after.getAdList()) {
//				if (hotSaleItem.getId() == item.getId()) {
//					count++;
//					break;
//				}
//			}
//		}
//		if (count == befor.getCount()) {
//			// 表示相等
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	private void initData() {
//		before = null;
//		final String url = CommonUtils.getHotSaleRequestUrl();
//		try {
//			before = NetCache.readCache(url);
//			showHotSaleView(before);
//		} catch (ClassCastException e1) {
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		} catch (ClassNotFoundException e1) {
//			e1.printStackTrace();
//		}
//		OnlineMusicManager.getInstence().getHotSale(this,
//				new OnDataPreparedListener<HotSaleVO>() {
//					@Override
//					public void onDataPrepared(HotSaleVO data) {
//						if (data != null && data.getCount() > 0) {
//							// 判断两个对象是否相等 ,相等就不刷新了
//							boolean flag = compatorHotSale(before, data);
//							if (flag) {// 跟之前的数据相等
//								return;
//							}
//							showHotSaleView(data);
//							try {
//								NetCache.saveCache(data, url);// 保存缓存
//							} catch (IOException e) {
//								// e.printStackTrace();
//							}
//						} else {
//							Lg.d("getHotSale() == null");
//							hasResultFresh();
//							return;
//						}
//					}
//				}, url);
//		
//	}
//
//	private void init() {
//		hotsaleList = (ListView) findViewById(R.id.hotsaleList);
//		hotsaleList.setVisibility(View.GONE);
//		imgDance = (AnimImageView) super.findViewById(R.id.ImgDance);
//	}
//
//	/**
//	 * 有结果刷新
//	 */
//	private void hasResultFresh() {
//		hotsaleList.setVisibility(View.VISIBLE);
//		if (imgDance.getVisibility() == View.VISIBLE) {
//			imgDance.setVisibility(View.GONE);
//			imgDance.stop();
//		}
//	}
//
//	private void showHotSaleView(HotSaleVO hotSaleVO) {
//		hasResultFresh();
//		List<HotSaleItem> listHostSaleVO=hotSaleVO.getAdList();
//		listHostSaleVO.remove(0);
//		listHostSaleVO.remove(0);
//		adapter = new HotSaleAdapter(listHostSaleVO, this);
//		hotsaleList.setAdapter(adapter);
//		hotsaleList.setOnItemClickListener(itemClickListener);
//		adapter.setListView(hotsaleList);
//	}
//
//	private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position,
//				long id) {
//			TextView textView = (TextView) view.findViewById(R.id.title);
//			showHotSaleSubView(textView.getText().toString(),
//					Integer.parseInt(textView.getTag().toString()));
//		}
//	};
//
//	private void showHotSaleSubView(String title, int typeId) {
//		Intent intent = new Intent(
//				IntentAction.INTENT_ONLINE_HOTSALE_SUBLIST_ACTIVITY);
//		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME,
//				"HotSaleSubListActivity");
//		if (Easou.activityBundles != null) {
//			Bundle bundle = new Bundle();
//			bundle.putString(HotSaleSubListActivity.TITLE, title);
//			bundle.putInt(HotSaleSubListActivity.TYPEID, typeId);
//			bundle.putString(HotSaleSubListActivity.KEY_BACK_ACTION,
//					IntentAction.INTENT_ONLINE_ACTIVITY);
//			bundle.putString(HotSaleSubListActivity.KEY_ACTIVITY_NAME,
//					"OnlineActivity");
//			bundle.putBoolean(OmnibusDetailActivity.KEY_BACK_BTN, true);
//			Easou.activityBundles
//					.put(IntentAction.INTENT_ONLINE_HOTSALE_SUBLIST_ACTIVITY,
//							bundle);
//		}
//		int level = Easou.newInstance().getPageLevel();
//		BaseActivity.newInstance().showActivity(intent, level + 1);
//	}
//	
//	public void onResume() {
//	    super.onResume();
//	}
//
//	public void onPause() {
//	    super.onPause();
//	}
//}
