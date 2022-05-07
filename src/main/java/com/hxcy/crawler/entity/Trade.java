package com.hxcy.crawler.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author kevin
 * @date 2022/5/7
 * @desc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("bigtrade")
public class Trade {
    @TableId
    private String txn;
    private String method;
    private String hashTime;
    private Float ether;
    @TableField("innerHTML")
    private String innerHTML;
}
