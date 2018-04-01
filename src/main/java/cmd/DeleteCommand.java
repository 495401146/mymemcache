package cmd;

import Message.Message;
import cache.Cache;
import connection.model.Connection;

public class DeleteCommand{
    private Connection connection;
    private String key;
    private String time;


    public DeleteCommand(Connection connection, String key, String time) {
        this.connection = connection;
        this.key = key;
        this.time = time;
    }

    public Message execute() {
        String value = Cache.getValue(this.key);
        StringBuilder builder = new StringBuilder();
        if(value==null)
        {
            builder.append(Response.CMD_DELETE_NO_EXISTS);
        }
        else{
            Cache.delete(key);
            builder.append(Response.CMD_DELETE_SUCCESS);
        }
        Message message = new Message(builder.toString(),CMDType.DELETE_CMD,connection);
        return message;
    }
}
