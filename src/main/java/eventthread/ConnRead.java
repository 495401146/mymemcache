package eventthread;

import config.Config;
import connection.model.Connection;
import connection.model.ConnectionState;
import eventthread.model.ReadState;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.net.Socket;

/**
 * 读状态，用于写入数据
 */
public class ConnRead implements Runnable {
    private static final Logger logger = Logger.getLogger(ConnRead.class);
    private boolean flag = false;
    private Connection connection;

    public ConnRead(Connection connection) {
        logger.info(Thread.currentThread().getName()+":create a readConn thread");
        this.connection = connection;
    }

    public void run() {
        logger.info(Thread.currentThread().getName()+":start read");
        assert connection!=null;
        int state = readFromNetwork(connection);
        //没有读到数据，转到waiting状态
        if(ReadState.READ_NO_DATA_RECEIVED==state)
        {
            logger.info(Thread.currentThread().getName()+":current have no data,change to waiting");
            connection.setConnectionState(ConnectionState.CONN_WAITING);
        }
        //读到了数据，进入解析状态
        else if(ReadState.READ_DATA_RECEIVED==state){
            logger.info(Thread.currentThread().getName()+":have date,start parse command");
            connection.setConnectionState(ConnectionState.CONN_PARSE_CMD);
        }
        //出错，进入close状态
        else{
            logger.error(Thread.currentThread().getName()+":read wrong,close connection");
            connection.setConnectionState(ConnectionState.CONN_CLOSING);
        }
        //将连接放入阻塞队列
        try {
            connection.getConnections().put(connection);
        } catch (InterruptedException e) {
            logger.error(Thread.currentThread().getName()+":put connection is error");
            logger.error(e.getMessage());
            connection = null;
        }
    }

    //进行读取
    private int readFromNetwork(Connection connection) {
        assert connection!=null;
        //System.out.println("开始读客户端命令");
        try {
            Socket socket = connection.getClientSocket();
            if (socket == null) {
                //throw new Exception.NullConnectionException("Socket is NULL");
            }
            InputStream in = socket.getInputStream();
            //如果输入流没有数据，返回无数据可读状态
            if(in.available()==0)
            {
                return ReadState.READ_NO_DATA_RECEIVED;
            }
            byte[] buf = new byte[Config.READ_Bytes_PER];
            int len;
            if((len = in.read(buf)) != -1) {
                String str = new String(buf,0,len,"utf8");
                connection.getBuilder().append(str);
            }
            //System.out.println("正在读的builder："+connection.getBuilder().toString());
        } catch (Exception e) {
            //出现异常，返回读出错状态
            return ReadState.READ_ERROR;
            //e.printStackTrace();
        }
        //读取成功，返回成功状态
        return ReadState.READ_DATA_RECEIVED;
    }
}
