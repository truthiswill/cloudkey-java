<h1>My videos on DM Cloud</h1>

<%@ page import="com.dmcloud.*"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.net.InetAddress"%>
<%@ page import="java.util.ArrayList"%>
<%@ include file="Config.jsp"%>
<%
String server_url = "http://" + InetAddress.getLocalHost().getHostName();

try
{
    CloudKey cloud = new CloudKey(user_id, api_key);

    DCObject result = cloud.call
        (
         "media.list",
         DCObject.create()
         .push("fields", DCArray.create()
             .push("id")
             .push("meta.title")
             .push("assets.jpeg_thumbnail_auto.stream_url")
             .push("assets.mp4_h264_aac.video_width")
             .push("assets.mp4_h264_aac.video_height")
             .push("assets.source.download_url")
             )
        );
    DCArray list = DCArray.create((ArrayList)result.get("list"));
    for(int i=0; i<list.size(); i++)
    {
        DCObject item = DCObject.create((Map)list.get(i));
        out.write("<p>Title : " + item.pull("meta.title") + "</p>");
        out.write("<p><img src=\"" + item.pull("assets.jpeg_thumbnail_auto.stream_url") + "\" /></p>");
        String[] referers = {server_url};
        String embed_url = cloud.get_embed_url(CloudKey.CLOUDKEY_API_URL, item.get("id").toString(), CloudKey.CLOUDKEY_SECLEVEL_REFERER, "", "", "", null, referers, 0);
        out.write("<iframe width=\"" + item.pull("assets.mp4_h264_aac.video_width") + "\" height=\"" + item.pull("assets.mp4_h264_aac.video_height") + "\" src=\"" + embed_url  + "\"></iframe>");
        String stream_url = cloud.get_stream_url(CloudKey.CLOUDKEY_API_URL, item.get("id").toString(), "mp4_h264_aac", CloudKey.CLOUDKEY_SECLEVEL_REFERER, "", "", "", null, referers, 0, "", false);
        String dl_url = item.pull("assets.source.download_url");
        out.write("<p><a href=\"" + dl_url + "\">Download source</a></p>");
        out.write("<p><a href=\"" + stream_url + "\">Stream url</a></p>");
    }
}
catch(Exception e)
{
    out.write("<p>Error : " + e.getMessage() + "</p>");
}
%>
