package com.trade.dao.bitso;

public class BitsoPayload {
	
	String oid;
	String book;
	String original_amount;
	String unfilled_amount;
	String original_value;
	String created_at;
	String updated_at;
	String price;
	String side;
	String status;
	String type;
	

	public String getSide() {
		return side;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getBook() {
		return book;
	}

	public void setBook(String book) {
		this.book = book;
	}

	public String getOriginal_amount() {
		return original_amount;
	}

	public void setOriginal_amount(String original_amount) {
		this.original_amount = original_amount;
	}

	public String getUnfilled_amount() {
		return unfilled_amount;
	}

	public void setUnfilled_amount(String unfilled_amount) {
		this.unfilled_amount = unfilled_amount;
	}

	public String getOriginal_value() {
		return original_value;
	}

	public void setOriginal_value(String original_value) {
		this.original_value = original_value;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}
