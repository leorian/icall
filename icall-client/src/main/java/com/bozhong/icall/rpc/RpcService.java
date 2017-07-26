package com.bozhong.icall.rpc;

import com.bozhong.icall.common.ICallPath;

import java.util.Map;

/**
 * Created by xiezg@317hu.com on 2017/7/26 0026.
 */
public interface RpcService {

    Object dynamicalRemoteCall(ICallPath iCallPath, Map map) throws ClassNotFoundException;
}
