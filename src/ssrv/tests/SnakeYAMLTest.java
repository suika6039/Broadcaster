package ssrv.tests;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class SnakeYAMLTest
{
	public static void main(String _args[])
	{
		Yaml yaml = new Yaml();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("hogege","fugaga");
		map.put("fugaga",Arrays.asList("fuga","higa","piga"));
		
		yaml.dump(map,new PrintWriter(System.out));
	}
}
