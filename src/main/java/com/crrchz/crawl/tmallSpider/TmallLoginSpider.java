package com.crrchz.crawl.tmallSpider;
import lombok.Data;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.*;

/**
 * @author lhj
 * @Description:
 * @Date: 2019/1/28 14:40
 */
@Data
public class TmallLoginSpider implements PageProcessor{
    public static Set<Cookie> cookies = null;
    private int pageTotal = -1;
    private Site site = Site.me().setRetryTimes(3).setSleepTime(0).setTimeOut(10000);
    public static String itemId;    // 商品编号
    public static String sellerId;  // 估计是卖家信息
    public static String goodsName;  // 商品名称

    private String head_url = "https://rate.tmall.com/list_detail_rate.htm?";  // head_url
    private String detail_url = "https://detail.tmall.com/item.htm";






//            List<String> content = new JsonPathSelector("$.rateDetail.rateCount.total").selectList(handleRawText);
//            if(pageTotal == -1){
//                pageTotal = Integer.valueOf(content.get(0))/ 20;//多出来的在折叠页
//            }

    /**
     * 这次爬虫的目标是要获得itemId，sellerId
     * @param page
     */
    @Override
    public void process(Page page) {
        System.out.println("开始获取商品号和卖家号");
        String tempStr = page.getHtml().regex("itemId:(.*?),shopId").toString();
        System.out.println(tempStr);
        itemId = tempStr.substring(tempStr.indexOf("\"")+1,tempStr.indexOf("sellerId")-2);
        System.out.println("itemid: "+itemId);
        sellerId = tempStr.substring(tempStr.indexOf("sellerId:")+"sellerId:".length()+1,tempStr.length()-1);
        System.out.println("sellerId: "+sellerId);
        if (goodsName.equals("")){
            goodsName = page.getHtml().xpath("//div[@class='tb-detail-hd']/h1/text()").toString();
        }
        System.out.println("goodsName: "+goodsName);
    }

    @Override
    public Site getSite() {
        // TODO Auto-generated method stub
        for(Cookie cookie:cookies) {
            site.addCookie(cookie.getName().toString(), cookie.getValue().toString());
        }
        return site;
    }

    /**
     * 模拟登陆
     */
    public void Login(String chromedriverPath) {
//        System.setProperty("webdriver.chrome.driver",
//                "D:\\ProgramFiles\\chromedriver_win32\\chromedriver.exe");
        try{
            System.setProperty("webdriver.chrome.driver",chromedriverPath);
            WebDriver driver = new ChromeDriver();
            driver.get("https://login.taobao.com/member/login.jhtml");
            try {
                // 防止页面未能及时加载出来而设置一段时间延迟
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cookies=driver.manage().getCookies();
            //关闭浏览器
            driver.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("谷歌插件地址错误");
        }
    }




//    public static void main(String[] args) {
//        TmallLoginSpider csdn = new TmallLoginSpider();
//        //获取cookie
//        csdn.Login();
//        //获取商品信息
//        Spider.create(csdn)
//                .addUrl("https://detail.tmall.com/item.htm?id=566596019398")
//                .thread(1)
//                .run();
//
//        //获取真正的评论总数
//        TmallCommentCountSpider tmallCommentCountSpider = new TmallCommentCountSpider();
//        Spider.create(tmallCommentCountSpider)
//                .addUrl("https://rate.tmall.com/list_detail_rate.htm?itemId="+ itemId +"&currentPage=1&sellerId="+sellerId+"&order=3")
//                .thread(1)
//                .run();
//
//        TmallCommentSpider commentSpider = new TmallCommentSpider();
//        commentSpider.setExcel_path("d://webmagic//"+goodsName+".xlsx");
//        for(int i = 1;i <= TmallCommentCountSpider.pageTotal;i++){
//           String newRequset = "https://rate.tmall.com/list_detail_rate.htm?itemId="+ itemId +"&currentPage="+i+"&sellerId="+sellerId+"&order=3";
//            Spider.create(commentSpider)
//                    .addUrl(newRequset)
//                    //.addUrl("https://rate.tmall.com/list_detail_rate.htm?currentPage=1&itemId=566596019398&spuId=846818316&sellerId=3860666361&order=3&append=0&content=1&tagId=&posi=&picture=&groupId=&ua=098%23E1hv1vvZvLUvjQCkvvvvvjiPR2LUtjEVR2SpsjivPmP9tjlnRLMhQj3CRLFOzj6tvpvhvvvvvUhCvv147tcMlr147DitDn%2FrvpvEvvkmvk9IvVo3CQhvCli4zYMwcrgjvpvj7DdNzYQJPu6CvvyvmhUmB6wvjHTjvpvhvvpvvvwCvva47rMNzn1MRphvCvvvvvvCvpvVvmvvvhCvmphvLvpSEvvjafevD76wd56OfJCldChzOyTxfJCl5dUfUz7%2Bkj6%2FR5hNNOkQD40OjomxfJClHqyQc8cBIU9BDVQEfwClYb8rwoAgAXcX%2BFetvpvIvvvvvhCvvvvvvUUdphvvl9vv9krvpvQvvvmm86CvmVWvvUUdphvUOgyCvvOUvvVva6JivpvUvvmvn2zd%2BkOPvpvhMMGvv8wCvvpvvUmmRphvCvvvvvvjvpvj7DdNzYQYOFyCvvpvvvvvRphvCvvvvvv%3D&needFold=0&_ksTS=1548299455869_1137&callback=jsonp1138")
//                    .addPipeline(new JsonFilePipeline("D:\\webmagic"))
//                    .thread(1)
//                    .run();
//        }
//    }

}
