package com.wd.util;

import org.springframework.util.StringUtils;
import org.apache.commons.httpclient.util.DateUtil;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DatetimeUtil {
	public final static String STANDARD_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public final static String STANDARD_DATE_PATTERN = "yyyy-MM-dd";
    static DateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
    static DateFormat dtfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
    static DateFormat tfd = new SimpleDateFormat("HH:mm");  
    static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
    static DateFormat shortdf = new SimpleDateFormat("yyyyMMdd");  
    static DateFormat year = new SimpleDateFormat("yyyy");  
    static DateFormat month = new SimpleDateFormat("MM");  
    static DateFormat day = new SimpleDateFormat("dd");  
    static DateFormat ym = new SimpleDateFormat("yyyy-MM"); 
	public static Timestamp currentTimestamp(){
		return new Timestamp(System.currentTimeMillis());
	}
	public static String currentDatetime(){
	    return DateUtil.formatDate(new Date());
	}
	public static Date parseDate(String dateStr) throws ParseException{
		return df.parse(dateStr);
	}
	public static Timestamp parseDateTime(String dateStr,String pattern) throws ParseException{
		DateFormat tf=new SimpleDateFormat(pattern);
		Date d = tf.parse(dateStr);
		return new Timestamp(d.getTime());
	}
	public static Date parseDateTime(String dateStr) throws ParseException{
		Date d = dtfd.parse(dateStr);
		return d;
	}
	public static Date parseTime(String timeStr) throws ParseException{
        Date d = tfd.parse(timeStr);
        return d;
    }
	public static Date parseDateTime2(String dateStr) throws ParseException{
	    Date d = dtf.parse(dateStr);
	    return d;
	}
	public static String formatDateTime(Date date) throws ParseException{
	    return dtfd.format(date);
    }
	public static Date parseBorn(String dateStr) throws ParseException{
		return df.parse(dateStr);
	}
	
	public static Timestamp getFutureTime(int month){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, month);
		return new Timestamp(c.getTimeInMillis());
	}
	
	public static String today(){
		return df.format(new Date());
	}
	
	public static String formatDate(Timestamp t, String pattern) {
        return formatDate(new Date(t.getTime()), STANDARD_DATE_PATTERN);
    }   

    public static String formatDate(Date date, String pattern) {
        if (date == null)
            return null;
        DateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
	
	public static String formatDate(Date d){
	    if (d == null)
	        return "";
		return df.format(d);
	}
	
	public static String formatShowDate(Date d){
        return dtf.format(d);
    }
	
	public static Date parseShortDate(String expire) throws ParseException {
		return shortdf.parse(expire);
	}
	public static String yyyymmdd(){
		return shortdf.format(new Date());
	}
	public static boolean beforeHour(int end, int errorInSecond){
	    Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY,end);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND, 0);
        long endT = c.getTime().getTime();
        
        long nowT = System.currentTimeMillis();
        
        if (nowT < (endT + errorInSecond*1000)){
            return true;
        } else {
            return false;
        }
	}
	
	public static Date getLastTime(){
//		Map<String,Date> map = new HashMap<String,Date>();
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		int dayOfWeek=calendar1.get(Calendar.DAY_OF_WEEK)-1; 
		
		int Fri = 5-dayOfWeek;
		
//		calendar1.add(Calendar.DATE, Fri-14);//上上周
		calendar2.add(Calendar.DATE, Fri-7);  //上周
		
//		calendar1.set(Calendar.HOUR_OF_DAY, 18);
//		calendar1.set(Calendar.MINUTE, 0);
		calendar2.set(Calendar.HOUR_OF_DAY, 14);
		calendar2.set(Calendar.MINUTE, 30);
		
//		map.put("laLaFriday", calendar1.getTime());
//		map.put("lastFriday", calendar2.getTime());
		return calendar2.getTime();
	}
	
	
    public static String yearOfBorn(int age) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int bornYear = year - age;
        c.set(Calendar.YEAR, bornYear);
        return df.format(c.getTime());
    }
    public static long parseDateInSecond(String dateStr) throws ParseException{
        Date d = df.parse(dateStr);
        return d.getTime()/1000;
    }
    public static long currentTimeInSecond() {
        return System.currentTimeMillis()/1000;
    }
    
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = { "日", "一", "二", "三", "四", "五", "六" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) w = 0;
        return weekDays[w];
    }

    /**
     * 获取过去第几天的日期  不包含今天
     *
     * @param past
     * @return
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        //获取昨天日期
        calendar.add(Calendar.DATE, -1);
        Date today = calendar.getTime();
        //Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }

    /**
     * 获取未来 第 past 天的日期  包含今天
     * @param past
     * @return
     */
    public static String getFetureDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }

    public static List<String> calendar(int day){
        ArrayList<String> pastDaysList = new ArrayList<>();
        ArrayList<String> fetureDaysList = new ArrayList<>();
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i <day; i++) {
            pastDaysList.add(getPastDate(i));
        }

        for (int i = 0; i <=day; i++) {
            fetureDaysList.add(getFetureDate(i));
        }
        stringList.addAll(pastDaysList);
        stringList.addAll(fetureDaysList);
        Collections.sort(stringList, new Comparator<String>(){
            @Override
            public int compare(String d1, String d2) {
                return d1.compareTo(d2);
            }
        });
        return stringList;
    }

    public static int getWeekdayIndex(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) w = 0;
        return w;
    }
    public static boolean isAM(Date d){
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return (hour<12);
    }
    public static Date todayBegin() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    public static Date tomorrowBegin() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    public static Date daysBefore(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 0-days);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    public static Date agoMonth(int offset) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 0-offset);
        return cal.getTime();
    }
    
    public static boolean isToday(Date date) {
        return todayBegin().equals(date);
    }
    public static Date nextDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH,1);
        return c.getTime();
    }
    public static Date afterDay(int i) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH,i);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    public static Date monthBegin() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    public static Date nextMonthBegin() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    public static void main(String[] args) throws ParseException {

        //System.out.println(parseDate());
    }
    public static int daysBetween(Date countStartDate, Date countEndDate) {
        long between_days=(countEndDate.getTime()-countStartDate.getTime())/(1000*3600*24);  
        return Integer.parseInt(String.valueOf(between_days));     
    }
    public static int year() {
        return Integer.valueOf(year.format(new Date()));
    }
    public static int year(Date d) {
        return Integer.valueOf(year.format(d));
    }
    public static int month() {
        return Integer.valueOf(month.format(new Date()));
    }
    public static int month(Date d) {
        return Integer.valueOf(month.format(d));
    }
    public static int day() {
        return Integer.valueOf(day.format(new Date()));
    }
    public static int day(Date d) {
        return Integer.valueOf(day.format(d));
    }
    public static Date parseYM(String str) throws ParseException {
        if (StringUtils.isEmpty(str))
            return null;
        return ym.parse(str);
    }
    public static Date nextMonth(int i) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, i);
        return cal.getTime();
    }
    public static String reviseWeekday(int weekper) {
        // 比特表示转换成
        return null;
    }
    public static Date parseDate(String str, String pattern) throws ParseException {
        if (StringUtils.isEmpty(str))
            return null;
        return new SimpleDateFormat(pattern).parse(str);
    }
    public static int computeAge(Date born){
        Date today = new Date();
        int year1 = year(today);
        int month1 = month(today);
        int day1 = day(today);
        int year2 = year(born);
        int month2 = month(born);
        int day2 = day(born);
        int year = year1 - year2;
        if (month1 > month2){
            year ++;
        } else if (month1 == month2){
            if (day1 > day2){
                year ++;
            }
        }
        return year;
    }
    /**
     * 只比较到秒，不比较毫秒
     * @param d1
     * @param d2
     * @param level 1比较到小时，2比较到分钟，3比较到秒，默认为3
     * @return, d1>d2 return 1, d1<d2 return -1, d1==d2 return 0;
     */
    
    public static int compareTime(Date d1, Date d2, int level){
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        int h1 = c1.get(Calendar.HOUR_OF_DAY);
        int h2 = c2.get(Calendar.HOUR_OF_DAY);
        int m1 = c1.get(Calendar.MINUTE);
        int m2 = c2.get(Calendar.MINUTE);
        int s1 = c1.get(Calendar.SECOND);
        int s2 = c2.get(Calendar.SECOND);
        if (level == 1){
            return compareInt(h1,h2);
        } else if (level == 2){
            int hR = compareInt(h1,h2);
            if (hR != 0){
                return hR;
            } else {
                return compareInt(m1,m2);
            }
        } else {
            int hR = compareInt(h1,h2);
            if (hR != 0){
                return hR;
            } else {
                int mR = compareInt(m1,m2);
                if (mR != 0){
                    return mR;
                } else {
                    return compareInt(s1, s2);
                }
            }
        }
    }
    
    private static int compareInt(int i1, int i2){
        if (i1 > i2) return 1;
        if (i1 < i2) return -1;
        return 0;
    }
    /**
     * 
     * @param d1
     * @param d2
     * @return d1-d2
     */
    public static String timeDiff(Date d1, Date d2){
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        c1.set(Calendar.YEAR, 1970);
        c2.set(Calendar.YEAR, 1970);
        c1.set(Calendar.MONTH, 1);
        c2.set(Calendar.MONTH, 1);
        c1.set(Calendar.DAY_OF_MONTH, 1);
        c2.set(Calendar.DAY_OF_MONTH, 1);
        
        long time1 = c1.getTime().getTime();  
        long time2 = c2.getTime().getTime();  
        long hour = 0;  
        long min = 0;  
        long diff ;  
        if(time1<time2) {  
            diff = time2 - time1;  
        } else {  
            diff = time1 - time2;  
        }  
        hour = (diff / (60 * 60 * 1000));  
        min = ((diff / (60 * 1000)) - hour * 60);  
        return hour + "小时" + min + "分";
    }
    public static Date datePart(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    public static Date add(Date d, int count, int unit) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(unit, count);
        return c.getTime();
    }

    public static Date computeLeaveEndDate(Date startDate, int ampmBegin, int duration) {
        Date end = null;
        if (ampmBegin == 1){
            int days = duration/2;
            if (days < 0){
                days = 0;
            }
            //从上午开始
            end = add(startDate, days, Calendar.DAY_OF_MONTH);
            if (duration%2 ==0){
                //再加1天
                end = add(end, -1, Calendar.DAY_OF_MONTH);
            }
        } else {
            int days = duration/2;
            end = add(startDate, days, Calendar.DAY_OF_MONTH);
        }
        return end;
    }
}
