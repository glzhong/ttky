package com.tiantiankuyin.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 精选集列表数据对象
 * @author Erica
 *
 */
public class OlAlbumVO implements Serializable{
	private static final long serialVersionUID = -4180346070335734817L;
	/** 总记录数 */
	private int countIntRow;
	private int countRow;
	/** 总页数 */
	private int countPage;
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
	/** 精选集列表 */
	private List<OlAlbumItem> dataList;
	

	public int getCountIntRow() {
		return countIntRow;
	}

	public void setCountIntRow(int countIntRow) {
		this.countIntRow = countIntRow;
	}

	public int getCountRow() {
		return countRow;
	}

	public void setCountRow(int countRow) {
		this.countRow = countRow;
	}

	public int getCountPage() {
		return countPage;
	}

	public void setCountPage(int countPage) {
		this.countPage = countPage;
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

	public List<OlAlbumItem> getDataList() {
		return dataList;
	}

	public void setDataList(List<OlAlbumItem> dataList) {
		this.dataList = dataList;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null) {
			OlAlbumVO another = (OlAlbumVO) o;
			if (another != null && another.getDataList() != null
					&& this.getDataList() != null && this.getDataList().get(0).equals(another.getDataList().get(0)))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		if(this.getDataList().size()>0){
			if(this.getDataList().get(0).getId()>0)
				return this.getDataList().get(0).getId();
		}
		return 0;
	}
	
}
