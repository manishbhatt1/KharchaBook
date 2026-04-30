<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    Integer uid = (Integer) session.getAttribute("userId");
    String ctx = request.getContextPath();
    if (uid != null) {
        String role = (String) session.getAttribute("userRole");
        if ("ADMIN".equals(role)) {
            response.sendRedirect(ctx + "/admin/dashboard");
        } else {
            response.sendRedirect(ctx + "/user/dashboard");
        }
        return;
    }
    response.sendRedirect(ctx + "/login.jsp");
%>
