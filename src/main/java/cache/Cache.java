package cache;

import cache.model.DictValue;
import cache.model.LruCache;

import java.util.Date;

//对lrucache的外层封装
public class Cache {

    public static LruCache<String, DictValue> cache = LruCache.newInstance();


    /**
     * get方法，实现惰性删除，当delete时获取不到等情况
     * @param key
     * @return DictValue
     */
    public static DictValue get(String key) {
        //System.out.println("获取了键值对，键为" + key + ",值为" + cache.get(key));
        DictValue dictValue = cache.get(key);
        if (dictValue == null) {
            return null;
        }
        if(dictValue.getExpireTime()==null)
        {
            return cache.get(key);
        }
        if(dictValue.isDeleted())
        {
            return null;
        }
        Date nowDate = new Date();
        //实现惰性删除
        if (nowDate.after(new Date(Long.valueOf(dictValue.getExpireTime())))) {
//            System.out.println(new Date());
//            System.out.println("惰性删除");
            cache.remove(key);
            return null;
        }
        return cache.get(key);
    }


    /**
     * set方法，实现了过期机制
     * @param key
     * @param value
     * @param flag
     * @param expire
     * @return
     */
    public static boolean set(String key, String value, String flag, String expire) {
        Date nowDate = new Date();
        int expires = Integer.valueOf(expire);
        String expireTime;
        if (expires == 0) {
            expireTime = null;
        } else {
            expireTime = (nowDate.getTime() + expires*1000)+"";
        }
        DictValue dictValue = new DictValue(value, flag, expireTime);
        cache.put(key, dictValue);
        //System.out.println("设置了键值对，键为" + key + ",值为" + value);
        if (cache.containsKey(key)) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 当time=0时，直接删除，当time>0时，设置delete标志，后台线程在
     * times时间后进行删除
     * @param key
     * @param time
     * @return
     */
    public static boolean delete(String key, String time) {
        int times = Integer.valueOf(time);
        DictValue dictValue = cache.get(key);
        if(times==0) {
            cache.remove(key);
            if (cache.containsKey(key)) {
                return false;
            } else {
                dictValue = null;
                return true;
            }
        }else{
            dictValue.setDeleted(true);
            Date nowDate = new Date();
            String expiredTime = (nowDate.getTime()+times*1000)+"";
            dictValue.setExpireTime(expiredTime);
        }
        return true;


    }
}
