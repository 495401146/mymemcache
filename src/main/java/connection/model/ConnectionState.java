package connection.model;

public enum ConnectionState {
    CONN_WAITING,
    CONN_PARSE_CMD,
    CONN_READ,
    CONN_WRITE,
    CONN_CLOSING,
    CONN_CLOSED,
    CONN_NEW_CMD
}
