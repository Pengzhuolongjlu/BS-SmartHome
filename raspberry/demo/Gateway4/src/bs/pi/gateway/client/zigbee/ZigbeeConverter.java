package bs.pi.gateway.client.zigbee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.test.Debugger;

import bs.pi.gateway.assist.Tool;
import bs.pi.gateway.main.IConverter;
import bs.pi.gateway.msg.HttpCommandArrivedMsg;
import bs.pi.gateway.msg.IMsg;
import bs.pi.gateway.msg.OtherZigbeeConnectedMsg;
import bs.pi.gateway.msg.OutSensorValuesComingMsg;
import bs.pi.gateway.msg.PortMsgArrivedMsg;
import bs.pi.gateway.msg.QueryZigbeeIsOnlineMsg;
import bs.pi.gateway.msg.ResponseToZigbeeOnlineQueryMsg;
import bs.pi.gateway.msg.SendMsgToAppMsg;
import bs.pi.gateway.msg.UploadExecuterValueToHttpServerMsg;
import bs.pi.gateway.msg.UploadSensorValueToHttpServerMsg;

public class ZigbeeConverter implements IConverter {
	
	private final static byte CMD_SEND_MSG_TO_APP = 0x01;
	private final static short CMD_UPLAOD_DATA = 0x02;
	private final static byte UPLOAD_DATA_INDEX_SENSOR_VALUE = 0x01;
	private final static byte UPLOAD_DATA_INDEX_EXECUTER_VALUE = 0x02;
	private final static byte EXECUTER_VALUE_OPENED = 0x01;
	private final static byte EXECUTER_VALUE_CLOSED = 0x02;
	private final static byte CMD_OUT_SENSOR_VALUES_COMING = 0x03;
	private final static byte CMD_QUERY_ZIGBEE_IS_ONLINE = 0x04;
	private final static byte CMD_RESPONSE_ZIGBEE_ONLINE = 0x05;
	private final static byte CMD_UPLOAD_ALL_DEVICE_VALUE = 0x06;
	
	private final static byte SENSOR_ID_IN_TEMPERATURE = 0x01;
	private final static byte SENSOR_ID_IN_HUMIDITY = 0x02;
	private final static byte SENSOR_ID_IN_HEAT = 0x03;
	private final static byte SENSOR_ID_HARMFUL_GAS = 0x04;
	private final static byte SENSOR_ID_FIRE = 0x05;

	private final static byte SENSOR_ID_OUT_TEMPERATURE = 0x11;
	private final static byte SENSOR_ID_OUT_HUMIDITY = 0x12;
	private final static byte SENSOR_ID_OUT_HEAT = 0x13;
	private final static byte SENSOR_ID_SOLID_HUMIDITY = 0x14;
	private final static byte SENSOR_ID_DUST_DENSITY = 0x15;
	private final static byte SENSOR_ID_LIGHT_INTENSITY = 0x16;
	
	private final static byte EXECUTER_ID_SOCKET1 = 0x01;
	private final static byte EXECUTER_ID_SOCKET2 = 0x02;
	private final static byte EXECUTER_ID_SOCKET3 = 0x03;
	
	private final static byte APP_ID_IN_ZIGBEE = 0x30;
	private final static byte APP_ID_IN_SENSOR = 0x31;
	private final static byte APP_ID_DANGER_ALARM = 0x32;
	private final static byte APP_ID_SIMPLE_EXECUTER = 0x33;
	private final static byte APP_ID_IRREMOTE = 0x34;
	private final static byte APP_ID_LCD = 0x35;
	private final static byte APP_ID_THH_UPDATA = 0x36;
	
	private final static byte APP_ID_OUT_ZIGBEE = (byte) 0x90;
	private final static byte APP_ID_OUT_SENSOR = (byte) 0x91;
	private final static byte APP_ID_NOTICE_OUT_SENSOR_VALUE = (byte) 0x92;
	
	/******SimpleExecuterApp 命令******/
	//开启执行器命令，格式："1byte命令头 + 1byte执行器ID
	private final static byte CMD_OPEN_SIMPLE_EXECUTER = 0x01;
	//关闭执行器命令，格式：1byte命令头 + 1byte执行器ID
	private final static byte CMD_CLOSE_SIMPLE_EXECUTER = 0x02;
	
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
		
		PortMsgArrivedMsg msg1 = zigbeeMsgReceive.getMsg();
		
		byte cmd0 = msg1.getCmd0();
		byte cmd1 = msg1.getCmd1();
		byte[] data = msg1.getData();
		
		//数据到来
		if(cmd0 == (byte)0x44 && cmd1 == (byte)0x81){
			if(data == null || data.length < 18)
				return null;
			byte len = data[16];
			if(len == 0x00)
				return null;
			byte cmd = data[17];
			//数据上传命令
			if(cmd == CMD_UPLAOD_DATA){
				byte[] data1 = new byte[len-1];
				System.arraycopy(data, 18, data1, 0, len-1);
				return resolveUplaodDataToHttpServerMsg(data1);
			}else if(cmd == CMD_QUERY_ZIGBEE_IS_ONLINE){
				byte[] NWKAddr = new byte[2];
				NWKAddr[0] = data[4];
				NWKAddr[1] = data[5];
				QueryZigbeeIsOnlineMsg queryZigbeeIsOnlineMsg = new QueryZigbeeIsOnlineMsg();
				queryZigbeeIsOnlineMsg.setSrcAddr(NWKAddr);
				return queryZigbeeIsOnlineMsg;
			}else if(cmd == CMD_OUT_SENSOR_VALUES_COMING && data.length == 42){
				byte bs[] = new byte[24];
				System.arraycopy(data, 18, bs, 0, 24);
				return resolveOutSensorValuesComing(bs);
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
	
	private IMsg resolveOutSensorValuesComing(byte[] data)
	{
		if(data == null || data.length != 24)
			return null;
		
		try{
			OutSensorValuesComingMsg outSensorValuesComingMsg = new OutSensorValuesComingMsg();
			byte bs1[] = new byte[4];
			System.arraycopy(data, 0, bs1, 0, 4);
			outSensorValuesComingMsg.setTemperature(Tool.bytesToFloat(bs1));
			System.arraycopy(data, 4, bs1, 0, 4);
			outSensorValuesComingMsg.setHumidity(Tool.bytesToFloat(bs1));
			System.arraycopy(data, 8, bs1, 0, 4);
			outSensorValuesComingMsg.setHeat(Tool.bytesToFloat(bs1));
			System.arraycopy(data, 12, bs1, 0, 4);
			outSensorValuesComingMsg.setDustConcentration(Tool.bytesToFloat(bs1));
			System.arraycopy(data, 16, bs1, 0, 4);
			outSensorValuesComingMsg.setLightIntensity(Tool.bytesToFloat(bs1));
			System.arraycopy(data, 20, bs1, 0, 4);
			outSensorValuesComingMsg.setSolidHumidity(Tool.bytesToFloat(bs1));
			return outSensorValuesComingMsg;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private IMsg resolveUplaodDataToHttpServerMsg(byte[] data){
		if(data == null || data.length<2)
			return null;
		byte uploadIndex = data[0];
		//上传传感器数据
		if(uploadIndex == UPLOAD_DATA_INDEX_SENSOR_VALUE && data.length == 6)
		{
			byte deviceID = data[1];
			byte[] valueBytes = new byte[4];
			System.arraycopy(data, 2, valueBytes, 0, 4);
			float value;
			try {
				value = Tool.bytesToFloat(valueBytes);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			UploadSensorValueToHttpServerMsg msg = new UploadSensorValueToHttpServerMsg();
			msg.setSensorValue(value);
			
			if(deviceID == SENSOR_ID_IN_TEMPERATURE)
				msg.setSensorID(UploadSensorValueToHttpServerMsg.SENSOR_ID_IN_TEMPERATURE);
			else if(deviceID == SENSOR_ID_IN_HUMIDITY)
				msg.setSensorID(UploadSensorValueToHttpServerMsg.SENSOR_ID_IN_HUMIDITY);
			else if(deviceID == SENSOR_ID_IN_HEAT)
				msg.setSensorID(UploadSensorValueToHttpServerMsg.SENSOR_ID_IN_HEAT);
			else if(deviceID == SENSOR_ID_OUT_TEMPERATURE)
				msg.setSensorID(UploadSensorValueToHttpServerMsg.SENSOR_ID_OUT_TEMPERATURE);
			else if(deviceID == SENSOR_ID_OUT_HUMIDITY)
				msg.setSensorID(UploadSensorValueToHttpServerMsg.SENSOR_ID_OUT_HUMIDITY);
			else if(deviceID == SENSOR_ID_OUT_HEAT)
				msg.setSensorID(UploadSensorValueToHttpServerMsg.SENSOR_ID_OUT_HEAT);
			else if(deviceID == SENSOR_ID_SOLID_HUMIDITY)
				msg.setSensorID(UploadSensorValueToHttpServerMsg.SENSOR_ID_SOLID_HUMIDITY);
			else if(deviceID == SENSOR_ID_DUST_DENSITY)
				msg.setSensorID(UploadSensorValueToHttpServerMsg.SENSOR_ID_DUST_DENSITY);
			else if(deviceID == SENSOR_ID_LIGHT_INTENSITY)
				msg.setSensorID(UploadSensorValueToHttpServerMsg.SENSOR_ID_LIGHT_INTENSITY);
			else if(deviceID == SENSOR_ID_FIRE){
				msg.setSensorID(UploadSensorValueToHttpServerMsg.SENSOR_ID_FIRE);
			}else if(deviceID == SENSOR_ID_HARMFUL_GAS){
				msg.setSensorID(UploadSensorValueToHttpServerMsg.SENSOR_ID_HARMFUL_GAS);
			}else
				return null;
			return msg;
		}else if(UPLOAD_DATA_INDEX_EXECUTER_VALUE == uploadIndex && data.length == 3){
			byte executerID = data[1];
			byte executerValue = data[2];
			
			UploadExecuterValueToHttpServerMsg msg = new UploadExecuterValueToHttpServerMsg();
			if(executerID == EXECUTER_ID_SOCKET1){
				msg.setExecuterID(UploadExecuterValueToHttpServerMsg.EXECUTER_ID_SOCKET1);
			}else if(executerID == EXECUTER_ID_SOCKET2){
				msg.setExecuterID(UploadExecuterValueToHttpServerMsg.EXECUTER_ID_SOCKET2);
			}else if(executerID == EXECUTER_ID_SOCKET3){
				msg.setExecuterID(UploadExecuterValueToHttpServerMsg.EXECUTER_ID_SOCKET3);
			}else{
				return null;
			}
			
			if(executerValue == EXECUTER_VALUE_OPENED){
				msg.setExecuterValue(UploadExecuterValueToHttpServerMsg.EXECUTER_VALUE_ON);
			}else{
				msg.setExecuterValue(UploadExecuterValueToHttpServerMsg.EXECUTER_VALUE_OFF);
			}
			return msg;
		}
		
		return null;
	}

	@Override
	public Object convertMsgSend(IMsg msg) {
		if(msg == null)
			return null;
		
		ZigbeeMsgSend zigbeeMsgSend = new ZigbeeMsgSend();
		zigbeeMsgSend.setDstEndpoint(cfg.getAppReg().getEndpoint());
		zigbeeMsgSend.setSrcEndpoint(cfg.getAppReg().getEndpoint());
		zigbeeMsgSend.setClusterID(cfg.getClusterID());
		zigbeeMsgSend.setOptions(cfg.getOptions());
		zigbeeMsgSend.setRadius(cfg.getRadius());
		zigbeeMsgSend.setTransID((byte) 0x00);
		
		if(IMsg.MSG_SEND_MSG_TO_APP.equals(msg.getName())){
			
			
		}else if(IMsg.MSG_RESPONSE_TO_ZIGBEE_ONLINE_QUERY.equals(msg.getName())){
			ResponseToZigbeeOnlineQueryMsg responseToZigbeeOnlineQueryMsg = (ResponseToZigbeeOnlineQueryMsg) msg;
			zigbeeMsgSend.setDstAddr(responseToZigbeeOnlineQueryMsg.getSrcAddr());
			byte data[] = {CMD_RESPONSE_ZIGBEE_ONLINE};
			zigbeeMsgSend.setData(data);
			return zigbeeMsgSend;
		}else if(IMsg.MSG_OUT_SENSOR_VALUES_COMING.equals(msg.getName())){
			byte[]dstAddr = getDstAddr(APP_ID_LCD);
			if(dstAddr == null)
				return null;
			
			OutSensorValuesComingMsg outSensorValuesComingMsg = (OutSensorValuesComingMsg) msg;
			
			zigbeeMsgSend.setDstAddr(dstAddr);
			byte data[] = new byte[25];
			data[0] = CMD_OUT_SENSOR_VALUES_COMING;
			byte[] bs2 = Tool.floatToBytes(outSensorValuesComingMsg.getTemperature());
			System.arraycopy(bs2, 0, data, 1, 4);
			bs2 = Tool.floatToBytes(outSensorValuesComingMsg.getHumidity());
			System.arraycopy(bs2, 0, data, 5, 4);
			bs2 = Tool.floatToBytes(outSensorValuesComingMsg.getHeat());
			System.arraycopy(bs2, 0, data, 9, 4);
			bs2 = Tool.floatToBytes(outSensorValuesComingMsg.getDustConcentration());
			System.arraycopy(bs2, 0, data, 13, 4);
			bs2 = Tool.floatToBytes(outSensorValuesComingMsg.getLightIntensity());
			System.arraycopy(bs2, 0, data, 17, 4);
			bs2 = Tool.floatToBytes(outSensorValuesComingMsg.getSolidHumidity());
			System.arraycopy(bs2, 0, data, 21, 4);
			zigbeeMsgSend.setData(data);
			
			return zigbeeMsgSend;
		}else if(IMsg.MSG_UPLAOD_ALL_DEVICE_VALUE.equals(msg.getName())){
			byte[] dstAddr = {(byte) 0xff, (byte) 0xff};
			zigbeeMsgSend.setDstAddr(dstAddr);
			byte data[] = {CMD_UPLOAD_ALL_DEVICE_VALUE};
			zigbeeMsgSend.setData(data);
			return zigbeeMsgSend;
		}else if(IMsg.MSG_HTTP_COMMAND_ARRIVED.equals(msg.getName())){
			HttpCommandArrivedMsg httpCmd = (HttpCommandArrivedMsg) msg;
			if(HttpCommandArrivedMsg.CMD_ON_SWITCH.equals(httpCmd.getCmd()) ||
					HttpCommandArrivedMsg.CMD_OFF_SWITCH.equals(httpCmd.getCmd())){
				byte[]dstAddr = getDstAddr(APP_ID_SIMPLE_EXECUTER);
				if(dstAddr == null)
					return null;
				zigbeeMsgSend.setDstAddr(dstAddr);
				byte data[] = new byte[4];
				data[0] = CMD_SEND_MSG_TO_APP;
				data[1] = APP_ID_SIMPLE_EXECUTER;
				data[2] = CMD_OPEN_SIMPLE_EXECUTER;
				if(HttpCommandArrivedMsg.CMD_OFF_SWITCH.equals(httpCmd.getCmd())){
					data[2] = CMD_CLOSE_SIMPLE_EXECUTER;
				}
				String executerIDStr = (String) httpCmd.getParams().get("sensorId");
				int sensorID = Integer.parseInt(executerIDStr);
				if(sensorID == 30)
					data[3] = EXECUTER_ID_SOCKET1;
				else if(sensorID == 34)
					data[3] = EXECUTER_ID_SOCKET2;
				else if(sensorID == 35)
					data[3] = EXECUTER_ID_SOCKET3;
				
				zigbeeMsgSend.setData(data);
				return zigbeeMsgSend;
			}
		}
		
		return null;
	}
	
	private byte[] getDstAddr(byte appID){
		
		ArrayList<ZigbeeInfo> zigbeeInfoList = cfg.getZigbeeInfoList();
		if(zigbeeInfoList == null || zigbeeInfoList.isEmpty())
			return null;

		for(ZigbeeInfo info : zigbeeInfoList){
			ArrayList<Byte> appIDList = info.getAppIDList();
			if(info.getNWKAddr() != null && 
					info.getNWKAddr().length == 2 && 
					appIDList != null && 
					appIDList.size()>0){
				for(byte id : appIDList){
					if(id == appID)
						return info.getNWKAddr();
				}
			}
		}
		
		return null;
	}
	
}
