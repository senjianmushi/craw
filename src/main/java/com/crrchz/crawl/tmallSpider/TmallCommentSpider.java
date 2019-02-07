package com.crrchz.crawl.tmallSpider;

import com.crrchz.crawl.util.ExcelUtil;
import com.crrchz.crawl.util.MatcherUtil;
import com.google.common.collect.Lists;
import lombok.Data;
import org.openqa.selenium.Cookie;
import org.springframework.beans.factory.annotation.Value;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lhj
 * @Description:
 * @Date: 2019/1/30 10:22
 */
@Data
public class TmallCommentSpider implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(0).setTimeOut(10000);


    private String head_url = "https://rate.tmall.com/list_detail_rate.htm?";  // head_url
    private String excel_path;

    @Override
    public void process(Page page) {
        if (page.getUrl().toString().startsWith(head_url)) {
            //这里开始爬评论内容
            System.out.print("开始爬评论: ");
            String handleRawText = MatcherUtil.aliJsonMatcher(page.getRawText());
//            System.out.print("handleRawText: ");
//            System.out.println(handleRawText);

            List<String> rateContentList = new JsonPathSelector("$.rateDetail.rateList[*].rateContent").selectList(handleRawText);
            List<String> rateNameList = new JsonPathSelector("$.rateDetail.rateList[*].displayUserNick").selectList(handleRawText);
            List<String> commentTimeList = new JsonPathSelector("$.rateDetail.rateList[*].rateDate").selectList(handleRawText);
            List<Map<String, Object>> values = Lists.newArrayList();
            for (int i = 0; i < rateContentList.size(); i++) {
                Map<String, Object> map = new HashMap<>();
                map.put("用户名", rateNameList.get(i));
                map.put("评论时间", commentTimeList.get(i));
                map.put("评论内容", rateContentList.get(i));
                values.add(map);
//                page.putField(rateNameList.get(i) + "|" + commentTimeList.get(i) + i, rateContentList.get(i));
                System.out.println(rateNameList.get(i) + " :" + rateContentList.get(i));
            }
            List<String> titles = Lists.newArrayList();
            titles.add("用户名");
            titles.add("评论时间");
            titles.add("评论内容");
            //String path = "d://webmagic//webmagic2.xlsx";
            String name = "天猫内容" + new Date().getTime();
            ExcelUtil.writerExcel(excel_path, name, titles, values);
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
