package net.dmcloud.cloudkey;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import junit.framework.TestCase;
import net.dmcloud.util.*;

public class CloudKeyTest extends TestCase
{
	public static String user_id = "YOUR USER ID";
	public static String api_key = "YOUR API KEY";
	public static String video_id = "YOUR VIDEO ID";
	
        @Override
        protected void setUp()
        {
            Properties prop = new Properties();
            try
            {
                InputStream in = getClass().getClassLoader().getResourceAsStream("cloudkey.properties");
                prop.load(in);
                user_id = prop.getProperty("user_id");
                api_key = prop.getProperty("api_key");
                video_id = prop.getProperty("video_id");
            }
            catch (IOException e)
            {
                System.out.println("Cannot load cloudkey.properties file. Using defaults.");
            }
        }
        
	public void testCloudKey_Normalize()
	{
		assertEquals
		(
			Helpers.normalize
			(
				DCArray.create()
					   .push("foo")
					   .push(42)
					   .push("bar")
			), "foo42bar"
		);

		assertEquals
		(
			Helpers.normalize
			(
				DCObject.create()
						.push("yellow", 1)
						.push("red", 2)
						.push("pink", 3)
			), "pink3red2yellow1"
		);

		assertEquals
		(
			Helpers.normalize
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
			cloud.mediaGetEmbedUrl(CloudKey.API_URL, video_id, CloudKey.SECLEVEL_REFERER, "", "", "", null, referers, 0);
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
			assertEquals(((DCException)e).getCode(), 400);
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
			Helpers.sign_url("", "", CloudKey.SECLEVEL_COUNTRY, "", "", "", null, null, 0);
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
