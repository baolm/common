/*
 * @(#)XMLUtil.java        1.0 2009-8-18
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

package com.handpay.obm.common.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * xml工具类.
 * 
 * @version 1.0 2014-11-11
 * @author zqjiang
 * @history
 * 
 */
public class XMLUtil {
	private static final Logger logger = Logger
			.getLogger(XMLUtil.class);
	/**
	 * 解析xml字符串
	 * 
	 * @param xmlStr
	 * @return dom4J的root Element
	 * @throws Exception
	 */
	public static Element parseXmlStr(String xmlStr) throws Exception {
		if (StringUtils.isBlank(xmlStr)) {
			throw new Exception("null xml string");
		}

		SAXReader sax = new SAXReader();
		Document doc = null;

		try {
			/** 解析xml格式的应答报文 */
			doc = sax.read(new StringReader(xmlStr.replaceAll("&amp;", "-_-")
					.replaceAll("&", "&amp;").replaceAll("-_-", "&amp;")));
		} catch (Exception ex) {
			logger.error("========xmlStr="+xmlStr);
			throw ex;
		}

		if (doc == null) {
			throw new Exception("null xml document");
		}

		/** 获得根element */
		Element root = doc.getRootElement();
		if (root == null) {
			throw new Exception("null xml string");
		}
		return root;
	}

	public static void main(String[] ss) {

	}

	/**
	 * 根据xpath路径查询字符串值
	 * 
	 * @param root
	 * @param xPath
	 * @return
	 */
	public static String getStringValue(Element root, String xPath) {
		Node node = root.selectSingleNode(xPath);
		if (node == null) {
			return null;
		}
		String value = node.getText();
		if (StringUtils.isBlank(value)) {
			return null;
		}
		return value.trim();
	}

	/**
	 * 根据xpath路径查询整数
	 * 
	 * @param root
	 * @param xPath
	 * @return
	 */
	public static int getIntValue(Element root, String xPath) {
		Node node = root.selectSingleNode(xPath);
		if (node == null) {
			return 0;
		}
		String value = node.getText();
		if (StringUtils.isBlank(value)) {
			return 0;
		}
		try {
			double val = Double.parseDouble(value);
			return (int)val;
		} catch (Exception ex) {
			return 0;
		}
	}

	/**
	 * 根据xpath路径查询浮点数
	 * 
	 * @param root
	 * @param xPath
	 * @return
	 */
	public static double getDoubleValue(Element root, String xPath) {
		Node node = root.selectSingleNode(xPath);
		if (node == null) {
			return 0.00d;
		}
		String value = node.getText();
		if (StringUtils.isBlank(value)) {
			return 0.00d;
		}
		try {
			return Double.parseDouble(value);
		} catch (Exception ex) {
			return 0.00d;
		}
	}

	/**
	 * 根据xpath路径查询bool值
	 * 
	 * @param root
	 * @param xPath
	 * @return
	 */
	public static boolean getBoolValue(Element root, String xPath) {
		Node node = root.selectSingleNode(xPath);
		if (node == null) {
			return false;
		}
		String value = node.getText();
		if (StringUtils.isBlank(value)) {
			return false;
		}
		try {
			return Boolean.parseBoolean(value);
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * 根据xpath路径查询字符串列表
	 * 
	 * @param root
	 * @param xPath
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getStringList(Element root, String xPath) {
		List<Node> nodes = root.selectNodes(xPath);
		if (nodes == null || nodes.size() == 0) {
			return new ArrayList<String>();
		}
		List<String> values = new ArrayList<String>();
		for (Node node : nodes) {
			String value = node.getText();
			if (StringUtils.isNotBlank(value)) {
				values.add(value);
			}
		}
		return values;
	}

	/**
	 * 根据xpath路径查询整数列表
	 * 
	 * @param root
	 * @param xPath
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Integer> getIntList(Element root, String xPath) {
		List<Node> nodes = root.selectNodes(xPath);
		if (nodes == null || nodes.size() == 0) {
			return new ArrayList<Integer>();
		}
		List<Integer> values = new ArrayList<Integer>();
		for (Node node : nodes) {
			String value = node.getText();
			try {
				values.add(Integer.valueOf(value));
			} catch (Exception ex) {

			}
		}
		return values;
	}

	/**
	 * 根据xpath路径查询浮点数列表
	 * 
	 * @param root
	 * @param xPath
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Double> getDoubleList(Element root, String xPath) {
		List<Node> nodes = root.selectNodes(xPath);
		if (nodes == null || nodes.size() == 0) {
			return new ArrayList<Double>();
		}
		List<Double> values = new ArrayList<Double>();
		for (Node node : nodes) {
			String value = node.getText();
			try {
				values.add(Double.valueOf(value));
			} catch (Exception ex) {

			}
		}
		return values;
	}

	/**
	 * 将XML格式的数据解析，并存入Map
	 * 
	 * @param xmlContent
	 *            xml格式数据内容
	 * @param encode
	 *            编码格式
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	public static Map paraseXmlToMap(String xmlContent, String encode)
			throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SAXReader saxReader = null;
		Document document = null;
		ByteArrayInputStream in;
		if (xmlContent != null && !xmlContent.trim().equals("")) {
			in = new ByteArrayInputStream(xmlContent.getBytes(encode));
			saxReader = new SAXReader();
			document = saxReader.read(in);
			Element handpayElement = document.getRootElement();
			for (Iterator i = handpayElement.elementIterator(); i.hasNext();) {
				Element element = (Element) i.next();
				String key = element.getName();
				String value = element.getTextTrim();
				paramMap.put(key, value);
			}

		}

		return paramMap;
	}

	// public static void main(String[] ss) throws Exception{
	// String xml = "<Param><Head><aa>adfafd</aa></Head></Param>";
	//		
	// Element root = parseXmlStr(xml);
	// Element head = root.element("Head");
	// System.out.println("XMLUtil.main() aa=" + head.elementText("aa"));
	//		
	// boolean bb = Pattern.matches("([a-z]|[A-Z]|[0-9])*",
	// "P04aYt4XoJPSIBp4UVU7hhz3DDUtJo@aaa");
	// System.out.println("XMLUtil.main()bb=" + bb) ;
	// }

}
