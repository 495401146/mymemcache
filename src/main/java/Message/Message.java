package Message;

import cmd.CMDType;
import connection.model.Connection;

import java.net.Socket;

public class Message {
    String msg = "";
    CMDType cmdType;
    Connection connection;

    public Message(String msg, CMDType cmdType, Connection connection) {
        this.msg = msg;
        this.cmdType = cmdType;
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public CMDType getCmdType() {
        return cmdType;
    }

    public void setCmdType(CMDType cmdType) {
        this.cmdType = cmdType;
    }

}
