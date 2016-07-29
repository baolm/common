/*
 * @(#)MD5Utils.java        1.0 2014-6-10
 *
 * Copyright (c) 2007-2014 Shanghai Handpay IT, Co., Ltd.
 * 16/F, 889 YanAn Road. W., Shanghai, China
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * Shanghai Handpay IT Co., Ltd. ("Confidential Information").  
 * You shall not disclose such Confidential Information and shall use 
 * it only in accordance with the terms of the license agreement you 
 * entered into with Handpay.
 */

package com.handpay.obm.common.utils.md5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class description goes here.
 * 
 * @version 1.0 2014-6-10
 * @author "lmbao"
 * @history
 * 
 */
public class MD5Utils {

	private static final Logger logger = LoggerFactory.getLogger(MD5Utils.class);

	public static String digest(String source) {

		try {

			MessageDigest digest = MessageDigest.getInstance("MD5");
			// ʹ��ָ�����ֽڸ���ժҪ
			digest.update(source.getBytes());
			// �������
			byte[] md = digest.digest();
			// �ַ�����ת�����ַ�������
			return byteArrayToHex(md);

		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	

	public static String digest(byte[] source) {

		try {

			MessageDigest digest = MessageDigest.getInstance("MD5");
			// ʹ��ָ�����ֽڸ���ժҪ
			digest.update(source);
			// �������
			byte[] md = digest.digest();
			// �ַ�����ת�����ַ�������
			return byteArrayToHex(md);

		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * �ַ�����ת�����ַ���
	 */
	private static String byteArrayToHex(byte[] byteArray) {

		// ���ȳ�ʼ��һ���ַ����飬�������ÿ��16�����ַ�
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

		// newһ���ַ����飬�������������ɽ���ַ����ģ�����һ�£�һ��byte�ǰ�λ�����ƣ�Ҳ����2λʮ�������ַ���2��8�η�����16��2�η�����
		char[] resultCharArray = new char[byteArray.length * 2];

		// �����ֽ����飬ͨ��λ���㣨λ����Ч�ʸߣ���ת�����ַ��ŵ��ַ�������ȥ
		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b & 0xf];
		}

		// �ַ�������ϳ��ַ�������
		return new String(resultCharArray);
	}
}
