package com.evsward.butler.util;

public class MainTest {
	public static Long convertHex2Long(String nfctagIDHex) {
		Long nfctagIDDecimal = null;
		try {
			nfctagIDDecimal = Long.parseLong(nfctagIDHex, 16);
		} catch (NumberFormatException e) {
			return nfctagIDDecimal;
		}
		return nfctagIDDecimal;
	}
	public static void main(String[] args) {
		System.out.println(convertHex2Long("0004040201000F03"));
	}
}
