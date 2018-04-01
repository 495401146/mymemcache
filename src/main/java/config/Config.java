package config;

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
}
