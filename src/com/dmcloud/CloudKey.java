package com.dmcloud;

import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;

public class CloudKey
{
	public static int CLOUDKEY_SECLEVEL_NONE      = 0;
	public static int CLOUDKEY_SECLEVEL_DELEGATE  = 1 << 0;
	public static int CLOUDKEY_SECLEVEL_ASNUM     = 1 << 1;
	public static int CLOUDKEY_SECLEVEL_IP        = 1 << 2;
	public static int CLOUDKEY_SECLEVEL_USERAGENT = 1 << 3;
	public static int CLOUDKEY_SECLEVEL_USEONCE   = 1 << 4;
	public static int CLOUDKEY_SECLEVEL_COUNTRY   = 1 << 5;
	public static int CLOUDKEY_SECLEVEL_REFERER   = 1 << 6;
	
	public static String CLOUDKEY_API_URL = "http://api.dmcloud.net";
	public static String CLOUDKEY_CDN_URL = "http://cdn.dmcloud.net";
	public static String CLOUDKEY_STATIC_URL = "http://static.dmcloud.net";

	protected String user_id = null;
	protected String api_key = null;
	protected String base_url = null;
	protected String cdn_url = null;
	protected String proxy = null;

	public CloudKey(String _user_id, String _api_key) throws Exception
	{
		this(_user_id, _api_key, CLOUDKEY_API_URL, CLOUDKEY_CDN_URL, "");
	}

	public CloudKey(String _user_id, String _api_key, String _base_url, String _cdn_url, String _proxy) throws Exception
	{
		if (_user_id == null || _api_key == null)
		{
			throw new CloudKey_Exception("You must provide valid user_id and api_key parameters");
		}
		this.user_id = _user_id;
		this.api_key = _api_key;
		this.base_url = _base_url;
		this.cdn_url = _cdn_url;
		this.proxy = _proxy;
	}

	public DCObject call(String call, DCObject args) throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();

		DCObject jo = DCObject.create()
							  .push("call", call)
							  .push("args", args);
		
		jo.push("auth", this.user_id + ":"  + CloudKey_Helpers.md5(this.user_id + CloudKey_Helpers.normalize(jo) + this.api_key));
		
		String json_encoded = mapper.writeValueAsString(jo);
		String response = CloudKey_Helpers.curl(this.base_url + "/api", json_encoded);
		DCObject json_response = DCObject.create(mapper.readValue(response, Map.class));
		if (json_response.containsKey("error"))
		{
			String message = "";
			int error_code = Integer.parseInt(json_response.pull("error.code"));
			switch (error_code)
			{
				case 200  : message = "ProcessorException"; break;
				case 300  : message = "TransportException"; break;
				case 400  : message = "AuthenticationErrorException"; break;
				case 410  : message = "RateLimitExceededException"; break;
				case 500  : message = "SerializerException"; break;
				
				case 600  : message = "InvalidRequestException"; break;
				case 610  : message = "InvalidObjectException"; break;
				case 620  : message = "InvalidMethodException"; break;
				case 630  : message = "InvalidParamException"; break;
				
				case 1000 : message = "ApplicationException"; break;
				case 1010 : message = "NotFoundException"; break;
				case 1020 : message = "ExistsException"; break;
				case 1030 : message = "LimitExceededException"; break;
				
				default   : message = "RPCException (error:" + json_response.pull("error.code").toString() + ")"; break;
			}
			message += " : " + json_response.pull("error.message").toString();
			throw new CloudKey_Exception(message, error_code);
		}
		return DCObject.create((Map)json_response.get("result"));
	}
	
	public String get_embed_url(String id) throws CloudKey_Exception
	{
		return this.get_embed_url(CLOUDKEY_API_URL, id, CloudKey.CLOUDKEY_SECLEVEL_NONE, "", "", "", null, null, 0);
	}
	
	public String get_embed_url(String url, String id, int seclevel, String asnum, String ip, String useragent, String[] countries, String[] referers, int expires)  throws CloudKey_Exception
	{
		String _url = url + "/embed/" + this.user_id + "/" + id;
		return CloudKey_Helpers.sign_url(_url, this.api_key, seclevel, asnum, ip, useragent, countries, referers, expires);
	}
	
	public String get_stream_url(String id) throws CloudKey_Exception
	{
		return this.get_stream_url(CLOUDKEY_API_URL, id, "mp4_h264_aac", CloudKey.CLOUDKEY_SECLEVEL_NONE, "", "", "", null, null, 0, "", false);
	}
	
	public String get_stream_url(String url, String id, String asset_name, int seclevel, String asnum, String ip, String useragent, String[] countries, String[] referers, int expires, String extension, Boolean download)  throws CloudKey_Exception
	{
		if (extension == "")
		{
			String[] parts = asset_name.split("\\_");
			extension = (parts[0] != asset_name) ? parts[0] : extension;
		}
		if (asset_name.length() >= 15 && asset_name.substring(0, 15) == "jpeg_thumbnail_")
		{
			return CLOUDKEY_STATIC_URL + this.user_id + "/" + id + "/" + asset_name + "." + extension;
		}
		else
		{
			String _url = this.cdn_url + "/route/" + this.user_id + "/" + id + "/" + asset_name + ((extension != "") ? "." + extension : "");
			return CloudKey_Helpers.sign_url(_url, this.api_key, seclevel, asnum, ip, useragent, countries, referers, expires) + (download ? "&throttle=0&helper=0&cache=0" : "");
		}
	}
}
