package com.evsward.butler.util;

public class ByteUtils {
	
	/**
	 * 以小尾的顺序读取整数
	 * 
	 * @param ab
	 * @param offset
	 * @return
	 */
	public static int getInt(byte[] ab, int offset) {
		return ((ab[offset + 3] & 0xff) << 24) | ((ab[offset + 2] & 0xff) << 16)
				| ((ab[offset + 1] & 0xff) << 8) | (ab[offset + 0] & 0xff);
	}
}
