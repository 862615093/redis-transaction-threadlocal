package com.ww.aspect;

import com.ww.annotation.RedisTransaction;
import com.ww.util.RedisOperateUtil;
import com.ww.util.RedisTransactionCacheUtils;
import com.ww.util.RedisTransactionUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Stack;

/**
 * redis事务，切面处理类
 */
@Slf4j
@Aspect
@Component
public class RedisTransactionAspect {

    @Resource
    private RedisTransactionCacheUtils redisTransactionCacheUtils;

    @Pointcut("@annotation(com.ww.annotation.RedisTransaction)")
    public void transactionPointCut() {
    }

    @AfterReturning("transactionPointCut() && @annotation(redisTransaction)")
    public void afterReturning(RedisTransaction redisTransaction) {
        //如果方法注解中开启自动清除，就去除
        if (redisTransaction.atuoRemove()) {
            RedisTransactionUtil.remove();
            log.info("自动清除RedisTransactionUtil:{}", Thread.currentThread().getName());
        }
    }

    @AfterThrowing("transactionPointCut() && @annotation(redisTransaction)")
    public void afterThrowing(RedisTransaction redisTransaction) {
        try {
            Stack<RedisOperateUtil> stack = RedisTransactionUtil.get();
            if (Objects.isNull(stack)) {
                return;
            }
            log.info("redis回滚:{}", stack);
            RedisOperateUtil redisOperateUtil;
            while (!stack.isEmpty()) {
                redisOperateUtil = stack.pop();
                switch (redisOperateUtil.getOperateTypeEnum()) {
                    case STRING_SET:
                        if (Objects.nonNull(redisOperateUtil.getPrevValue())) {
                            redisTransactionCacheUtils.stringAdd(redisOperateUtil.getKey(), redisOperateUtil.getPrevValue());
                            break;
                        }
                        redisTransactionCacheUtils.stringDelete(redisOperateUtil.getKey());
                        break;
                    case STRING_DELETE:
                        redisTransactionCacheUtils.stringAdd(redisOperateUtil.getKey(), redisOperateUtil.getValue());
                        break;
                    case LIST_ADD:
                        redisTransactionCacheUtils.listRemove(redisOperateUtil.getKey(), redisOperateUtil.getValue(), redisTransaction.index());
                        break;
                    case LIST_REMOVE:
                        redisTransactionCacheUtils.listAdd(redisOperateUtil.getKey(), redisOperateUtil.getValue());
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            log.error("reidis回滚失败:{}", e);
        } finally {
            //如果方法注解中开启自动清除，就去除
            if (redisTransaction.atuoRemove()) {
                RedisTransactionUtil.remove();
                log.info("自动清除RedisTransactionUtil:{}", Thread.currentThread().getName());
            }
        }
    }

}