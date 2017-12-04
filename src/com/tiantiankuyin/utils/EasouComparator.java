package com.tiantiankuyin.utils;

import java.util.Comparator;

import com.tiantiankuyin.bean.RecordBean;

public class EasouComparator implements Comparator<RecordBean>{

	@Override
	public int compare(RecordBean object1, RecordBean object2) {
		if(object1.count>object2.count){
			return -1;
		}
		if(object1.count==object2.count){
			return 0;
		}
		if(object1.count<object2.count){
			return 1;
		}
		return 0;
	}

}
