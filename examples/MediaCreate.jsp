<%@ page import="com.dmcloud.*"%>
<%
    String url = request.getParameter("url");

    String user_id = "YOUR USER ID";
    String api_key = "YOUR API KEY";
    CloudKey_Media media = new CloudKey_Media(user_id, api_key);
    out.write("video id : " + media.create(url));
%>
