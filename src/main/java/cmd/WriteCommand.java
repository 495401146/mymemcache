package cmd;

import Message.Message;
import Message.MessageType;
import cache.Cache;
import connection.model.Connection;

public class WriteCommand implements Command {
    private String key;
    private String value;
    private String expire;
    private String flags;
    private Connection connection;

    public WriteCommand(String key,String value,String expire
            ,String flags,Connection connection) {
        this.key = key;
        this.value = value;
        this.expire = expire;
        this.flags = flags;
        this.connection = connection;
    }

    public Message execute() {
        StringBuilder sb = new StringBuilder();
        if(Cache.getValue(this.key)!=null)
        {
            sb.append(Response.CMD_SET_EXISTS);
        }
        else{
            Cache.set(this.key,this.value,this.flags);
            sb.append(Response.CMD_SET_SUCCESS);
        }
        System.out.println("设置键值，key:"+this.key+",value:"+this.value);
        Message message = new Message(sb.toString(),CMDType.SET_CMD,
                                 connection);
        return message;
    }

    public CMDType getCmdType() {
        return CMDType.SET_CMD;
    }
}
