package cache;

import java.util.HashMap;

public class Cache {

    public static HashMap<String,String> cache = new HashMap<String, String>();


    public static String getValue(String key)
    {
        System.out.println("获取了键值对，键为"+key+",值为"+cache.get(key));
        return cache.get(key);
    }


    public static boolean set(String key,String value)
    {
        cache.put(key,value);
        System.out.println("设置了键值对，建为"+key+",值为"+value);
        if(cache.get(key)!=null)
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
        if(cache.get(key)!=null)
        {
            return false;
        }
        else{
            return true;
        }
    }
}
