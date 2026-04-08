package com.sinlei.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一响应结果类
 * 用于所有Controller的返回值封装，实现RESTful风格的统一响应格式
 *
 * @param <T> 响应数据的泛型类型
 *
 * 响应格式示例：
 * {
 *     "code": 200,        // 状态码，200表示成功
 *     "message": "success", // 提示信息
 *     "data": {...}       // 响应数据
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    /**
     * 响应状态码
     * 200: 成功
     * 400: 请求参数错误
     * 500: 服务器内部错误
     */
    private Integer code;

    /**
     * 响应提示信息
     */
    private String message;

    /**
     * 响应数据，可以是任意类型
     */
    private T data;

    /**
     * 成功响应（无数据）
     * @return code=200, message="success", data=null
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    /**
     * 成功响应（带数据）
     * @param data 响应数据
     * @return code=200, message="success", data=传入的数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /**
     * 成功响应（带自定义消息和数据）
     * @param message 自定义提示信息
     * @param data 响应数据
     * @return code=200, message=传入的消息, data=传入的数据
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 错误响应（无自定义信息）
     * @return code=500, message="error", data=null
     */
    public static <T> Result<T> error() {
        return new Result<>(500, "error", null);
    }

    /**
     * 错误响应（带自定义消息）
     * @param message 自定义错误信息
     * @return code=500, message=传入的消息, data=null
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 错误响应（带自定义状态码和消息）
     * @param code 自定义状态码
     * @param message 自定义错误信息
     * @return code=传入的状态码, message=传入的消息, data=null
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
