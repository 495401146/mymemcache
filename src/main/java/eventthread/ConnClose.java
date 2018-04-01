package eventthread;

import connection.model.Connection;
import org.apache.log4j.Logger;

/**
 * 线程关闭事件，当state为CONN_CLOSE时，调用其run方法
 */
public class ConnClose implements Runnable {
    private static final Logger logger = Logger.getLogger(ConnClose.class);
    private Connection connection;
    private int fail_Num = 0;

    public ConnClose(Connection connection)
    {
        logger.info(Thread.currentThread().getName()+"create a connClose thread");
        this.connection = connection;
    }
    //清除connection状态
    public void run() {
        assert connection !=null;
        connection.getConnections().remove(connection);
        closeSocket(connection);
        connection = null;
    }
    //关闭socket，进行重试，三次以上结束
    public void closeSocket(Connection connection)
    {
        try {
            if(fail_Num>3)
            {
                logger.error(Thread.currentThread().getName()+"close the socket is fail more 3 times");
                return;
            }
            connection.getClientSocket().close();
        } catch (Exception e) {
            fail_Num++;
            logger.error(e.getMessage());
            closeSocket(connection);
        }
    }
}
