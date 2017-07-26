package com.bozhong.icall.restful;

import com.alibaba.fastjson.JSON;
import com.bozhong.common.util.ResultMessageBuilder;
import com.bozhong.icall.common.ICallErrorEnum;
import com.bozhong.icall.common.ICallPath;
import com.sun.jersey.spi.resource.Singleton;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * Created by xiezg@317hu.com on 2017/7/26 0026.
 * {module}:模块名称（医院、运营、后台）
 */
@Controller
@Singleton
@Path(ICallPath.PATH_PREFIX + "/{module}")
public class WebDynamicRest {

    /**
     * POST动态调用
     * {service}:服务类全路径
     * {group}:分组
     * {version}:版本
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("{service}/{group}/{version}")
    public String iCallPostWithNotMethod(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        return iCallService(request, uriInfo, httpHeaders);
    }

    /**
     * POST请求动态调用
     * {service}:服务类全路径名称
     * {group}:分组
     * {version}:版本
     * {method}:方法名称
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("{service}/{group}/{version}/{method}")
    public String iCallPostWithMethod(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        return iCallService(request, uriInfo, httpHeaders);
    }

    /**
     * GET请求动态调用
     * {service}:服务类名全路径名称
     * {group}:分组
     * {version}:版本
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @GET
    @Path("{service}/{group}/{version}")
    public String iCallGetWithNotMethod(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        return iCallService(request, uriInfo, httpHeaders);
    }

    /**
     * GET请求动态调用
     * {service}:服务类名全路径名称
     * {group}:分组
     * {version}:版本
     * {method}:方法名称
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @GET
    @Path("{service}/{group}/{version}/{method}")
    public String iCallGetMethod(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        return iCallService(request, uriInfo, httpHeaders);
    }

    /**
     * 动态调用
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    private String iCallService(Request request, UriInfo uriInfo, HttpHeaders httpHeaders) {
        List<String> matchedUris = uriInfo.getMatchedURIs();
        //请求路径不匹配
        if (CollectionUtils.isEmpty(matchedUris) || matchedUris.size() != 2) {
            return ResultMessageBuilder.build(false, ICallErrorEnum.E10003.getError(),
                    ICallErrorEnum.E10003.getError()).toJSONString();
        }

        ICallPath iCallPath = new ICallPath();
        iCallPath.setAbsolutePath(matchedUris.get(0));
        iCallPath.setRelativePath(matchedUris.get(0).replace(matchedUris.get(1) + "/", ""));
        String[] paths = iCallPath.getRelativePath().split("/");
        if (paths == null || (paths.length != 3 && paths.length != 4)) {
            return ResultMessageBuilder.build(false, ICallErrorEnum.E10003.getError(),
                    ICallErrorEnum.E10003.getError()).toJSONString();
        }

        iCallPath.setService(paths[0]);
        iCallPath.setGroup(paths[1]);
        iCallPath.setVersion(paths[2]);
        if (paths.length==4) {
            iCallPath.setMethod(paths[3]);
        } else {
            iCallPath.setMethod(ICallPath.DEFAULT_METHOD);
        }

        System.out.println(JSON.toJSONString(iCallPath));
        return "ddd";
    }
}
