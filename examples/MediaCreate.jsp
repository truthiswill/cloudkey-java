<%@ page import="com.dmcloud.*"%>
<%@ page import="java.util.*"%>
<%
    String url = request.getParameter("url");

    String user_id = "4de60a1394a6f67a1a0004de";
    String api_key = "5c3c25530b51ad73b23c8582c3c144e880f6a9a9";
    CloudKey_Media media = new CloudKey_Media(user_id, api_key);
    out.write(media.create(url));
%>
