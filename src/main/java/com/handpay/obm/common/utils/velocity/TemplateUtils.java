/*
 * @(#)TemplateUtils.java        1.0 2014-9-28
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

package com.handpay.obm.common.utils.velocity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class description goes here.
 * 
 * @version 1.0 2014-9-28
 * @author "lmbao"
 * @history
 * 
 */
public class TemplateUtils {

	private static final Logger logger = LoggerFactory.getLogger(TemplateUtils.class);

	public static void generateFromClasspath(String template, String outputPath, String outputFile,
			Map<String, Object> params) throws Exception {

		VelocityEngine velocityEngine = getClasspathVelocityEngine();
		output(template, outputPath, outputFile, params, velocityEngine);
	}

	public static void generateFromFileResource(String fileResource, String template, String outputPath,
			String outputFile, Map<String, Object> params) throws Exception {

		logger.info("fileResource: {}", fileResource);
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, fileResource);
		ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new NullLogChute());
		ve.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
		ve.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
		ve.init();
		output(template, outputPath, outputFile, params, ve);
	}

	public static String generateStringResultFromClasspath(String template, Map<String, Object> params)
			throws Exception {

		VelocityEngine velocityEngine = getClasspathVelocityEngine();
		Template t = velocityEngine.getTemplate(template);
		VelocityContext context = new VelocityContext();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			context.put(entry.getKey(), entry.getValue());
		}
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		return writer.toString();
	}

	private static VelocityEngine getClasspathVelocityEngine() throws Exception {
		VelocityEngine velocityEngine = new VelocityEngine();
		// 改变Velocity读取模板文件方式
		velocityEngine.setProperty("resource.loader", "class");
		velocityEngine.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new NullLogChute());
		velocityEngine.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
		velocityEngine.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
		velocityEngine.init();
		return velocityEngine;
	}

	private static void output(String template, String outputPath, String outputFile, Map<String, Object> params,
			VelocityEngine ve) throws Exception {

		Template t = ve.getTemplate(template);
		VelocityContext context = new VelocityContext();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			context.put(entry.getKey(), entry.getValue());
		}

		File file = new File(outputPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		outputFile = outputPath + outputFile;

		FileOutputStream fos = new FileOutputStream(outputFile);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
		t.merge(context, writer);
		writer.close();
		logger.info("generate file complete. {}, {}", template, outputFile);
	}
}
