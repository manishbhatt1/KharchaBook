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
 * Servlet filter that restricts access to user-only pages.
 *
 * If the current session does not belong to a normal USER, the request is
 * redirected to the admin dashboard.
 */
public class UserRoleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // No initialization required for this filter.
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

        // Use an existing session if available; do not create a new one solely for role checking.
        HttpSession session = req.getSession(false);
        String role = session == null ? null : (String) session.getAttribute(SessionKeys.USER_ROLE);

        // Redirect non-user roles to the admin dashboard.
        if (!"USER".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        }

        // User role is valid; continue processing.
        chain.doFilter(request, response);
    }
}
