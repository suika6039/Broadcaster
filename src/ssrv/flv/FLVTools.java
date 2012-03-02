package ssrv.flv;


public class FLVTools
{
	public static byte[] int2ui24(int _number)
	{
		byte[] ui24 = new byte[3];
		ui24[0] = (byte)((_number & 0x00FF0000) >> 16);
		ui24[1] = (byte)((_number & 0x0000FF00) >> 8);
		ui24[2] = (byte)(_number & 0x000000FF);
		
		return ui24;
	}
	
	public static int bytes2int(byte[] _array)
	{
		int result = 0;
		
		int p = _array.length - 1;
		for(byte b : _array){
			result += (b & 0xFF) << 8*p;
			p--;
		}
		
		return result;
	}
}
