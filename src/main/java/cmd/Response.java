package cmd;

public class Response {
    //set操作
    public static final String CMD_SET_SUCCESS = "STORED\\r\\n";
    public static final String CMD_SET_EXISTS = "NOT_STORED\\r\\n";

    //delete操作
    public static final String CMD_DELETE_SUCCESS = "DELETED\\r\\n";
    public static final String CMD_DELETE_NO_EXISTS = "NOT_FOUND\\r\\n";

    //出现错误
    //出现不存在的命令
    public static final String ERROR_NO_EXISTS_CMD = "ERROR\\r\\n";
    //客户端出现错误
    public static final String ERROR_CLIENT = "CLIENT_ERROR <error>\\r\\n";
    //服务器端出现错误
    public static final String ERROR_SERVER = "SERVER_ERROR <error>\\r\\n";
}