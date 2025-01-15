<%--
  Created by IntelliJ IDEA.
  User: Thathshila
  Date: 1/9/2025
  Time: 2:55 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>JSP</title>
</head>
<body>
<% String msg = "Hello JSP1";
out.println(msg);
%>
<%!
public String getMessage(){
    return "Hello JSP";
}
%>
<%=getMessage()%>
<%=request.getParameter("name")%>
</body>
</html>
