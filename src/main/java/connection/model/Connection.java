package connection.model;

import Message.Message;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * 连接类，此类是最关键类的，用于在线程中传递，他的状态改变可以触发对应的事件
 * 保存了整个连接的所有信息
 */
public class Connection {
    //存储与客户端保持连接的socket
    private Socket clientSocket;

    //读取到的字节数
    private StringBuilder builder = new StringBuilder();


    //所在的connection容器
    private volatile BlockingQueue<Connection> connections;
    //对应的连接状态
    private volatile ConnectionState connectionState;

    //回写的message
    Message message;
    //命令解析后需要读取的数据块长度
    private StringBuilder itemBuilder = new StringBuilder();

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public StringBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(StringBuilder builder) {
        this.builder = builder;
    }

    public BlockingQueue<Connection> getConnections() {
        return connections;
    }

    public void setConnections(BlockingQueue<Connection> connections) {
        this.connections = connections;
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(ConnectionState connectionState) {
        this.connectionState = connectionState;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Connection(Socket clientSocket, ConnectionState connectionState,
                      BlockingQueue<Connection> connections)
    {

        this.clientSocket = clientSocket;
        this.connectionState = connectionState;
        this.connections = connections;
    }


}
