package com.superstudio.commons;

/**
 * Created by zj-db0720 on 2017/6/19.
 */
public class CancellationToken {
    public static CancellationToken None=new CancellationToken();
    public  boolean  isCancellationRequested(){
        return false;
    }
    public  boolean ThrowIfCancellationRequested(){
        return  false;
    }
}
