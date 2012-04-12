package net.dmcloud.util;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.io.IOException;
import net.dmcloud.util.DCObject;
import net.dmcloud.cloudkey.Helpers;
import org.codehaus.jackson.map.ObjectMapper;
import org.apache.commons.codec.binary.Base64;

public class FAToken {

    private String user_id  = null;
    private String media_id = null;
    private String api_key = null;
    private HashMap<String,String> rights;
    private HashMap<String,String> data;
    private String callback = null;
    private Integer expires = 0;

    public static int DEFAULT_TIMEOUT = 600;

    public FAToken(String token, String api_key) throws Exception {
        this(token, api_key, true);
    }

    public FAToken(String token, String api_key, Boolean check_auth) throws Exception {
		ObjectMapper mapper;
		DCObject token_data;
        String auth;

        this.api_key = api_key;

        if (Base64.isBase64(token)) {
            token = new String(Base64.decodeBase64(token));
        }

        mapper = new ObjectMapper();
        token_data = DCObject.create(mapper.readValue(token, Map.class));
        user_id = token_data.get("user_id").toString();
        media_id = token_data.get("media_id").toString();
        expires = Integer.parseInt(token_data.get("expires").toString());
        if (token_data.containsKey("rights")) {
            rights = (HashMap<String,String>)token_data.get("rights");
        }
        if (token_data.containsKey("data")) {
            data = (HashMap<String,String>)token_data.get("data");
        }
        if (token_data.containsKey("callback")) {
            callback = token_data.get("callback").toString();
        }

        if (check_auth == true) {
            auth = token_data.get("auth").toString();
        
            token_data.remove("auth");
            if (!getAuth(token_data).equals(auth)) {
                throw new Exception("Auth doesn't match");
            }
        }
    }
 
    public FAToken(String user_id, String media_id, String api_key) {
        this(user_id, media_id, api_key, (int)(new Date().getTime() / 1000) + DEFAULT_TIMEOUT);
    }

    public FAToken(String user_id, String media_id, String api_key, Integer expires) {
        this.user_id = user_id;
        this.media_id = media_id;
        this.api_key = api_key;
        this.rights = new HashMap<String,String>();
        this.data = new HashMap<String,String>();
        this.expires = expires;
    }

    public Boolean isExpired() {
        Date expireDate = new Date(Long.valueOf(expires) * 1000);
        return expireDate.before(new Date());
    }
    
    public void setExpires(Integer expires) {
        this.expires = expires;
    }

    public Integer getExpires() {
        return this.expires;
    }

    public String getUserId() {
        return user_id;
    }

    public String getMediaId() {
        return media_id;
    }

    public void setRight(String name, String value) {
        rights.put(name, value);
    }

    public HashMap<String,String> getRights() {
        return rights;
    }

    public void setData(String name, String value) {
        data.put(name, value);
    }

    public HashMap<String,String> getData() {
        return data;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getCallback() {
        return this.callback;
    }

    public String getAuth(DCObject token_data) {
        return Helpers.md5(Helpers.normalize(token_data) + api_key);
    }

    public String toJSON() throws Exception {
		ObjectMapper mapper;
        DCObject token_data;

		token_data = DCObject.create()
            .push("user_id", user_id)
            .push("media_id", media_id)
            .push("expires", expires);
        if (callback != null) {
            token_data.push("callback", callback);
        }
        if (!this.rights.isEmpty()) {
            token_data.push("rights", rights);
        }
        if (!this.data.isEmpty()) {
            token_data.push("data", data);
        }
        mapper = new ObjectMapper();
		token_data.push("auth", getAuth(token_data));
        return mapper.writeValueAsString(token_data);
    }

    public String toBase64() throws Exception {
        return Base64.encodeBase64String(toJSON().getBytes());
   }

}