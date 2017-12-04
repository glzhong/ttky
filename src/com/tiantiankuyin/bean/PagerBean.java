package com.tiantiankuyin.bean;

public class PagerBean {

	// 当前页数
	private int currentPageNumber = 0;

	// 每页行数
	private int pageRowNumber=10;

	// 总页数
	private int countPageNumber=0;

	// 总行数
	private int countRowNumber=0;

	public int getCurrentPageNumber() {
		return currentPageNumber;
	}

	public void setCurrentPageNumber(int currentPageNumber) {
		this.currentPageNumber = currentPageNumber;
	}

	public int getPageRowNumber() {
		return pageRowNumber;
	}

	public void setPageRowNumber(int pageRowNumber) {
		this.pageRowNumber = pageRowNumber;
	}

	public int getCountPageNumber() {
		return countPageNumber;
	}

	public void setCountPageNumber(int countPageNumber) {
		this.countPageNumber = countPageNumber;
	}

	public int getCountRowNumber() {
		return countRowNumber;
	}

	public void setCountRowNumber(int countRowNumber) {
		this.countRowNumber = countRowNumber;
		this.countPageNumber = countRowNumber % pageRowNumber == 0 ? 
				               countRowNumber / pageRowNumber
				               : countRowNumber / pageRowNumber + 1;
	}
}
