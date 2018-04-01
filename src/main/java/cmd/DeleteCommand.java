package cmd;

import Message.Message;
import cache.Cache;
import cache.model.DictValue;
import connection.model.Connection;

public class DeleteCommand implements Command{
    private Connection connection;
    private String key;
    private String time;
    private CMDType cmdType = CMDType.DELETE_CMD;


    public DeleteCommand(Connection connection, String key, String time) {
        this.connection = connection;
        this.key = key;
        this.time = time;
    }

    public Message execute() {
        DictValue dictValue = Cache.get(this.key);
        StringBuilder builder = new StringBuilder();
        if(dictValue==null)
        {
            builder.append(Response.CMD_DELETE_NO_EXISTS);
        }
        else{
            if(!Cache.delete(key))
            {
                Message message= new Message(Response.ERROR_SERVER_DELETE,
                        CMDType.DELETE_CMD,connection);
                return message;
            }
            builder.append(Response.CMD_DELETE_SUCCESS);
        }
        Message message = new Message(builder.toString(),CMDType.DELETE_CMD,connection);
        return message;
    }

    public CMDType getCmdType() {
        return this.cmdType;
    }
}
