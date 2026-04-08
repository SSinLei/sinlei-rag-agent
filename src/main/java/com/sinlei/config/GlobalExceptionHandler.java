package com.sinlei.config;

import com.sinlei.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * 全局异常处理类
 *
 * 作用：统一捕获项目中抛出的异常，返回规范的JSON响应
 *
 * 使用 @RestControllerAdvice 注解：
 * - 这是 Spring 提供的全局异常处理注解
 * - 会拦截所有 @RestController 抛出的异常
 * - 返回值会自动序列化为 JSON
 *
 * 异常处理流程：
 * 1. 捕获异常
 * 2. 记录日志（包含请求URI和异常信息）
 * 3. 返回统一的 Result 格式
 *
 * HTTP状态码说明：
 * - 200: 成功
 * - 400: 客户端请求错误（参数错误、校验失败等）
 * - 500: 服务器内部错误
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 日志记录器
     * 用于记录异常信息，便于排查问题
     */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理参数绑定异常
     * 通常发生在请求参数无法绑定到方法参数时，如参数类型不匹配
     *
     * @param e      绑定异常
     * @param request HTTP请求对象，用于获取请求路径
     * @return 错误响应 Result
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e, HttpServletRequest request) {
        // 获取所有字段错误，拼接成友好的错误消息
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        // 记录错误日志
        logger.error("请求地址: {}, 参数绑定异常: {}", request.getRequestURI(), message);
        // 返回400状态码和错误消息
        return Result.error(400, message);
    }

    /**
     * 处理参数校验异常
     * 通常发生在使用 @Valid 注解进行参数校验失败时
     * 比如：@NotBlank、@NotNull 等校验注解不通过
     *
     * @param e      参数校验异常
     * @param request HTTP请求对象
     * @return 错误响应 Result
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        // 获取所有字段校验错误，拼接成友好的错误消息
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        // 记录错误日志
        logger.error("请求地址: {}, 参数校验异常: {}", request.getRequestURI(), message);
        // 返回400状态码和错误消息
        return Result.error(400, message);
    }

    /**
     * 处理非法参数异常
     * 通常在业务逻辑中手动抛出，如参数校验失败
     *
     * @param e      非法参数异常
     * @param request HTTP请求对象
     * @return 错误响应 Result
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        // 记录错误日志
        logger.error("请求地址: {}, 非法参数异常: {}", request.getRequestURI(), e.getMessage());
        // 返回400状态码和异常消息
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理运行时异常
     * 捕获所有未明确处理的运行时异常，如空指针、类型转换等
     *
     * @param e      运行时异常
     * @param request HTTP请求对象
     * @return 错误响应 Result
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        // 记录完整堆栈信息，便于排查
        logger.error("请求地址: {}, 运行时异常: {}", request.getRequestURI(), e.getMessage(), e);
        // 返回500状态码
        return Result.error(500, "服务器内部错误");
    }

    /**
     * 处理其他所有异常
     * 兜底处理，确保所有异常都有返回
     *
     * @param e      通用异常
     * @param request HTTP请求对象
     * @return 错误响应 Result
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        // 记录完整堆栈信息
        logger.error("请求地址: {}, 系统异常: {}", request.getRequestURI(), e.getMessage(), e);
        // 返回500状态码
        return Result.error(500, "系统异常");
    }
}
