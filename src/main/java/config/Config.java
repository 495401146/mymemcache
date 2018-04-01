package config;

/**
 * 配置类，线程池的大小，存储的初始大小等都可以进行配置
 */
public class Config {
    //读线程数
    public static final int WRITE_THREAD_NUM = 10;

    //用作钩子，可以使线程结束
    public static final boolean STOP = false;

    //LRUCache初始长度
    public static final int INIT_LEN = 16;
    //LRUCache最大长度，超过此长度开始淘汰
    public static final int MAX_LEN = 1<<32;

    //最大连接数
    public static final int MAX_CONNECTION_NUM = 1024;

    //flushThread定时刷新的时间
    public static final int FLUSH_TIMES = 1*1000;

    //等待线程超时次数
    public static final int WAIT_NUM = 100;

    //控制读状态时写入的大小
    public static final int READ_Bytes_PER = 20;

    //当读取buffer的字节数已经大于这个数但是还没读到/n时报异常
    public static final int EXCEPTED_BYTES = 1024;
    //服务器端口号
    public static final int port = 11000;
}
