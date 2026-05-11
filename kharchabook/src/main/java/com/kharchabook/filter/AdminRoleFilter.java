package com.kharchabook.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kharchabook.util.SessionKeys;

/**
 * Servlet filter that restricts access to admin-only pages.
 *
 * If the current user session does not have the ADMIN role, the request is
 * redirected to the user dashboard with a flash error message.
 */
public class AdminRoleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // No filter-specific initialization required.
    }

    @Override
    public void destroy() {
        // No cleanup required when the filter is destroyed.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Use an existing session if present; do not create a new one just for checking role.
        HttpSession session = req.getSession(false);
        String role = session == null ? null : (String) session.getAttribute(SessionKeys.USER_ROLE);

        // If the user is not an admin, redirect to the dashboard with an error message.
        if (!"ADMIN".equals(role)) {
            session = req.getSession(true);
            session.setAttribute(SessionKeys.FLASH_ERROR, "You do not have permission to access that page.");
            resp.sendRedirect(req.getContextPath() + "/user/dashboard");
            return;
        }

        // User is an admin, allow the request to continue.
        chain.doFilter(request, response);
    }
}
 