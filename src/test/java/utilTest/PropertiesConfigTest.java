package utilTest;

import org.junit.Test;
import util.PropertiesConfig;

import java.util.HashMap;
import java.util.Map;

public class PropertiesConfigTest {
    @Test
    public void updatePropertiesTest() {
        PropertiesConfig propertiesConfig = new PropertiesConfig("./src/test/resources/test.properties", false);
        Map<String, String> mapCase = new HashMap<>();
        mapCase.put("dddd", "dddd1");
        mapCase.put("12","1111");
        propertiesConfig.updateProperties(mapCase);
    }
}
