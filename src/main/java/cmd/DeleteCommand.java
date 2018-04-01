package cmd;

import Message.Message;
import cache.Cache;
import cache.model.DictValue;
import connection.model.Connection;

/**
 * delete命令
 */
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
        //当无此key存在，返回NOT FOUND
        if(dictValue==null)
        {
            builder.append(Response.CMD_DELETE_NO_EXISTS);
        }
        //当有key时，调用Cache类进行处理，根据返回值判断成功失败
        else{
            if(!Cache.delete(key,this.time))
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
