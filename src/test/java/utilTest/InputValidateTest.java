package utilTest;

import org.junit.Assert;
import org.junit.Test;
import util.InputValidate;

public class InputValidateTest {
    @Test
    public void testAnalysisIndexFileName() {
        String result = InputValidate.analysisIndexFileName("./input/HS300_20100101-20220930.csv");
        Assert.assertEquals("HS300",result);
    }
}
