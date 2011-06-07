package com.dmcloud;

import java.util.ArrayList;
import java.util.Map;

public class CloudKey extends CloudKey_LL {
	
	public CloudKey(String _user_id, String _api_key) throws Exception
	{
		super(_user_id, _api_key, CLOUDKEY_API_URL, CLOUDKEY_CDN_URL, "");
	}

	public CloudKey(String _user_id, String _api_key, String _base_url, String _cdn_url, String _proxy) throws Exception
	{
		super(_user_id, _api_key, _base_url, _cdn_url, _proxy);
	}
	
	public String mediaCreate() throws Exception
	{
		return mediaCreate("", null, null);
	}
	
	public String mediaCreate(String url, ArrayList<String> assets_names, Map meta) throws Exception
	{
		DCObject args = DCObject.create()
								.push("url", url)
								.push("meta", meta)
								.push("assets_names", assets_names);
		Map result = this.call("media.create", args);
		return result.get("id").toString();
	}
	
	public void mediaDelete(String id) throws Exception
	{
		this.call("media.delete", DCObject.create().push("id", id));
	}

	public String upload() throws Exception
	{
		Map result = this.call("file.upload", null);
		return result.get("url").toString();
	}
}
