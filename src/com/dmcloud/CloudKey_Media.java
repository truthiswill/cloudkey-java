package com.dmcloud;

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
	
	public void delete(String id) throws Exception
	{
		this.call("media.delete", DCObject.create().push("id", id));
	}

	public String upload() throws Exception
	{
		DCObject result = upload(false, false, "");
		return result.pull("url");
	}
	
	public DCObject upload(Boolean status, Boolean jsonp_cb, String target) throws Exception
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
