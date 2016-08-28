package com.evsward.butler.entities;

/**
 * 用于接收NFC查询结果的实体
 * 
 * @Date Apr 13, 2015
 * @author liuwb.edward
 */
public class NFCInfo {
	private String uuidLong;
	private String nfcState;
	private String cardno;
	private String id;

	public String getUuidLong() {
		return uuidLong;
	}

	public void setUuidLong(String uuidLong) {
		this.uuidLong = uuidLong;
	}

	public String getNfcState() {
		return nfcState;
	}

	public void setNfcState(String nfcState) {
		this.nfcState = nfcState;
	}

	public String getCardno() {
		return cardno;
	}

	public void setCardno(String cardno) {
		this.cardno = cardno;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
