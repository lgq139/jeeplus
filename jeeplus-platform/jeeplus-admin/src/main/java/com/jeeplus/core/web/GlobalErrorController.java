package com.jeeplus.core.web;

import com.jeeplus.common.beanvalidator.BeanValidators;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.Collections3;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;

@ControllerAdvice
@RestController
public class GlobalErrorController{

    Log log = LogFactory.getLog(ErrorController.class);

    //access token过期
    @RequestMapping("/401")
    public ResponseEntity error401() {
        AjaxJson j = new AjaxJson();
        j.setSuccess(false);
        j.setCode(401);
        j.setMsg("您的token已过期，请刷新token重新获取！");
        return ResponseEntity.status(401).body(j);
    }

    // 没有access token, 请登录
    @RequestMapping("/402/1")
    public ResponseEntity error4021() {
        AjaxJson j = new AjaxJson();
        j.setSuccess(false);
        j.setCode(402);
        j.setMsg("请登录系统再操作！");
        return ResponseEntity.status(402).body(j);
    }

    // refresh token 过期，用户要重新登录
    @RequestMapping("/402/2")
    public ResponseEntity error4022() {
        AjaxJson j = new AjaxJson();
        j.setSuccess(false);
        j.setCode(402);
        j.setMsg("您的登录已过期，请重新登录！");
        return ResponseEntity.status(402).body(j);
    }


    @RequestMapping("/403/1")
    public ResponseEntity error4031() {
        AjaxJson j = new AjaxJson();
        j.setSuccess(false);
        j.setCode(403);
        j.setMsg("账号已在其它地方登录，您被禁止登录！");
        return ResponseEntity.status(403).body(j);
    }

    @RequestMapping("/403/2")
    public ResponseEntity error4032() {
        AjaxJson j = new AjaxJson();
        j.setSuccess(false);
        j.setCode(403);
        j.setMsg("您的账号在另一台设备上登录,如非本人操作，请立即修改密码！");
        return ResponseEntity.status(403).body(j);
    }


    @ExceptionHandler(Exception.class) //拦截全局异常
    public ResponseEntity getErrorPath(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
        log.error("{}", e);
        AjaxJson j = new AjaxJson();
        j.setSuccess(false);
        HttpStatus httpStatus;
        if (e instanceof BindException ||
                e instanceof MissingServletRequestPartException ||
                e instanceof MissingServletRequestParameterException) {
            httpStatus = HttpStatus.BAD_REQUEST;
            j.setCode(httpStatus.value());
            j.setMsg("您提交的参数，服务器无法处理");
        } else if(e instanceof UnauthorizedException){
            httpStatus = HttpStatus.NETWORK_AUTHENTICATION_REQUIRED;
            j.setCode(httpStatus.value());
            j.setMsg("权限不足:"+ e.getMessage());
        } else if (e instanceof AccessDeniedException) {
            httpStatus = HttpStatus.FORBIDDEN;
            j.setCode(httpStatus.value());
            j.setMsg("权限不足:"+e.getMessage());
        }else if (e instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) e;
            Set<? extends ConstraintViolation> constraintViolations = constraintViolationException.getConstraintViolations();
            List<String> list = BeanValidators.extractPropertyAndMessageAsList((ConstraintViolationException) e, ": ");
            list.add(0, "数据验证失败：");
            httpStatus = HttpStatus.BAD_REQUEST;
            j.setCode(httpStatus.value());
            j.setMsg( Collections3.convertToString(list, ""));
            j.put("data", constraintViolationException);
        } else if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) e;
            Object o = BeanValidators.extractPropertyAndMessage(methodArgumentNotValidException);
            List<String> list = BeanValidators.extractPropertyAndMessageAsList(methodArgumentNotValidException, ": ");
            list.add(0, "数据验证失败：");
            httpStatus = HttpStatus.BAD_REQUEST;
            j.setCode(httpStatus.value());
            j.setMsg( Collections3.convertToString(list, ""));
            j.put("data", o);
        }else{
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            j.setCode(httpStatus.value());
            j.setMsg("内部错误，操作异常; " + e.getMessage());
        }
        return ResponseEntity.status(httpStatus).body(j);
    }


    /**
     * 登录过去跳转到401
     */
    public static void response401(ServletRequest req, ServletResponse resp){
        try {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) resp;
            request.getRequestDispatcher("/401").forward(request,response);
        } catch (IOException e) {
           e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    /**
     * 没有登录跳转到402
     */
    public static void response4021(ServletRequest req, ServletResponse resp) {
        try {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) resp;
            request.getRequestDispatcher("/402/1").forward(request,response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    /**
     * 需要重新登录获取refresh token
     */
    public static void response4022(ServletRequest req, ServletResponse resp){
        try {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) resp;
            request.getRequestDispatcher("/402/2").forward(request,response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }


}
