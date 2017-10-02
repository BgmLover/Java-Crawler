import org.jsoup.Jsoup;

import java.io.*;
import java.net.*;
import java.util.regex.*;

public class Main {
    public static void main(String args[]) {
        String url = "http://www.qingyunian.net/519.html";
        String charsetname = "utf-8";//该小说网站采用utf-8编码方式
        String FileName = "庆余年.txt";
        getArticle(url,charsetname,FileName);
        Crawler a=new Crawler();
    }
    //主函数，从网上爬取整本小说以txt格式保存
    static void getArticle(String url,String charsetname,String FileName)
    {
        String article="";
        String result=getUrlContent(url,charsetname);//获得Url对应网页的HTML内容
        String pattern = "下一章：<a href=\"[^\\s\"]*";//利用正则表达式获得下一章小说的链接
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(result);
        article+=Extract(result);//获得第一章小说的正文
        String next=null;//next保存下一章的Url
        while (m.find())    //这里用一个循环来爬取到所有章节的正文
        {
            next=m.group().substring(13);
            //System.out.println(next);
            result=getUrlContent(next,charsetname);
            m = r.matcher(result);
            article+=Extract(result);//把对应章节附加在前一章后
            article+="\r\n\r\n";
        }

        if(WriteToFile(article,FileName))
        {
            System.out.println("Success");
        }
        else System.out.println("Failure");
    }
    //通过已知url获得网页HTML的内容
    static String getUrlContent(String url,String charsetname)
    {
        String result="";
        BufferedReader in=null;

        try
        {
            URL realUrl=new URL(url);
            URLConnection connection=realUrl.openConnection();
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(),charsetname));
            String Line;
            while((Line=in.readLine())!=null)
            {
                result+=(Line+"\n");//按行读取HTML内容
            }
        }
        catch (IOException e)
        {
            System.out.println("Error !");
            e.printStackTrace();
        }
        finally {
            try{
                if(in!=null)
                    in.close();//关闭输入流
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }
    //把String内容写到文件里
    public static boolean WriteToFile(String result,String FileName)
    {
        try{
            OutputStream os=new FileOutputStream(FileName);
            OutputStreamWriter fos=new OutputStreamWriter(os);
            result = result.replaceAll("\n","\r\n");//更换换行符
            fos.append(result);
            fos.close();
            os.close();
        }
        catch (IOException e)
        {
            System.out.println("Exception");
            return false;
        }
        return true;
    }
    //用到正则表达式把HTML里的纯小说内容提取出来
    static String Extract(String result)
    {
        String article;
        String pattern0="title>";//提取标题
        String pattern1="<p>[^a]*</p>";//提取含小说的部分
        String pattern2="[^<>p&nbsr;/*]*";//去除多余的字符
        Pattern r0 = Pattern.compile(pattern0);
        Matcher m0 = r0.matcher(result);
        int s=0,e=0;
        if(m0.find())
            s=m0.end();
        if(m0.find())
            e=m0.start();
        article=result.substring(s,e-2);
        article+="\n";
        Pattern r1 = Pattern.compile(pattern1);
        Matcher m1 = r1.matcher(result);
        if(m1.find())
            result=m1.group();
        Pattern r2 = Pattern.compile(pattern2);
        Matcher m2=r2.matcher(result);
        while (m2.find())
            article+=m2.group();
        return article;
    }
}

