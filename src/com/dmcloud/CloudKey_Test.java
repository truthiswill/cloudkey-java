package com.dmcloud;

import java.util.ArrayList;
import java.util.Map;
import junit.framework.TestCase;

public class CloudKey_Test extends TestCase
{
	final String user_id = "YOUR USER ID";
	final String api_key = "YOUR API KEY";
	final String video_id = "YOUR VIDEO ID";

	public void checkConfigFile()
	{
		System.exit(0);
	}
	
	public void testCloudKey_Normalize()
	{
		assertEquals
		(
			CloudKey_Helpers.normalize
			(
				DCArray.create()
					   .push("foo")
					   .push(42)
					   .push("bar")
			), "foo42bar"
		);

		assertEquals
		(
			CloudKey_Helpers.normalize
			(
				DCObject.create()
						.push("yellow", 1)
						.push("red", 2)
						.push("pink", 3)
			), "pink3red2yellow1"
		);

		assertEquals
		(
			CloudKey_Helpers.normalize
			(
				DCArray.create()
					   .push("foo")
					   .push(42)
					   .push
					   	(
					   		DCObject.create()
					   				.push("yellow", 1)
					   				.push("red", 2)
					   				.push("pink", 3)
					   	)
					   .push("bar")
			), "foo42pink3red2yellow1bar"
		);
	}

	public void testCloudKey_getEmbedUrl()
	{
		try
		{
			CloudKey_LL cloud = new CloudKey_LL(user_id, api_key);
			String[] referers = {"http://test.dmcloud.net"};
			cloud.get_embed_url(CloudKey_LL.CLOUDKEY_API_URL, video_id, CloudKey_LL.CLOUDKEY_SECLEVEL_REFERER, "", "", "", null, referers, 0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void testCloudKey_getStreamUrl()
	{
		try
		{
			CloudKey_LL cloud = new CloudKey_LL(user_id, api_key);
			cloud.get_stream_url(video_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void testCloudKey()
	{
		try
		{
			CloudKey_LL cloud = new CloudKey_LL(user_id, api_key);
			DCObject result = cloud.call
			(
				"media.list",
				DCObject.create()
						.push("fields", DCArray.create().push("id").push("meta.title"))
			);
			DCArray list = DCArray.create((ArrayList)result.get("list"));
			for(int i=0; i<list.size(); i++)
			{
				DCObject item = DCObject.create((Map)list.get(i));
				assertEquals((item.pull("meta.title") != ""), true);
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public void testCloudKeyError()
	{
		Boolean error = false;
		try
		{
			CloudKey_LL cloud = new CloudKey_LL(user_id.substring(0, 2), api_key);
			cloud.call
			(
				"media.list",
				DCObject.create()
						.push("fields", DCArray.create().push("id").push("meta.title"))
			);
		}
		catch (Exception e)
		{
			error = true;
			assertEquals(((CloudKey_Exception)e).getCode(), 400);
		}
		finally
		{
			assertEquals(error.booleanValue(), true);
		}
	}
	
	public void testCloudKeySign_UrlError()
	{
		Boolean error = false;
		try
		{
			CloudKey_Helpers.sign_url("", "", CloudKey_LL.CLOUDKEY_SECLEVEL_COUNTRY, "", "", "", null, null, 0);
		}
		catch (Exception e)
		{
			error = true;
		}
		finally
		{
			assertEquals(error.booleanValue(), true);
		}
	}
}
