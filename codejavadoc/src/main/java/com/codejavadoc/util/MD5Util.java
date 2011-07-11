package com.codejavadoc.util;

import static com.codejavadoc.util.StringUtil.padLeft;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	private MD5Util() {
	}
	
	public static String getMD5(String string) {
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(string.getBytes());
			byte messageDigest[] = algorithm.digest();
			StringBuilder hexString = new StringBuilder();
			for (int i = 0; i < messageDigest.length; i++) {
				hexString.append(padLeft(Integer.toHexString(0xFF & messageDigest[i]), '0', 2));
			}
			return hexString.toString().toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
