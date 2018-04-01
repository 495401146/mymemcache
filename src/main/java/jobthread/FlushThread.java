package jobthread;

import cache.model.DictValue;
import cache.model.LruCache;
import config.Config;

import org.apache.log4j.Logger;

/**
 * 后台线程，用于定期对过期键进行删除
 */
public class FlushThread implements Runnable {
    private static final Logger logger = Logger.getLogger(FlushThread.class);
    private boolean stop = Config.STOP;
    LruCache<String,DictValue> lruCache = LruCache.newInstance();
    public FlushThread()
    {
        logger.info(Thread.currentThread().getName()+":start a flush thread");
    }
    //进行刷新，config中stop用于控制
    public void run() {
        while(!stop)
        {
            logger.info(Thread.currentThread().getName()+":start flush");
            stop = Config.STOP;
            //System.out.println("用户名"+lruCache.get("username"));
            if(lruCache.getLength()!=0)
            {
                //System.out.println("刷新");
                lruCache.flush();
                logger.info(Thread.currentThread().getName()+"success flush");
            }
            try {
                //定时用sleep控制
                Thread.sleep(Config.FLUSH_TIMES);
            } catch (InterruptedException e) {
                logger.error(Thread.currentThread().getName()+"thread is exception");
            }
        }
    }
}
