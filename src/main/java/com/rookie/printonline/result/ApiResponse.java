package com.rookie.printonline.result;

import java.util.Collections;

/**
 *@Title: 返回体定义
 *@Package: com.rookie.printonline.result
 *@Description: TODO
 *@Author: mahx 马怀啸
 *@Email: 616107968@qq.com
 *@Date: 2025/5/16 10:37
 *@Version: V1.0.0
 *@Copyright: 菜鸟
 */
public class ApiResponse <T>{
    private boolean success;     // HTTP状态码或业务状态码

    private int errorCode; //返回失败的代码

    private String errorMsg;//返回的错误信息

    private T data;       // 实际数据（对象/数组）

    // 构造方法
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data);
    }
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true);
    }

    public ApiResponse(boolean success) {
        this.success = success;
        this.data= (T) Collections.emptyList();
    }

    public static ApiResponse<?> error(int code, String message) {
        return new ApiResponse<>(false, code, message);
    }

    public ApiResponse(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public ApiResponse(boolean success, int errorCode, String errorMsg) {
        this.success = success;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }



    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
