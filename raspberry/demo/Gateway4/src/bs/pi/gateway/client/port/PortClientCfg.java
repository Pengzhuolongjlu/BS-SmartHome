package bs.pi.gateway.client.port;

import java.io.FileInputStream;
import java.util.Properties;

public class PortClientCfg {

	public final static String KEY_PORT_NAME = "portName";
	public final static String KEY_BAUD_RATE = "baudRate";
	public final static String KEY_DATA_BITS = "dataBits";
	public final static String KEY_STOP_BITS = "stopBits";
	public final static String KEY_PARITY = "parity";
	
	public final static String DEFAULT_PORT_NAME = "COM6";
	public final static int DEFAULT_BAUD_RATE = 19200;
	public final static int DEFAULT_DATA_BITS = 8;
	public final static int DEFAULT_STOP_BITS = 1;
	public final static int DEFAULT_PARITY = 0;
	
	private String portName;
	private int baudRate;
	private int dataBits;
	private int stopBits;
	private int parity;
	
	public PortClientCfg(){
		portName = DEFAULT_PORT_NAME;
		baudRate = DEFAULT_BAUD_RATE;
		stopBits = DEFAULT_STOP_BITS;
		parity = DEFAULT_PARITY;
	}
	
	public PortClientCfg(String cfgPath) throws Exception{
		Properties properties = new Properties();
		properties.load(new FileInputStream(cfgPath));
		portName = properties.getProperty(KEY_PORT_NAME);
		baudRate = Integer.parseInt(properties.getProperty(KEY_BAUD_RATE));
		dataBits = Integer.parseInt(properties.getProperty(KEY_DATA_BITS));
		stopBits = Integer.parseInt(properties.getProperty(KEY_STOP_BITS));
		parity = Integer.parseInt(properties.getProperty(KEY_PARITY));
	}
	
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public int getBaudRate() {
		return baudRate;
	}
	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}
	public int getDataBits() {
		return dataBits;
	}
	public void setDataBits(int dataBits) {
		this.dataBits = dataBits;
	}
	public int getStopBits() {
		return stopBits;
	}
	public void setStopBits(int stopBits) {
		this.stopBits = stopBits;
	}
	public int getParity() {
		return parity;
	}
	public void setParity(int parity) {
		this.parity = parity;
	}
}
