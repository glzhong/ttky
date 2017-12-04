package com.tiantiankuyin.bean;

import java.io.Serializable;
import java.util.List;

/** 歌手列表Bean对象
 * @author Erica
 *  */
public class SingerListBean implements Serializable{

	private static final long serialVersionUID = 7366592716812255195L;
	/** 总记录数 */
	private int countIntRow;
	/** 总页数 */
	private int countPage;
	/** 歌曲列表 */
	private List<SingerBean> dataList;
	/** 每页显示行数 */
	private int everyPageRow;
	/** 是否存在下一页 */
	private boolean hasNext;
	/** 下一页 */
	private int nextPage;
	/** 当前页码 */
	private int thisPage;
	/** 当前页面显示行数 */
	private int thisPageRow;
	
	public SingerListBean(){}

	public int getCountIntRow() {
		return countIntRow;
	}

	public void setCountIntRow(int countIntRow) {
		this.countIntRow = countIntRow;
	}

	public int getCountPage() {
		return countPage;
	}

	public void setCountPage(int countPage) {
		this.countPage = countPage;
	}

	public List<SingerBean> getDataList() {
		return dataList;
	}

	public void setDataList(List<SingerBean> dataList) {
		this.dataList = dataList;
	}

	public int getEveryPageRow() {
		return everyPageRow;
	}

	public void setEveryPageRow(int everyPageRow) {
		this.everyPageRow = everyPageRow;
	}

	public boolean isHasNext() {
		return hasNext;
	}

	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	public int getThisPage() {
		return thisPage;
	}

	public void setThisPage(int thisPage) {
		this.thisPage = thisPage;
	}

	public int getThisPageRow() {
		return thisPageRow;
	}

	public void setThisPageRow(int thisPageRow) {
		this.thisPageRow = thisPageRow;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null) {
			SingerListBean another = (SingerListBean) o;
			if (another != null && another.getDataList() != null
					&& this.getDataList() != null && this.getDataList().get(0).equals(another.getDataList().get(0)))
				return true;
		}
		return false;
	}
	
}
