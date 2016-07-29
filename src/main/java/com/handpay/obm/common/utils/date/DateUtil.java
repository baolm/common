package com.handpay.obm.common.utils.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.alibaba.dubbo.common.utils.StringUtils;

public class DateUtil {
  public static final String TIME_PATTERN_MILLISECOND = "yyyyMMddHHmmssSSS";
  public static final String TIME_PATTERN = "yyyyMMddHHmmss";
  public static final String DATA_PATTERN = "yyyyMMdd";
  public static final String DATA_LONG_PATTERN = "yyyy-MM-dd";
  public static final String TIME_LONG_PATTERN = "yyyy-MM-dd HH:mm:ss";

  /**
   * 获取date类型日期
   * @param pattern
   * @param str
   * @return
   * @throws ParseException
   */
  public static Date parseToDate(String pattern, String str)
    throws ParseException
  {
    if (str == null) {
      return null;
    }

    SimpleDateFormat formatter = new SimpleDateFormat(pattern);
    formatter.setLenient(false);
    return formatter.parse(str);
  }

  /**
   * 获取字符类型时间
   * @param pattern
   * @param str
   * @return
   * @throws ParseException
   */
  public static String parseFormatDate(String pattern, Date str)
    throws ParseException
  {
    if (str == null) {
      return null;
    }

    SimpleDateFormat formatter = new SimpleDateFormat(pattern);
    formatter.setLenient(false);
    return formatter.format(str);
  }
  
  /**
   * 获取字符类型时间
   * @param pattern
   * @param str
   * @return
   * @throws ParseException
   */
  public static String parseFormatDate(String str,String oldPattern, String newPatter)
    throws ParseException
  {
    if (StringUtils.isEmpty(str) || StringUtils.isEmpty(oldPattern) || StringUtils.isEmpty(newPatter)) {
      return null;
    }
    Date date=parseToDate(oldPattern,  str);
    
    return parseFormatDate(newPatter,date);
  }
  
  /** 
   * 获取当前时间+num天的日期
   * @param num
   * @return
   */
  public static String getAssignDate(int num){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, num);
		try {
			return parseFormatDate(DateUtil.DATA_LONG_PATTERN, cal.getTime());
		} catch (ParseException e) {
		}
		return null;
	}
  
  /**
	 * 根据基准时间加上天数,得到新的时间
	 * 
	 * @param baseDate
	 *            基准时间
	 * @param days
	 *            天数
	 * @return
	 */
	public static Date addDays(Date baseDate, int days) {
		Calendar expiration = Calendar.getInstance();
		expiration.setTime(baseDate);
		expiration.add(Calendar.DAY_OF_MONTH, days);

		return expiration.getTime();
	}
	
	/**
	 * 根据基准时间加上天数,得到新的时间
	 * 
	 * @param baseDate
	 *            基准时间
	 * @param days
	 *            天数
	 * @return
	 */
	public static Date addMonth(Date baseDate, int month) {
		Calendar expiration = Calendar.getInstance();
		expiration.setTime(baseDate);
		expiration.add(Calendar.MONTH, month);

		return expiration.getTime();
	}
	
	/**
	 * 根据基准时间加上天数,得到新的时间
	 * @param date
	 * @param pattern
	 * @param days
	 * @return
	 * @throws ParseException
	 */
	public static Date addDays(String date,String pattern, int days) throws ParseException {
			return addDays(parseToDate( pattern,  date),days);
	}
	
	/**
	 * 根据基准时间加上天数,得到新的时间
	 * @param date
	 * @param pattern
	 * @param days
	 * @return
	 * @throws ParseException
	 */
	public static Date addMonths(String date,String pattern, int days) throws ParseException {
			return addMonth(parseToDate( pattern,  date),days);
	}
  
  public static void main(String[] args){
	  
	 try {
		 String beforeMonthFirstDay1=DateUtil.parseFormatDate("yyyyMM",DateUtil.addMonths("20160112000000","yyyyMMddHHmmss",1*-1))+"01";
		 String beforeMonthFirstDay=DateUtil.parseFormatDate("yyyyMM",DateUtil.addDays("20160112000000","yyyyMMddHHmmss",2*-1))+"01";
			System.out.println("beforeMonthFirstDay1="+beforeMonthFirstDay1+"beforeMonthFirstDay"+beforeMonthFirstDay);
		 System.out.println(DateUtil.parseFormatDate("2016-01-06 18:05:01","yyyy-MM-dd HH:mm:ss","yyyyMM"));
		System.out.println(parseFormatDate("yyyyMM",addDays(new Date(),-6)));
		
		System.out.println(parseFormatDate("yyyy/MM/dd HH:mm:ss",new Date()));
		System.out.println(parseFormatDate("yyyy/MM/dd HH:mm:ss",addDays("2016-01-06","yyyy-MM-dd",-6)));
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}
