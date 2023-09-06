package com.ww.controller;

import com.ww.annotation.RedisTransaction;
import com.ww.enums.RedisClientTypeEnum;
import com.ww.util.RedisTransactionCacheUtils;
import com.ww.util.RedisTransactionCommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Slf4j
@RestController
@Api(tags = {"测试"})
@RequestMapping("/test")
public class TestController {

    @Resource
    private RedisTransactionCacheUtils<Object> redisTransactionCacheUtils;

    @GetMapping("/t1")
    @ApiOperation("测试Redis事务回滚")
    @RedisTransaction(atuoRemove = true)
    public boolean test() {

//        设置redis客户端为RedisTemplate
        RedisTransactionCommonUtil.setRedisClient(RedisClientTypeEnum.REDIS_TEMPLATE);

//        设置查询前镜像
        RedisTransactionCommonUtil.setQueryPrev(true);

        //业务处理
        boolean b = redisTransactionCacheUtils.stringSetTransaction("k1", "v2");

//        int i = 1 / 0;


        return b;
    }

}
