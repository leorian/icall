package com.bozhong.icall.common;

import org.apache.log4j.Logger;


/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class ICallLogger {

    public static Logger getLogger() {
        return Logger.getRootLogger();
    }

    public static Logger getSysLogger() {
        return Logger.getLogger("icall");
    }
}
