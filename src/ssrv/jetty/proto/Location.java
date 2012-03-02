package ssrv.jetty.proto;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Base64;

public class Location
{
	private String id;
	private LinkedList<String> dirs;

	public Location(String _id)
	{
		id = _id;
		dirs = new LinkedList<String>();
	}

	public Location(String _id,LinkedList<String> _dirs)
	{
		id = _id;
		dirs = new LinkedList<String>(_dirs);
	}

	public Location add(String _name)
	{
		Location newLocation = new Location(id,dirs);
		newLocation.dirs.add(_name);
		return newLocation;
	}
	
	public String getID()
	{
		return id;
	}
	
	public List<String> getList()
	{
		return Collections.unmodifiableList(dirs);
	}
	
	public static Location decodeFromBase64URLSafe(String _base64)
	{
		StringTokenizer tokens = new StringTokenizer(_base64,".");
		String id = tokens.nextToken();
		if(id == null){
			return null;
		}
		
		LinkedList<String> dirs = new LinkedList<String>();
		while(tokens.hasMoreTokens()){
			dirs.add(new String(Base64.decodeBase64(tokens.nextToken())));
		}
		
		return new Location(id,dirs);
	}

	public String encodeToBase64URLSafe()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(id);
		for(String dir : dirs){
			buf.append(".").append(Base64.encodeBase64URLSafeString(dir.getBytes()));
		}

		return buf.toString();
	}
	
	public String toString()
	{
		return id+":"+dirs.toString();
	}
}
