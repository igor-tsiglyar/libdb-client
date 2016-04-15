package utils;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

public class Utils {

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1, str.length()).toLowerCase();
    }

    public static String randomNumberString(int size) {
        StringBuilder sb = new StringBuilder(size);
        Random rand = new Random();
        for( int i = 0; i < size; i++ )
            sb.append(rand.nextInt(9) + 1);
        return sb.toString();
    }

    public static int randomNumber(int upperBound) {
        Random rand = new Random();
        return rand.nextInt(upperBound) + 1;
    }

    public static Date randomDate() {
        Random rand = new Random();
        GregorianCalendar calendar = new GregorianCalendar(
                2015, rand.nextInt(2) + 10, rand.nextInt(29) + 1);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date advanceDate(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date todayDate() {
        return new Date(Calendar.getInstance().getTime().getTime());
    }
}