package com.hxcy.crawler;

import java.math.BigDecimal;

/**
 * @author kevin
 * @date 2022/5/6
 * @desc
 */
public class Test {

    public static void main(String[] args) throws Exception {
        String str = "2.89 Ether";
        //String s = StringUtil.replaceLast(str, ",", ".");
        String num = str.replaceAll(" Ether", "").replaceAll(",","");
        System.out.println(Float.parseFloat(num));
        System.out.println(BigDecimal.valueOf(Double.parseDouble(num)));
        System.out.println(System.currentTimeMillis());

    }
}
