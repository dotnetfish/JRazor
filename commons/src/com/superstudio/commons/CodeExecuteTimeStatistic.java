package com.superstudio.commons;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zj-db0720 on 2017/6/18.
 */
public class CodeExecuteTimeStatistic {
    private static Map<String,Long> times=new HashMap<>();
    public static void evalute(String key,long time){
      Long oldValue=  times.get(key);
      if(oldValue==null)oldValue=0L;
      oldValue+=time;
      times.put(key,oldValue);
    }

    public  static  Map<String,Long> getList(){
        return times;

    }

    public static void  reset(){
        for (Map.Entry<String,Long> entry:times.entrySet()
             ) {
            times.put(entry.getKey(),0L);
        }
    }

    public  static void clear(String key){

        times.put(key,0L);
    }

    public  static void evaluteTick(String key){
        System.out.println(key);
        Long oldValue=  times.get(key);
        if(oldValue==null)oldValue=0L;
        oldValue+=1;
        times.put("_count_"+key,oldValue);
    }
}
