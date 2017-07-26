package com.bozhong.icall.restful;

import com.bozhong.common.util.ResultMessageBuilder;
import com.bozhong.common.util.StringUtil;
import com.bozhong.icall.common.ImanagerErrorEnum;
import com.bozhong.icall.tools.ImanagerUtil;
import com.bozhong.insist.common.InsistUtil;
import com.bozhong.insist.consumer.InsistConsumerMeta;
import com.bozhong.insist.module.ServiceMeta;
import com.bozhong.insist.zk.InsistZkClient;
import com.sun.jersey.spi.resource.Singleton;
import com.yx.eweb.main.EWebServletContext;
import org.apache.zookeeper.KeeperException;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiezg@317hu.com on 2017/5/24 0024.
 */
@Singleton
@Controller
@Path("insist")
public class InsistRest {

    static {
        try {
            InsistZkClient.getInstance().connect(System.getProperty("insist.zkHosts"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求参数：{
     * serviceName:服务名，
     * group:组,
     * ip:ip
     * }
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("providerList")
    public String providerList(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        String serviceName = (String) EWebServletContext.getEWebContext().get("serviceName");
        String group = (String) EWebServletContext.getEWebContext().get("group");
        String ip = (String) EWebServletContext.getEWebContext().get("ip");
        List<ServiceMeta> serviceMetaList = new ArrayList<>();
        //分组
        List<String> groupPaths = getChildrenPath(InsistUtil.getProviderZkPath());
        if (CollectionUtils.isEmpty(groupPaths)) {
            return ResultMessageBuilder.build(false, ImanagerErrorEnum.E10002.getError(),
                    ImanagerErrorEnum.E10002.getMsg()).
                    toJSONString();
        }

        //查询条件分组不为空时
        if (StringUtil.isNotBlank(group)) {
            groupPaths = new ArrayList<>();
            groupPaths.add(group.trim());
        }

        for (String groupPath : groupPaths) {
            //服务
            List<String> serviceGroupPaths = getChildrenPath(ImanagerUtil.getProviderGroupPath(groupPath));
            if (CollectionUtils.isEmpty(serviceGroupPaths)) {
                continue;
            }

            //查询条件服务名不为空时
            if (StringUtil.isNotBlank(serviceName)) {
                serviceGroupPaths = new ArrayList<>();
                serviceGroupPaths.add(serviceName.trim());
            }

            for (String serviceGroupPath : serviceGroupPaths) {
                //版本
                List<String> versionServiceGroupPaths = getChildrenPath(ImanagerUtil.
                        getProviderGroupServiceNamePath(groupPath, serviceGroupPath));
                if (CollectionUtils.isEmpty(versionServiceGroupPaths)) {
                    continue;
                }

                for (String versionServiceGroupPath : versionServiceGroupPaths) {
                    //IP端口号
                    List<String> versionServiceGroupPathAndIpPorts = getChildrenPath(ImanagerUtil.
                            getProviderServiceNameGroupVersionZkPath(groupPath, serviceGroupPath, versionServiceGroupPath));
                    if (CollectionUtils.isEmpty(versionServiceGroupPathAndIpPorts)) {
                        continue;
                    }

                    List<String> ipPorts = new ArrayList<>();
                    if (StringUtil.isNotBlank(ip)) {
                        for (String versionServiceGroupPathAndIpPort : versionServiceGroupPathAndIpPorts) {
                            if (versionServiceGroupPathAndIpPort.startsWith(ip.trim())) {
                                ipPorts.add(versionServiceGroupPathAndIpPort);
                            }
                        }
                        versionServiceGroupPathAndIpPorts = ipPorts;
                    }

                    //节点数据
                    for (String versionServiceGroupPathAndIpPort :
                            versionServiceGroupPathAndIpPorts) {
                        String serviceMetaStr = getPathData(ImanagerUtil.getProviderIpPortPath(groupPath, serviceGroupPath,
                                versionServiceGroupPath, versionServiceGroupPathAndIpPort));
                        if (StringUtil.isNotBlank(serviceMetaStr)) {
                            serviceMetaList.add(InsistUtil.jsonToServiceMeta(serviceMetaStr));
                        }

                    }

                }

            }

        }

        return ResultMessageBuilder.build(serviceMetaList).toJSONString();
    }

    /**
     * 请求参数：{
     * serviceName:"服务名",
     * group:组,
     * ip:ip
     * }
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("consumerList")
    public String consumerList(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        String serviceName = (String) EWebServletContext.getEWebContext().get("serviceName");
        String group = (String) EWebServletContext.getEWebContext().get("group");
        String ip = (String) EWebServletContext.getEWebContext().get("ip");
        List<InsistConsumerMeta> insistConsumerMetaList = new ArrayList<>();
        //分组
        List<String> groupPaths = getChildrenPath(InsistUtil.getConsumerZkPath());
        if (CollectionUtils.isEmpty(groupPaths)) {
            return ResultMessageBuilder.build(false, ImanagerErrorEnum.E10002.getError(),
                    ImanagerErrorEnum.E10002.getMsg()).
                    toJSONString();
        }

        //查询条件分组不为空时
        if (StringUtil.isNotBlank(group)) {
            groupPaths = new ArrayList<>();
            groupPaths.add(group.trim());
        }

        for (String groupPath : groupPaths) {
            //服务
            List<String> serviceGroupPaths = getChildrenPath(ImanagerUtil.getConsumerGroupPath(groupPath));
            if (CollectionUtils.isEmpty(serviceGroupPaths)) {
                continue;
            }

            //查询条件服务名不为空时
            if (StringUtil.isNotBlank(serviceName)) {
                serviceGroupPaths = new ArrayList<>();
                serviceGroupPaths.add(serviceName.trim());
            }

            for (String serviceGroupPath : serviceGroupPaths) {
                //版本
                List<String> versionServiceGroupPaths = getChildrenPath(ImanagerUtil.
                        getConsumerGroupServiceNamePath(groupPath, serviceGroupPath));
                if (CollectionUtils.isEmpty(versionServiceGroupPaths)) {
                    continue;
                }

                for (String versionServiceGroupPath : versionServiceGroupPaths) {
                    //IP端口号
                    List<String> versionServiceGroupPathAndIpPorts = getChildrenPath(ImanagerUtil.
                            getConsumerServiceNameGroupVersionZkPath(groupPath, serviceGroupPath, versionServiceGroupPath));
                    if (CollectionUtils.isEmpty(versionServiceGroupPathAndIpPorts)) {
                        continue;
                    }

                    if (StringUtil.isNotBlank(ip)) {
                        List<String> ipPorts = new ArrayList<>();
                        for (String versionServiceGroupPathAndIpPort : versionServiceGroupPathAndIpPorts) {
                            if (versionServiceGroupPathAndIpPort.startsWith(ip)) {
                                ipPorts.add(versionServiceGroupPathAndIpPort);
                            }
                        }

                        versionServiceGroupPathAndIpPorts = ipPorts;
                    }

                    //节点数据
                    for (String versionServiceGroupPathAndIpPort :
                            versionServiceGroupPathAndIpPorts) {
                        String serviceMetaStr = getPathData(ImanagerUtil.getConsumerIpPortPath(groupPath, serviceGroupPath,
                                versionServiceGroupPath, versionServiceGroupPathAndIpPort));
                        if (StringUtil.isNotBlank(serviceMetaStr)) {
                            insistConsumerMetaList.add(InsistUtil.jsonToClientMeta(serviceMetaStr));
                        }

                    }

                }

            }

        }
        return ResultMessageBuilder.build(insistConsumerMetaList).toJSONString();
    }

    /**
     * 获取zookeeper连接信息
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("zkHosts")
    public String zkHosts(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        return System.getProperty("insist.zkHosts");
    }

    /**
     * 获取子节点
     *
     * @param baseZkPath
     * @return
     */
    private List<String> getChildrenPath(String baseZkPath) {
        try {
            return InsistZkClient.getInstance().getNodeChildren(baseZkPath);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取节点数据
     *
     * @param zkPath
     * @return
     */
    private String getPathData(String zkPath) {
        try {
            return InsistZkClient.getInstance().getDataForStr(zkPath, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
