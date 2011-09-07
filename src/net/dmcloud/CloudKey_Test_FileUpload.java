package net.dmcloud;

import java.io.File;

public class CloudKey_Test_FileUpload
{
	public static void main(String[] args)
	{
		try
		{
			CloudKey cloud = new CloudKey(CloudKey_Test.user_id, CloudKey_Test.api_key);
			File f = new File("file-example.mp4");
			String media_id = cloud.mediaCreate(f);
			System.out.println(media_id);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}