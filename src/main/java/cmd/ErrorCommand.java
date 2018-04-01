package cmd;

import Message.Message;
import connection.model.Connection;

public class ErrorCommand implements Command {
    private Connection connection;
    private String errMessage;

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
        return null;
    }
}
