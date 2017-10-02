import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;


public class Crawler {
    public static void main(String args[])
    {
        String article="";
        String url="http://www.qingyunian.net/519.html";
        String next=url;
        while (next!=null)
        {
            String[] tmp=getContent(next);
            article+=tmp[0];
            next=tmp[1];
        }
        Main.WriteToFile(article,"庆余年2.txt");//调用Main函数里的写文件函数
    }
    //用String[]来保存每一章的信息，String[0]保存文章正文，String[1]保存下一章链接
    public static String[] getContent(String url)
    {
        String article="";
        String next="";
        String[]Content=null;
        try {
            Document doc = Jsoup.connect(url).get();
            article+=doc.getElementsByTag("title").text();
            article+="\r\n\r\n";
            Elements texts = doc.getElementsByTag("p");
            for(Element paragraph:texts)
            {
                if(paragraph.text().contains("上一章："))
                {
                    if(paragraph.text().contains("下一章："))
                    next=paragraph.getElementsByTag("a").attr("href");
                    else next=null;
                    break;
                }
                article+=paragraph.text();
                article+="\r\n\r\n";
            }
            String[] Content1={article,next};
            Content=Content1;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return Content;
    }
}
