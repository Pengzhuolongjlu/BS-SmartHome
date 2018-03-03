package com.demo;
import java.io.IOException;
import java.io.InputStream;

/*
 * port�ͻ�����Ϣ������
 */

public class PortReceiver implements MessageReceiver {

	private MessageReceiveCallBack callBack;
	private Thread thread;
	private boolean isStart;
	
	public PortReceiver(final InputStream inputStream){
		isStart = false;
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(callBack != null){
						byte[] buffer = new byte[1024];
			            int len = -1;
			            while ( isStart && (( len = inputStream.read(buffer)) > -1 ))
		                {
			            	Message msg = new Message();
			            	msg.setMsg(new String(buffer,0,len));
			            	callBack.received(msg);
		                }
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		});
	}
	
	@Override
	public void setReceiveCallBack(MessageReceiveCallBack callBack) {
		// TODO Auto-generated method stub
		this.callBack = callBack;
	}

	@Override
	public void start() {
		isStart = true;
		thread.start();
	}

	@Override
	public void stop() {
		isStart = false;
	}
}