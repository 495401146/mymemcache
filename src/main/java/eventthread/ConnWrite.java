package eventthread;

import Message.Message;
import connection.model.Connection;
import connection.model.ConnectionState;
import eventthread.model.ReadState;
import eventthread.model.WriteToClientState;
import org.apache.log4j.Logger;
import sun.misc.ClassFileTransformer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 回写线程，当conn处于回写状态时调用
 */
public class ConnWrite implements Runnable {
    private static Logger logger = Logger.getLogger(ConnWrite.class);
    private Connection connection;

    public ConnWrite(Connection connection)
    {
        logger.info(Thread.currentThread().getName()+":create a write Conn thread");
        this.connection = connection;
    }
    public void run() {
        assert connection!=null;
        //写入成功就继续解析命令
        if(writeToClient())
        {
            logger.info(Thread.currentThread().getName()+"restart parse command");
            connection.setConnectionState(ConnectionState.CONN_PARSE_CMD);
        }
        try {
            connection.getConnections().put(connection);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            logger.error(Thread.currentThread().getName()+":put connection  is error");
            connection = null;
        }
    }

    private boolean writeToClient()
    {
        Socket socket = connection.getClientSocket();
        OutputStream os = null;
        try {
            os = socket.getOutputStream();
            Message message = connection.getMessage();
            os.write(message.getMsg().getBytes());
            //os.flush();
        } catch (Exception e) {
            //出现异常，写入失败，直接进入close状态
            logger.error(Thread.currentThread().getName()+"write thread is exception");
            logger.error(e.getMessage());
            connection.setConnectionState(ConnectionState.CONN_CLOSING);
            return false;
        }
        return true;
    }

}
