package com.bozhong.icall.pipeline;

import com.bozhong.common.util.StringUtil;
import com.bozhong.config.util.CookiesUtil;
import com.bozhong.icall.common.ICallConstants;
import com.bozhong.icall.common.WebSettingParam;
import com.bozhong.myredis.MyRedisClusterForHessian;
import com.yx.eweb.main.PipeLineInter;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class SecurityPipeLine implements PipeLineInter {
    public static final String pattern = "^[\\w\\W]*/icall/api/[\\w\\W]*$";

    private MyRedisClusterForHessian myRedisClusterForHessian;

    private static final Logger logger = Logger.getLogger(SecurityPipeLine.class);

    @Override
    public boolean run(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        logger.warn("SecurityPipeLine has execute ! ");
        httpServletRequest.setAttribute("html_title", WebSettingParam.HTML_TITLE);
        httpServletRequest.setAttribute("switch_crop", WebSettingParam.CORP);
        httpServletRequest.setAttribute("switch_department", WebSettingParam.DEPARTMENT);
        Cookie tokenCookie = CookiesUtil.getCookieByName(httpServletRequest, "iCall_token");
        if (tokenCookie == null) {
            //动态链接调用过滤
            boolean isMatch = Pattern.matches(pattern, httpServletRequest.getRequestURL());
            if (isMatch) {
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

        String token = tokenCookie.getValue();
        String uId = myRedisClusterForHessian.getForStr(ICallConstants.ICALL_CENTER_USERNAME_PREFIX + token);
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
