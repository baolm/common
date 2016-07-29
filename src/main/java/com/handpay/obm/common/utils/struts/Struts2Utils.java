/*
 * @(#)Struts2Utils.java        1.0 2014-1-4
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

package com.handpay.obm.common.utils.struts;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Struts2������
 * 
 * @version 1.0 2014-1-4
 * @author "lmbao"
 * 
 */
public class Struts2Utils {

	// -- header �������� --//
	private static final String HEADER_ENCODING = "encoding";
	private static final String HEADER_NOCACHE = "no-cache";
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final boolean DEFAULT_NOCACHE = true;

	public static final String TEXT_TYPE = "text/plain";
	public static final String JSON_TYPE = "application/json";
	public static final String XML_TYPE = "text/xml";
	public static final String HTML_TYPE = "text/html";
	public static final String JS_TYPE = "text/javascript";
	public static final String EXCEL_TYPE = "application/vnd.ms-excel";

	private static ObjectMapper mapper = new ObjectMapper();

	// -- ȡ��Request/Response/Session�ļ򻯺��� --//
	/**
	 * ȡ��HttpSession�ļ򻯺���.
	 */
	public static HttpSession getSession() {
		return ServletActionContext.getRequest().getSession();
	}

	/**
	 * ȡ��HttpSession�ļ򻯺���.
	 */
	public static HttpSession getSession(boolean isNew) {
		return ServletActionContext.getRequest().getSession(isNew);
	}

	/**
	 * ȡ��HttpSession��Attribute�ļ򻯺���.
	 */
	public static Object getSessionAttribute(String name) {
		HttpSession session = getSession(false);
		return (session != null ? session.getAttribute(name) : null);
	}

	/**
	 * ȡ��HttpRequest�ļ򻯺���.
	 */
	public static HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}

	/**
	 * ȡ��HttpRequest��Parameter�ļ򻯷���.
	 */
	public static String getParameter(String name) {
		return getRequest().getParameter(name);
	}

	/**
	 * ȡ��HttpResponse�ļ򻯺���.
	 */
	public static HttpServletResponse getResponse() {
		return ServletActionContext.getResponse();
	}

	// -- �ƹ�jsp/freemakerֱ������ı��ĺ��� --//
	/**
	 * ֱ��������ݵļ�㺯��.
	 * 
	 * eg. render("text/plain", "hello", "encoding:GBK"); render("text/plain",
	 * "hello", "no-cache:false"); render("text/plain", "hello", "encoding:GBK",
	 * "no-cache:false");
	 * 
	 * @param headers
	 *            �ɱ��header���飬Ŀǰ���ܵ�ֵΪ"encoding:"��"no-cache:",Ĭ��ֵ�ֱ�ΪUTF-8��true.
	 */
	public static void render(final String contentType, final String content, final String... headers) {
		HttpServletResponse response = initResponseHeader(contentType, headers);
		try {
			response.getWriter().write(content);
			response.getWriter().flush();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try {
				response.getWriter().close();
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	/**
	 * ֱ������ı�.
	 * 
	 * @see #render(String, String, String...)
	 */
	public static void renderText(final String text, final String... headers) {
		render(TEXT_TYPE, text, headers);
	}

	/**
	 * ֱ�����HTML.
	 * 
	 * @see #render(String, String, String...)
	 */
	public static void renderHtml(final String html, final String... headers) {
		render(HTML_TYPE, html, headers);
	}

	/**
	 * ֱ�����XML.
	 * 
	 * @see #render(String, String, String...)
	 */
	public static void renderXml(final String xml, final String... headers) {
		render(XML_TYPE, xml, headers);
	}

	/**
	 * ֱ�����JSON.
	 * 
	 * @param jsonString
	 *            json�ַ���.
	 * @see #render(String, String, String...)
	 */
	public static void renderJson(final String jsonString, final String... headers) {
		render(JSON_TYPE, jsonString, headers);
	}

	/**
	 * ֱ�����JSON,ʹ��Jacksonת��Java����.
	 * 
	 * @param data
	 *            ������List<POJO>, POJO[], POJO, Ҳ����Map��ֵ��.
	 * @see #render(String, String, String...)
	 */
	public static void renderJson(final Object data, final String... headers) {
		HttpServletResponse response = initResponseHeader(JSON_TYPE, headers);
		try {
			mapper.writeValue(response.getWriter(), data);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * ֱ�����֧�ֿ���Mashup��JSONP.
	 * 
	 * @param callbackName
	 *            callback������.
	 * @param object
	 *            Java����,������List<POJO>, POJO[], POJO ,Ҳ����Map��ֵ��, ����ת��Ϊjson�ַ���.
	 */
	public static void renderJsonp(final String callbackName, final Object object, final String... headers) {
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(object);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}

		String result = new StringBuilder().append(callbackName).append("(").append(jsonString).append(");").toString();

		// ��ȾContent-TypeΪjavascript�ķ�������,������Ϊjavascript���,
		// ��callback197("{html:'Hello World!!!'}");
		render(JS_TYPE, result, headers);
	}

	/**
	 * ����������contentType��headers.
	 */
	private static HttpServletResponse initResponseHeader(final String contentType, final String... headers) {
		// ����headers����
		String encoding = DEFAULT_ENCODING;
		boolean noCache = DEFAULT_NOCACHE;
		for (String header : headers) {
			String headerName = StringUtils.substringBefore(header, ":");
			String headerValue = StringUtils.substringAfter(header, ":");

			if (StringUtils.equalsIgnoreCase(headerName, HEADER_ENCODING)) {
				encoding = headerValue;
			} else if (StringUtils.equalsIgnoreCase(headerName, HEADER_NOCACHE)) {
				noCache = Boolean.parseBoolean(headerValue);
			} else {
				throw new IllegalArgumentException(headerName + "����һ���Ϸ���header����");
			}
		}

		HttpServletResponse response = ServletActionContext.getResponse();

		// ����headers����
		String fullContentType = contentType + ";charset=" + encoding;
		response.setContentType(fullContentType);
		if (noCache) {
			setDisableCacheHeader(response);
		}

		return response;
	}

	private static void setDisableCacheHeader(HttpServletResponse response) {
		// Http 1.0 header
		response.setDateHeader("Expires", 1L);
		response.addHeader("Pragma", "no-cache");
		// Http 1.1 header
		response.setHeader("Cache-Control", "no-cache, no-store, max-age=0");
	}
}
