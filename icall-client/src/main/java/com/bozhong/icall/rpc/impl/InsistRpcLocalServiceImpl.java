package com.bozhong.icall.rpc.impl;

import com.bozhong.common.util.StringUtil;
import com.bozhong.icall.common.ICallPath;
import com.bozhong.icall.rpc.RpcService;
import com.bozhong.insist.common.*;
import com.bozhong.insist.consumer.InsistConsumerMeta;
import com.bozhong.insist.consumer.manager.InsistPullProvider;
import com.bozhong.insist.consumer.manager.InsistServiceStore;
import com.bozhong.insist.module.ServiceMeta;
import com.bozhong.insist.rpc.netty.client.NettyChannelHandlerStore;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by xiezg@317hu.com on 2017/7/26 0026.
 */
public class InsistRpcLocalServiceImpl implements RpcService {
    @Override
    public Object dynamicalRemoteCall(ICallPath iCallPath, Map map) throws ClassNotFoundException {
        //远程服务执行方法
        Method execMethod = null;
        Class clazz = null;
        clazz = Class.forName(InsistRpcRemoteServiceImpl.class.getName());
        Method[] methods = clazz.getDeclaredMethods();// 仅获取当前类方法

        for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.equals("dynamicalRemoteCall")) {
                execMethod = method;
            }
        }
        // 进行服务消费
        InsistConsumerMeta meta = new InsistConsumerMeta();
        meta.setInterfaceName(RpcService.class.getName());
        meta.setGroup(iCallPath.getGroup());
        meta.setVersion(iCallPath.getVersion());
        InsistPullProvider.pull(meta);
        InsistSendDTO sendDTO;
        String requestId = InsistUtil.getRequestId();
        InsistResult result = null;
        int index = 1;
        boolean callSuccess = false;
        ServiceMeta serviceMeta = null;
        while (!callSuccess && index <= 3) {
            try {
                ++index;
                serviceMeta = InsistServiceStore.getServiceMeta(meta);
                if (serviceMeta == null) {
                    throw new InsistException(InsistError.NOT_FIND_SERVICE);
                }

                sendDTO = createInsistSendDTO(requestId,
                        execMethod,
                        new Object[]{iCallPath, map},
                        serviceMeta.getIp(), serviceMeta.getPort(), iCallPath.getGroup(), iCallPath.getVersion());
                result = RemoteExcutorFactory.getRpcExcutor().remoteCall(sendDTO);
                callSuccess = true;
            } catch (InsistException ie) {

                InsistLogger.record(StringUtil.format("InsistConsumerHandler.excute error ! " +
                                "requestId:%s,serviceName:%s,group:%s,version:%s errorCode:%s",
                        requestId,
                        meta.getInterfaceName(),
                        meta.getGroup(),
                        meta.getVersion(),
                        ie.getErrorCode()), ie);

                if (InsistError.NOT_FIND_SERVICE.getErrorCode().equals(ie.getErrorCode())) {
                    //服务不存在异常
                    throw new InsistException(InsistError.NOT_FIND_SERVICE, ie);
                } else if (InsistError.CLIENT_TIME_OUT.getErrorCode().equals(ie.getErrorCode())) {
                    //抛出异常
                    throw new InsistException(InsistError.CLIENT_TIME_OUT, ie);

                } else if (InsistError.CLOSED_SELECTOR_EXCEPTION.getErrorCode().equals(ie.getErrorCode())) {
                    //删除服务的本地地址 找到服务删除这个IP
                    InsistServiceStore.delAllServiceByServiceMeta(serviceMeta);
                    NettyChannelHandlerStore.remove(InsistUtil.ipAndPortCreateKey(serviceMeta));
                    if (index > 3) {
                        throw new InsistException(InsistError.CLOSED_SELECTOR_EXCEPTION, ie);
                    }

                } else if (InsistError.CONNECTION_INTERRUPT.getErrorCode().equals(ie.getErrorCode())) {
                    //删除服务的本地地址 找到服务删除这个IP
                    NettyChannelHandlerStore.remove(InsistUtil.ipAndPortCreateKey(serviceMeta));
                    if (index > 3) {
                        throw new InsistException(InsistError.CONNECTION_INTERRUPT, ie);
                    }
                }
            } catch (Throwable e) {

                InsistLogger.record(StringUtil.format("InsistConsumerHandler.excute error ! " +
                                "requestId:%s,serviceName:%s,group:%s,version:%s errorCode:%s",
                        requestId,
                        meta.getInterfaceName(),
                        meta.getGroup(),
                        meta.getVersion(),
                        e.getMessage()), e);

                throw new InsistException(e.getMessage(), e);

            } finally {
                if (index > 3) {
                    callSuccess = true;
                }
            }
        }

        return result.getModule();
    }

    private InsistSendDTO createInsistSendDTO(String requestId, Method method, Object[] args, String ip, int port,
                                              String group, String version) {
        return new InsistSendDTO(
                requestId,
                RpcService.class.getName(),
                method.getName(),
                args,
                method.getParameterTypes(),
                ip,
                port,
                group,
                3000,
                InsistUtil.geLocalIp(),
                version
        );
    }
}
