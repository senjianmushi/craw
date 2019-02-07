package com.crrchz.crawl.tmallSpider;

import com.crrchz.crawl.util.MatcherUtil;
import lombok.Data;
import org.openqa.selenium.Cookie;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.util.List;

/**
 * @author lhj
 * @Description:
 * @Date: 2019/1/30 10:22
 */
@Data
public class TmallCommentCountSpider implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(0).setTimeOut(10000);


    private String head_url = "https://rate.tmall.com/list_detail_rate.htm?";  // head_url
    private String excel_path;
    public  static int pageTotal;

    @Override
    public void process(Page page) {
        if (page.getUrl().toString().startsWith(head_url)) {
            System.out.print("开始爬评论总条数: ");
            String handleRawText = MatcherUtil.aliJsonMatcher(page.getRawText());
//            System.out.print("handleRawText: ");
            List<String> content = new JsonPathSelector("$.rateDetail.rateCount.total").selectList(handleRawText);
            pageTotal = Integer.valueOf(content.get(0))/ 20;//多出来的在折叠页
            if(pageTotal == 0)pageTotal=1;
            System.out.println("实际评论条数:" + content.get(0));
            System.out.println("pageTotal[总页数]:" + pageTotal);
        }
    }

    @Override
    public Site getSite() {
        for(Cookie cookie:TmallLoginSpider.cookies) {
            site.addCookie(cookie.getName().toString(), cookie.getValue().toString());
        }
        return site;
    }
}
