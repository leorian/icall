package com.bozhong.icall.restful;

import com.sun.jersey.spi.resource.Singleton;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

/**
 * Created by xiezg@317hu.com on 2017/7/26 0026.
 * {module}:模块名称（医院、运营、后台）
 */
@Controller
@Singleton
@Path("api/{module}")
public class WebDynamicRest {

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
    public String iCallPost(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
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
    public String iCallGet(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
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
        return null;
    }
}
