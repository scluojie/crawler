package com.hxcy.crawler.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author kevin
 * @date 2022/4/28
 * @desc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private String txnHash;
    private String method;
    private long block;
    private String age;
    private String from;
    private String to;
    private String value;
    private BigDecimal txnFee;
    private String detail;
}
