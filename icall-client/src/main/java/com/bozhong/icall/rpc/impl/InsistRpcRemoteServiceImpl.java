package com.bozhong.icall.rpc.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bozhong.common.util.ResultMessageBuilder;
import com.bozhong.icall.common.ICallErrorEnum;
import com.bozhong.icall.common.ICallPath;
import com.bozhong.icall.rpc.RpcService;
import com.bozhong.insist.common.InsistUtil;
import com.bozhong.insist.provider.InsistProviderMeta;
import com.bozhong.insist.provider.InsistProviderStore;
import org.apache.commons.beanutils.MethodUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
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
        if (parameterNames == null && types == null) {
            objects = new Object[0];
        } else if (parameterNames.length != types.length) {
            return ResultMessageBuilder.build(false, ICallErrorEnum.E10004.getError(),
                    ICallErrorEnum.E10004.getMsg());
        } else {
            objects = new Object[types.length];
        }

        int i = 0;
        for (Type type : types) {
            String[] currentParameter = (String[]) map.get(parameterNames[i]);
            if (currentParameter == null || currentParameter.length == 0) {
                objects[i] = null;
            } else if (type.toString().equals(Class.class.toString())) {
                try {
                    objects[i] = Class.forName(currentParameter[0]);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (type.toString().equals(String.class.toString())) {
                objects[i] = currentParameter[0];
            } else if (type.toString().equals(int.class.toString())
                    || type.toString().equals(Integer.class.toString())) {
                objects[i] = Integer.parseInt(currentParameter[0]);
            } else if (type.toString().equals(long.class.toString())
                    || type.toString().equals(Long.class.toString())) {
                objects[i] = Long.parseLong(currentParameter[0]);
            } else if (type.toString().equals(double.class.toString())
                    || type.toString().equals(Double.class.toString())) {
                objects[i] = Double.parseDouble(currentParameter[0]);
            } else if (type.toString().equals(float.class.toString())
                    || type.toString().equals(Float.class.toString())) {
                objects[i] = Float.parseFloat(currentParameter[0]);
            } else if (type.toString().equals(boolean.class.toString())
                    || type.toString().equals(Boolean.class.toString())) {
                objects[i] = Boolean.parseBoolean(currentParameter[0]);
            } else if (type.toString().equals(int[].class.toString())
                    || type.toString().equals(Integer[].class.toString())) {
                JSONArray subJsonArray = JSON.parseArray(currentParameter[0]);
                int[] ints = new int[subJsonArray.size()];
                for (int j = 0; j < subJsonArray.size(); j++) {
                    ints[j] = subJsonArray.getIntValue(j);
                }
                objects[i] = ints;
            } else if (type.toString().equals(long[].class.toString())
                    || type.toString().equals(Long[].class.toString())) {
                JSONArray subJsonArray = JSON.parseArray(currentParameter[0]);
                long[] longs = new long[subJsonArray.size()];
                for (int j = 0; j < subJsonArray.size(); j++) {
                    longs[j] = subJsonArray.getLongValue(j);
                }
                objects[i] = longs;
            } else if (type.toString().equals(double[].class.toString())
                    || type.toString().equals(Double[].class.toString())) {
                JSONArray subJsonArray = JSON.parseArray(currentParameter[0]);
                double[] doubles = new double[subJsonArray.size()];
                for (int j = 0; j < subJsonArray.size(); j++) {
                    doubles[j] = subJsonArray.getDoubleValue(j);
                }
                objects[i] = doubles;
            } else if (type.toString().equals(float[].class.toString())
                    || type.toString().equals(Float[].class.toString())) {
                JSONArray subJsonArray = JSON.parseArray(currentParameter[0]);
                float[] floats = new float[subJsonArray.size()];
                for (int j = 0; j < subJsonArray.size(); j++) {
                    floats[j] = subJsonArray.getFloatValue(j);
                }
                objects[i] = floats;
            } else if (type.toString().equals(boolean[].class.toString())
                    || type.toString().equals(Boolean[].class.toString())) {
                JSONArray subJsonArray = JSON.parseArray(currentParameter[0]);
                boolean[] booleans = new boolean[subJsonArray.size()];
                for (int j = 0; j < subJsonArray.size(); j++) {
                    booleans[j] = subJsonArray.getBooleanValue(j);
                }
                objects[i] = booleans;
            } else if (type.toString().startsWith("class [L") && type.toString().endsWith(";")) {//对象数组
                try {
                    JSONArray subJsonArray = JSON.parseArray(currentParameter[0]);
                    Class cls = Class.forName(type.toString().replace("class [L", "").replace(";", "").trim());
                    Object objects1 = Array.newInstance(cls, subJsonArray.size());
                    objects[i] = JSON.parseArray(subJsonArray.toJSONString(), cls).toArray((Object[]) objects1);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (!type.toString().startsWith("class [") && type.toString().startsWith("class ")) {//单个对象
                try {
                    JSONObject jsonObject = JSON.parseObject(currentParameter[0]);
                    Class cls = Class.forName(type.toString().replace("class ", "").trim());
                    objects[i] = JSON.parseObject(jsonObject.toJSONString(), cls);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (type.toString().startsWith("java.util.List")) {//list集合
                try {
                    JSONArray subJsonArray = JSON.parseArray(currentParameter[0]);
                    Class cls = Class.forName(type.toString().replace("java.util.List<", "").replace(">", "").trim());
                    objects[i] = JSON.parseArray(subJsonArray.toJSONString(), cls);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (type.toString().startsWith("java.util.Set")) {//set集合
                try {
                    JSONArray subJsonArray = JSON.parseArray(currentParameter[0]);
                    Class cls = Class.forName(type.toString().replace("java.util.Set<", "").replace(">", "").trim());
                    objects[i] = new HashSet(JSON.parseArray(subJsonArray.toJSONString(), cls));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (type.toString().startsWith("java.util.Map")) {//map集合
                JSONObject jsonObject = JSON.parseObject(currentParameter[0]);
                String[] typeStr = type.toString().replace("java.util.Map<", "").replace(">", "").split(",");
                try {
                    Class keyCls = Class.forName(typeStr[0].trim());
                    Class valueCls = Class.forName(typeStr[1].trim());
                    Map innerMap = JSON.parseObject(jsonObject.toJSONString(), Map.class);
                    Map keyValueMap = new HashMap();
                    for (Object key : innerMap.keySet()) {
                        keyValueMap.put(parseObject(key, keyCls), parseObject(innerMap.get(key), valueCls));
                    }
                    objects[i] = keyValueMap;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(type.toString());
            i++;
        }

        Object target = null;
        try {
            target = MethodUtils.invokeExactMethod(provider,
                    iCallPath.getMethod(),
                    objects,
                    method.getParameterTypes());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if (target == null) {
            return null;
        }

        return target;
    }

    private Object parseObject(Object o, Class c) {
        if (c == String.class) {
            return String.valueOf(o);
        } else if (c == Integer.class) {
            return Integer.parseInt(o.toString());
        } else if (c == Long.class) {
            return Long.parseLong(o.toString());
        } else if (c == Double.class) {
            return Double.parseDouble(o.toString());
        } else if (c == Float.class) {
            return Float.parseFloat(o.toString());
        } else if (c == Boolean.class) {
            return Boolean.parseBoolean(o.toString());
        } else {
            if (o instanceof String) {
                return JSON.parseObject(o.toString(), c);
            } else {
                return JSON.parseObject(((JSONObject) o).toJSONString());
            }
        }
    }
}
