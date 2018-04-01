package cmd;

import Message.Message;
import cache.Cache;
import connection.model.Connection;

public class GETCommand implements Command {
    private CMDType cmdType = CMDType.GET_CMD;
    private String[] keys;
    private Connection connection;

    public GETCommand( String[] keys, Connection connection) {
        this.keys = keys;
        this.connection = connection;
    }

    public Message execute() {
        //调用get方法
        StringBuilder sb = new StringBuilder();
        //VALUE <key> <flags> <bytes>\r\n
        for (String key:keys) {
            String value = Cache.getValue(key);
            if(value==null)
            {
                continue;
            }
            String flags = Cache.getFlag(key);
            sb.append("VALUE "+key+" "+flags+" "+value.length()+"\\r\\n"+
            value+"\r\n");
        }
        System.out.println("get操作返回的数据："+sb.toString());
        Message message = new Message(sb.toString(),CMDType.GET_CMD,connection);
        return message;
    }
    //public Message getMessage()


    public CMDType getCmdType() {
        return this.cmdType;
    }
}
