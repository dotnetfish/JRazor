package com.superstudio.codedom.compiler;

/**
 * Created by zj-db0720 on 2017/7/1.
 */
public class MutilReturn<TReturn,TRef> {
    private TRef tRef;
    private TReturn tReturn;
    public  MutilReturn(TReturn tReturn,TRef tRef){
        this.tReturn=tReturn;
        this.tRef=tRef;
    }

    public TRef gettRef() {
        return tRef;
    }

    public void settRef(TRef tRef) {
        this.tRef = tRef;
    }

    public TReturn gettReturn() {
        return tReturn;
    }

    public void settReturn(TReturn tReturn) {
        this.tReturn = tReturn;
    }

    public  static <TReturn,TRef> MutilReturn<TReturn,TRef>  Return(TReturn tReturn,TRef ref){
        return new MutilReturn<>(tReturn,ref);
    }
}
