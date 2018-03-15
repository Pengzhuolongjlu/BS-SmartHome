package bs.pi.gateway.client.zigbee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.test.Debugger;

import bs.pi.gateway.assist.Tool;
import bs.pi.gateway.main.IConverter;
import bs.pi.gateway.msg.IMsg;
import bs.pi.gateway.msg.PortMsgReceivedMsg;
import bs.pi.gateway.msg.SendMsgToAppMsg;
import bs.pi.gateway.msg.UploadDataToHttpServerMsg;
import bs.pi.gateway.msg.OtherZigbeeConnectedMsg;

public class ZigbeeConverter implements IConverter {
	
	private byte[] dstAddr1;
	private byte[] dstAddr2;
	private ZigbeeClientCfg cfg;
	
	public ZigbeeConverter(ZigbeeClientCfg cfg){
		this.cfg = cfg;
	}
	
	@Override
	public IMsg convertMsgReceive(Object msg) {
		if(msg ==null)
			return null;
		
		ZigbeeMsgReceive zigbeeMsgReceive = null;
		try{
			zigbeeMsgReceive = (ZigbeeMsgReceive)msg;
		}catch(Exception e){
			return null;
		}
		
		PortMsgReceivedMsg msg1 = zigbeeMsgReceive.getMsg();
		
		byte cmd0 = msg1.getCmd0();
		byte cmd1 = msg1.getCmd1();
		byte[] data = msg1.getData();
		
		//数据到来
		if(cmd0 == (byte)0x44 && cmd1 == (byte)0x81){
			if(data != null || data.length > 17){
				byte len = data[16];
				byte cmd3 = data[17];
				byte cmd4 = data[18];
				//0x0001为数据上传命令
				if(cmd3 == 0x00 && cmd4 == 0x01 && len == 8){
					byte[] data1 = new byte[6];
					System.arraycopy(data, 19, data1, 0, 6);
					return resolveUplaodDataToHttpServerCMsg(data1);
				}
			}
		}else if(cmd0 == (byte)0x45 && cmd1 == (byte)0xC1){
			//如果当前zigbee作协调器，有其他节点连接到协调器的时候会收到这条信息，包含了其他zigbee的硬件地址和网络地址
				byte[] IEEEAddr = new byte[8];
				System.arraycopy(data, 4, IEEEAddr, 0, 8);
				byte[] NWKAddr = new byte[2];
				NWKAddr[0] = data[2];
				NWKAddr[1] = data[3];
				return new OtherZigbeeConnectedMsg(IEEEAddr, NWKAddr);
			}
		
		return null;
	}
	
	private IMsg resolveUplaodDataToHttpServerCMsg(byte[] data){
		int sensorID = data[0]*256 + data[1];
		byte[] valueBytes = new byte[4];
		System.arraycopy(data, 2, valueBytes, 0, 4);
		float sensorValue;
		try {
			sensorValue = Tool.bytesToFloat(valueBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		UploadDataToHttpServerMsg msg = new UploadDataToHttpServerMsg();
		msg.setSensorID(sensorID);
		msg.setSensorValue(sensorValue);
		
		return msg;
	}

	@Override
	public Object convertMsgSend(IMsg msg) {
		if(msg == null)
			return null;
		if(SendMsgToAppMsg.MSG_NAME.equals(msg.getName())){
			SendMsgToAppMsg sendMsgToAppMsg = (SendMsgToAppMsg)msg;
			byte[] appID = Tool.intTo2Byte(sendMsgToAppMsg.getAppID());
			String cmd = sendMsgToAppMsg.getCmd();
			HashMap<String, Object> params = sendMsgToAppMsg.getParams();
			
			byte[] data = null;
			if(SendMsgToAppMsg.CMD_TEST_CMD.equals(cmd)){
				data = new byte[4];
				data[0] = appID[0];
				data[1] = appID[1];
				data[2] = ZigbeeMsgSend.CMD_TEST_CMD[0];
				data[3] = ZigbeeMsgSend.CMD_TEST_CMD[1];
			}

			byte[] dstAddr = getDstAddr(appID);
			if(dstAddr == null)
				return null;
			
			ZigbeeMsgSend zigbeeMsgSend = new ZigbeeMsgSend();
			zigbeeMsgSend.setDstAddr(dstAddr);
			zigbeeMsgSend.setDstEndpoint(cfg.getAppReg().getEndpoint());
			zigbeeMsgSend.setSrcEndpoint(cfg.getAppReg().getEndpoint());
			zigbeeMsgSend.setData(data);
			zigbeeMsgSend.setClusterID(cfg.getClusterID());
			zigbeeMsgSend.setOptions(cfg.getOptions());
			zigbeeMsgSend.setRadius(cfg.getRadius());
			zigbeeMsgSend.setTransID((byte) 0x00);
			return zigbeeMsgSend;
		}
		return null;
	}
	
	private byte[] getDstAddr(byte[] appID){
		
		ArrayList<ZigbeeInfo> zigbeeInfoList = cfg.getZigbeeInfoList();
		if(zigbeeInfoList == null || zigbeeInfoList.isEmpty())
			return null;

		for(ZigbeeInfo info : zigbeeInfoList){
			ArrayList<byte[]> appIDList = info.getAppIDList();
			if(info.getNWKAddr() != null && 
					info.getNWKAddr().length == 2 && 
					appIDList != null && 
					appIDList.size()>0){
				for(byte[] id : appIDList){
					if(Arrays.equals(id, appID))
						return info.getNWKAddr();
				}
			}
		}
		
		return null;
	}
	
}
