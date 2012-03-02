package ssrv.flv;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FLVMetadata
{
	public static final String METHOD_NAME = "onMetaData";
	
	public static final int FLVTAG_SCRIPTDATATYPE = 18;
	
	// constants of SCRIPTDATAVALUE types
	public static final int SCRIPTDATATYPE_NUMBER = 0;
	public static final int SCRIPTDATATYPE_BOOLEAN = 1;
	public static final int SCRIPTDATATYPE_STRING = 2;
	public static final int SCRIPTDATATYPE_OBJECT = 3;
	public static final int SCRIPTDATATYPE_MOVIECLIP = 4;
	public static final int SCRIPTDATATYPE_NULL = 5;
	public static final int SCRIPTDATATYPE_UNDEFINED = 6;
	public static final int SCRIPTDATATYPE_REFERENCE = 7;
	public static final int SCRIPTDATATYPE_ECMAARRAY = 8;
	public static final int SCRIPTDATATYPE_OJBECTENDMARKER = 9;
	public static final int SCRIPTDATATYPE_STRICTARRAY = 10;
	public static final int SCRIPTDATATYPE_DATE = 11;
	public static final int SCRIPTDATATYPE_LONGSTRING = 12;
	
	public static enum PropertyName{
		audiocodecid(Double.class),
		audiodatarate(Double.class),
		audiodelay(Double.class),
		audiosamplerate(Double.class),
		audiosamplesize(Double.class),
		canseektoend(Boolean.class),
		creationdate(String.class),
		duration(Double.class),
		filesize(Double.class),
		framerate(Double.class),
		height(Double.class),
		stereo(Boolean.class),
		videocodecid(Double.class),
		videodatarate(Double.class),
		width(Double.class),
		starttime(Double.class),
		totalduration(Double.class),
		bytelength(Double.class),
		canseekontime(Boolean.class),
		sourcedata(String.class),
		purl(String.class),
		pmsg(String.class),
		httphostheader(String.class),
		totaldatarate(Double.class);
		
		private Class<?> type;
		
		private PropertyName(Class<?> _type)
		{
			setType(_type);
		}

		public void setType(Class<?> type)
		{
			this.type = type;
		}

		public Class<?> getType()
		{
			return type;
		}
	}
	
	// FLV Tag Information
	private int prevTagSize; // UI32
	private int tagType; // UI8
	private int timeStamp; // UI24
	private int timeStampExtended; // UI8
	private int streamID; //UI24
	
	// Properties
	private Map<PropertyName,Object> properties;
	
	public FLVMetadata()
	{
		prevTagSize = 0;
		tagType = FLVMetadata.FLVTAG_SCRIPTDATATYPE;
		timeStamp = 0;
		timeStampExtended = 0;
		streamID = 0;
		
		properties = new HashMap<PropertyName,Object>();
	}
	
	private FLVMetadata(int _prevTagSize,int _tagType,int _timeStamp,int _timeStampExtended,int _streamID,Map<PropertyName,Object> _properties)
	{
		prevTagSize = _prevTagSize;
		tagType = _tagType;
		timeStamp = _timeStamp;
		timeStampExtended = _timeStampExtended;
		streamID = _streamID;
		properties = _properties;
	}
	
	public FLVMetadata copy()
	{
		FLVMetadata copy = new FLVMetadata(prevTagSize,tagType,timeStamp,timeStampExtended,streamID,new HashMap<PropertyName,Object>(properties));
		return copy;
	}
	
	public byte[] serialize()
	{
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(buf);
		
		try{
			byte[] amf = serializeAMF();
			
			out.writeInt(prevTagSize);
			out.writeByte(tagType);
			out.write(FLVTools.int2ui24(amf.length));
			out.write(FLVTools.int2ui24(timeStamp));
			out.writeByte(timeStampExtended);
			out.write(FLVTools.int2ui24(streamID));
			out.write(amf);
			
			byte[] serialized = buf.toByteArray();
			return serialized;
		}catch(IOException _e){
			// this is must not be happen.
			_e.printStackTrace();
		}
		
		return null;
	}
	
	private byte[] serializeAMF()
	{
		//Serialize onMetadata format
		
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(buf);
		
		/*
		 * SCRIPTDATA
		 * Name	SCRIPTDATAVALUE "Method or object name"
		 * Value SCRIPTDATAVALUE "AMF arguments or object properties"
		 */
		
		/*
		 * SCRIPTDATAVALUE
		 * Type UI8 "Type of the ScriptDataValue"
		 * ScriptDataValue DependsOnTheTypes "Script data value"
		 */
		
		try{
			//Write SCRIPTDATA "Name"
			
			/*
			 * SCRIPTDATASTRING 
			 * StringLength UI16 "StringData length in bytes."
			 * StringData STRING "StringData"
			 */
			
			out.writeByte(SCRIPTDATATYPE_STRING);
			int methodStringLength = METHOD_NAME.length();
			out.writeShort(methodStringLength); // UI16 , length of method name
			out.write(METHOD_NAME.getBytes()); // write method name
			
			//Write SCRIPTDATA "Value"
			
			/*
			 * SCRIPTDATAECMAARRAY
			 * ECMAArrayLength UI32 "Approximate number of items in ECMA array"
			 * Variables SCRIPTDATAOBJECTPROPERTY[] "List of variables"
			 * ListTerminator SCRIPTDATAOBJECTEND "List terminator"
			 */
			
			out.writeByte(SCRIPTDATATYPE_ECMAARRAY);
			out.writeInt(properties.size());
			
			/*
			 * SCRIPTDATAOBJECTPROPERTY
			 * PropertyName SCRIPTDATASTRING "Name of the object property or variable"
			 * PropertyData SCRIPTDATAVALUE "Value and type of the object property or variable"
			 */
			for(PropertyName name : properties.keySet()){
				Object value = properties.get(name);
				
				/*
				 * according to the FLV Specification , 
				 * only three data types (Double,String,Boolean)
				 * appears in onMetaData properties
				 */
				
				int nameLength = name.name().length();
				out.writeShort(nameLength);
				out.write(name.name().getBytes());
				
				if(value instanceof Double){
					out.writeByte(SCRIPTDATATYPE_NUMBER);
					out.writeDouble(((Double)value).doubleValue());
					continue;
				}
				
				if(value instanceof String){
					out.writeByte(SCRIPTDATATYPE_STRING);
					int valueLen = value.toString().length();
					out.writeInt(valueLen);
					out.write(value.toString().getBytes());
					continue;
				}
				
				if(value instanceof Boolean){
					out.writeByte(SCRIPTDATATYPE_BOOLEAN);
					out.writeBoolean(((Boolean)value).booleanValue());
					continue;
				}
			}
			
			/*
			 * SCRIPTDATAOBJECTEND
			 * ObjectEndMarker UI8[3] "Shall be 0,0,9"
			 */
			out.write(new byte[]{0x00,0x00,0x09});
			
			byte[] amf = buf.toByteArray();
			return amf;
		}catch(IOException _e){
			// this is must not be happen.
			_e.printStackTrace();
		}
		
		
		return null;
	}
	
	public void setProperty(PropertyName _name,Object _value)
	{
		Class<?> type = _name.getType();
		if(_value.getClass().equals(type)){
			properties.put(_name,_value);
			return;
		}
		
		throw new IllegalArgumentException("_value.class != "+type);
	}
	
	public Object getProperty(PropertyName _name)
	{
		return properties.get(_name);
	}
	
	/*
	public double getAudioCodecID()
	{
		return (Double)properties.get(PropertyName.audioCodecID);
	}
	
	public double getAudioDataRate()
	{
		return (Double)properties.get(PropertyName.audioDataRate);
	}
	
	public double getAudioDelay()
	{
		return (Double)properties.get(PropertyName.audioDelay);
	}
	
	public double getAudioSampleRate()
	{
		return (Double)properties.get(PropertyName.audioSampleRate);
	}
	
	public double getAudioSampleSize()
	{
		return (Double)properties.get(PropertyName.audioSampleSize);
	}
	
	public boolean getCanSeekToEnd()
	{
		return (Boolean)properties.get(PropertyName.canSeekToEnd);
	}
	
	public String getCreationDate()
	{
		return (String)properties.get(PropertyName.creationDate);
	}
	
	public double getDuration()
	{
		return (Double)properties.get(PropertyName.duration);
	}
	
	public double getFileSize()
	{
		return (Double)properties.get(PropertyName.fileSize);
	}
	
	public double getFrameRate()
	{
		return (Double)properties.get(PropertyName.frameRate);
	}
	
	public double getHeight()
	{
		return (Double)properties.get(PropertyName.height);
	}
	
	public boolean getStereo()
	{
		return (Boolean)properties.get(PropertyName.stereo);
	}
	
	public double getVideoCodecID()
	{
		return (Double)properties.get(PropertyName.videoCodecID);
	}
	
	public double getVideoDataRate()
	{
		return (Double)properties.get(PropertyName.videoDataRate);
	}
	
	public double getWidth()
	{
		return (Double)properties.get(PropertyName.width);
	}
	
	public void setAudioCodecID(double _audioCodecID)
	{
		properties.put(PropertyName.audioCodecID,(Double)_audioCodecID);
	}
	
	public void setAudioDataRate(double _audioDataRate)
	{
		properties.put(PropertyName.audioDataRate,(Double)_audioDataRate);
	}
	
	public void setAudioDelay(double _audioDelay)
	{
		properties.put(PropertyName.audioDelay,(Double)_audioDelay);
	}
	
	public void setAudioSampleRate(double _audioSampleRate)
	{
		properties.put(PropertyName.audioSampleRate,(Double)_audioSampleRate);
	}
	
	public void setAudioSampleSize(double _audioSampleSize)
	{
		properties.put(PropertyName.audioSampleSize,(Double)_audioSampleSize);
	}
	
	public void setCanSeekToEnd(boolean _canSeekToEnd)
	{
		properties.put(PropertyName.canSeekToEnd,(Boolean)_canSeekToEnd);
	}
	
	public void setCreationDate(String _creationDate)
	{
		properties.put(PropertyName.creationDate,_creationDate);
	}
	
	public void setDuration(double _duration)
	{
		properties.put(PropertyName.duration,(Double)_duration);
	}
	
	public void setFileSize(double _fileSize)
	{
		properties.put(PropertyName.fileSize,(Double)_fileSize);
	}
	
	public void setFrameRate(double _frameRate)
	{
		properties.put(PropertyName.frameRate,(Double)_frameRate);
	}
	
	public void setHeight(double _height)
	{
		properties.put(PropertyName.height,(Double)_height);
	}
	
	public void setStereo(boolean _stereo)
	{
		properties.put(PropertyName.stereo,(Boolean)_stereo);
	}
	
	public void setVideoCodecID(double _videoCodecID)
	{
		properties.put(PropertyName.videoCodecID,(Double)_videoCodecID);
	}
	
	public void setVideoDataRate(double _videoDataRate)
	{
		properties.put(PropertyName.videoDataRate,(Double)_videoDataRate);
	}
	
	public void setWidth(double _width)
	{
		properties.put(PropertyName.width,(Double)_width);
	}
	*/
}

