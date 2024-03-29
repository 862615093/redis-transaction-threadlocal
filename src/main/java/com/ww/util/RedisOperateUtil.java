package com.ww.util;



import com.ww.enums.RedisOperateTypeEnum;
import lombok.Data;

/**
 * redis事务工具类
 *
 * @author tingmailang
 */
@Data
public class RedisOperateUtil<T> {

    private RedisOperateTypeEnum operateTypeEnum;
    private String key;
    private T value;
    private T prevValue;
}
