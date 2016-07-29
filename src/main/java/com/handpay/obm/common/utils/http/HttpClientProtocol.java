/*
 * @(#)HpptProtocol.java        1.0 2009-8-11
 *
 * Copyright (c) 2007-2009 Shanghai Handpay IT, Co., Ltd.
 * 16/F, 889 YanAn Road. W., Shanghai, China
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * Shanghai Handpay IT Co., Ltd. ("Confidential Information").  
 * You shall not disclose such Confidential Information and shall use 
 * it only in accordance with the terms of the license agreement you 
 * entered into with Handpay.
 */

package com.handpay.obm.common.utils.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

import com.handpay.core.common.util.ObjectUtil;

/**
 * Http��ʽ�����Э���� ���Ĳ���name-value��.
 * 
 * @version 1.0 2009-8-11
 * @author yzhu
 * @history
 * 
 */
public class HttpClientProtocol {

	private static final Logger logger = Logger.getLogger(HttpClientProtocol.class);

	public static final String CONTENT_TYPE = "Content-type";

	public static final String INPUT_ENCODING = "inputEncoding";
	
	public static final String REFERER = "referer";

	/**
	 * ���ͱ��� ʹ��http post�ķ�ʽ
	 * 
	 * @param url
	 *            ����URL
	 * @param sendPamams
	 *            �������
	 * @return ������
	 */
	public static Object sendRequestPost(String url, Map<String, String> sendPamams) {
		return sendRequestPost(url, sendPamams, null);
	}

	/**
	 * ���ͱ��� ʹ��http post�ķ�ʽ
	 * 
	 * @param url
	 *            ����URL
	 * @param sendPamams
	 *            �������
	 * @param connectionParams
	 *            ����ͷ���� <br>
	 *            eg: <br>
	 *            connectionParams.put(HttpClientProtocol.CONTENT_TYPE,
	 *            "application/x-www-form-urlencoded; charset=utf-8");<br>
	 *            connectionParams.put(HttpClientProtocol.INPUT_ENCODING,
	 *            "utf-8");
	 * @return ������
	 */
	public static Object sendRequestPost(String url, Map<String, String> sendPamams,
			Map<String, String> connectionParams) {

		long start = System.currentTimeMillis();

		HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
		PostMethod postMethod = new PostMethod(url);
		postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);

		// ���ñ���ͷ��Ϣ
		if (connectionParams != null && !ObjectUtil.isNull(connectionParams.get(CONTENT_TYPE))) {
			postMethod.addRequestHeader(CONTENT_TYPE, connectionParams.get(CONTENT_TYPE));
		} else {
			postMethod.addRequestHeader(CONTENT_TYPE, "application/x-www-form-urlencoded; charset=utf-8");
		}
		if(connectionParams != null && !ObjectUtil.isNull(connectionParams.get(REFERER))){
			postMethod.addRequestHeader(REFERER, connectionParams.get(REFERER));
		}

		// �����������
		logger.debug("params: " + sendPamams);
		NameValuePair[] values = new NameValuePair[sendPamams.size()];
		int index = 0;
		for (Map.Entry<String, String> param : sendPamams.entrySet()) {
			values[index++] = new NameValuePair(param.getKey(), param.getValue());
		}
		postMethod.setRequestBody(values);

		// ִ������
		int respCode = 0;
		try {
			respCode = httpClient.executeMethod(postMethod);
		} catch (HttpException e) {
			logger.error("sendRequest HttpException: ", e);
			logger.info("protocal to merchant time:" + (System.currentTimeMillis() - start) / 1000.00d + ",url=" + url);
			throw new RuntimeException(e.getMessage(), e);
		} catch (Exception e) {
			logger.error("sendRequest Exception: ", e);
			logger.info("protocal to merchant time:" + (System.currentTimeMillis() - start) / 1000.00d + ",url=" + url);
			throw new RuntimeException(e.getMessage(), e);
		}

		// ///////////// ����response /////////////
		// ����ʧ��
		if (respCode != 200) {
			logger.error("http resp code:" + respCode);
			logger.info("protocal to merchant time:" + (System.currentTimeMillis() - start) / 1000.00d);
			throw new RuntimeException("http resp code:" + respCode);
		}

		// ���سɹ�
		try {
			InputStream in = postMethod.getResponseBodyAsStream();
			BufferedReader reader = null;
			if (connectionParams != null && !ObjectUtil.isNull(connectionParams.get(INPUT_ENCODING))) {
				reader = new BufferedReader(new InputStreamReader(in, connectionParams.get(INPUT_ENCODING)));
			} else {
				reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
			}
			String str;
			StringBuffer buffer = new StringBuffer();
			while ((str = reader.readLine()) != null) {
				buffer.append(str);
			}
			return buffer.toString();
		} catch (IOException e) {
			logger.error("����response�쳣��", e);
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			// �Ͽ�����
			postMethod.releaseConnection();
		}

	}
	

	/**
	 * ���ͱ��� ʹ��http post�ķ�ʽ
	 * 
	 * @param url
	 *            ����URL
	 * @param sendPamams
	 *            �������
	 * @param connectionParams
	 *            ����ͷ���� <br>
	 *            eg: <br>
	 *            connectionParams.put(HttpClientProtocol.CONTENT_TYPE,
	 *            "application/x-www-form-urlencoded; charset=utf-8");<br>
	 *            connectionParams.put(HttpClientProtocol.INPUT_ENCODING,
	 *            "utf-8");
	 * @return ������
	 */
	public static Object sendRequestPost(String url,Integer connectionTimeout,Integer ioTimeOut, Map<String, String> sendPamams,
			Map<String, String> connectionParams) {

		long start = System.currentTimeMillis();

		HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);
		PostMethod postMethod = new PostMethod(url);
		postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, ioTimeOut);

		// ���ñ���ͷ��Ϣ
		if (connectionParams != null && !ObjectUtil.isNull(connectionParams.get(CONTENT_TYPE))) {
			postMethod.addRequestHeader(CONTENT_TYPE, connectionParams.get(CONTENT_TYPE));
		} else {
			postMethod.addRequestHeader(CONTENT_TYPE, "application/x-www-form-urlencoded; charset=utf-8");
		}

		// �����������
		logger.debug("params: " + sendPamams);
		NameValuePair[] values = new NameValuePair[sendPamams.size()];
		int index = 0;
		for (Map.Entry<String, String> param : sendPamams.entrySet()) {
			values[index++] = new NameValuePair(param.getKey(), param.getValue());
		}
		postMethod.setRequestBody(values);

		// ִ������
		int respCode = 0;
		try {
			respCode = httpClient.executeMethod(postMethod);
		} catch (HttpException e) {
			logger.error("sendRequest HttpException: ", e);
			logger.info("protocal to merchant time:" + (System.currentTimeMillis() - start) / 1000.00d + ",url=" + url);
			throw new RuntimeException(e.getMessage(), e);
		} catch (Exception e) {
			logger.error("sendRequest Exception: ", e);
			logger.info("protocal to merchant time:" + (System.currentTimeMillis() - start) / 1000.00d + ",url=" + url);
			throw new RuntimeException(e.getMessage(), e);
		}

		// ///////////// ����response /////////////
		// ����ʧ��
		if (respCode != 200) {
			logger.error("http resp code:" + respCode);
			logger.info("protocal to merchant time:" + (System.currentTimeMillis() - start) / 1000.00d);
			throw new RuntimeException("http resp code:" + respCode);
		}

		// ���سɹ�
		try {
			InputStream in = postMethod.getResponseBodyAsStream();
			BufferedReader reader = null;
			if (connectionParams != null && !ObjectUtil.isNull(connectionParams.get(INPUT_ENCODING))) {
				reader = new BufferedReader(new InputStreamReader(in, connectionParams.get(INPUT_ENCODING)));
			} else {
				reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
			}
			String str;
			StringBuffer buffer = new StringBuffer();
			while ((str = reader.readLine()) != null) {
				buffer.append(str);
			}
			return buffer.toString();
		} catch (IOException e) {
			logger.error("����response�쳣��", e);
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			// �Ͽ�����
			postMethod.releaseConnection();
		}

	}

	/**
	 * ���ͱ��� ʹ��http get�ķ�ʽ
	 * 
	 * @param url
	 *            ����URL
	 * @return ������
	 */
	public static Object sendRequestGet(String url) {
		return sendRequestGet(url, null);
	}

	/**
	 * ���ͱ��� ʹ��http get�ķ�ʽ
	 * 
	 * @param url
	 *            ����URL
	 * @param connectionParams
	 *            ����ͷ���� <br>
	 *            eg: <br>
	 *            connectionParams.put(HttpClientProtocol.CONTENT_TYPE,
	 *            "application/x-www-form-urlencoded; charset=utf-8");<br>
	 *            connectionParams.put(HttpClientProtocol.INPUT_ENCODING,
	 *            "utf-8");
	 * @return ������
	 */
	public static Object sendRequestGet(String url, Map<String, String> connectionParams) {

		long start = System.currentTimeMillis();

		HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
		GetMethod getMethod = new GetMethod(url);
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);

		// ���ñ���ͷ��Ϣ
		if (connectionParams != null && !ObjectUtil.isNull(connectionParams.get(CONTENT_TYPE))) {
			getMethod.addRequestHeader(CONTENT_TYPE, connectionParams.get(CONTENT_TYPE));
		} else {
			getMethod.addRequestHeader(CONTENT_TYPE, "application/x-www-form-urlencoded; charset=utf-8");
		}

		// ִ������
		int respCode = 0;
		try {
			respCode = httpClient.executeMethod(getMethod);
		} catch (HttpException e) {
			logger.error("sendRequest HttpException: ", e);
			logger.info("protocal to merchant time:" + (System.currentTimeMillis() - start) / 1000.00d + ",url=" + url);
			throw new RuntimeException(e.getMessage(), e);
		} catch (Exception e) {
			logger.error("sendRequest Exception: ", e);
			logger.info("protocal to merchant time:" + (System.currentTimeMillis() - start) / 1000.00d + ",url=" + url);
			throw new RuntimeException(e.getMessage(), e);
		}

		// ///////////// ����response /////////////
		// ����ʧ��
		if (respCode != 200) {
			logger.error("http resp code:" + respCode);
			logger.info("protocal to merchant time:" + (System.currentTimeMillis() - start) / 1000.00d);
			throw new RuntimeException("http resp code:" + respCode);
		}

		// ���سɹ�
		try {
			InputStream in = getMethod.getResponseBodyAsStream();
			BufferedReader reader = null;
			if (connectionParams != null && !ObjectUtil.isNull(connectionParams.get(INPUT_ENCODING))) {
				reader = new BufferedReader(new InputStreamReader(in, connectionParams.get(INPUT_ENCODING)));
			} else {
				reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
			}
			String str;
			StringBuffer buffer = new StringBuffer();
			while ((str = reader.readLine()) != null) {
				buffer.append(str);
			}
			return buffer.toString();
		} catch (IOException e) {
			logger.error("����response�쳣��", e);
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			// �Ͽ�����
			getMethod.releaseConnection();
		}

	}
}
