package com.crrchz.crawl.controller;

import com.crrchz.crawl.tmallSpider.TmallCommentCountSpider;
import com.crrchz.crawl.tmallSpider.TmallCommentSpider;
import com.crrchz.crawl.tmallSpider.TmallLoginSpider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import us.codecraft.webmagic.Spider;

import java.util.Random;

/**
 * @author lhj
 * @Description:
 * @Date: 2019/1/30 20:12
 */
@Controller
public class tmallController {

    /**
     *
     * @param excelPath
     * @param spiderUrl
     * @return
     */
    @PostMapping("commentSpider")
    public ModelAndView commentSpider(String excelPath, String spiderUrl,String goodsName,String chromedriverPath){

        long startTime = System.currentTimeMillis();
        ModelAndView mv = new ModelAndView("download");
        TmallLoginSpider.goodsName = goodsName;
        TmallLoginSpider csdn = new TmallLoginSpider();
        //获取cookie
        csdn.Login(chromedriverPath);
        //获取商品信息
        Spider.create(csdn)
                .addUrl(spiderUrl)
                .thread(1)
                .run();

        //获取真正的评论总数
        TmallCommentCountSpider tmallCommentCountSpider = new TmallCommentCountSpider();
        Spider.create(tmallCommentCountSpider)
                .addUrl("https://rate.tmall.com/list_detail_rate.htm?itemId="+ TmallLoginSpider.itemId +"&currentPage=1&sellerId="+TmallLoginSpider.sellerId+"&order=3")
                .thread(1)
                .run();


        //循环爬评论并生成excel
        System.out.println("循环爬评论并生成excel");
        TmallCommentSpider commentSpider = new TmallCommentSpider();
        String excel_path = excelPath+"//"+TmallLoginSpider.goodsName+".xlsx";
        commentSpider.setExcel_path(excel_path);//d://webmagic//xxx.xlsx
        for(int i = 1;i <= TmallCommentCountSpider.pageTotal;i++){
            try {
                Thread.sleep(10000*(new Random().nextInt(5)+1));//10~10秒的停顿时间，看看可不可以停顿
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String newRequset = "https://rate.tmall.com/list_detail_rate.htm?itemId="+ TmallLoginSpider.itemId +"&currentPage="+i+"&sellerId="+TmallLoginSpider.sellerId+"&order=3";
            Spider.create(commentSpider)
                    .addUrl(newRequset)
                    .thread(1)
                    .run();
        }
        TmallLoginSpider.goodsName="";
        mv.addObject("excel_path",excel_path);
        long endTime = System.currentTimeMillis();
        System.out.println("爬虫结束-实际搜索数据有："+TmallCommentCountSpider.pageTotal+"条");
        System.out.println("爬虫结束：耗时"+(endTime-startTime)+"ms");
        System.out.println("爬虫结束：耗时"+((endTime-startTime)/60000)+"分左右");
        return mv;
    }

}
