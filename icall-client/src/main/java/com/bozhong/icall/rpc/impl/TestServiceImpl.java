package com.bozhong.icall.rpc.impl;

import com.bozhong.icall.rpc.TestService;

/**
 * Created by xiezg@317hu.com on 2017/7/26 0026.
 */
public class TestServiceImpl implements TestService {
    @Override
    public String getUserJsonByUserNameAndUserId(String userName, Long userId) {
        return userName + userId;
    }
}
