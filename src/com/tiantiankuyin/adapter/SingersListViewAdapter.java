package com.tiantiankuyin.adapter;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.tiantiankuyin.component.activity.MusicListActivity;
import com.tiantiankuyin.component.activity.local.SingersActivity.SingerSectionListItem;
import com.tiantiankuyin.database.bll.SqlString;
import com.tiantiankuyin.net.EasouAsyncImageLoader.ILoadedImage;
import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.EasouSectionIndexer;
import com.tiantiankuyin.view.EasouPinneListView;
import com.tiantiankuyin.view.EasouPinneListView.PinnedHeaderAdapter;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

public class SingersListViewAdapter implements ListAdapter,
		OnItemClickListener, PinnedHeaderAdapter, SectionIndexer,
		OnScrollListener, OnTouchListener, OnClickListener {

	private SectionIndexer mIndexer;
	/** 所有分组的名字 */
	private String[] mSections;
	/** 所有分组的个数 */
	private int[] mCounts;
	private int mSectionCounts = 0;
	private EasouPinneListView mListView;
	private final DataSetObserver dataSetObserver;

	private final StandardArrayAdapter linkedAdapter;
	private final Map<String, View> currentViewSections = new HashMap<String, View>();
	private int viewTypeCount;
	protected final LayoutInflater inflater;

	private View transparentSectionView;

	private OnItemClickListener linkedListener;
	private Context context;

	public SingersListViewAdapter(Context context, EasouPinneListView listView,
			final LayoutInflater inflater,
			final StandardArrayAdapter linkedAdapter) {
		this.linkedAdapter = linkedAdapter;
		this.context = context;
		this.inflater = inflater;
		this.mListView = listView;
		dataSetObserver = new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				updateTotalCount();
			}

			@Override
			public void onInvalidated() {
				super.onInvalidated();
				updateTotalCount();
			};
		};
		linkedAdapter.registerDataSetObserver(dataSetObserver);

		updateTotalCount();
		mIndexer = new EasouSectionIndexer(mSections, mCounts);
	}

	private boolean isTheSame(final String previousSection,
			final String newSection) {
		if (previousSection == null) {
			return newSection == null;
		} else {
			return previousSection.equals(newSection);
		}
	}

	private void fillSections() {
		mSections = new String[mSectionCounts];
		mCounts = new int[mSectionCounts];
		final int count = linkedAdapter.getCount();
		String currentSection = null;
		int newSectionIndex = 0;
		int newSectionCounts = 0;
		String previousSection = null;
		for (int i = 0; i < count; i++) {
			newSectionCounts++;
			currentSection = linkedAdapter.items[i].section;
			if (!isTheSame(previousSection, currentSection)) {
				mSections[newSectionIndex] = currentSection;
				previousSection = currentSection;
				if (newSectionIndex == 1) {// 如果是首次开始，则减1(因为第一次进入循环时，前一个为空，相当于indexCount多加了一次)
					mCounts[0] = newSectionCounts - 1;
				} else if (newSectionIndex != 0) {
					mCounts[newSectionIndex - 1] = newSectionCounts;
				}
				if (i != 0) {// 首次进入，计数不置0，其他情况，重新计数
					newSectionCounts = 0;
				}
				newSectionIndex++;
			} else if (i == count - 1) {// 如果是最后一个,因为进入的时候把newSectionCounts置为0，下次不会计数，少加了一次
				mCounts[newSectionIndex - 1] = newSectionCounts + 1;
			}

		}
	}

	private synchronized void updateTotalCount() {
		String currentSection = null;
		viewTypeCount = linkedAdapter.getViewTypeCount() + 1;
		final int count = linkedAdapter.getCount();
		for (int i = 0; i < count; i++) {
			final SingerSectionListItem item = (SingerSectionListItem) linkedAdapter
					.getItem(i);
			if (!isTheSame(currentSection, item.section)) {
				mSectionCounts++;
				currentSection = item.section;
			}
		}
		fillSections();
	}

	@Override
	public synchronized int getCount() {
		return linkedAdapter.getCount();
	}

	@Override
	public synchronized Object getItem(final int position) {
		final int linkedItemPosition = getLinkedPosition(position);
		return linkedAdapter.getItem(linkedItemPosition);
	}

	public synchronized String getSectionName(final int position) {
		return null;
	}

	@Override
	public long getItemId(final int position) {
		return linkedAdapter.getItemId(getLinkedPosition(position));
	}

	protected Integer getLinkedPosition(final int position) {
		return position;
	}

	@Override
	public int getItemViewType(final int position) {
		return linkedAdapter.getItemViewType(getLinkedPosition(position));
	}

	protected void setSectionText(final String section, final View sectionView) {
		final TextView textView = (TextView) sectionView
				.findViewById(R.id.header);
		textView.setText(section);
	}

	protected synchronized void replaceSectionViewsInMaps(final String section,
			final View theView) {
		if (currentViewSections.containsKey(theView)) {
			currentViewSections.remove(theView);
		}
		currentViewSections.put(section, theView);
	}

	@Override
	public View getView(final int position, View convertView,
			final ViewGroup parent) {

		SingersHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) TianlApp.newInstance()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(
					R.layout.local_singers_listview_item, null);
			holder = new SingersHolder();
			holder.singerImage = (ImageView) convertView
					.findViewById(R.id.singerImage);
			holder.singerName = (TextView) convertView
					.findViewById(R.id.singerName);
			holder.songCount = (TextView) convertView
					.findViewById(R.id.songCount);
			holder.header = (TextView) convertView.findViewById(R.id.header);
			holder.headerParent = (RelativeLayout) convertView
					.findViewById(R.id.header_parent);
			holder.play_status=(ImageView) convertView.findViewById(R.id.play_status);
			convertView.setTag(holder);
		} else
			holder = (SingersHolder) convertView.getTag();
		final SingerSectionListItem currentItem = linkedAdapter.items[position];
		if (currentItem != null) {
			holder.songCount.setText(currentItem.artistVO.getNumOfSong()
					+ TianlApp.newInstance().getString(R.string.singer_song));
			holder.singerImage
					.setImageResource(R.drawable.list_item_artist_defalut_img);
			holder.singerImage.setTag(CommonUtils.MD5(currentItem.artistVO
					.getArtistName()));
			holder.singerName.setText(currentItem.artistVO.getArtistName());
			holder.header.setText(currentItem.section);
			int section = getSectionForPosition(position);
			if (getPositionForSection(section) == position) {
				holder.headerParent.setVisibility(View.VISIBLE);
				holder.header.setVisibility(View.VISIBLE);
			} else {
				holder.headerParent.setVisibility(View.GONE);
				holder.header.setVisibility(View.GONE);
			}
		}
		Drawable localImage = getLocalImage(currentItem.artistVO
				.getArtistName());
		if (localImage != null) {
			holder.singerImage.setImageDrawable(localImage);
			/** 动画淡隐效果 */
			Animation animation = AnimationUtils.loadAnimation(
					context, R.anim.push_in);
			if (animation != null) {
				holder.singerImage.startAnimation(animation);
			}
		} else {
			ILoadedImage iLoadedImage = new ILoadedImage() {

				@Override
				public void onFinishLoadedLRC(String lrcPath, String songName) {
				}

				@Override
				public void onFinishLoaded(SoftReference<Drawable> drawable,String saveName) {
					ImageView imageview = (ImageView) mListView
							.findViewWithTag(CommonUtils
									.MD5(currentItem.artistVO.getArtistName()));
					if (drawable != null && imageview != null) {
						imageview.setImageDrawable(drawable.get());
						/** 动画淡隐效果 */
						Animation animation = AnimationUtils.loadAnimation(
								context, R.anim.push_in);
						if (animation != null) {
							imageview.startAnimation(animation);
						}
					}
				}

				@Override
				public void onError(Exception e) {
				}
			};
			CommonUtils.getSingerImageDrawable(
					currentItem.artistVO.getArtistName(), iLoadedImage);
		}
		convertView.setOnTouchListener(this);
		convertView.setOnClickListener(this);
		//設置播放狀態
		String newCurrentPlayPath=TianlApp.currentPlayPath+"/"+currentItem.artistVO.getArtistName();
		if(newCurrentPlayPath!=null&&newCurrentPlayPath.equals(SPHelper.newInstance().getFoldPath())){//判断当前是否在播放这个歌曲
			holder.play_status.setVisibility(View.VISIBLE);
			/*if(PlayLogicManage.newInstance().getIsPlaying()){
				holder.play_status.setImageResource(R.drawable.list_item_play_img);
			}else{
				holder.play_status.setImageResource(R.drawable.list_item_pause_img);
			}*/
			holder.play_status.setImageResource(R.drawable.list_item_play_img);
		}else{
			holder.play_status.setVisibility(View.GONE);
		}
		return convertView;
	}

	/**
	 * 获取本地图片
	 * 
	 * @param saveName
	 * @return
	 */
	private Drawable getLocalImage(String saveName) {
		String filePath = Constant.SdcardPath.IMAGE_SAVEPATH + "/"
				+ CommonUtils.MD5(saveName);
		File imageFile = new File(filePath);
		if (imageFile.exists()) {
			return Drawable.createFromPath(filePath);
		}
		return null;
	}

	public class SingersHolder {
		private ImageView singerImage; // 歌手图片
		public TextView singerName; // 歌手名
		private TextView songCount; // 歌手的歌曲数
		private TextView header; // 头
		private RelativeLayout headerParent; // 头
		private ImageView play_status; // 播放狀態標誌
	}

	@Override
	public int getViewTypeCount() {
		return viewTypeCount;
	}

	@Override
	public boolean hasStableIds() {
		return linkedAdapter.hasStableIds();
	}

	@Override
	public boolean isEmpty() {
		return linkedAdapter.isEmpty();
	}

	@Override
	public void registerDataSetObserver(final DataSetObserver observer) {
		linkedAdapter.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(final DataSetObserver observer) {
		linkedAdapter.unregisterDataSetObserver(observer);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return linkedAdapter.areAllItemsEnabled();
	}

	@Override
	public boolean isEnabled(final int position) {
		return linkedAdapter.isEnabled(getLinkedPosition(position));
	}

	public int getRealPosition(int pos) {
		return pos - 1;
	}

	public synchronized View getTransparentSectionView() {
		if (transparentSectionView == null) {
		}
		return transparentSectionView;
	}

	protected void sectionClicked(final String section) {
	}

	@Override
	public void onItemClick(final AdapterView<?> parent, final View view,
			final int position, final long id) {
		if (linkedListener != null) {
			linkedListener.onItemClick(parent, view,
					getLinkedPosition(position), id);
		}

	}

	public void setOnItemClickListener(final OnItemClickListener linkedListener) {
		this.linkedListener = linkedListener;
	}

	@Override
	public int getPinnedHeaderState(int position) {
		int realPosition = position;
		if (mIndexer == null) {
			return PINNED_HEADER_GONE;
		}
		if (realPosition < 0) {
			return PINNED_HEADER_GONE;
		}
		int section = getSectionForPosition(realPosition);// 得到此item所在的分组位置
		int nextSectionPosition = getPositionForSection(section + 1);// 得到下一个分组的位置
		if (nextSectionPosition != -1
				&& realPosition == nextSectionPosition - 1) {
			return PINNED_HEADER_PUSHED_UP;
		}
		return PINNED_HEADER_VISIBLE;
	}

	@Override
	public void configurePinnedHeader(View header, int position, int alpha) {

		int realPosition = position;
		int section = getSectionForPosition(realPosition);

		String title = (String) mIndexer.getSections()[section];
		((TextView) header.findViewById(R.id.header_text)).setText(title);

	}

	@Override
	public Object[] getSections() {
		if (mIndexer == null) {
			return new String[] { "" };
		} else {
			return mIndexer.getSections();
		}
	}

	@Override
	public int getPositionForSection(int section) {
		if (mIndexer == null) {
			return -1;
		}
		return mIndexer.getPositionForSection(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		if (mIndexer == null) {
			return -1;
		}
		return mIndexer.getSectionForPosition(position);
	}

	@Override
	public void onScrollStateChanged(final AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (view instanceof EasouPinneListView) {
			((EasouPinneListView) view).configureHeaderView(firstVisibleItem);
		}
	}

	public static class StandardArrayAdapter extends
			ArrayAdapter<SingerSectionListItem> {

		public SingerSectionListItem[] items;

		public StandardArrayAdapter(Context context, int textViewResourceId,
				SingerSectionListItem[] items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			v.setBackgroundColor(Color.rgb(247, 247, 247));
			return false;
		} else {
			v.setBackgroundColor(Color.WHITE);
			return false;
		}
	}

	@Override
	public void onClick(View v) {
		SingersHolder holder = (SingersHolder) v.getTag();
		String singerName = holder.singerName.getText().toString();
		TianlApp.currentPlayPath=TianlApp.currentPlayPath+"/"+singerName;
		Intent intent = new Intent(IntentAction.INTENT_MUSCI_LIST_ACTIVITY);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, "MusicListActivity");
		if (TianlApp.activityBundles != null) {
			Bundle bundle = new Bundle();
			bundle.putString(MusicListActivity.KEY_TITLE, singerName);
			bundle.putString(MusicListActivity.KEY_SQL, SqlString.getSqlForSelectMusicBySingerName());
			bundle.putStringArray(MusicListActivity.KEY_ARGS,
					new String[] { singerName });
			bundle.putString(MusicListActivity.KEY_BACK_ACTION,
					IntentAction.INTENT_LOCAL_SINGERS_ACTIVITY);
			bundle.putString(MusicListActivity.KEY_ACTIVITY_NAME,
					"SingersActivity");
			bundle.putBoolean(MusicListActivity.KEY_BACK_BTN, true);
			bundle.putBoolean(MusicListActivity.KEY_EDIT_BTN, true);
			TianlApp.activityBundles.put(IntentAction.INTENT_MUSCI_LIST_ACTIVITY,
					bundle);
		}
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent, level + 1);
	}
}
