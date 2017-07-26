package com.bozhong.imanager.tools;

import com.bozhong.insist.common.InsistConstants;
import com.bozhong.insist.common.InsistUtil;

/**
 * Created by xiezg@317hu.com on 2017/5/24 0024.
 */
public class ImanagerUtil {

    /**
     * @param group
     * @return
     */
    public static String getProviderGroupPath(String group) {
        return InsistUtil.getProviderZkPath() + InsistConstants.INSIST_ZK_SLASH + group;
    }

    /**
     * @param group
     * @return
     */
    public static String getConsumerGroupPath(String group) {
        return InsistUtil.getConsumerZkPath() + InsistConstants.INSIST_ZK_SLASH + group;
    }

    /**
     * @param group
     * @param serviceName
     * @return
     */
    public static String getProviderGroupServiceNamePath(String group, String serviceName) {
        return InsistUtil.getProviderZkPath() + InsistConstants.INSIST_ZK_SLASH + group +
                InsistConstants.INSIST_ZK_SLASH + serviceName;
    }

    /**
     * @param group
     * @param serviceName
     * @return
     */
    public static String getConsumerGroupServiceNamePath(String group, String serviceName) {
        return InsistUtil.getConsumerZkPath() + InsistConstants.INSIST_ZK_SLASH + group +
                InsistConstants.INSIST_ZK_SLASH + serviceName;
    }

    /**
     * @param group
     * @param serviceName
     * @param version
     * @return
     */
    public static String getProviderServiceNameGroupVersionZkPath(String group, String serviceName, String version) {
        return InsistUtil.getProviderZkPath() + "/" + group + "/" + serviceName + "/" + version;
    }

    /**
     * @param group
     * @param serviceName
     * @param version
     * @return
     */
    public static String getConsumerServiceNameGroupVersionZkPath(String group, String serviceName, String version) {
        return InsistUtil.getConsumerZkPath() + "/" + group + "/" + serviceName + "/" + version;
    }


    /**
     * @param group
     * @param serviceName
     * @param version
     * @param ipPort
     * @return
     */
    public static String getProviderIpPortPath(String group, String serviceName, String version, String ipPort) {
        return getProviderServiceNameGroupVersionZkPath(group, serviceName, version) +
                InsistConstants.INSIST_ZK_SLASH + ipPort;
    }

    /**
     * @param group
     * @param serviceName
     * @param version
     * @param ipPort
     * @return
     */
    public static String getConsumerIpPortPath(String group, String serviceName, String version, String ipPort) {
        return getConsumerServiceNameGroupVersionZkPath(group, serviceName, version) +
                InsistConstants.INSIST_ZK_SLASH + ipPort;
    }
}
