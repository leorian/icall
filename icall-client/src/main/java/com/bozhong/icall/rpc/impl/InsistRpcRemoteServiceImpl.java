package com.bozhong.icall.rpc.impl;

import com.alibaba.fastjson.JSON;
import com.bozhong.icall.common.ICallPath;
import com.bozhong.icall.rpc.RpcService;
import com.bozhong.insist.common.InsistUtil;
import com.bozhong.insist.provider.InsistProviderMeta;
import com.bozhong.insist.provider.InsistProviderStore;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by xiezg@317hu.com on 2017/7/26 0026.
 */
public class InsistRpcRemoteServiceImpl implements RpcService {
    @Override
    public Object dynamicalRemoteCall(ICallPath iCallPath, Map map) {
        System.out.println("远程接收到的路径：" + JSON.toJSONString(iCallPath) +
                "\n远程接收到的数据：" + JSON.toJSONString(map));
        InsistProviderMeta providerMeta =
                InsistProviderStore.get(InsistUtil.serviceGroupVersionCreateKey(iCallPath.getService(),
                        iCallPath.getGroup(), iCallPath.getVersion()));
        Object provider = providerMeta.getRef();
        Class providerClass = provider.getClass();
        Method[] methods = providerClass.getDeclaredMethods();
        Method method = null;
        for (Method m : methods) {
            if (iCallPath.getMethod().equals(m.getName())) {
                method = m;
                break;
            }
        }

        ParameterNameDiscoverer parameterNameDiscoverer =
                new LocalVariableTableParameterNameDiscoverer();

        String[] parameterNames = parameterNameDiscoverer
                .getParameterNames(method);
        System.out.println(iCallPath.getService() + "." + iCallPath.getMethod() + "解析到的参数名称 : ");
        if (parameterNames != null && parameterNames.length > 0) {
            for (String parameterName : parameterNames) {
                System.out.print(parameterName + ' ');
            }
        }
        System.out.println();

        Type[] types = method.getGenericParameterTypes();
        Object[] objects = null;

        return "SUCCESS";
    }
}
