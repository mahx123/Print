package com.rookie.printonline.result;
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
    private int code;     // HTTP状态码或业务状态码
    private String message;
    private T data;       // 实际数据（对象/数组）

    // 构造方法
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data);
    }

    public static ApiResponse<?> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
