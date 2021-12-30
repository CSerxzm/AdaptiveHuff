package com.xzm.untils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiangzhimin
 * @Description
 * @create 2021-12-30 10:46
 */
public class ParmUtil {

    /**
     * 对参数进行解析
     * @param args
     * @return
     * @throws Exception
     */
    public static Map<String,String> getParm(String[] args) throws Exception {

        Map<String,String> map = new HashMap<String,String>(16);
        for (String arg : args) {
            if(!arg.startsWith("-")){
                throw  new Exception("参数错误,参数样例：-rounds=100 ");
            }
            String substring = arg.substring(1);
            String[] split = substring.split("=");
            if(split.length!=2){
                throw  new Exception("参数错误,参数样例：-rounds=100 ");
            }
            map.put(split[0],split[1]);
        }
        return map;

    }
}