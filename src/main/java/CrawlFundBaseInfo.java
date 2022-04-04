import com.alibaba.fastjson.JSON;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.util.HashSet;
import java.util.Set;

public class CrawlFundBaseInfo {
    public static String get(){
        //创建客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String entityr = "";
        //创建Get实例
        HttpGet httpGet = new HttpGet("http://fund.eastmoney.com/js/fundcode_search.js");

        //添加请求头的信息，模拟浏览器访问
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3573.0 Safari/537.36");

        try{
            //获得Response
            CloseableHttpResponse response = httpClient.execute(httpGet);

            if(response.getStatusLine().getStatusCode() == 200){
                //当响应状态码为200时，获得该网页源码并打印
                String entity = EntityUtils.toString(response.getEntity(),"utf-8");
                entityr = entity;
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return entityr;
    }

    public static void main(String[] args) {
        //获得响应的ajax，json格式的String
        String str = CrawlFundBaseInfo.get();
        String tempString = str.substring(9,1281514);

        final JSONArray obj = new JSONArray(tempString);

        Set<String> jsonSet = new HashSet<>();
        for (int i = 0; i < obj.length(); i++) {
            JSONArray array = obj.getJSONArray(i);
            String property = array.getString(3);
            jsonSet.add(property);
        }
        for(String temp : jsonSet) {
            System.out.println(temp);
        }
    }

}