package bs.pi.gateway.msg;

public class OtherZigbeeConnectedMsg implements IMsg {

	public final static String MSG_NAME = "OtherZigbeeConnectedMsg";
	
	private byte[] IEEEAddr;
	private byte[] NWKAddr;
	
	public OtherZigbeeConnectedMsg(){
		
	}
		
	public OtherZigbeeConnectedMsg(byte[] IEEEAddr, byte[] NWKAddr){
		this.IEEEAddr = IEEEAddr.clone();
		this.NWKAddr = NWKAddr.clone();
	}
	
	@Override
	public String getName() {
		return MSG_NAME;
	}

	public byte[] getIEEEAddr() {
		return IEEEAddr;
	}

	public void setIEEEAddr(byte[] iEEEAddr) {
		IEEEAddr = iEEEAddr.clone();
	}

	public byte[] getNWKAddr() {
		return NWKAddr;
	}

	public void setNWKAddr(byte[] nWKAddr) {
		NWKAddr = nWKAddr.clone();
	}
	
}
