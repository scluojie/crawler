package com.hxcy.crawler.utils;

/**
 * @author kevin
 * @date 2022/4/29
 * @desc
 */
public class StringUtil {

    public static String getValue(String str){
        return str.replace("<b>.</b>", ".")
                .replace("<span class=\"small text-secondary\">","")
                .replace("<span class=\"text-secondary\">","")
                .replace("<td>","")
                .replace("</td>","")
                .replace("</span>","");
    }


    // 替换字符串里最后出现的元素
    public static String replaceLast( String text, String strToReplace,
                                      String replaceWithThis ) {
        return text.replaceFirst( "(?s)" + strToReplace + "(?!.*?" + strToReplace
                + ")", replaceWithThis );
    }
}
