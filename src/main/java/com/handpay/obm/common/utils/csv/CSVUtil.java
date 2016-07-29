package com.handpay.obm.common.utils.csv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.handpay.obm.common.utils.FileUtil;

public class CSVUtil{
	private static final Logger logger = LoggerFactory.getLogger(CSVUtil.class);
	
	
	public static void writeDataToFile(List<String[]> strData,
			String fileName,boolean isAppend,String charset)  throws Exception {
		FileUtil.appendData(new File(fileName), strData,charset);
	}
	
	public static void writeDataToFile(String data,
			String fileName,boolean isAppend,String charset)  throws Exception {
		
		FileUtil.writeWithTransferAppend(new File(fileName), data,charset,isAppend);
		
	}



	/*public static void writeDateToFile(String data, String fileName,
			boolean isAppend,String charset) throws Exception {
		FileUtil.writeData(new File(new String(fileName.getBytes(charset),charset)), data,charset);
	}
	*/
	

	public static void deleteFileIfExsits(String fileName) throws Exception {
		FileUtil.deleteFileIfExsits(fileName);
	}
	
}

