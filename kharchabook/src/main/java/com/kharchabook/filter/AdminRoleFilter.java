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

public class AdminRoleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        String role = session == null ? null : (String) session.getAttribute(SessionKeys.USER_ROLE);
        if (!"ADMIN".equals(role)) {
            session = req.getSession(true);
            session.setAttribute(SessionKeys.FLASH_ERROR, "You do not have permission to access that page.");
            resp.sendRedirect(req.getContextPath() + "/user/dashboard");
            return;
        }
        chain.doFilter(request, response);
    }
}
