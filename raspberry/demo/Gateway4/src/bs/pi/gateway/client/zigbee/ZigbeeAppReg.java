package bs.pi.gateway.client.zigbee;

import java.util.Properties;

import bs.pi.gateway.assist.Tool;

public class ZigbeeAppReg {
	
	public final static String KEY_ENDPOINT = "reg_endpoint";
	public final static String KEY_APP_PROF_ID = "reg_appProfID";
	public final static String KEY_APP_DEVICE_ID = "reg_appDeviceID";
	public final static String KEY_DEV_VER = "reg_endDevVer";
	public final static String KEY_LATENCY_REQ = "reg_latencyReq";
	public final static String KEY_APP_NUM_IN_CLUSTERS = "reg_endDevVer";
	public final static String KEY_APP_IN_CLUSTER_LIST = "reg_endDevVer";
	public final static String KEY_APP_NUM_OUT_CLUSTERS = "reg_endDevVer";
	public final static String KEY_APP_OUT_CLUSTER_LIST = "reg_endDevVer";
	
	public final static byte DEFAULT_DENPOINT = 0x55;
	public final static byte[] DEFAULT_APP_PROF_ID = {0x00, 0x00};
	public final static byte[] DEFAULT_APP_DEVICE_ID = {0x00, 0x00};
	public final static byte DEFAULT_DEV_VER = 0x00;
	public final static byte DEFAULT_LATENCY_REQ = 0x00;
	public final static byte DEFAULT_APP_NUM_IN_CLUSTERS = 0x00;
	public final static byte[] DEFAULT_APP_IN_CLUSTER_LIST = {};
	public final static byte DEFAULT_APP_NUM_OUT_CLUSTERS = 0x00;
	public final static byte[] DEFAULT_APP_OUT_CLUSTER_LIST = {};
	
	private byte endpoint;
	private byte[] appProfID;	//2byte
	private byte[] appDeviceID;	//2byte
	private byte endDevVer;
	private byte latencyReq;
	private byte appNumInClusters;
	private byte[] appInClusterList;
	private byte appNumOutClusters;
	private byte[] appOutClusterList;
	
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
		appNumInClusters = Tool.strToByte(properties.getProperty(KEY_APP_NUM_IN_CLUSTERS));
		if(appNumInClusters == 0){
			appInClusterList = new byte[0];
		}else{
			appInClusterList = Tool.strToBytes(properties.getProperty(KEY_APP_IN_CLUSTER_LIST));
		}
		appNumOutClusters = Tool.strToByte(properties.getProperty(KEY_APP_NUM_OUT_CLUSTERS));
		if(appNumOutClusters == 0){
			appOutClusterList = new byte[0];
		}else{
			appInClusterList = Tool.strToBytes(properties.getProperty(KEY_APP_OUT_CLUSTER_LIST));
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
		appInClusterList = new byte[appNumInClusters];
	}

	public byte[] getAppInClusterList() {
		return appInClusterList;
	}

	public void setAppInClusterList(byte[] appInClusterList) {
		this.appInClusterList = appInClusterList.clone();
		appOutClusterList = new byte[appNumOutClusters];
	}

	public byte getAppNumOutClusters() {
		return appNumOutClusters;
	}

	public void setAppNumOutClusters(byte appNumOutClusters) {
		this.appNumOutClusters = appNumOutClusters;
	}

	public byte[] getAppOutClusterList() {
		return appOutClusterList.clone();
	}

	public void setAppOutClusterList(byte[] appOutClusterList) {
		this.appOutClusterList = appOutClusterList.clone();
	}
}
