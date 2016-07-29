package com.handpay.obm.common.spring;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * �Ծ�̬��������ApplicationContext, �Ա��ڳ�����ʹ��
 * 
 * @author "baolm"
 * @version $Id: SpringContextHolder.java, v 0.1 2012-8-17 ����11:06:53 "baolm"
 *          Exp $
 */
public class SpringContextHolder implements ApplicationContextAware,
		DisposableBean {

	private static ApplicationContext applicationContext = null;

	private static Log logger = LogFactory.getLog(SpringContextHolder.class);

	/**
	 * ȡ�ô洢�ھ�̬�����е�ApplicationContext.
	 */
	public static ApplicationContext getApplicationContext() {
		assertContextInjected();
		return applicationContext;
	}

	/**
	 * �Ӿ�̬����applicationContext��ȡ��Bean, �Զ�ת��Ϊ����ֵ���������.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		assertContextInjected();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * �Ӿ�̬����applicationContext��ȡ��Bean, �Զ�ת��Ϊ����ֵ���������.
	 */
	public static <T> T getBean(Class<T> requiredType) {
		assertContextInjected();
		return applicationContext.getBean(requiredType);
	}

	/**
	 * ���SpringContextHolder�е�ApplicationContext.
	 */
	public static void clearHolder() {
		logger.debug("���SpringContextHolder�е�ApplicationContext:"
				+ applicationContext);
		applicationContext = null;
	}

	/**
	 * ʵ��ApplicationContextAware�ӿ�, ע��Context����̬������.
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		logger.debug("ע��ApplicationContext��SpringContextHolder");
		if (SpringContextHolder.applicationContext != null) {
			logger.warn("SpringContextHolder�е�ApplicationContext������, ԭ��ApplicationContextΪ:"
					+ SpringContextHolder.applicationContext);
		}
		SpringContextHolder.applicationContext = applicationContext; // NOSONAR
	}

	/**
	 * ʵ��DisposableBean�ӿ�, ��Context�ر�ʱ����̬����.
	 */
	@Override
	public void destroy() throws Exception {
		SpringContextHolder.clearHolder();
	}

	/**
	 * ���ApplicationContext��Ϊ��.
	 */
	private static void assertContextInjected() {
		Validate.notNull(applicationContext,
				"applicaitonContext����δע��, ����spring�����ļ��ж���SpringContextHolder.");
	}
}
