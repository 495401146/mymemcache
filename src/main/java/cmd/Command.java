package cmd;

import Message.Message;

public interface Command {
    public Message execute();
    public CMDType getCmdType();
}
