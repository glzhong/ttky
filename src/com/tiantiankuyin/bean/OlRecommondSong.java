package com.tiantiankuyin.bean;

import java.io.Serializable;
import java.util.List;

public class OlRecommondSong implements Serializable{
	private static final long serialVersionUID = 1974406812603375701L;
	private String countPage; // 总页数
	private String countRow; // 总记录数
	private String everyPageRow; // 每页几条数据
	private String thisPage; // 当前页
	private String nextPage; // 下一页
	private String chartCode;
	private boolean isLastPage;
	private List<OlSongVO> dataList; // 返回数据集

	public String getCountPage() {
		return countPage;
	}

	public void setCountPage(String countPage) {
		this.countPage = countPage;
	}

	public String getCountRow() {
		return countRow;
	}

	public void setCountRow(String countRow) {
		this.countRow = countRow;
	}

	public String getEveryPageRow() {
		return everyPageRow;
	}

	public void setEveryPageRow(String everyPageRow) {
		this.everyPageRow = everyPageRow;
	}

	public String getThisPage() {
		return thisPage;
	}

	public void setThisPage(String thisPage) {
		this.thisPage = thisPage;
	}

	public String getNextPage() {
		return nextPage;
	}

	public void setNextPage(String nextPage) {
		this.nextPage = nextPage;
	}

	public List<OlSongVO> getDataList() {
		return dataList;
	}

	public void setDataList(List<OlSongVO> dataList) {
		this.dataList = dataList;
	}

	public String getChartCode() {
		return chartCode;
	}

	public void setChartCode(String chartCode) {
		this.chartCode = chartCode;
	}

	public boolean isLastPage() {
		return isLastPage;
	}

	public void setLastPage(boolean isLastPage) {
		this.isLastPage = isLastPage;
	}
}
