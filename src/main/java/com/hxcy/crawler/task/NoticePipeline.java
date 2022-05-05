package com.hxcy.crawler.task;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hxcy.crawler.entity.Item;
import com.hxcy.crawler.utils.DingDingUtil;
import com.hxcy.crawler.utils.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.selector.Html;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author kevin
 * @date 2022/4/28
 * @desc
 */
@Component
public class NoticePipeline implements Pipeline {
    private static Logger logger = LoggerFactory.getLogger(NoticePipeline.class);

    /**
     * 发送信息到钉钉对象
     */
    @Autowired
    private DingDingUtil send;

    /**
     * 详情页url
     */
    @Value("${crawler.detail.url}")
    private String DETAIL_URL;


    @Override
    public void process(ResultItems resultItems, Task task) {
        // 获取数据
        List<Item> jobsItems = resultItems.get("key");
        // 判断获取的数据是否符合
        if (jobsItems != null) {
            logger.info("发送钉钉：" + jobsItems);
            for (Item jobsItem : jobsItems) {
                String response = HttpClientUtil.sendGetForProxy(DETAIL_URL + jobsItem.getTxnHash());
                Html html = new Html(response);
                List<String> list = html.css(".media-body span").css("span", "text").all();
                if (list.size() == 0) {
                    logger.warn("no data....");
                }else{
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 7; i++) {
                        sb.append(list.get(i) + " ");
                    }
                    jobsItem.setDetail(sb.toString());
                }
            }
            send.dingRequest(jobsItems.toString());
        }
    }
}
