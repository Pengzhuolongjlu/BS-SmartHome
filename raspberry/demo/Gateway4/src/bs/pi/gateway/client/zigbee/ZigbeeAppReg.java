package bs.pi.gateway.client.zigbee;

import java.util.Properties;

import bs.pi.gateway.assist.Tool;

public class ZigbeeAppReg {
	
	public final static String KEY_ENDPOINT = "reg_endpoint";
	public final static String KEY_APP_PROF_ID = "reg_appProfID";
	public final static String KEY_APP_DEVICE_ID = "reg_appDeviceID";
	public final static String KEY_DEV_VER = "reg_endDevVer";
	public final static String KEY_LATENCY_REQ = "reg_latencyReq";
	public final static String KEY_APP_IN_CLUSTER_LIST = "reg_appInClusterList";
	public final static String KEY_APP_OUT_CLUSTER_LIST = "reg_appOutClusterList";
	
	public final static byte DEFAULT_DENPOINT = 0x55;
	public final static byte[] DEFAULT_APP_PROF_ID = {0x00, 0x00};
	public final static byte[] DEFAULT_APP_DEVICE_ID = {0x00, 0x00};
	public final static byte DEFAULT_DEV_VER = 0x00;
	public final static byte DEFAULT_LATENCY_REQ = 0x00;
	public final static byte DEFAULT_APP_NUM_IN_CLUSTERS = 0x00;
	public final static short[] DEFAULT_APP_IN_CLUSTER_LIST = {};
	public final static byte DEFAULT_APP_NUM_OUT_CLUSTERS = 0x00;
	public final static short[] DEFAULT_APP_OUT_CLUSTER_LIST = {};
	
	private byte endpoint;
	private byte[] appProfID;	//2byte
	private byte[] appDeviceID;	//2byte
	private byte endDevVer;
	private byte latencyReq;
	private byte appNumInClusters;
	private short[] appInClusterList;
	private byte appNumOutClusters;
	private short[] appOutClusterList;
	
	public ZigbeeAppReg()
	{	
		endpoint = DEFAULT_DENPOINT;
		appProfID = DEFAULT_APP_PROF_ID;
		appDeviceID = DEFAULT_APP_DEVICE_ID;
		endDevVer = DEFAULT_DEV_VER;
		latencyReq = DEFAULT_LATENCY_REQ;
		appNumInClusters = DEFAULT_APP_NUM_IN_CLUSTERS;
		appInClusterList = DEFAULT_APP_IN_CLUSTER_LIST;
		appNumOutClusters = DEFAULT_APP_NUM_OUT_CLUSTERS;
		appOutClusterList = DEFAULT_APP_OUT_CLUSTER_LIST;
	}
	
	public ZigbeeAppReg(Properties properties) throws Exception{
		endpoint = Tool.strToByte(properties.getProperty(KEY_ENDPOINT));
		appProfID = Tool.strToBytes(properties.getProperty(KEY_APP_PROF_ID));
		appDeviceID = Tool.strToBytes(properties.getProperty(KEY_APP_DEVICE_ID));
		endDevVer = Tool.strToByte(properties.getProperty(KEY_DEV_VER));
		latencyReq = Tool.strToByte(properties.getProperty(KEY_LATENCY_REQ));
		if(properties.containsKey(KEY_APP_IN_CLUSTER_LIST)){
			String[] strs = properties.getProperty(KEY_APP_IN_CLUSTER_LIST).split(",");
			appNumInClusters = (byte) strs.length;
			appInClusterList = new short[strs.length];
			for(int i=0; i<strs.length;i++){
				byte[] bs = Tool.strToBytes(strs[i]);
				appInClusterList[i] = Tool.bytesToShort(bs[0], bs[1]);
			}
		}else{
			appNumInClusters = 0x00;
			appInClusterList = new short[0];
		}
		
		if(properties.containsKey(KEY_APP_OUT_CLUSTER_LIST)){
			String[] strs = properties.getProperty(KEY_APP_OUT_CLUSTER_LIST).split(",");
			appNumOutClusters = (byte) strs.length;
			appOutClusterList = new short[strs.length];
			for(int i=0; i<strs.length;i++){
				byte[] bs = Tool.strToBytes(strs[i]);
				appOutClusterList[i] = Tool.bytesToShort(bs[0], bs[1]);
			}
		}else{
			appNumOutClusters = 0x00;
			appOutClusterList = new short[0];
		}
		
	}
	
	public byte getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(byte endpoint) {
		this.endpoint = endpoint;
	}

	public byte[] getAppProfID() {
		return appProfID;
	}

	public void setAppProfID(byte[] appProfID) {
		this.appProfID = appProfID.clone();
	}

	public byte[] getAppDeviceID() {
		return appDeviceID;
	}

	public void setAppDeviceID(byte[] appDeviceID) {
		this.appDeviceID = appDeviceID.clone();
	}

	public byte getEndDevVer() {
		return endDevVer;
	}

	public void setEndDevVer(byte endDevVer) {
		this.endDevVer = endDevVer;
	}

	public byte getLatencyReq() {
		return latencyReq;
	}

	public void setLatencyReq(byte latencyReq) {
		this.latencyReq = latencyReq;
	}

	public byte getAppNumInClusters() {
		return appNumInClusters;
	}

	public void setAppNumInClusters(byte appNumInClusters) {
		this.appNumInClusters = appNumInClusters;
	}

	public short[] getAppInClusterList() {
		return appInClusterList;
	}
	
	public byte[] getAppInClusterListBytes() {
		if(appInClusterList != null && appInClusterList.length > 0){
			byte[] result = new byte[appInClusterList.length*2];
			for(int i=0;i<appInClusterList.length;i++){
				byte[] bs = Tool.shortTo2Byte(appInClusterList[i]);
				result[2*i] = bs[0];
				result[2*i+1] = bs[1];
			}
			return result;
		}else {
			return null;
		}
	}

	public void setAppInClusterList(short[] appInClusterList) {
		if(appInClusterList != null)
			this.appInClusterList = appInClusterList.clone();
		else {
			this.appInClusterList = null;
		}
	}

	public byte getAppNumOutClusters() {
		return appNumOutClusters;
	}

	public void setAppNumOutClusters(byte appNumOutClusters) {
		this.appNumOutClusters = appNumOutClusters;
	}

	public short[] getAppOutClusterList() {
		return appOutClusterList;
	}

	public byte[] getAppOutClusterListBytes() {
		if(appOutClusterList != null && appOutClusterList.length > 0){
			byte[] result = new byte[appOutClusterList.length*2];
			for(int i=0;i<appOutClusterList.length;i++){
				byte[] bs = Tool.shortTo2Byte(appOutClusterList[i]);
				result[2*i] = bs[0];
				result[2*i+1] = bs[1];
			}
			return result;
		}else {
			return null;
		}
	}
	
	public void setAppOutClusterList(short[] appOutClusterList) {
		if(appOutClusterList != null)
			this.appOutClusterList = appOutClusterList.clone();
		else {
			this.appOutClusterList = null;
		}
	}
}
