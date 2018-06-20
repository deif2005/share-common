package com.usi.util;

import java.util.UUID;

/**
 * Created by aikuangyong on 2016/2/29.
 * 下面就是实现为数据库获取一个唯一的主键id的代码
 */
public class UUIDGenerator {
    public UUIDGenerator() {
    }
    /**
     * 获得一个UUID
     * @return String UUID
     */
    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    /**
     * 获得指定数目的UUID
     * @param number int 需要获得的UUID数量
     * @return String[] UUID数组
     */
    public static String[] getUUID(int number){
        if(number < 1){
            return null;
        }
        String[] ss = new String[number];
        for(int i=0;i<number;i++){
            ss[i] = getUUID();
        }
        return ss;
    }
    public static void main(String[] args){
        System.out.println(getUUID());
        String[] ss = getUUID(10);
        for(int i=0;i<ss.length;i++){
            System.out.println(ss[i]);
        }
    }
}
