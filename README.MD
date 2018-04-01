# 用java简单实现memcache服务端

    1.传输机制：参照memcache实现了get，set，和delete等的传输机制
    2.事件分发:当一个socket连上之后为这个socket维护一个connection，里面包含写入的消息和写出的消息
    3.存储采用linkedhashmap中的lru实现