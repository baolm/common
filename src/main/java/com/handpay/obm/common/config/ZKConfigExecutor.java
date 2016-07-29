/*
 * @(#)ZKConfigCenter.java        1.0 2016-2-19
 *
 * Copyright (c) 2007-2016 Shanghai Handpay IT, Co., Ltd.
 * 16/F, 889 YanAn Road. W., Shanghai, China
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * Shanghai Handpay IT Co., Ltd. ("Confidential Information").  
 * You shall not disclose such Confidential Information and shall use 
 * it only in accordance with the terms of the license agreement you 
 * entered into with Handpay.
 */

package com.handpay.obm.common.config;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.handpay.obm.common.exception.ConfigException;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.recipes.cache.PathChildrenCache;
import com.netflix.curator.framework.recipes.cache.PathChildrenCacheEvent;
import com.netflix.curator.framework.recipes.cache.PathChildrenCacheListener;
import com.netflix.curator.framework.state.ConnectionState;
import com.netflix.curator.framework.state.ConnectionStateListener;
import com.netflix.curator.retry.RetryNTimes;

/**
 * ≈‰÷√÷––ƒ
 * 
 * @version 1.0 2016-2-19
 * @author "lmbao"
 * @history
 * 
 */
@Component("ZKConfigExecutor")
public class ZKConfigExecutor implements InitializingBean, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(ZKConfigExecutor.class);

	private static final String DEFAULT_NAMESPACE = "config";

	private static final int DEFAULT_SESSION_TIMEOUT = 15000;

	private static final int DEFAULT_CONNECTION_TIMEOUT = 30000;

	private static final String ZK_ERROR_CODE = "CE_ZK_001";

	@Value("${conf.zookeeper.appCode}")
	private String appCode;

	@Value("${conf.zookeeper.connectServer}")
	private String connectServer;

	@Value("${conf.zookeeper.connectionTimeout}")
	private int connectionTimeout;

	@Value("${conf.zookeeper.sessionTimeout}")
	private int sessionTimeout;

	private CuratorFramework client;
	private PathChildrenCache childrenCache;

	public void afterPropertiesSet() throws Exception {
		logger.info("init ZKConfigExecutor ...");
		if (StringUtils.isBlank(connectServer)) {
			throw new IllegalArgumentException("connectServer not exist!!!");
		}
		if (StringUtils.isBlank(appCode)) {
			throw new IllegalArgumentException("appCode not exist!!!");
		}
		if (connectionTimeout <= 0) {
			connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
		}
		if (sessionTimeout <= 0) {
			sessionTimeout = DEFAULT_SESSION_TIMEOUT;
		}
		logger.debug("appCode: {}", appCode);
		logger.debug("connectServer: {}", connectServer);
		logger.debug("connectionTimeout: {}", connectionTimeout);
		logger.debug("sessionTimeout: {}", sessionTimeout);

		client = CuratorFrameworkFactory.builder() // builder
				.connectString(connectServer) // use brandzk
				.connectionTimeoutMs(connectionTimeout) // default 30000ms
				.sessionTimeoutMs(sessionTimeout) // default 15000ms
				.namespace(DEFAULT_NAMESPACE) // config
				.retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000)) //
				.build(); // build

		client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
			@Override
			public void stateChanged(CuratorFramework client, ConnectionState newState) {

				logger.info("ZKConfigExecutor Connection State Changed: ", newState.toString());
				if (newState == ConnectionState.LOST) { // session lost
					// ...
				}
			}
		});
		client.start();
		// try {
		// client.getZookeeperClient().blockUntilConnectedOrTimedOut();
		// } catch (InterruptedException e) {
		// logger.error("zk error: ", e);
		// throw new ConfigException(ZK_ERROR_CODE, e);
		// }

		try {
			if (client.checkExists().forPath(appCode) == null) {
				logger.info("create zk node: {}", appCode);
				client.create()//
						.creatingParentsIfNeeded()//
						.withMode(CreateMode.PERSISTENT)//
						.forPath(appCode);
			} else { // init props
				List<String> childs = client.getChildren().forPath(appCode);
				for (String cnode : childs) {
					String path = "/" + appCode + "/" + cnode;
					byte[] data = client.getData().forPath(path);
					System.setProperty(path, new String(data));
				}
			}
		} catch (Exception e) {
			logger.error("zk error: ", e);
			throw new ConfigException(ZK_ERROR_CODE, e);
		}

		addChildrenListener();

		ConfigUtil.init(appCode);
		logger.info("init ZKConfigExecutor ok!");
	}

	@Override
	public void destroy() throws Exception {
		if (childrenCache != null) {
			childrenCache.close();
		}
		if (client != null) {
			client.close();
		}
	}

	private void addChildrenListener() {

		childrenCache = new PathChildrenCache(client, appCode, false);

		try {
			childrenCache.start();
		} catch (Exception e) {
			logger.error("zk error: ", e);
			throw new ConfigException(ZK_ERROR_CODE, e);
		}
		childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				String path = event.getData().getPath();
				String data = new String(event.getData().getData());
				logger.info("config node change: {},{},{}", event.getType(), path, data);
				switch (event.getType()) {
				case CHILD_ADDED:
					System.setProperty(path, data);
					break;
				case CHILD_REMOVED:
					System.clearProperty(path);
					break;
				case CHILD_UPDATED:
					System.setProperty(path, data);
					break;
				default:
					break;
				}
			}
		});
	}

	public String getConnectServer() {
		return connectServer;
	}

	public void setConnectServer(String connectServer) {
		this.connectServer = connectServer;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

}
