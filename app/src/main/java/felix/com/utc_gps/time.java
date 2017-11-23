package felix.com.utc_gps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by student on 2017/10/13.
 */

public class time {

    private static int UTC_delta_ms = 0;

    public static void setUTC_delta_ms(int UTC_delta_ms) {
        time.UTC_delta_ms = UTC_delta_ms;
    }

    public static int getUTC_delta_ms() {
        return UTC_delta_ms;
    }

    //    public static final SimpleDateFormat sdf = new SimpleDateFormat("HH時mm分");//定義好時間字串的格式
    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH時mm分ss秒SSS毫秒");//定義好時間字串的格式

    // ====== 截止時間換算 ======
    public static Calendar string2calendar(String t) {
        Calendar cal = Calendar.getInstance(); // 取得目前時間
        try {
            Date dt = sdf.parse(t);                              //將字串轉成Date型
            cal.setTime(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }

    public static Calendar string2calendar(String t, String format, String zone) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone(zone));
        Calendar cal = Calendar.getInstance(); // 取得目前時間
        try {
            Date dt = sdf.parse(t);                              //將字串轉成Date型
            cal.setTime(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }

    public static Calendar systime() {
        return stamp2calendar(System.currentTimeMillis());
    }

    public static Calendar adjtime(Calendar cal) {
        cal.add(Calendar.MILLISECOND, UTC_delta_ms); //調整誤差
        return cal;
    }

    public static Calendar nowtime() {
        Calendar cal = adjtime(systime()); // 取得校正時間
        return cal;
    }

    public static Calendar settime(int hh, int mm) {
        Calendar cal = nowtime(); // 取得目前時間

        cal.set(Calendar.HOUR_OF_DAY, hh);
        cal.set(Calendar.MINUTE, mm);
//        cal.add(Calendar.HOUR, hh);        //小時+hh
//        cal.add(Calendar.MINUTE, mm);      //分+mm
        return cal;
    }

    public static String calendar2string(Calendar cal) {
        Date d = cal.getTime();
        String dateStr = sdf.format(d);
        return dateStr;
    }

    public static Calendar stamp2calendar(long stamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(stamp);
        return cal;
    }

    public static long calendar2stamp(Calendar cal) {
        return cal.getTimeInMillis();
    }

}
