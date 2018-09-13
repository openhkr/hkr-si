package com.reachauto.hkr.si.mathcer;

import org.mockito.ArgumentMatcher;

/**
 * @author zhang.shuo-neu
 * @create 2018-02-27 14:23
 */

public class ObjectEqualsMathcer<T> extends ArgumentMatcher<T> {

    public Object object;

    public ObjectEqualsMathcer(Object object){
        this.object = object;
    }

    public boolean matches(Object obj) {
        return object == obj || object == null || object.equals(obj);
    }
}
