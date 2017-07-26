package com.bozhong.imanager.pipeline;

import com.bozhong.common.util.StringUtil;
import com.bozhong.config.util.CookiesUtil;
import com.bozhong.imanager.common.ImanagerConstants;
import com.bozhong.imanager.common.WebSettingParam;
import com.bozhong.myredis.MyRedisClusterForHessian;
import com.yx.eweb.main.PipeLineInter;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class SecurityPipeLine implements PipeLineInter {

    private MyRedisClusterForHessian myRedisClusterForHessian;

    private static final Logger logger = Logger.getLogger(SecurityPipeLine.class);

    @Override
    public boolean run(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        logger.warn("SecurityPipeLine has execute ! ");
        httpServletRequest.setAttribute("html_title", WebSettingParam.HTML_TITLE);
        httpServletRequest.setAttribute("switch_crop", WebSettingParam.CORP);
        httpServletRequest.setAttribute("switch_department", WebSettingParam.DEPARTMENT);
        Cookie tokenCookie = CookiesUtil.getCookieByName(httpServletRequest, "document_token");
        if (tokenCookie == null) {
            try {
                httpServletResponse.sendRedirect(httpServletRequest.getContextPath() +
                        "/monitor/login.htm");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        String token = tokenCookie.getValue();
        String uId = myRedisClusterForHessian.getForStr(ImanagerConstants.IMANAGER_CENTER_USERNAME_PREFIX + token);
        if (StringUtil.isNotBlank(uId)) {
            httpServletRequest.setAttribute("uId", uId);
            return true;
        }

        try {
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() +
                    "/monitor/login.htm");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void setMyRedisClusterForHessian(MyRedisClusterForHessian myRedisClusterForHessian) {
        this.myRedisClusterForHessian = myRedisClusterForHessian;
    }
}
