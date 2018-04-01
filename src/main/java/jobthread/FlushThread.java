package jobthread;

import cache.model.DictValue;
import cache.model.LruCache;
import config.Config;


public class FlushThread implements Runnable {
    private boolean stop = Config.STOP;
    public FlushThread()
    {
        System.out.println("启动了定时清洗任务");
    }
    public void run() {
        while(!stop)
        {
            stop = Config.STOP;
            LruCache<String,DictValue> lruCache = LruCache.newInstance();
            //System.out.println("flushTHREAD:" +lruCache);
            if(lruCache.getLength()!=0)
            {
                System.out.println("开始清洗");
                lruCache.flush();
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
