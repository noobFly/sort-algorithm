package com.noob.repayPlan;

import java.util.Calendar;
import java.util.Date;

public interface TimeUtil {
	public static int getBetweenDays(Date fDate, Date sDate) {
		return (int) ((fDate.getTime() - sDate.getTime()) / 86400000L);// (24小时 * 60分 * 60秒 * 1000毫秒= 1天毫秒数)
	}

	/**
	 * 取日期当天的开始时间，即0时0分0秒
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateBegin(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * 取日期当天的结束时间，即23时59分59秒
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateEnd(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
}
