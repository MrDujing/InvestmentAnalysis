package util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * Transfer date between String, LocalDate and int.
 */
public class DateTransForm {
    private String dateStr;
    private LocalDate dateLocalDate;
    private int dateCount;

    //DATE_BASE: for calculate date count from DATE_BASE, which 2000-01-01 correspond to 0.
    private static LocalDate DATE_BASE = LocalDate.parse("2000-01-01");

    public DateTransForm(String str) {
        dateStr = str;
        dateLocalDate = LocalDate.parse(dateStr);
        //Calculate dataCount from 2000-01-01, which 2000-01-01 correspond to 0.
        //startDate inclusive, endDate exclusive.
        dateCount = (int) ChronoUnit.DAYS.between(DATE_BASE, dateLocalDate);
    }

    public DateTransForm(int count) {
        dateCount = count;
        dateLocalDate = DATE_BASE.plusDays(dateCount);
        dateStr = dateLocalDate.toString();
    }

    public DateTransForm(LocalDate local) {
        dateLocalDate = local;
        dateStr = dateLocalDate.toString();
        dateCount = (int) ChronoUnit.DAYS.between(DATE_BASE, dateLocalDate);
    }

    public DateTransForm() {
        this(LocalDate.now(ZoneId.of("Asia/Shanghai")));
    }

    public int getDateCount() {
        return dateCount;
    }

    public String getDateStr() {
        return dateStr;
    }

    /**
     * Count quarter count from 2000-01-01, 2000/1/1-2000/3/31 correspond to quarter 0.
     * @return Quarter count.
     */
    public int getQuarterCount() {
        int monthCount = (int) ChronoUnit.MONTHS.between(DATE_BASE, dateLocalDate);
        return (int)Math.floor(monthCount / 3f );
    }
}
