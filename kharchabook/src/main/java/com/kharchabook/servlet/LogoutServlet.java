package com.kharchabook.servlet;

import com.kharchabook.dao.RememberMeTokenDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final String REMEMBER_COOKIE = "KB_REMEMBER";
    private final RememberMeTokenDAO rememberMeTokenDAO = new RememberMeTokenDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        clearRememberMe(req, resp);
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }

    private void clearRememberMe(HttpServletRequest req, HttpServletResponse resp) {
        String selector = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (REMEMBER_COOKIE.equals(c.getName())) {
                    String v = c.getValue();
                    if (v != null) {
                        String[] parts = v.split(":", 2);
                        if (parts.length == 2) {
                            selector = parts[0];
                        }
                    }
                }
            }
        }
        if (selector != null) {
            try {
                rememberMeTokenDAO.deleteBySelector(selector);
            } catch (SQLException ignored) {
            }
        }
        Cookie c = new Cookie(REMEMBER_COOKIE, "");
        c.setHttpOnly(true);
        c.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
        c.setMaxAge(0);
        resp.addCookie(c);
    }
}
