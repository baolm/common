/*
 * @(#)HotelBaseException.java        1.0 2013-11-30
 *
 * Copyright (c) 2007-2013 Shanghai Handpay IT, Co., Ltd.
 * 16/F, 889 YanAn Road. W., Shanghai, China
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * Shanghai Handpay IT Co., Ltd. ("Confidential Information").  
 * You shall not disclose such Confidential Information and shall use 
 * it only in accordance with the terms of the license agreement you 
 * entered into with Handpay.
 */

package com.handpay.obm.common.exception;

/**
 * Class description goes here.
 * 
 * @version 1.0 2013-11-30
 * @author "lmbao"
 * 
 */
public class ObmBaseException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3565041668191154037L;

	/**
	 * �쳣������룬ʹ��4λ�ַ����� ��һλ��������쳣��ϵͳ���� ����λ�������Ĵ�����뺬�� ��������ɾ���ĳ�������
	 */
	protected String errorCode;

	/** �쳣������Ϣ����ʵ���׳��쳣���ඨ�� */
	protected String errorMsg;

	public ObmBaseException(String errorCode, String errorMsg) {
		super(errorMsg);

		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public ObmBaseException(String errorCode, Throwable caused) {
		super(caused);

		this.errorCode = errorCode;
	}

	public ObmBaseException(String errorCode, String errorMsg,
			Throwable caused) {
		super(errorMsg, caused);

		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	/**
	 * ����쳣�Ĵ������
	 * 
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * ����쳣�Ĵ�����Ϣ
	 * 
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}
}
