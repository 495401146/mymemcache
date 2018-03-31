package Message;

import cmd.CMDType;

import java.net.Socket;

public class Message {
    Socket socket;
    String msg;
    CMDType cmdType;
    MessageType messageType;

    public Message(Socket socket, String msg, CMDType cmdType, MessageType messageType) {
        this.socket = socket;
        this.msg = msg;
        this.cmdType = cmdType;
        this.messageType = messageType;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
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

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
