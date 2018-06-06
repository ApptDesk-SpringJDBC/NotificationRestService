package com.telappoint.notification.common.utils;
import java.util.Random;

/**
 * 
 * @author Balaji
 *
 */
public class GenerateRandomToken {

	private GenerateRandomToken() {

	}

	public static String getRandomToken(int length, String tokenType) {
		if ("N".equals(tokenType)) {
			return randomNumber(length);
		} else if ("AN".equals(tokenType)) {
			return randomAlphaNumeric(length);
		} else if ("A".equals(tokenType)) {
			return randomString(length);
		} else {
			throw new IllegalArgumentException("Unknown " + tokenType);
		}
	}

	private static String randomString(int length) {
		StringBuilder buffer = new StringBuilder();

		for (;;) {
			if (buffer.length() < length) {
				buffer.append(randomUppercase());
			} else {
				break;
			}
		}
		return buffer.toString();
	}

	private static String randomAlphaNumeric(int length) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(randomUppercase());
		for (int i = 2;; i++) {
			if (buffer.length() < length) {
				if (i % 2 == 0) {
					buffer.append(randomNumeric());
				} else {
					buffer.append(randomUppercase());
				}
			} else {
				break;
			}
		}
		return buffer.toString();
	}

	private static String randomNumber(int length) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < length; i++) {
			buffer.append(randomNumeric());
		}
		return buffer.toString();
	}

	private static char randomNumeric() {
		return (char) (48 + (int) (Math.random() * 10));
	}

	private static char randomUppercase() {
		return (char) (65 + (int) (Math.random() * 26));
	}

	private static char[] alphanumeric = alphanumeric();
	private static Random rand = new Random();

	public static String getAlphaNumericRandom(int len) {
		StringBuilder out = new StringBuilder();

		while (out.length() < len) {
			int idx = Math.abs(rand.nextInt() % alphanumeric.length);
			out.append(alphanumeric[idx]);
		}
		return out.toString();
	}

	private static char[] alphanumeric() {
		StringBuilder buf = new StringBuilder(128);
		for (int i = 48; i <= 57; i++) {
			buf.append((char) i); // 0-9
		}
		for (int i = 65; i <= 90; i++) {
			buf.append((char) i); // A-Z
		}
		for (int i = 97; i <= 122; i++) {
			buf.append((char) i); // a-z
		}
		return buf.toString().toCharArray();
	}

}
