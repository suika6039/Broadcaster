package ssrv.flv;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FLVHeader
{
	public static final String FLV_SIGNATURE = "FLV";
	private String signature;
	private int version;
	private int flags; 
	private int dataOffset;
	
	public FLVHeader(String _signature,int _version,int _flags,int _dataOffset)
	{
		signature = _signature;
		version = _version;
		flags = _flags;
		dataOffset = _dataOffset;
	}
	
	public FLVHeader copy()
	{
		return new FLVHeader(signature,version,flags,dataOffset);
	}
	
	public byte[] serialize()
	{
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(buf);
		
		try{
			out.write(signature.getBytes());
			out.writeByte(version);
			out.writeByte(flags);
			out.writeInt(dataOffset);
		}catch(IOException _e){
			//this is must not be happen.
			_e.printStackTrace();
			return null;
		}
		
		byte[] data = buf.toByteArray();
		return data;
	}
	
	public void setSignature(String _signature)
	{
		signature = _signature;
	}
	
	public void setVersion(int _version)
	{
		version = _version;
	}
	
	public void setFlags(int _flags)
	{
		flags = _flags;
	}
	
	public void setDataOffset(int _dataOffset)
	{
		dataOffset = _dataOffset;
	}
	
	public String getSignature()
	{
		return signature;
	}
	
	public int getVersion()
	{
		return version;
	}
	
	public int getFlags()
	{
		return flags;
	}
	
	public int getDataOffset()
	{
		return dataOffset;
	}
}
