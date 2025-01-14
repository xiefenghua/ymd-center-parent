package com.ymd.cloud.authorizeCenter.util;

import com.google.common.collect.Maps;
import com.ymd.cloud.common.utils.TypeParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 实例化工具类
 * 
 * @author 黄华东
 * @version [V1.00, 2020年04月4日]
 * @since V1.00
 */
public final class InstanceUtil
{
    /**
     * 日志类
     */
    private static final Logger LOG = LoggerFactory.getLogger(InstanceUtil.class);
    
    /**
     * <默认构造函数>
     */
    private InstanceUtil()
    {
    }
    
    /**
     * 实例化并复制属性
     * 
     * @param orig
     * @param clazz
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static final <T> T to(Object orig, Class<T> clazz)
    {
        T bean = null;
        try
        {
            bean = clazz.newInstance();
            Class<?> cls = orig.getClass();
            BeanInfo orgInfo = Introspector.getBeanInfo(cls);
            PropertyDescriptor[] orgPty = orgInfo.getPropertyDescriptors();
            Map<String, PropertyDescriptor> propertyMap = Maps.newHashMap();
            for (PropertyDescriptor property : orgPty)
            {
                propertyMap.put(property.getName(), property);
            }
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors)
            {
                String key = property.getName();
                // 过滤class属性
                if (!key.equals("class") && propertyMap.containsKey(key))
                {
                    Method getter = propertyMap.get(key).getReadMethod();
                    Method setter = property.getWriteMethod();
                    Object value = "";
                    try
                    {
                        value = getter.invoke(orig);
                        value = TypeParseUtil.convert(value, property.getPropertyType().getName(), null);
                        setter.invoke(bean, value);
                    }
                    catch (Exception e)
                    {
                        LOG.error("to Error " + key + ":" + value + ">" + e);
                    }
                }
            }
        }
        catch (Exception e)
        {
            LOG.error("to Error " + e);
        }
        return bean;
    }
    
}
