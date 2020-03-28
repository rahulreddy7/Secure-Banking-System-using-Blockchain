package io.sbs.model;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.LoggerNameAwareMessage;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;
import java.io.*;
import java.util.*;

public class Logs4jBankSystem {


    //private Logger logger = LogManager.getLogger();



    /*
    sample:
    add the followed code, the logs will store api url, method, parameter, method use time to info file.


    ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("execution(public * io.sbs.controller.UserController.*(..))")
    public void getlog(){
    }

    @Before("getlog()")
    public void dobefore(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        startTime.set(System.currentTimeMillis());

        logger.info("url={}", request.getRequestURI());
        logger.info("method={}", request.getMethod());

        Object[] args = joinPoint.getArgs();

        try{
            List<Object> objects = Arrays.asList(args);
            logger.info("parameter get={}", objects);
        }
        catch (Exception e){

        }



    }

    @After("getlog()")
    public void doafter(){


        logger.info("the method use time={}s", System.currentTimeMillis()-startTime.get());

    }


    output:
    url=/users/getUserInfo
    method=GET
    parameter get=[joliver91]
    18.222.64.16:27017
    the method use time=799s
    */


}
