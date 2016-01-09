package com.superstudio.jrazor.template;

/**
 * Created by Chaoqun on 2015/11/8.
 */

public interface IDependencyResolver
{
    Object getService(java.lang.Class<?> serviceType);
    Iterable<Object> getServices(java.lang.Class<?> serviceType);
}
