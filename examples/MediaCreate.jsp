<%@ page import="com.dmcloud.*"%>
<%@ include file="Config.jsp"%>
<%
    String url = request.getParameter("url");

    CloudKey_Media media = new CloudKey_Media(user_id, api_key);
    out.write("video id : " + media.create(url));
%>
