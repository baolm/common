package com.handpay.obm.common.web.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.dubbo.config.annotation.Reference;
import com.handpay.core.user.bean.ChannelBean;
import com.handpay.core.user.exception.UserInfoBusiException;
import com.handpay.core.user.service.IUserChannelDubboService;
import com.handpay.obm.common.utils.PlatFormConstant;
import com.handpay.obm.common.utils.PlatFormType;

/**
 * 
 * 平台、渠道记录过滤器
 * 
 * @version 1.0 2011-12-23
 * @author pwang
 * @history
 * 
 */
public class PlatformChannelMobileFilter implements Filter {

	private Logger logger = Logger.getLogger(this.getClass());

	/** 渠道服务接口 */
	@Reference(version = "1.0.0")
	private IUserChannelDubboService userChannelService;

	private Map<String, ChannelBean> channelHost = new HashMap<String, ChannelBean>();

	private ChannelBean getChannelByHost(String host) {
		return this.channelHost.get(host);
	}

	private void putChannelHost(ChannelBean channel, String host) {
		this.channelHost.put(host, channel);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {

		HttpServletRequest request = (HttpServletRequest) req;

		setChannelInfo(request);

		chain.doFilter(req, res);
	}

	private void setChannelInfo(HttpServletRequest request) {

		if (request.getSession().getAttribute(PlatFormConstant.CHANNEL_KEY) != null) {
			return;
		}

		String host = request.getHeader(PlatFormConstant.HEADER_HOST);
		if (StringUtils.isBlank(host)) {
			host = request.getServerName();
		}
		logger.info("get channel host is:: " + host);

		ChannelBean channel = getChannelByHost(host);

		/**
		 * 获取channel的信息考虑通过一个服务获得 该服务通过域名查找对应的channel对象 查询服务可以使用缓存机制
		 */
		if (channel == null) {

			try {
				channel = userChannelService.queryChannelByHost(host);
				putChannelHost(channel, host);

				if (channel == null) {
					logger.error("empty channel of host: " + host);
					return;
				}
			} catch (UserInfoBusiException e) {
				logger.error(e, e);
			}
		}

		String platForm = null;
		if (StringUtils.isNotBlank(channel.getPlatForm())) {
			platForm = channel.getPlatForm();
		} else {
			platForm = PlatFormType.WAP.getCode();
		}
		request.getSession().setAttribute(PlatFormConstant.PLATFORM_KEY, platForm);
		request.getSession().setAttribute(PlatFormConstant.CHANNEL_KEY, channel.getChannel());
		request.getSession().setAttribute(PlatFormConstant.BUSINESS_CHANNEL_KEY, channel.getBusinessChannelCode());
		request.getSession().setAttribute(PlatFormConstant.DOMAIN_KEY, host);
		request.getSession().setAttribute(PlatFormConstant.INDEX_URL, channel.getIndexUrl());
	}
}
