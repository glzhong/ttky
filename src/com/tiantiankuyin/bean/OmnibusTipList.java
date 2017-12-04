package com.tiantiankuyin.bean;

import java.io.Serializable;
import java.util.List;

/** 精选集标签对象集合
 * @author Erica
 *  */
public class OmnibusTipList implements Serializable{
	private static final long serialVersionUID = -5624873070538872486L;
	/** 标签对象集合 */
	private List<OmnibusTip> adList;
	/** 标签个数 */
	private int count;
	public OmnibusTipList(){
		
	}
	
	public List<OmnibusTip> getAdList() {
		return adList;
	}

	public void setAdList(List<OmnibusTip> adList) {
		this.adList = adList;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public boolean equals(Object o) {
		if (o != null) {
			OmnibusTipList another = (OmnibusTipList) o;
			if (another != null && another.getAdList() != null
					&& this.getAdList() != null && this.getAdList().get(0).equals(another.getAdList().get(0)))
				return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		if(this.getAdList().size()>0){
			if(this.getAdList().get(0).getId()>0)
				return this.getAdList().get(0).getId();
		}
		return 0;
	}
	
}
