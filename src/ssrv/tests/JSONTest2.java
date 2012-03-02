package ssrv.tests;

import java.util.LinkedList;

import net.sf.json.JSONObject;

public class JSONTest2
{
	public static void main(String _args[])
	{
		LinkedList<String> list = new LinkedList<String>();
		list.add("aaa");
		list.add("bbb");
		list.add("ccc");
		
		JSONObject json = new JSONObject();
		json.put("list1",list);
		
		list.removeLast();
		json.put("list2",list);
		
		System.out.println(json.toString());
	}
}
