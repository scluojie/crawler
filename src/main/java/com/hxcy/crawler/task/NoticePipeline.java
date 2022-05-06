package com.hxcy.crawler.task;


import com.hxcy.crawler.entity.Item;
import com.hxcy.crawler.utils.DingDingUtil;
import com.hxcy.crawler.utils.HttpClientUtil;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.selector.Html;

import java.util.List;

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


    @SneakyThrows
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
                //List<String> list = html.css(".media-body span").css("span", "text").all();
                String detail = html.css(".media-body").replace("<[^<>]+>", " ").replace("\n", "").replace("[ ]+", " ").toString();
                if (detail == null || "".equals(detail.trim())) {
                    logger.warn("no data....");
                }else{
                    jobsItem.setDetail(detail);
                }
            }
            StringBuilder sb = new StringBuilder();
            int i = 1;
            for (Item jobsItem : jobsItems) {
                if(i == 1) {
                    sb.append("---  \n  ");
                }
                sb.append("## ITEM" + i + " ##  \n  ");
                //sb.append("**交易哈希:**" + jobsItem.getTxnHash() + "  \n  ");
                //sb.append("**交易类型:**" + jobsItem.getMethod() + "  \n  ");
                //sb.append("**区块高度:**" + jobsItem.getBlock() + "  \n  ");
                sb.append("交易时间:<font color=#0000FF>" + jobsItem.getAge() + "</font>  \n  ");
                sb.append("卖家地址:" + jobsItem.getFrom() + "  \n  ");
                sb.append("买家地址:" + jobsItem.getTo() + "  \n  ");
                //sb.append("**交易数量:**" + jobsItem.getValue() + "  \n  ");
                //sb.append("**交易费用:**" + jobsItem.getTxnFee() + "  \n  ");
                sb.append("交易明细:<font color=#0000FF>" + jobsItem.getDetail() + "</font>  \n  ");
                sb.append("[详情链接](" + DETAIL_URL + jobsItem.getTxnHash() + ")  \n  ");
                sb.append("---  \n  ");
                i++;
            }
            send.dingRequest(sb.toString());
        }
    }
}
