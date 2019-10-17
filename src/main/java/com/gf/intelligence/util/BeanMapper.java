package com.gf.intelligence.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wushubiao
 * @Title: BeanMapper
 * @ProjectName gf-intelligence
 * @Description: 转换位ESMap工具类
 * @date 2019/10/10
 */
public class BeanMapper {

    public static<T> Map<String, Object> beansToEsMap(T bean) throws Exception{
        Map<String,Object> map = new HashMap<String, Object>();
        BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
        // 获取所有的属性描述器
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            String key = pd.getName();
            Method getter = pd.getReadMethod();
            Object value = getter.invoke(bean);
            map.put(key, value);
        }
        return map;
    }
}
