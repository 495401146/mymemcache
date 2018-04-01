package cache.model;

import cmd.CommandFactory;
import config.Config;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LruCache<K,V> {
    private static int MAX_LENGTH = Config.WRITE_THREAD_NUM;  //最大长度
    private static int initLen = Config.INIT_LEN;//初始长度
    private LinkedHashMap<K, V> map;
    private ReadWriteLock lock = new ReentrantReadWriteLock(); //读写锁
    private static volatile LruCache lruCache;


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


    public void flush()
    {
        System.out.println("进行清洗");
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
            Date nowDate = new Date();
            if(nowDate.before(new Date(dictValue.getExpireTime())))
            {
                lock.writeLock().lock();
                remove(key);
                lock.writeLock().unlock();
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
