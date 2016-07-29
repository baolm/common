/**
 * 
 */
package com.handpay.obm.common.web.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.handpay.core.common.util.BASE64Coding;
import com.handpay.core.user.bean.LoginUserBean;
import com.handpay.obm.common.utils.PlatFormConstant;

/**
 * @author zqjiang
 *
 */
public class UserSecurityFilter implements Filter {
	private Logger logger = Logger.getLogger(this.getClass());
	
	private final static String UTF_8 = "utf-8";
	private final static String F1 = "?";
	private final static String F2 = "=";
	private final static String F3 = "&";
	private final static String F4 = "*";
	private final static String CALLBACK = "?callback=";
	private final static String XML_HTTP_REQUEST = "XMLHttpRequest";
	private final static String X_REQUESTED_WITH = "X-Requested-With";
	private final static String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	private final static String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	
	private String loginUrl = PlatFormConstant.LOGIN_URL;

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		securityCheck(req, res);

		chain.doFilter(request, response);

	}
	
	/**
	 * 检查当前页面是否需要登录.只有在不需要登录的列表中的地址才不用登陆.
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void securityCheck(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String url = request.getRequestURI();
		String host = (String) request.getSession().getAttribute(PlatFormConstant.DOMAIN_KEY);
		
		logger.info("[obm user request] 品牌过滤器访问请求验证: sessionId["+ request.getSession().getId() +"], url["+ host + this.assemblyRequest(request) +"]"); 
		
		List<String> noneLoginList = Lists.newArrayList();
		noneLoginList.add("/train/iphone/order!toSaveIndPageParams.action");
		noneLoginList.add("/train/iphone/train!queryList.action");
		noneLoginList.add("/train/iphone/train!getLoginStatue.action");
		noneLoginList.add("/train/cloudpos/train!getMaxDate.action");
		noneLoginList.add("/train/cloudpos/train!queryList.action");
		noneLoginList.add("/train/cloudpos/order!toSaveIndPageParams.action");
		if(CollectionUtils.isEmpty(noneLoginList) || !noneLoginList.contains(url)){
			userCheck(request, response);
		}
	}
	
	/**
	 * 判断用户是否登录,登录地址"https://login.99wuxian.com/wap/myaccount/loginIndex.do?callback="
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void userCheck(HttpServletRequest request, HttpServletResponse response) throws IOException{
		LoginUserBean user = (LoginUserBean) request.getSession().getAttribute(PlatFormConstant.LOGIN_USER_KEY);
		if (user == null) {
			if(StringUtils.equals(XML_HTTP_REQUEST, request.getHeader(X_REQUESTED_WITH))){
				response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, F4);
				response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, X_REQUESTED_WITH);
			}
			String host = (String) request.getSession().getAttribute(PlatFormConstant.DOMAIN_KEY);
			String redirectUrl = PlatFormConstant.HTTP + host + this.assemblyRequest(request);
			redirectUrl = URLEncoder.encode(redirectUrl, UTF_8);
			redirectUrl = BASE64Coding.encode(redirectUrl);
			response.sendRedirect(loginUrl + CALLBACK + redirectUrl);
			return;
		}
	}
	
	/**
	 * 拼装reuest的url参数.
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private String assemblyRequest(HttpServletRequest request){
		StringBuilder url = new StringBuilder();
		url.append(request.getRequestURI());
		url.append(F1);
		Enumeration params = request.getParameterNames();
		if (params != null && params.hasMoreElements()) {
			while (params.hasMoreElements()) {
				String name = (String) params.nextElement();
				String[] values = request.getParameterValues(name);
				for (int i = 0; i < values.length; i++) {
					url.append(name).append(F2).append(values[i]).append(F3);
				}
			}
		}
		url.setLength(url.length() - 1);
		return url.toString();
	}

}
