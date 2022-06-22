package com.baidu.facesdklibrary.callback;

/**
 * 1. 可自定义返回码
 * 2. 超时时间15秒，回调onError
 */
public interface InitCallback {
    /**
     * 初始化成功 *
     *
     * @param code 成功应答码
     * @param desc 描述
     */
    void onSucces(int code, String desc);

    /**
     * 初始化失败 *
     *
     * @param code 错误码
     * @param desc 错误描述
     */
    void onError(int code, String desc);
}
