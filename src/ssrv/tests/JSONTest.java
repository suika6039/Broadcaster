package ssrv.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

public class JSONTest
{
	public static void main(String _args[]) throws IOException
	{
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("hoge","fuga");
		map.put("foo",new String[]{"ho{}gea","f]]ugaa","f[,\"oobar"});
		map.put("path",Arrays.asList("hoge","fuga","higa","shiga"));
		JSONObject json = JSONObject.fromObject(map);
		String jsonStr = json.toString();
		json = JSONObject.fromObject(jsonStr);
		
		System.out.println(json);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String str = br.readLine();
		map.put("userinput",new String[]{str,str,str});
		System.out.println(JSONObject.fromObject(map));
		
	}
}
