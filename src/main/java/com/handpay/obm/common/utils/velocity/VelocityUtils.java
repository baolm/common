/**
 * 
 */
package com.handpay.obm.common.utils.velocity;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;

import com.handpay.obm.common.exception.ObmBaseException;


/**
 * @author zqjiang
 *
 */
public class VelocityUtils {

	public static String merge(String template, Map<String, Object> param) throws ObmBaseException{
		try {
			VelocityEngine ve = new VelocityEngine();
			// 改变Velocity读取模板文件方式
			ve.setProperty("resource.loader", "class");
			ve.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new NullLogChute());
			ve.init();
			Template t = ve.getTemplate(template);
			VelocityContext context = new VelocityContext();
			for (Map.Entry<String, Object> entry : param.entrySet()) {
				context.put(entry.getKey(), entry.getValue());
			}
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			return writer.toString();
		} catch (Throwable e) {
			throw new ObmBaseException("parse error", e);
		}
	}
}
