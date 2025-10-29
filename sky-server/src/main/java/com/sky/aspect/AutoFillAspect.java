package com.sky.aspect;

import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import com.sky.annotation.AutoFill;

import java.lang.reflect.Method;
import java.time.LocalDateTime;


@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut() {
    }


    @Before("autoFillPointcut()")
    public void autoFillBefore(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        // 获取数据库操作类型
        OperationType operationType = autoFill.value();
        //获取方法第一个参数
        Object arg = joinPoint.getArgs()[0];
        Class<?> aClass = arg.getClass();
        //获取当前时间和当前用户id
        LocalDateTime now = LocalDateTime.now();
        Long currentUser = BaseContext.getCurrentId();
        //获取设置更新时间和更新人方法
        Method setUpdateTimeMethod;
        Method setUpdateUserMethod;
        try {
            setUpdateTimeMethod = aClass.getDeclaredMethod("setUpdateTime", LocalDateTime.class);
            setUpdateUserMethod = aClass.getDeclaredMethod("setUpdateUser", Long.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        if(operationType == OperationType.INSERT){
            //如果是插入操作，设置创建时间、更新时间、创建人、更新人
            try {
                //再获取设置创建时间和创建人方法
                Method setCreateTimeMethod = aClass.getDeclaredMethod("setCreateTime", LocalDateTime.class);
                Method setCreateUserMethod = aClass.getDeclaredMethod("setCreateUser", Long.class);
                setCreateTimeMethod.invoke(arg, now);
                setCreateUserMethod.invoke(arg, currentUser);
                setUpdateTimeMethod.invoke(arg, now);
                setUpdateUserMethod.invoke(arg, currentUser);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            //如果是更新操作，设置更新时间、更新人
            try {
                setUpdateTimeMethod.invoke(arg, now);
                setUpdateUserMethod.invoke(arg, currentUser);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
