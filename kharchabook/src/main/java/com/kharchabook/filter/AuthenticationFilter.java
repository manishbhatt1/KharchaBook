package com.kharchabook.filter;

import com.kharchabook.dao.RememberMeTokenDAO;
import com.kharchabook.dao.UserDAO;
import com.kharchabook.model.User;
import com.kharchabook.util.PasswordUtil;
import com.kharchabook.util.SessionKeys;
import com.kharchabook.util.TokenUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AuthenticationFilter implements Filter {

    private static final String REMEMBER_COOKIE = "KB_REMEMBER";
    private static final int REMEMBER_DAYS = 30;
    private final RememberMeTokenDAO rememberMeTokenDAO = new RememberMeTokenDAO();
    private final UserDAO userDAO = new UserDAO();

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
        Integer userId = session == null ? null : (Integer) session.getAttribute(SessionKeys.USER_ID);
        if (userId == null) {
            if (tryRememberMe(req, resp)) {
                chain.doFilter(request, response);
                return;
            }
            session = req.getSession(true);
            session.setAttribute(SessionKeys.FLASH_ERROR, "Please log in to access this page.");
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean tryRememberMe(HttpServletRequest req, HttpServletResponse resp) {
        String raw = readCookie(req, REMEMBER_COOKIE);
        if (raw == null || raw.trim().isEmpty()) {
            return false;
        }
        String[] parts = raw.split(":", 2);
        if (parts.length != 2) {
            clearCookie(resp, req.getContextPath(), REMEMBER_COOKIE);
            return false;
        }
        String selector = parts[0];
        String validator = parts[1];
        try {
            RememberMeTokenDAO.TokenRecord record = rememberMeTokenDAO.findBySelector(selector);
            if (record == null || record.expiresAt == null || record.expiresAt.isBefore(LocalDateTime.now())) {
                rememberMeTokenDAO.deleteBySelector(selector);
                clearCookie(resp, req.getContextPath(), REMEMBER_COOKIE);
                return false;
            }
            String validatorHash = PasswordUtil.sha256Hex(validator);
            if (record.tokenHash == null || !record.tokenHash.equalsIgnoreCase(validatorHash)) {
                rememberMeTokenDAO.deleteBySelector(selector);
                clearCookie(resp, req.getContextPath(), REMEMBER_COOKIE);
                return false;
            }

            User u = userDAO.findById(record.userId);
            if (u == null || !"APPROVED".equals(u.getStatus())) {
                rememberMeTokenDAO.deleteBySelector(selector);
                clearCookie(resp, req.getContextPath(), REMEMBER_COOKIE);
                return false;
            }

            // Rotate validator to reduce replay risk.
            String newValidator = TokenUtil.randomUrlToken(32);
            String newHash = PasswordUtil.sha256Hex(newValidator);
            LocalDateTime newExpiry = LocalDateTime.now().plusDays(REMEMBER_DAYS);
            rememberMeTokenDAO.updateTokenHashAndExpiry(record.id, newHash, newExpiry);

            Cookie cookie = new Cookie(REMEMBER_COOKIE, selector + ":" + newValidator);
            cookie.setHttpOnly(true);
            cookie.setSecure(req.isSecure());
            cookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
            cookie.setMaxAge(REMEMBER_DAYS * 24 * 60 * 60);
            resp.addCookie(cookie);

            HttpSession session = req.getSession(true);
            session.setAttribute(SessionKeys.USER_ID, u.getId());
            session.setAttribute(SessionKeys.USER_ROLE, u.getRole());
            session.setAttribute(SessionKeys.USER_NAME, u.getFullName());
            session.setAttribute(SessionKeys.USER_EMAIL, u.getEmail());
            return true;
        } catch (SQLException | IllegalStateException e) {
            return false;
        }
    }

    private static String readCookie(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    private static void clearCookie(HttpServletResponse resp, String ctx, String name) {
        Cookie c = new Cookie(name, "");
        c.setHttpOnly(true);
        c.setPath((ctx == null || ctx.isEmpty()) ? "/" : ctx);
        c.setMaxAge(0);
        resp.addCookie(c);
    }
}
