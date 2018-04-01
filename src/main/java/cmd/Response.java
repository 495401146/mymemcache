package cmd;
//对所有固定的响应消息做封装
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
    public static final String ERROR_CLIENT_GET_NO_PARAMS = "CLIENT_ERROR "
            +" get command is no params\\r\\n";
    public static final String ERROR_CLIENT_SET_PARAMS_NUM_NOEXCEPTED
            = "CLIENT_ERROR <set params num is not my excepted>\\r\\n";
    public static final String ERROR_CLIENT_SET_BYTES_NOT_MATCH_VALUE
            = "CLIENT_ERROR <set bytes is not equals data block length>\\r\\n";
    public static final String ERROR_CLIENT_SET_PARAMS_NOT_VERIFY
            = "CLIENT_ERROR <set params is not my excepted>\\r\\n";
    public static final String ERROR_CLIENT_DELETE_PARAMS_NUM_NOEXCEPTED
            = "CLIENT_ERROR <delete params num is not my excepted>\\r\\n";
    public static final String ERROR_CLIENT_DELETE_PARAMS_NOT_VERIFY
            = "CLIENT_ERROR <delete params is not my excepted>\\r\\n";
    public static final String ERROR_CLIENT_END_PARAMS_NOT_MATCH =
            "CLIENT_ERROR <delete end params num is not my excepted";
    //服务器端出现错误
    public static final String ERROR_SERVER_GET = "SERVER_ERROR <get command is wrong>\\r\\n";
    public static final String ERROR_SERVER_SET = "SERVER_ERROR <get command is wrong>\\r\\n";
    public static final String ERROR_SERVER_DELETE = "SERVER_ERROR <get command is wrong>\\r\\n";
}