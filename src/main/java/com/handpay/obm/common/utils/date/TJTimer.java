package com.handpay.obm.common.utils.date;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

public class TJTimer {
	private Logger logger = Logger.getLogger(getClass());

	private ConcurrentHashMap<String, AtomicLong> timer = new ConcurrentHashMap<String, AtomicLong>();
//	private int count = 0;
	public long tongji(String methodName,long time){
		long result = 0L;
		AtomicLong  totalTime= this.timer.get(methodName);
		if(totalTime == null){
			this.timer.put(methodName, new AtomicLong(time));
			result = time;
		}else{
			result = totalTime.addAndGet(time);
			this.timer.put(methodName, totalTime);
		}
//		count++;
		logger.info("["+methodName+"]本次执行时间: "+time);
//		if(count%100==0){
//			logger.info("["+methodName+"]累计100次执行时间: "+result);
//		}
		return result;
	}
}
