package utilTest;

import org.junit.Assert;
import org.junit.Test;
import util.DateTransForm;

public class DateTransFormTest {
    @Test
    public void testDate() {
        DateTransForm dateTransFormOne = new DateTransForm("2000-01-01");
        Assert.assertEquals(0,dateTransFormOne.getDateCount());

        DateTransForm dateTransFormTwo = new DateTransForm("2001-01-01");
        Assert.assertEquals(366, dateTransFormTwo.getDateCount());

        DateTransForm dateTransFormThree = new DateTransForm(366);
        System.out.println(dateTransFormThree.getDateStr());

        DateTransForm quarterTransOne = new DateTransForm("2000-03-31");
        System.out.println(quarterTransOne.getQuarterCount());

        DateTransForm quarterTransTwo = new DateTransForm("2001-01-01");
        System.out.println(quarterTransTwo.getQuarterCount());

    }
}
