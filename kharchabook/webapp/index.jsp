<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    // Get logged-in user ID from session
    Integer uid = (Integer) session.getAttribute("userId");

    // Get application context path (used for building URLs)
    String ctx = request.getContextPath();

    // If user is already logged in
    if (uid != null) {

        // Get user role (ADMIN or USER)
        String role = (String) session.getAttribute("userRole");

        // Redirect based on role
        if ("ADMIN".equals(role)) {
            // Admin goes to admin dashboard
            response.sendRedirect(ctx + "/admin/dashboard");
        } else {
            // Normal user goes to user dashboard
            response.sendRedirect(ctx + "/user/dashboard");
        }

        // Stop further execution after redirect
        return;
    }

    // If user is NOT logged in → redirect to login page
    response.sendRedirect(ctx + "/login.jsp");
%>