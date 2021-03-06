package bs.pi.gateway.client.zigbee;

import java.util.ArrayList;
import java.util.Arrays;

import bs.pi.gateway.client.port.PortClient;
import bs.pi.gateway.main.IClient;
import bs.pi.gateway.main.IConverter;
import bs.pi.gateway.main.IReceivedListener;
import bs.pi.gateway.main.IReceiver;
import bs.pi.gateway.main.ISender;
import bs.pi.gateway.msg.IMsg;
import bs.pi.gateway.msg.OtherZigbeeConnectedMsg;
import bs.pi.gateway.msg.PortSendResponseMsg;
import bs.pi.gateway.msg.QueryZigbeeIsOnlineMsg;
import bs.pi.gateway.msg.ResponseToZigbeeOnlineQueryMsg;
import bs.pi.gateway.msg.SendPortMsgMsg;

public class ZigbeeClient implements IClient{

	public static final String DEFAULT_CFG_PATH = System.getProperty("user.dir")+System.getProperty("file.separator")+"zigbeeClientCfg.properties";
	private String cfgPath;
	private ZigbeeClientCfg cfg;
	private PortClient portClient;
	private ISender portSender;
	private ZigbeeSender zigbeeSender;
	private ZigbeeReceiver zigbeeReceiver;
	private IConverter converter;
	//监听器，用于捕捉硬件地址对应的网络地址
	private IReceivedListener receivedListener = new IReceivedListener() {
		@Override
		public void handleEvent(IMsg msg) {
			ArrayList<ZigbeeInfo> zigbeeInfoList =  cfg.getZigbeeInfoList();
			if(zigbeeInfoList == null || zigbeeInfoList.isEmpty())
				return;
			
			if(IMsg.MSG_OTHER_ZIGBEE_CONNECTED.equals(msg.getName())){
				OtherZigbeeConnectedMsg connectedMsg = (OtherZigbeeConnectedMsg) msg;
				for(ZigbeeInfo info : zigbeeInfoList){
					if(Arrays.equals(connectedMsg.getIEEEAddr() , info.getIEEEAddr())){
						info.setNWKAddr(connectedMsg.getNWKAddr());
					}
				}
				
			}
		}
	};
	
	public ZigbeeClient(String cfgPath, PortClient portClient){
		this.cfgPath = cfgPath;
		this.portClient = portClient;
	}
	
	@Override
	public void init() throws Exception {
		
		if(cfgPath == null)
			cfgPath = DEFAULT_CFG_PATH;
		cfg = new ZigbeeClientCfg(cfgPath);

		portClient.init();
		portClient.start();
		portSender = portClient.getSender();
		IReceiver portReceiver = portClient.getReceiver();
		portReceiver.flush();
		
		portSend(CodeGenerator.CMD_STARTUP_WITHOUT_LAST_STATE);
		Thread.sleep(1);
		portSend(CodeGenerator.CMD_DEVICE_RESET);
		Thread.sleep(1500);
		portSend(CodeGenerator.chanlistCfg(cfg.getChannel()));
		Thread.sleep(1);
		portSend(CodeGenerator.PANIDCfg(cfg.getPanID()));
		Thread.sleep(1);
		portSend(CodeGenerator.deviceTypeCfg(cfg.getDeviceType()));
		Thread.sleep(1);
		portSend(CodeGenerator.CMD_ZDO_DIRECT_CB);
		Thread.sleep(1);
		portSend(CodeGenerator.appRegister(cfg.getAppReg()));
		Thread.sleep(1);
		
		portSend(CodeGenerator.CMD_STARTUP_FROM_APP);
		
	}
	
	private PortSendResponseMsg portSend(byte[] data) throws Exception{
		SendPortMsgMsg portSendMsg = new SendPortMsgMsg();
		portSendMsg.setData(data);
		return (PortSendResponseMsg) portSender.send(portSendMsg);
	}

	@Override
	public void start() throws Exception {
		
		zigbeeReceiver = new ZigbeeReceiver(portClient.getReceiver(), converter);
		zigbeeReceiver.addReceivedListenr(receivedListener);//监听应将地址对应的网络地址
		zigbeeSender = new ZigbeeSender(portSender, converter, cfg);
	}

	@Override
	public void destroy() {
		portClient.destroy();
		zigbeeSender = null;
		zigbeeReceiver = null;
	}

	@Override
	public ISender getSender() {
		return zigbeeSender;
	}

	@Override
	public IReceiver getReceiver() {
		return zigbeeReceiver;
	}

	@Override
	public void setConverter(IConverter converter) {
		this.converter = converter;
	}
	
	public ZigbeeClientCfg getZigbeeClientCfg(){
		return cfg;
	}
}
