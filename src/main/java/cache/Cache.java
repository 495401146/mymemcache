package cache;

import cache.model.DictValue;
import cache.model.LruCache;

import java.util.Date;


public class Cache {

    public static LruCache<String,DictValue> cache = LruCache.newInstance();
    public static DictValue get(String key)
    {
        System.out.println(cache);
        System.out.println("获取了键值对，键为"+key+",值为"+cache.get(key));
        DictValue dictValue = cache.get(key);
        if(dictValue==null)
        {
            return null;
        }
        Date nowDate = new Date();
        //实现惰性删除
        if(nowDate.before(new Date(dictValue.getExpireTime())))
        {
            cache.remove(key);
            return null;
        }
        return cache.get(key);
    }


    public static boolean set(String key,String value,String flag,String expire)
    {
        Date nowDate = new Date();
        int expires = Integer.valueOf(expire);
        String expireTime;
        if(expires==0)
        {
            expireTime = null;
        }
        expireTime = new Date(nowDate.getTime()+expires*1000).toString();
        DictValue dictValue = new DictValue(value,flag,expireTime);
        cache.put(key,dictValue);
        System.out.println("设置了键值对，键为"+key+",值为"+value);
        if(cache.containsKey(key))
        {
            return true;
        }
        else{
            return false;
        }

    }

    public static boolean delete(String key)
    {
        cache.remove(key);
        if(cache.containsKey(key))
        {
            return false;
        }
        else{
            return true;
        }
    }
}
