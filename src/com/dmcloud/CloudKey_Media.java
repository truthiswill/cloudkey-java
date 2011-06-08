package com.dmcloud;

import java.io.*;
import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.codehaus.jackson.map.ObjectMapper;

public class CloudKey_Media extends CloudKey
{
	public CloudKey_Media(String _user_id, String _api_key) throws Exception
	{
		super(_user_id, _api_key, CLOUDKEY_API_URL, CLOUDKEY_CDN_URL, "");
	}

	public CloudKey_Media(String _user_id, String _api_key, String _base_url, String _cdn_url, String _proxy) throws Exception
	{
		super(_user_id, _api_key, _base_url, _cdn_url, _proxy);
	}
	
	public String create() throws Exception
	{
		return create("", null, null);
	}
	
	public String create(String url) throws Exception
	{
		return create(url, null, null);
	}
	
	public String create(String url, DCArray assets_names, DCObject meta) throws Exception
	{
		DCObject args = DCObject.create().push("url", url);
		if (assets_names != null && assets_names.size() > 0)
		{
			args.push("assets_names", assets_names);
		}
		if (meta != null && meta.size() > 0)
		{
			args.push("meta", meta);
		}
		DCObject result = this.call("media.create", args);
		return result.pull("id");
	}
	
	public String uploadFile(File f) throws Exception
	{
		return this.uploadFile(f, null, null);
	}
	
	public String uploadFile(File f, DCArray assets_names, DCObject meta) throws Exception
	{
		String upload_url = this.getUploadUrl();
		
		PostMethod filePost = null;
		try
		{			
			filePost = new PostMethod(upload_url);

			Part[] parts = {
					new FilePart("file", f)
			};
			
			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
			HttpClient client = new HttpClient();
            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            
            int status = client.executeMethod(filePost);
            if (status == HttpStatus.SC_OK)
            {
    			ObjectMapper mapper = new ObjectMapper();
            	DCObject json_response = DCObject.create(mapper.readValue(filePost.getResponseBodyAsString(), Map.class));
            	return this.create(json_response.pull("url"), assets_names, meta);
            }
            else
            {
            	throw new CloudKey_Exception("Upload failed.");
            }
		}
		catch (Exception e)
		{
			throw new CloudKey_Exception("Upload failed.");
		}
		finally
		{
			if (filePost != null)
			{
				filePost.releaseConnection();
			}
        }
	}
	
	public void delete(String id) throws Exception
	{
		this.call("media.delete", DCObject.create().push("id", id));
	}

	public String getUploadUrl() throws Exception
	{
		DCObject result = getUploadUrl(false, false, "");
		return result.pull("url");
	}
	
	public DCObject getUploadUrl(Boolean status, Boolean jsonp_cb, String target) throws Exception
	{
		DCObject args = DCObject.create();
		if (status)
		{
			args.push("status", true);
		}
		if (jsonp_cb)
		{
			args.push("jsonp_cb", "?");
		}
		if (target != "")
		{
			args.push("target", target);
		}
		return (DCObject) this.call("file.upload", args);
	}
}
