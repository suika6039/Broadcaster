package ssrv.tests;

import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.lang.StringUtils;

public class StringJoinTest
{
	public static void main(String _args[])
	{
		String str = StringUtils.join(Arrays.asList("hoge","fuga","higa"),"/");
		System.out.println(str);
		String str2 = StringUtils.join(Collections.emptyList(),"/");
		System.out.println(str2);
	}
}
