package ssrv.flv;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FLVTag
{
	public static final int FLVTAG_AUDIODATA = 8;
	public static final int FLVTAG_VIDEODATA = 9;
	public static final int FLVTAG_SCRIPTDATA = 18;
	
	private int prevTagSize;
	private int tagType;
	private int dataSize;
	private int timeStamp;
	private int timeStampExtended;
	private int streamID;
	private byte[] body;
	
	public FLVTag(int _prevTagSize,int _tagType,int _dataSize,int _timeStamp,int _timeStampExtended,int _streamID,byte[] _body)
	{
		prevTagSize = _prevTagSize;
		
		switch(_tagType){
			case FLVTAG_AUDIODATA:
			case FLVTAG_VIDEODATA:
			case FLVTAG_SCRIPTDATA:
				break;
			default:
				throw new IllegalArgumentException("invalid FLVTAG type _tagType = "+_tagType);
		}
	
		tagType = _tagType;
		dataSize = _dataSize;
		timeStamp = _timeStamp;
		timeStampExtended = _timeStampExtended;
		streamID = _streamID;
		body = _body;
	}
	
	public FLVTag copy()
	{
		byte[] copyBody = new byte[body.length];
		System.arraycopy(body,0,copyBody,0,body.length);
		FLVTag copy = new FLVTag(prevTagSize,tagType,dataSize,timeStamp,timeStampExtended,streamID,copyBody);
		
		return copy;
	}
	
	@Override
	public String toString()
	{
		return String.format("[tag:%d,size:%d,timestamp:%d,timestampex:%d,streamid:%d,prevtagsize:%d]",tagType,dataSize,timeStamp,timeStampExtended,streamID,prevTagSize);
	}
	
	public byte[] serialize()
	{
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(buf);
		
		try{
			out.writeInt(prevTagSize);
			out.writeByte(tagType);
			out.write(FLVTools.int2ui24(dataSize));
			out.write(FLVTools.int2ui24(timeStamp));
			out.writeByte(timeStampExtended);
			out.write(FLVTools.int2ui24(streamID));
			out.write(body);
		
			byte[] flvTag = buf.toByteArray();
			return flvTag;
		}catch(IOException _e){
			//this is must not be happen.
			_e.printStackTrace();
		}
		
		return null;
	}

	public void setPrevTagSize(int _prevTagSize)
	{
		this.prevTagSize = _prevTagSize;
	}

	public int getPrevTagSize()
	{
		return prevTagSize;
	}

	public void setTagType(int _tagType)
	{
		this.tagType = _tagType;
	}

	public int getTagType()
	{
		return tagType;
	}

	public void setDataSize(int _dataSize)
	{
		this.dataSize = _dataSize;
	}

	public int getDataSize()
	{
		return dataSize;
	}

	public void setTimeStamp(int _timeStamp)
	{
		this.timeStamp = _timeStamp;
	}

	public int getTimeStamp()
	{
		return timeStamp;
	}

	public void setTimeStampExtended(int _timeStampExtended)
	{
		this.timeStampExtended = _timeStampExtended;
	}

	public int getTimeStampExtended()
	{
		return timeStampExtended;
	}

	public void setStreamID(int _streamID)
	{
		this.streamID = _streamID;
	}

	public int getStreamID()
	{
		return streamID;
	}

	public void setBody(byte[] _body)
	{
		this.body = _body;
	}

	public byte[] getBody()
	{
		return body;
	}
}
