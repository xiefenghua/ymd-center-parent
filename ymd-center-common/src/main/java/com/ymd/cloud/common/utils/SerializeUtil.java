package com.ymd.cloud.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 序列化工具类
 *  
 * @author  黄华东
 * @version  [V1.00, 2020年04月5日]
 * @since V1.00
 */
public final class SerializeUtil
{
    private static final Logger LOG = LoggerFactory.getLogger(SerializeUtil.class);
    private SerializeUtil()
    {
    }
    /** 
     * 序列化
     * @param object
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static final byte[] serialize(Object object)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try
        {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            return baos.toByteArray();
        }
        catch (IOException e)
        {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        finally
        {
            if (oos != null)
            {
                try
                {
                    oos.close();
                }
                catch (IOException e)
                {
                    LOG.error(e.getMessage(), e);
                }
            }
            if (baos != null)
            {
                try
                {
                    baos.close();
                }
                catch (IOException e)
                {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
        
    }

    /**
     * 反序列化
     * 
     * @param bytes
     * @return
     */
    public static final Object deserialize(byte[] bytes)
    {
        return deserialize(bytes, Object.class);
    }
    
    /**
     * 反序列化
     * 
     * @param bytes
     * @return
     */
    @SuppressWarnings("unchecked")
    public static final <K> K deserialize(byte[] bytes, Class<K> cls) 
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        try
        {
            ois = new ObjectInputStream(bais);
            return (K)ois.readObject();
        } 
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage(), ex);
        } 
        catch (ClassNotFoundException ex) 
        {
            throw new RuntimeException(ex.getMessage(), ex);
        } 
        finally 
        {
            try 
            {
                if (ois != null)
                {
                    ois.close();
                }
            }
            catch (Exception e) 
            {
                LOG.error("", e);
            }
            try
            {
                if (bais != null) 
                {
                    bais.close();
                }
            } 
            catch (Exception e) 
            {
                LOG.error("", e);
            }
        }
    }

}
