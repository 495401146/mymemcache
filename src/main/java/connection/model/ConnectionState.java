package connection.model;

/**
 * 连接状态，分发线程根据状态进行不同处理子线程的分发
 */
public enum ConnectionState {
    CONN_WAITING,
    CONN_PARSE_CMD,
    CONN_READ,
    CONN_WRITE,
    CONN_CLOSING,
}
