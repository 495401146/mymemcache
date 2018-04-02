# 测试说明

    特点:
    1.本服务端支持多客户端，所以可以同时访问，但是默认配置的处理线程池是10个，所以太高的并发处理可能会有问题，
    2.本服务端支持过期失效，并使用了惰性删除和定期删除结合的方式
    3.本服务端多支持一种add命令
    4.此服务端没有对flags进行检测，因为我没使用到，暂时没必要，只是对其是否为整形进行了检测
    5.支持delete的time策略，如果time大于0，则会拒绝add，且get获取为null，到期删除

    1.第一个客户端发送错误命令"et username 2323131 10 10\r\nhechangzhi\r\n"，会向客户端返回错误命令
    2.第二个客户端可以发送"get username \\r\\n",客户端只会收到一个end\r\n
    3.第三个客户端发送“set username 2323131 10 10\\r\\nhechangzhi\\r\\n”,一般情况下都会接收到stored请求
    4.发送"get username \\r\\n",应该会收到反馈，格式跟memcache协议一样
    5.sleep10秒再用"get username \\n"，应该不会获取到值，因为有定期删除策略，默认时间为10s，不过也不一定，不过sleep20s肯定获取不到
    6.发送“set username 2323131 10 0\\r\\nhechangzhi\\r\\n”
    7.发送“delete username 10 \\r\\n”
    8.发送"get username \\n"  
    
    //验证错误
    可以对set或者get的参数进行改变验证，都会收到正确的错误反馈
    比如说set可以让expire不为数字或者为不符合参数的数字等