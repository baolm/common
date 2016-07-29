/*
 * @(#)PlatFormType.java        1.0 2014-1-9
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

package com.handpay.obm.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Class description goes here.
 *
 * @version 	1.0 2014-1-9
 * @author		"lmbao"
 * @history	
 *		
 */
public enum PlatFormType {

	IPHONE("iphone", "iphoneƽ̨"),
	WAP("xhtml", "WAPƽ̨"),
	IPAD("ipad", "IPADƽ̨");
	
	private String code;
    private String desc;
    
    private PlatFormType (String code, String desc) {
    	this.code = code;
    	this.desc = desc;
    }

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
	
    public static PlatFormType valueOfCode(String code) {
		for (PlatFormType type : values()){
    		if(StringUtils.equals(code, type.getCode())){
    			return type;
    		}
    	}
    	return null;
    }
}
