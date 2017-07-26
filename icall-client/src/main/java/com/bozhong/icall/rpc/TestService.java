package com.bozhong.icall.rpc;

/**
 * Created by xiezg@317hu.com on 2017/7/26 0026.
 */
public interface TestService {

    /**
     * @param userName
     * @param userId
     * @return
     */
    public String getUserJsonByUserNameAndUserId(String userName, Long userId);
}
