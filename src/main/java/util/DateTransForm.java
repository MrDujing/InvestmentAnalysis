package util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class DateTransForm {
    private String dateStr;
    private LocalDate dateLocalDate;
    private int dateCount;

    //DATE_BASE: for calculate date count from DATE_BASE, which 1978-01-01 correspond to 0.
    private static LocalDate DATE_BASE = LocalDate.parse("1978-01-01");
    private static LocalDate DATE_NOW = LocalDate.now(ZoneId.of("Asia/Shanghai"));

    public DateTransForm(String str) {
        dateStr = str;
        dateLocalDate = LocalDate.parse(dateStr);
        //Calculate dataCount from 1978-01-01, which 1978-01-01 correspond to 0.
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
        this(DATE_NOW);
    }

    public int getDateCount() {
        return dateCount;
    }

    public String getDateStr() {
        return dateStr;
    }

    public String getYesterdayStr() {
        return dateLocalDate.minusDays(1).toString();
    }

    public boolean isWeekend() {
        DayOfWeek weekDay = dateLocalDate.getDayOfWeek();
        return (weekDay == DayOfWeek.SATURDAY || weekDay == DayOfWeek.SUNDAY);
    }

    /**
     * Count quarter count from 1978-01-01, which include 1978.
     * @return Quarter count.
     */
    public int getQuarterCount() {
        int monthCount = (int) ChronoUnit.MONTHS.between(DATE_BASE, dateLocalDate) + 1;
        return (int)Math.ceil(monthCount / 3f );
    }
}
