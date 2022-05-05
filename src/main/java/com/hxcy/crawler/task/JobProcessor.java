package com.hxcy.crawler.task;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hxcy.crawler.entity.Item;
import com.hxcy.crawler.entity.MethodEnum;
import com.hxcy.crawler.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Selectable;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author kevin
 * @date 2022/4/28
 * @desc
 */
@Slf4j
@Component
public class JobProcessor implements PageProcessor {

    /**
     * 爬取网址
     */
    @Value("${crawler.url}")
    private String URL;

    /**
     * 阈值
     */
    private Double num = 1.0;

    private static Cache<String, Optional<Item>> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(24, TimeUnit.HOURS)
            .recordStats()
            .build();

    /**
     * 处理下载后的页面逻辑
     *
     * @param page
     */
    @Override
    public void process(Page page) {
        //1.创建存放封装item 的容器
        ArrayList<Item> itemList = new ArrayList<>();
        //2.解析Html 数据
        List<Selectable> selectables = page.getHtml().css("tbody").css("tr").nodes();
        if (selectables.size() == 0) {
            log.warn("no data....");
        } else {
            //封装数据
            for (Selectable selectable : selectables) {
                List<Selectable> tdNodes = selectable.css("td").nodes();
                Item item = new Item();
                item.setTxnHash(tdNodes.get(1).css("a.myFnExpandBox_searchVal", "text").toString());
                item.setMethod(tdNodes.get(2).css("span", "text").toString());
                item.setBlock(Long.parseLong(tdNodes.get(3).css("a", "text").toString()));
                item.setAge(tdNodes.get(4).css("span", "text").toString());
                item.setFrom(tdNodes.get(6).css("a", "text").toString());
                item.setTo(tdNodes.get(8).css("a", "text").toString());
                item.setValue(StringUtil.getValue(tdNodes.get(9).css("td").toString()));
                item.setTxnFee(BigDecimal.valueOf(Double.parseDouble(StringUtil.getValue(tdNodes.get(10).css("span").toString()))));

                if (item.getTxnHash() != null & item.getValue().contains("Ether")) {
                    if (Double.parseDouble(item.getValue().replaceAll(" Ether", "").replaceAll(",", ".")) >= 1.0 & MethodEnum.isEquals(item.getMethod())) {
                        //如果Guava 缓存中有这条数据 就不放入list
                        if (cache.getIfPresent(item.getTxnHash()) == null) {
                            itemList.add(item);
                            page.putField("key", itemList);
                            cache.put(item.getTxnHash(),Optional.of(item));
                        }
                    }
                }
            }
            //itemList.forEach(System.out::println);
            //大于1并且满足条件 获取详情页链接
            //itemList.forEach(item -> page.addTargetRequest("https://etherscan.io/tx/"  + item.getTxnHash()));
        }
    }

    private Site site = Site.me()
            .setCharset("UTF-8")
            // 设置超时时间
            .setTimeOut(60 * 1000)
            // 设置重试间隔
            .setRetrySleepTime(10 * 1000)
            // 设置重试次数
            .setSleepTime(3);

    @Override
    public Site getSite() {
        return site;
    }

    @Autowired
    private NoticePipeline noticePipeline;

    /**
     * initialDelay当任务启动后，等多久在执行
     * fixedDelay每隔多久执行一次
     */
    @Scheduled(initialDelay = 1000, fixedDelay = 10 * 1000)
    public void process() {
        log.info("开始执行任务.....");
        // 创建下载器Downloader
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();

        // 给下载器设置代理服务器信息 localhost 20001
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("127.0.0.1", 8123)));
        Spider.create(new JobProcessor())
                .setDownloader(httpClientDownloader)
                .addUrl(URL, URL + "?p=2", URL + "?p=3")
                //.addUrl("https://etherscan.io/tx" + "/0xe96bf897619cdccd636bde41f6f8e7e8a63ad7c6674ec7bed4f2c6a45ff183be")
                // 使用BloomFilter来进行去重，占用内存较小，但是可能漏抓页面   //100000是估计的页面数量
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)))
                .thread(2)
                // 设置输出位置
                .addPipeline(this.noticePipeline)
                .run();
    }
}
