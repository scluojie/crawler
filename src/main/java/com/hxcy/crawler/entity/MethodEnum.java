package com.hxcy.crawler.entity;

/**
 * @author kevin
 * @date 2022/4/29
 * @desc
 */
public enum MethodEnum {
    Num("0x13d79a0b"),SWAP("swap"),MULTICALL("multicall");
    private final String method;

    MethodEnum(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public static boolean isEquals(String name){
        return name.equalsIgnoreCase(MethodEnum.Num.getMethod()) || name.equalsIgnoreCase(MethodEnum.SWAP.getMethod()) || name.equalsIgnoreCase(MethodEnum.MULTICALL.getMethod());
    }
}
