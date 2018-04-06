package bs.pi.gateway.client.zigbee;

import java.util.Arrays;

import com.test.Debugger;

import bs.pi.gateway.main.IConverter;
import bs.pi.gateway.main.ISender;
import bs.pi.gateway.msg.IMsg;
import bs.pi.gateway.msg.PortSendResponseMsg;
import bs.pi.gateway.msg.SendPortMsgMsg;
import bs.pi.gateway.msg.OtherZigbeeConnectedMsg;
import bs.pi.gateway.msg.ZigbeeSendReponseMsg;

public class ZigbeeSender implements ISender {

	private ZigbeeClientCfg cfg;
	private ISender portSender;
	private IConverter converter;
	
	public ZigbeeSender(ISender portSender, IConverter converter, ZigbeeClientCfg cfg){
		this.portSender = portSender;
		this.converter = converter;
		this.cfg = cfg;
	}
	
	@Override
	public String getName() {
		return ISender.NAME_ZIGBEE_SENDER;
	}

	@Override
	public IMsg send(IMsg msg) {
		ZigbeeSendReponseMsg zigbeeSendReponseMsg = new ZigbeeSendReponseMsg();
		Object obj = converter.convertMsgSend(msg);
		if(obj != null){
			ZigbeeMsgSend zigbeeMsgSend = (ZigbeeMsgSend)obj;
			SendPortMsgMsg portSendMsg = new SendPortMsgMsg();
			portSendMsg.setData(CodeGenerator.packetSend(zigbeeMsgSend));
			PortSendResponseMsg portSendResponseMsg = (PortSendResponseMsg) portSender.send(portSendMsg);
			//���ͳɹ�
			zigbeeSendReponseMsg.setSendSuccess(portSendResponseMsg.getSendSuccess());
		}else{
			zigbeeSendReponseMsg.setSendSuccess(false);
		}
		
		return zigbeeSendReponseMsg;
	}
}
