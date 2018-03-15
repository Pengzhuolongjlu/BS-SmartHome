package bs.pi.gateway.processor;

import java.util.ArrayList;

import bs.pi.gateway.main.IProcessor;
import bs.pi.gateway.main.ISender;
import bs.pi.gateway.msg.IMsg;
import bs.pi.gateway.msg.SendMsgToAppMsg;
import bs.pi.gateway.msg.UploadDataToHttpServerMsg;
import bs.pi.gateway.msg.OtherZigbeeConnectedMsg;

public class MyProcessor implements IProcessor {

	private ArrayList<ISender> senderList;
	private ArrayList<IMsg> msgList;
	private Thread processThread = new Thread(new Runnable() {
		@Override
		public void run() {
			while( ! processThread.isInterrupted()){
				IMsg msg = nextMsg();
				if(msg != null)
					process(msg);
			}
		}
	});
	
	public MyProcessor(){
		msgList = new ArrayList<IMsg>();
	}
	
	@Override
	public void setSenders(ArrayList<ISender> senderList) {
		this.senderList = senderList;
	}

	@Override
	public void handleEvent(IMsg msg) {
		if(msg != null){
			synchronized (msgList) {
				msgList.add(msg);
				msgList.notifyAll();
			}
		}
	}
	
	@Override
	public void start(){
		processThread.start();
	}
	
	@Override
	public void stop(){
		//��nextMsg���˳�
		msgList.notifyAll();
		
		processThread.interrupt();
	}
	
	private IMsg nextMsg(){
		IMsg msg = null;
		synchronized (msgList) {
			if(msgList.size() == 0){
				try {
					msgList.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(msgList.size() > 0)
				msg = msgList.remove(0);
			msgList.notifyAll();
		}
		return msg;
	}
	
	private void process(IMsg msg){
		System.out.println(msg.getName());
		
		if(UploadDataToHttpServerMsg.MSG_NAME.equals(msg.getName())){
			send(ISender.V_SEND_NAME_HTTP_SNEDER, msg);
		}else if(SendMsgToAppMsg.MSG_NAME.equals(msg.getName())){
			send(ISender.V_SEND_NAME_ZIGBEE_SNEDER, msg);
		}else if(OtherZigbeeConnectedMsg.MSG_NAME.equals(msg.getName())){
			send(ISender.V_SEND_NAME_ZIGBEE_SNEDER, msg);
		}
	}
	
	private void send(String senderName, IMsg msg){
		for(ISender sender : senderList){
			if(senderName.equals(sender.getName())){
				try {
					sender.send(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
