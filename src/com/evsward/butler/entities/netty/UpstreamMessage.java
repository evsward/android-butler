package com.evsward.butler.entities.netty;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * netty 通讯专用
 * 
 * @Date Jun 16, 2015
 * @author liuBin
 */
public class UpstreamMessage {
	private int cmd;
	private ChannelBuffer body;
	private int bodyLength;
	private String ip;
	private int check;

	/**
	 * @return the check
	 */
	public int getCheck() {
		return check;
	}

	/**
	 * @param check
	 *            the check to set
	 */
	public void setCheck(int check) {
		this.check = check;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	/**
	 * @return the body
	 */
	public ChannelBuffer getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(ChannelBuffer body) {
		this.body = body;
	}

	/**
	 * @return the bodyLength
	 */
	public int getBodyLength() {
		return bodyLength;
	}

	/**
	 * @param bodyLength
	 *            the bodyLength to set
	 */
	public void setBodyLength(int bodyLength) {
		this.bodyLength = bodyLength;
	}

	@Override
	public String toString() {
		return "upmessage: {'ip':'" + getIp() + "','cmd':'" + getCmd() + "','bodyLength':'" + getBodyLength() + "'}}";
	}

}
