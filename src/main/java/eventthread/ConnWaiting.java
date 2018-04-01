package eventthread;

import config.Config;
import connection.model.Connection;
import connection.model.ConnectionState;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 * 等待线程，每个连接的初始都是wait，就会调用此函数，当有数据写入时，更新其状态为读状态
 */
public class ConnWaiting implements Runnable {
    private static final Logger logger = Logger.getLogger(ConnWaiting.class);
    private Connection connection;
    //用于检测等待次数，如果等待时间过长，转入closing状态
    private int waitNum = 0;

    public ConnWaiting(Connection connection)
    {
        logger.info(Thread.currentThread().getName()+":create a ConnWaiting thread");
        this.connection = connection;
    }
    public void run() {
        logger.info(Thread.currentThread().getName()+":start waiting thread");
        Socket socket = connection.getClientSocket();
        while(true)
        {
            try {
                waitNum++;
                if(waitNum> Config.WAIT_NUM)
                {
                    logger.info(Thread.currentThread().getName()+"waiting time is too long,close the connection");
                    connection.setConnectionState(ConnectionState.CONN_CLOSING);
                    break;
                }
                //当有数据时，转入读状态
                if(socket.getInputStream().available()>0)
                {
                    connection.setConnectionState(ConnectionState.CONN_READ);
                    break;
                }
                Thread.sleep(100);
            } catch (Exception e) {
                logger.error(Thread.currentThread().getName()+":the thread sleep is" +
                        "have exception");
                logger.error(e.getMessage());
                break;
            }
        }
        try {
            connection.getConnections().put(connection);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            logger.error(Thread.currentThread().getName()+":put connection is error");
            connection = null;
        }
    }
}
