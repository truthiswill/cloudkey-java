package net.dmcloud;

public class CloudKey_Exception extends Exception
{
	int code = 0;
	
	public CloudKey_Exception(String message)
	{
		super(message);
	}
	
	public CloudKey_Exception(String message, int _code)
	{
		this(message);
		this.code = _code;
	}
	
	public int getCode()
	{
		return this.code;
	}
}