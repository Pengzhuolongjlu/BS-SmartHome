package com.demo;


/*
 * 消息客户端
 * 消息客户端可以是串口消息客户端、mqtt客户端、http客户端、websocket客户端等等，可以按需拓展
 */
public interface MessageClient {
	
	//连接
	void connect() throws Exception;
	
	//消息接收回调，有消息来就会回调
	void setReceiveCallBack(MessageReceiveCallBack callBack);
	
	//开始消息接收
	void startReceive();
	
	//停止消息接收
	void stopReceive();
	
	//发送一条消息
	boolean send(Message message);
}
