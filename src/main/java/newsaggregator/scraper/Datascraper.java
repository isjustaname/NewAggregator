package newsaggregator.scraper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import newsaggregator.data.article.TableData;

public class Datascraper {
    static String inputFilePath = "src/main/resources/newsaggregator/url.txt"; // Tên file nhập vào
    static String outputFilePath = "src/main/resources/newsaggregator/data.json"; // Tên file xuất ra
    private final List<TableData> dataList = new ArrayList<>();

    public void scrapeData() {
        try {
            // Tạo kết nối đọc với file Url.txt 
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            
            String line;
            while ((line = reader.readLine()) != null) {
                String url = line.trim(); // Lấy Url trên từng dòng
                
                // Tạo kết nối HTTP đến Url mục tiêu
                Document doc = Jsoup.connect(url).get();
                
                // Lấy các phần tử cần thiết (<<<<<<<<<<<< Mày sửa chỗ này >>>>>>>>>>>>>>)
                String web_url = url.substring(0, url.indexOf(".com")+5);
                Elements type = doc.select("meta[property $= og:type]");
                Elements description = doc.select("meta[name = description]");
                Elements title = doc.select("meta[property $=title]");  
                Elements contents = doc.select("div#maincontent p, article.fck_detail p, section.at-body p");
                Elements author = doc.select("meta[property $= author],meta[name $=author]");
                Elements create_date = doc.select("meta[property $= published_time], meta[name $= datePublished]");
                Elements tag = doc.select("meta[property $= tag],meta[name $= keywords]");
                
               // Tạo đối tượng String lưu từng phần của content
               String contentText = " ";
               for (Element content: contents){
                   contentText = contentText.concat(content.text()+"\n");
               }
               // Tạo đối tượng ScrapedData và đẩy vào dataList 
               TableData data = new TableData(url, web_url, type.attr("content"), description.attr("content"), title.attr("content"),  contentText, create_date.attr("content"), tag.attr("content"), author.attr("content"));
               dataList.add(data);
            }
            
            reader.close();

            Gson gson = new GsonBuilder().disableHtmlEscaping().create();

            // Tạo kết nối viết với file Output.json 
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
            writer.write("[");
            for (TableData Data : dataList) {
                // Chuyển từng phần tử Data trong dataList thành JSON
                String jsonData = gson.toJson(Data);
                writer.write(jsonData);
                if (dataList.get(dataList.size() -1) != Data) writer.write(",");
                writer.newLine(); // Xuống dòng
            }
            writer.write("]");
            writer.close();  
            System.out.println("Scraping completed: " + outputFilePath);
        } catch (IOException e) {
            System.out.println("Program failed to send HTTP request due to internet loss");
        }
    }
}