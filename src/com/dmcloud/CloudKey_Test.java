package com.dmcloud;

import java.util.ArrayList;
import java.util.Map;
import junit.framework.TestCase;

public class CloudKey_Test extends TestCase
{
	public static String user_id = "YOUR USER ID";
	public static String api_key = "YOUR API KEY";
	public static String video_id = "YOUR VIDEO ID";
	
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
			CloudKey cloud = new CloudKey(user_id, api_key);
			String[] referers = {"http://test.dmcloud.net"};
			cloud.mediaGetEmbedUrl(CloudKey.CLOUDKEY_API_URL, video_id, CloudKey.CLOUDKEY_SECLEVEL_REFERER, "", "", "", null, referers, 0);
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
			CloudKey cloud = new CloudKey(user_id, api_key);
			cloud.mediaGetStreamUrl(video_id);
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
			CloudKey cloud = new CloudKey(user_id, api_key);
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
			e.printStackTrace();
		}
	}
	
	public void testCloudKeyError()
	{
		Boolean error = false;
		try
		{
			CloudKey cloud = new CloudKey(user_id.substring(0, 2), api_key);
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
			CloudKey_Helpers.sign_url("", "", CloudKey.CLOUDKEY_SECLEVEL_COUNTRY, "", "", "", null, null, 0);
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
