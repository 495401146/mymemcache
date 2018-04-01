package cmd;

import Message.Message;
import connection.model.Connection;
//当出现错误命令时调用此类
public class ErrorCommand implements Command {
    private Connection connection;
    private String errMessage;
    private CMDType cmdType = CMDType.Error_CMD;

    public ErrorCommand(Connection connection,String errMessage)
    {
        this.connection = connection;
        this.errMessage = errMessage;
    }


    public Message execute() {
        Message message = new Message(errMessage,CMDType.Error_CMD,connection);
        return message;
    }

    public CMDType getCmdType() {
        return this.cmdType;
    }
}
