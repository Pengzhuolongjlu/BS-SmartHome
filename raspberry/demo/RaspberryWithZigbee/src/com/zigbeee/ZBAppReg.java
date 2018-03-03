package com.zigbeee;

public class ZBAppReg {
	private byte endPoint;
	private byte[] appProfID;
	private byte[] appDeviceID;
	private byte endDevVer;
	private byte latencyReq;
	private byte appNumInClusters;
	private byte[] appInClusterList;
	private byte appNumOutClusters;
	private byte[] appOutClusterList;
	
	public ZBAppReg()
	{
		appProfID = new byte[2];
		appDeviceID = new byte[2];
		appInClusterList = new byte[32];
		appOutClusterList = new byte[32];
		
		endPoint = (byte) 0xff;
		appProfID[0] = 0x00;
		appProfID[1] = 0x00;
		appDeviceID[0] = 0x00;
		appDeviceID[0] = 0x00;
		endDevVer = 0x00;
		latencyReq = 0x00;
		appNumInClusters = 0x00;
		appNumOutClusters = 0x00;
	}

	public byte getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(byte endPoint) {
		this.endPoint = endPoint;
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

	public byte[] getAppInClusterList() {
		return appInClusterList;
	}

	public void setAppInClusterList(byte[] appInClusterList) {
		this.appInClusterList = appInClusterList;
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