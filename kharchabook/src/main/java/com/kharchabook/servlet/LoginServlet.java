package com.kharchabook.servlet;

import com.kharchabook.dao.UserDAO;
import com.kharchabook.dao.RememberMeTokenDAO;
import com.kharchabook.model.User;
import com.kharchabook.util.PasswordUtil;
import com.kharchabook.util.SessionKeys;
import com.kharchabook.util.TokenUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final RememberMeTokenDAO rememberMeTokenDAO = new RememberMeTokenDAO();

    private static final String REMEMBER_COOKIE = "KB_REMEMBER";
    private static final int REMEMBER_DAYS = 30;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(SessionKeys.USER_ID) != null) {
            redirectByRole(resp, req.getContextPath(), (String) session.getAttribute(SessionKeys.USER_ROLE));
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        boolean remember = req.getParameter("remember") != null;
        if (login == null || password == null || login.trim().isEmpty()) {
            req.getSession(true).setAttribute(SessionKeys.FLASH_ERROR, "This field is required. Please fill in all required fields before submitting.");
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        try {
            User u = userDAO.findByEmailOrPhone(login.trim());
            if (u == null || !PasswordUtil.matches(password, u.getPassword())) {
                req.getSession(true).setAttribute(SessionKeys.FLASH_ERROR, "Incorrect email or password. Please try again.");
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
            if ("PENDING".equals(u.getStatus())) {
                req.getSession(true).setAttribute(SessionKeys.FLASH_ERROR, "Your account is awaiting admin approval. Please check back later.");
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
            if ("BLOCKED".equals(u.getStatus())) {
                req.getSession(true).setAttribute(SessionKeys.FLASH_ERROR, "Your account has been blocked. Contact support.");
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
            HttpSession session = req.getSession(true);
            session.setAttribute(SessionKeys.USER_ID, u.getId());
            session.setAttribute(SessionKeys.USER_ROLE, u.getRole());
            session.setAttribute(SessionKeys.USER_NAME, u.getFullName());
            session.setAttribute(SessionKeys.USER_EMAIL, u.getEmail());

            if (remember) {
                rememberMeTokenDAO.deleteExpired();
                String selector = TokenUtil.randomUrlToken(16);
                String validator = TokenUtil.randomUrlToken(32);
                String tokenHash = PasswordUtil.sha256Hex(validator);
                LocalDateTime expires = LocalDateTime.now().plusDays(REMEMBER_DAYS);
                rememberMeTokenDAO.upsert(u.getId(), selector, tokenHash, expires);

                Cookie cookie = new Cookie(REMEMBER_COOKIE, selector + ":" + validator);
                cookie.setHttpOnly(true);
                cookie.setSecure(req.isSecure());
                cookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
                cookie.setMaxAge(REMEMBER_DAYS * 24 * 60 * 60);
                resp.addCookie(cookie);
            }

            redirectByRole(resp, req.getContextPath(), u.getRole());
        } catch (SQLException | IllegalStateException e) {
            log("Unable to complete login because the database is unavailable or misconfigured", e);
            req.getSession(true).setAttribute(
                    SessionKeys.FLASH_ERROR,
                    "Unable to connect to the database. Please check that MySQL is running and db.properties has the correct port, username, and password."
            );
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        }
    }

    private void redirectByRole(HttpServletResponse resp, String ctx, String role) throws IOException {
        if ("ADMIN".equals(role)) {
            resp.sendRedirect(ctx + "/admin/dashboard");
        } else {
            resp.sendRedirect(ctx + "/user/dashboard");
        }
    }
}
