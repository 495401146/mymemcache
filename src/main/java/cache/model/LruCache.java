package cache.model;

import cmd.CommandFactory;
import config.Config;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//封装的lru链，用读写锁保持线程安全
public class LruCache<K,V> {
    private static int MAX_LENGTH = Config.WRITE_THREAD_NUM;  //最大长度
    private static int initLen = Config.INIT_LEN;//初始长度
    private LinkedHashMap<K, V> map;
    private ReadWriteLock lock = new ReentrantReadWriteLock(); //读写锁
    private static volatile LruCache lruCache;

    //单例，此存储必须保证只有一个
    public static LruCache newInstance()
    {
        if(lruCache==null)
        {
            synchronized (CommandFactory.class)
            {
                if(lruCache==null)
                {
                    lruCache = new LruCache(initLen,MAX_LENGTH);
                }
            }
        }
        return lruCache;
    }
    private LruCache(int initLength, int maxLength) {
        MAX_LENGTH = maxLength;
        map = new LinkedHashMap<K, V>(initLength, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > MAX_LENGTH;
            }
        };
    }

    /**
     * 添加项,此时用写锁
     *
     * @param key  项
     * @param value 状态
     */
    public void put(K key, V value) {
        lock.writeLock().lock();
        map.put(key, value);
        lock.writeLock().unlock();
    }

    /**
     * 获取值,此时用读锁
     *
     * @param key 项
     * @return value 值
     */
    public V get(String key) {
        lock.readLock().lock();
        V value = map.get(key);
        lock.readLock().unlock();
        return value;
    }

    /**
     * 是否存在
     *
     * @param key 项
     * @return 是否存在
     */
    public boolean containsKey(String key) {
        lock.readLock().lock();
        boolean isContainer = map.containsKey(key);
        lock.readLock().unlock();
        return isContainer;
    }

    /**
     * 删除key
     *
     * @param key 项
     */
    public void remove(String key) {
        lock.writeLock().lock();
        map.remove(key);
        lock.writeLock().unlock();
    }

    /**
     * 用于后台线程定期进行清除过期键
     */
    public void flush()
    {
        lock.readLock().lock();
        if(map.size()==0)
        {
            lock.readLock().unlock();
            return;
        }
        for(Map.Entry entry:map.entrySet())
        {
            String key = (String) entry.getKey();
            DictValue dictValue = (DictValue) entry.getValue();
            lock.readLock().unlock();
            if(dictValue.getExpireTime()==null)
            {
                continue;
            }
            Date nowDate = new Date();
            if(nowDate.after(new Date(Long.valueOf(dictValue.getExpireTime()))))
            {
                //System.out.println("后台刷新删除了"+key);
                remove(key);
            }
        }


    }

    public int getLength()
    {
        lock.readLock().lock();
        int size = map.size();
        lock.readLock().unlock();
        return size;
    }
}
