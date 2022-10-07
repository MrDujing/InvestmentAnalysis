package utilTest;

import org.junit.Assert;
import org.junit.Test;
import util.IndexLibraryParser;

public class IndexLibraryParserTest {
    @Test
    public void getTableNameTest() {
        String result = new IndexLibraryParser().parseJson().getTableName("CNT");
        Assert.assertEquals("index_value_chn",result);
    }
}
