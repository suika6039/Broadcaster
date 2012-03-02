package ssrv.flv;

import java.io.ByteArrayInputStream;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class FLVReader
{
	private final DataInputStream flvIn;
	private final FLVHeader header;
	private final FLVMetadata metadata;
	private int previousTagSize;
	
	public static final FLVTag FLV_EOF = new FLVTag(0,FLVTag.FLVTAG_AUDIODATA,0,0,0,0,new byte[]{});
	
	public FLVReader(InputStream _in) throws FLVException
	{
		flvIn = new DataInputStream(_in);
		header = readFLVHeader(flvIn);
		metadata = readFLVMetadata(flvIn);
		previousTagSize = metadata.serialize().length + 11;
	}
	
	public int getPreviousTagSize()
	{
		return previousTagSize;
	}
	
	private static FLVHeader readFLVHeader(DataInputStream _in) throws FLVException
	{
		// Read FLV Header
		// see Adobe Flash Video File Format Specification Version 1.0 for more details
		
		/*
		 * Signature , UI32 , "FLV" 
		 * Version , UI8 , File Version
		 * TypeFlag , UI8 , Flags
		 * DataOffset , UI32 , The length of this header in bytes
		 */
		
		try{
			String signature;
			byte[] bufSignature = new byte[3];
			_in.readFully(bufSignature);
			signature = new String(bufSignature);
			if(!signature.equals(FLVHeader.FLV_SIGNATURE)){
				throw new FLVException(signature+" is not valid FLV Signature");
			}
		
			int version = _in.readUnsignedByte();
			int flags = _in.readUnsignedByte();
			int dataOffset = _in.readInt();
		
			FLVHeader header = new FLVHeader(signature,version,flags,dataOffset);
			return header;
		}catch(Exception _e){
			throw new FLVException(_e);
		}
	}
	
	private static FLVMetadata readFLVMetadata(DataInputStream _in) throws FLVException
	{
		// Read FLV Metadata
		// FLV Metadata is FLVTag (TagType = 0x18 SCRIPTDATA)
		
		try{
			_in.readInt(); //read prevTagSize should be 0
			int tagType = _in.readByte();
			if(tagType != FLVMetadata.FLVTAG_SCRIPTDATATYPE){
				throw new FLVException(String.format("Invalid TagID(id = %d) must be id = %d",tagType,FLVMetadata.FLVTAG_SCRIPTDATATYPE));
			}
			
			/*
			 * SCRIPTDATA
			 * Name SCRIPTDATAVALUE "Method or object name"
			 * Value SCRIPTDATAVALUE "AMF arguments or object properties"
			 */
			
			byte[] ui24 = new byte[3];
			_in.readFully(ui24);
			int dataSize = FLVTools.bytes2int(ui24);
			_in.readFully(ui24);
			//int timeStamp = FLVTools.bytes2int(ui24); 
			//int timeStampExtended = _in.readUnsignedByte();
			_in.readUnsignedByte();
			_in.readFully(ui24);
			//int streamID = FLVTools.bytes2int(ui24);
			
			byte[] body = new byte[dataSize];
			_in.readFully(body);
			FLVMetadata data = new FLVMetadata();
			DataInputStream amf = new DataInputStream(new ByteArrayInputStream(body));
			
			int type = amf.readUnsignedByte();
			if(type != FLVMetadata.SCRIPTDATATYPE_STRING){
				throw new FLVException("The first of AMF SCRIPTDATAVALUE must be method name , but it was = "+type);
			}
			
			int methodLen = amf.readUnsignedShort();
			byte[] methodNameBuf = new byte[methodLen];
			amf.read(methodNameBuf);
			String methodName = new String(methodNameBuf);
			if(!methodName.equals(FLVMetadata.METHOD_NAME)){
				throw new FLVException("The method name was not 'OnMetaData' , it was = "+methodName);
			}
		
			int valueType = amf.readUnsignedByte();
			if(valueType != FLVMetadata.SCRIPTDATATYPE_ECMAARRAY){
				throw new FLVException("The Value was not SCRIPTDATAECMAARRAY type = "+valueType);
			}
			int size = amf.readInt();
			
			for(int i = 0;i < size;i ++){
				//Read Variables
				
				/*
				 * SCRIPTDATAOBJECTPROPERTY[size]
				 * PropertyName SCRIPTDATASTRING "Name of the object property"
				 * PropertyData SCRIPTDATAVALUE "Value and type of the object"
				 */
				int propertyNameLen = amf.readUnsignedShort();
				byte[] propertyNameBuf = new byte[propertyNameLen];
				amf.read(propertyNameBuf);
				FLVMetadata.PropertyName propertyName = null;
				try{
					propertyName = FLVMetadata.PropertyName.valueOf(new String(propertyNameBuf));
				}catch(Exception _e){
					_e.printStackTrace();
					continue;
				}
				
				int propertyValueType = amf.readUnsignedByte();
				switch(propertyValueType){
					case FLVMetadata.SCRIPTDATATYPE_BOOLEAN:
						Boolean b = amf.readBoolean();
						data.setProperty(propertyName,b);
						break;
					case FLVMetadata.SCRIPTDATATYPE_STRING:
						int propertyValueLen = amf.readUnsignedShort();
						byte[] propertyValueBuf = new byte[propertyValueLen];
						amf.read(propertyValueBuf);
						String str = new String(propertyValueBuf);
						data.setProperty(propertyName,str);
						break;
					case FLVMetadata.SCRIPTDATATYPE_NUMBER:
						Double d = amf.readDouble();
						data.setProperty(propertyName,d);
						break;
					default:
						throw new FLVException("Invalid SCRIPTDATAVALUE type "+propertyValueType);
				}
			}
			return data;
		}catch(Exception _e){
			throw new FLVException(_e);
		}
	}
	
	public FLVHeader getHeader()
	{
		return header;
	}
	
	public FLVMetadata getMetadata()
	{
		return metadata;
	}
	
	public FLVTag getNextTag() throws FLVException
	{
		int prevTagSize = 0;
		int tagType = 0;
		int dataSize = 0;
		int timeStamp = 0;
		int timeStampExtended = 0;
		int streamID = 0;
		byte[] theBody = null;
		
		try{
			prevTagSize = flvIn.readInt();
			try{
				tagType = flvIn.readUnsignedByte();
			}catch(EOFException _e){
				return FLV_EOF;
			}
			
			byte[] ui24 = new byte[3];
			flvIn.readFully(ui24);
			dataSize = FLVTools.bytes2int(ui24);
			flvIn.readFully(ui24);
			timeStamp = FLVTools.bytes2int(ui24);
			timeStampExtended = flvIn.readUnsignedByte();
			flvIn.readFully(ui24);
			streamID = FLVTools.bytes2int(ui24);
			theBody = new byte[dataSize];
			flvIn.readFully(theBody);
			
		}catch(EOFException _e){
			return FLV_EOF;
		}catch(IOException _e){
			throw new FLVException(_e);
		}
		
		FLVTag tag = new FLVTag(prevTagSize,tagType,dataSize,timeStamp,timeStampExtended,streamID,theBody);
		previousTagSize = prevTagSize;
		return tag;
	}
}
