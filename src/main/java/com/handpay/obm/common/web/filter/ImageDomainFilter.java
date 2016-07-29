package com.handpay.obm.common.web.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.handpay.core.common.util.ImageConstants;
import com.handpay.core.common.util.ObjectUtil;
/**
 *
 * 图片服务器域名过滤器
 *
 * @version 	1.0 2012-6-13
 * @author		yongwang
 * @history	    yangpeng
 * @modify  2012-06-13  网站加速项目增加图片服务器域名替换
 *
 */
@Component
public class ImageDomainFilter implements Filter {

	private static Logger logger = Logger.getLogger(ImageDomainFilter.class);
	
	private static final String DEFAULT_HOST = "wap.99wuxian.com";

	private String projectImageUrl;

	private String commonImageUrl;
	/**
	 * 公共资源替换域名
	 */
	private String testCommonImageUrl;
	/**
	 * 资源替换域名
	 */
	private String testProjectImageUrl;
	/**
	 * 需要替换域名集合
	 */
	private List<String> imageDomainList;
	/**
	 * 是否打开替换域名开关
	 */
	private String testImageButton;
	/**
	 * js资源文件开发和测试、上线版本文件夹开关
	 */
	private String jsMinFolderButton="min";

	/**
	 * HTTPS 前缀的 projectImageUrl
	 */
	private String secureProjectImageUrl;

	/**
	 * HTTPS 前缀的 commonImageUrl
	 */
	private String secureCommonImageUrl;

	/**
	 * HTTPS 前缀的 testProjectImageUrl
	 */
	private String secureTestProjectImageUrl;

	/**
	 * HTTPS 前缀的 testCommonImageUrl
	 */
	private String secureTestCommonImageUrl;
	
	/**
	 * 根据域名设置https或http
	 */
	private Set<String> permanentHosts = new HashSet<String>();

	public String getProjectImageUrl() {
		return projectImageUrl;
	}

	public void setProjectImageUrl(String projectImageUrl) {
		this.projectImageUrl = projectImageUrl;
		this.secureProjectImageUrl = schemaAware(true, projectImageUrl);
	}

	public String getCommonImageUrl() {
		return commonImageUrl;
	}

	public void setCommonImageUrl(String commonImageUrl) {
		this.commonImageUrl = commonImageUrl;
		this.secureCommonImageUrl = schemaAware(true, commonImageUrl);
	}

	/**
	 * @return the testCommonImageUrl
	 */
	public String getTestCommonImageUrl() {
		return testCommonImageUrl;
	}

	/**
	 * @param testCommonImageUrl the testCommonImageUrl to set
	 */
	public void setTestCommonImageUrl(String testCommonImageUrl) {
		this.testCommonImageUrl = testCommonImageUrl;
		this.secureTestCommonImageUrl = schemaAware(true, testCommonImageUrl);
	}

	/**
	 * @return the testProjectImageUrl
	 */
	public String getTestProjectImageUrl() {
		return testProjectImageUrl;
	}

	/**
	 * @param testProjectImageUrl the testProjectImageUrl to set
	 */
	public void setTestProjectImageUrl(String testProjectImageUrl) {
		this.testProjectImageUrl = testProjectImageUrl;
		this.secureTestProjectImageUrl = schemaAware(true, testProjectImageUrl);
	}

	/**
	 * @return the testImageButton
	 */
	public String getTestImageButton() {
		return testImageButton;
	}

	/**
	 * @param testImageButton the testImageButton to set
	 */
	public void setTestImageButton(String testImageButton) {
		this.testImageButton = testImageButton;
	}

	/**
	 * @return the imageDomainList
	 */
	public List<String> getImageDomainList() {
		return imageDomainList;
	}

	/**
	 * @param imageDomainList the imageDomainList to set
	 */
	public void setImageDomainList(List<String> imageDomainList) {
		this.imageDomainList = imageDomainList;
	}
	
	public Set<String> getPermanentHosts() {
		return permanentHosts;
	}

	public void setPermanentHosts(Set<String> permanentHosts) {
		if (permanentHosts == null) {
			return;
		}
		for (String host : permanentHosts) {
			addPermanentHost(host);
		}
	}

	public boolean isPermanentHttps(String host) {
		if (host == null || host.trim().length() == 0) {
			return false;
		}
		return this.permanentHosts.contains(host);
	}

	private void addPermanentHost(String host) {
		if (host == null) {
			return;
		}
		if ((host = host.trim()).length() == 0) {
			return;
		}
		this.permanentHosts.add(host.toLowerCase());
	}
	
	private static String toHost(String host) {
		if (host == null) {
			return DEFAULT_HOST;
		}
		int offset = host.indexOf(':');
		if (offset < 0) {
			return host;
		}
		return host.substring(0, offset);
	}


	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) arg1;
		HttpServletRequest request = (HttpServletRequest) arg0;

		// 从 HTTP 头中确定请求是否为 HTTPS 请求
		// 为了能保证在 HTTPS 环境中  pic.99wuxian.com 的图片资源能采用 HTTPS，需要对 HTTP/HTTPS 环境进行判断
		// 此处不能使用 request.getSchema() 获取协议，由于客户端过来的路由为：
		//  +----------------+     https     +--------------+     http     +------------------+
		//  | Browser Client | <-----------> | Nginx Server | <----------> | JBoss APP Server |
		//  +----------------+               +--------------+              +------------------+
		// Nginx 与 JBoss 采用 http，因此，通过  request.getSchema() 只能得到 http
		// 在 Nginx HTTPS 的反向代理中添加 X-HANDPAY-PROXY-HTTPS 头，表示该请求是 https 过来的
		// update by gaobaowen, 2012.07.27
		boolean isSecure = "https".equals(request.getHeader("X-HANDPAY-PROXY-HTTPS"));

		// 获取各种图片 URL 是 HTTP 还是 HTTPS 的，之后均使用该局部变量的值
		String commonUrl      = isSecure ? secureCommonImageUrl : commonImageUrl;
		String projectUrl     = isSecure ? secureProjectImageUrl : projectImageUrl;
		String testCommonUrl  = isSecure ? secureTestCommonImageUrl : testCommonImageUrl;
		String testProjectUrl = isSecure ? secureTestProjectImageUrl : testProjectImageUrl;
		String jsMinFolder=(String)request.getAttribute(ImageConstants.JSMINFOLDER);
		if(ObjectUtil.isNull(jsMinFolder)){
			jsMinFolder=ObjectUtil.isNull(jsMinFolderButton)?"min":jsMinFolderButton;
		}
		request.setAttribute(ImageConstants.JSMINFOLDER, jsMinFolder);
		// 重置 request attribute 中相关的 URL
		String projectAttr = (String)request.getAttribute(ImageConstants.PROJECTIMAGEURL);
		if (projectAttr != null) {
			request.setAttribute(ImageConstants.PROJECTIMAGEURL, schemaAware(isSecure, projectAttr));
		}
		String commonAttr = (String)request.getAttribute(ImageConstants.COMMONIMAGEURL);
		if (commonAttr != null) {
			request.setAttribute(ImageConstants.COMMONIMAGEURL, schemaAware(isSecure, commonAttr));
		}

		// 输出调试用日志
		if (logger.isDebugEnabled()) {
			logger.debug("X-HANDPAY-PROXY-HTTPS: " + request.getHeader("X-HANDPAY-PROXY-HTTPS"));
			logger.debug("        schema: " + request.getScheme());
			logger.debug("   request url: " + request.getRequestURL());
			logger.debug("     commonUrl: " + commonUrl);
			logger.debug("    projectUrl: " + projectUrl);
			logger.debug(" testCommonUrl: " + testCommonUrl);
			logger.debug("testProjectUrl: " + testProjectUrl);
			logger.debug("   projectAttr: " + request.getAttribute(ImageConstants.PROJECTIMAGEURL));
			logger.debug("    commonAttr: " + request.getAttribute(ImageConstants.COMMONIMAGEURL));
		}

		if (request.getAttribute(ImageConstants.PROJECTIMAGEURL) == null) {
			//当替换图片服务器域名开关为true时，进行域名替换
			if("TRUE".equalsIgnoreCase(testImageButton)){
				String host=request.getHeader("Host");
				for (String  domain : imageDomainList) {
					if(!ObjectUtil.isNull(host)&&host.equals(domain)){
						request.setAttribute(ImageConstants.PROJECTIMAGEURL,testProjectUrl);
						request.setAttribute(ImageConstants.COMMONIMAGEURL,testCommonUrl);
						break;
					}
				}
				String project=(String) request.getAttribute(ImageConstants.PROJECTIMAGEURL);
				String common=(String) request.getAttribute(ImageConstants.COMMONIMAGEURL);
				if(ObjectUtil.isNull(project)){
					//logger.info("没有找到相应的项目资源替换域名信息，转向默认的图片服务器地址！");
					request.setAttribute(ImageConstants.PROJECTIMAGEURL, projectUrl);
				}
				if(ObjectUtil.isNull(common)){
					//logger.info("没有找到相应的公共资源替换域名信息，转向默认的图片服务器地址！");
					request.setAttribute(ImageConstants.COMMONIMAGEURL,commonUrl);
				}
			}else{
				request.setAttribute(ImageConstants.PROJECTIMAGEURL, projectUrl);
				request.setAttribute(ImageConstants.COMMONIMAGEURL, commonUrl);
			}
		}

		//根据配置的domain判断是否设置https协议 add by hbxu  2012-8-15 16:27:13
		String host = request.getHeader("Host");
		String httpDomain = "http://" + host;
		if(this.isPermanentHttps(toHost(host))){
			httpDomain = "https://" + host;
		}
		request.setAttribute("wapProtocolHttpDomain", httpDomain);
		request.setAttribute("wapProtocolHttpsDomain", "https://" + host);
		arg2.doFilter(request, response);
	}

	private String schemaAware(boolean isSecure, String domain) {
		if (domain == null) {
			return null;
		}
		return isSecure ? domain.replace("http://", "https://") : domain;
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	public void setJsMinFolderButton(String jsMinFolderButton) {
		this.jsMinFolderButton = jsMinFolderButton;
	}
	
}