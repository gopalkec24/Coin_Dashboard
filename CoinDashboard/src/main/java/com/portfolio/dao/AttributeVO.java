package com.portfolio.dao;

public class AttributeVO {
	
	String attr_name;
	String attr_src_dataType;
	String attr_tgt_dataType;
	String path;
	String tgt_attr_name;
	public String getTgt_attr_name() {
		return tgt_attr_name;
	}
	public void setTgt_attr_name(String tgt_attr_name) {
		this.tgt_attr_name = tgt_attr_name;
	}
	public String getAttr_name() {
		return attr_name;
	}
	public void setAttr_name(String attr_name) {
		this.attr_name = attr_name;
	}
	public String getAttr_src_dataType() {
		return attr_src_dataType;
	}
	public void setAttr_src_dataType(String attr_src_dataType) {
		this.attr_src_dataType = attr_src_dataType;
	}
	public String getAttr_tgt_dataType() {
		return attr_tgt_dataType;
	}
	public void setAttr_tgt_dataType(String attr_tgt_dataType) {
		this.attr_tgt_dataType = attr_tgt_dataType;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	

}
