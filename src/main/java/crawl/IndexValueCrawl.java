package crawl;

import cn.hutool.core.io.IoUtil;
import dao.IndexValueDao;
import form.IndexValueForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DateTransForm;
import util.IndexLibraryParser;
import util.InputValidate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class IndexValueCrawl {
    private Logger logger = LoggerFactory.getLogger(IndexValueCrawl.class);
    private Vector<IndexValueForm> indexValueForms = new Vector<>();
    private String fileName, indexCode;

    /**
     * @param fileName: csv filename, with relative path.
     */
    public IndexValueCrawl(String fileName) {
        this.fileName = fileName;
        indexCode = InputValidate.analysisIndexFileName(fileName);
    }

    private IndexValueCrawl() {
    }

    /**
     * Extract index value from csv file, and store data into database.
     *
     * @return true, if extract and store successfully; else false.
     * @throws IOException
     */
    public boolean storeIntoDatabase() throws IOException {
        if (null == indexCode) {
            logger.info("filename {} is invalid!!", fileName);
            return false;
        }

        //Read csv file.
        FileInputStream csvFileInput = IoUtil.toStream(new File(fileName));
        BufferedReader csvReaderUtf8 = IoUtil.getUtf8Reader(csvFileInput);
        Stream<String> allLines = csvReaderUtf8.lines();
        allLines.parallel().forEach(line ->
                {
                    IndexValueForm form = parseStrFromCSV(line);
                    if (null != form)
                        indexValueForms.add(form);
                }
        );
        logger.info("Extract data from {}, extract line is {}!", fileName, indexValueForms.size());

        //Close file
        allLines.close();
        csvReaderUtf8.close();
        csvFileInput.close();

        //Store into database.
        String tableName = new IndexLibraryParser().parseJson().getTableName(indexCode);
        int insertRows = new IndexValueDao(indexCode, tableName).storeIntoDatabase(indexValueForms);
        logger.info("Index value forms which insert into database, cnt is {}, code is {}, table is {}", insertRows, indexCode, tableName);
        if (insertRows >= 0) {
            File file = new File(fileName);
            StringBuilder newName = new StringBuilder(fileName);
            newName.insert(fileName.lastIndexOf('.'), "_read");
            file.renameTo(new File(newName.toString()));
            return true;
        } else
            return false;
    }
    //TODO, store all csv files , from directory.

    private IndexValueForm parseStrFromCSV(String str) {
        Pattern pattern = Pattern.compile("\\\"(\\d{4}-\\d{1,2}-\\d{1,2})\\\",\\\"([0-9.,]+)\\\",\\\"([0-9.,]+)\\\",\\\"([0-9.,]+)\\\",\\\"([0-9.,]+)\\\",\\\"([0-9,.BKM]*)\\\",\\\"([0-9.,%-]+)\\\"");
        Matcher matcher = pattern.matcher(str);
        IndexValueForm valueForm = new IndexValueForm();
        if (matcher.find()) {
            int date = new DateTransForm(matcher.group(1)).getDateCount();
            try {
                float open = NumberFormat.getInstance().parse(matcher.group(2)).floatValue();
                float close = NumberFormat.getInstance().parse(matcher.group(3)).floatValue();
                float high = NumberFormat.getInstance().parse(matcher.group(4)).floatValue();
                float low = NumberFormat.getInstance().parse(matcher.group(5)).floatValue();
                float trade = parseTradeVolume(matcher.group(6));
                float rate = NumberFormat.getInstance().parse(matcher.group(7)).floatValue();
                valueForm.setValueForm(indexCode, date, open, close, high, low, trade, rate);
            } catch (ParseException exception) {
                logger.error("Can not parse string {}!", matcher.group(0));
                exception.printStackTrace();
            }

        } else {
            logger.info("Can't parse string {}!", str);
            return null;
        }
        return valueForm;
    }

    /**
     * Translate trade volume to number, which unit is K.
     *
     * @param str
     * @return
     * @throws ParseException
     */
    private float parseTradeVolume(String str) throws ParseException {
        if ("".equals(str))
            return 0.0f;
        float number = NumberFormat.getInstance().parse(str).floatValue();
        char unit = str.charAt(str.length() - 1);
        switch (unit) {
            case 'K':
                break;
            case 'M':
                number *= 1000;
                break;
            case 'B':
                number *= 1000000;
                break;
            default: {
                if ('0' <= unit && unit <= '9')
                    number /= 1000;
                else
                    logger.warn("Trade volume is {}, which can not be parsed!", str);
            }
        }
        return number;
    }
}
