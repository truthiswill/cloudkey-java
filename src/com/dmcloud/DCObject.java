package com.dmcloud;

import java.util.HashMap;
import java.util.Map;

public class DCObject extends HashMap
{
	static public DCObject create()
	{
		return new DCObject();
	}
	
	static public DCObject create(Map map)
	{
		DCObject obj = create();
		obj.putAll(map);
		return obj;
	}

	public DCObject push(Object s, Object ss)
	{
		this.put(s, ss);
		return this;
	}
	
	public String pull(String path) throws CloudKey_Exception
	{
		try
		{
			String[] tokens = path.split("\\.");
			if (tokens.length == 0)
			{
				return this.get(path).toString();
			}

			Map map = (Map)this.get(tokens[0]);
			for(int i=1; i<tokens.length; i++)
			{
				if (map.get(tokens[i]) instanceof Map)
				{
					map = (Map)map.get(tokens[i]);
				}
				else
				{
					return map.get(tokens[i]).toString();
				}
			}
			return "";
		}
		catch (Exception e)
		{
			throw new CloudKey_Exception("Item not found");
		}
	}
}
