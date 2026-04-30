package com.kharchabook.servlet;

import com.kharchabook.dao.UserDAO;
import com.kharchabook.model.User;
import com.kharchabook.util.PasswordUtil;
import com.kharchabook.util.SessionKeys;
import com.kharchabook.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/profile")
public class AdminProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);
        try {
            User u = userDAO.findById(userId);
            req.setAttribute("profileUser", u);
            req.getRequestDispatcher("/admin/profile.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);
        String fullName = req.getParameter("fullName");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String newPassword = req.getParameter("newPassword");
        String currentPassword = req.getParameter("currentPassword");
        try {
            User u = userDAO.findById(userId);
            if (u == null) {
                resp.sendRedirect(req.getContextPath() + "/logout");
                return;
            }
            if (ValidationUtil.isBlank(fullName) || ValidationUtil.isBlank(phone) || ValidationUtil.isBlank(email)) {
                session.setAttribute(SessionKeys.FLASH_ERROR, "This field is required. Please fill in all required fields before submitting.");
                resp.sendRedirect(req.getContextPath() + "/admin/profile");
                return;
            }
            if (!ValidationUtil.isValidFullName(fullName)) {
                session.setAttribute(SessionKeys.FLASH_ERROR, "Full name must contain letters only. Please enter a valid name.");
                resp.sendRedirect(req.getContextPath() + "/admin/profile");
                return;
            }
            if (userDAO.phoneExists(phone, userId)) {
                session.setAttribute(SessionKeys.FLASH_ERROR, "A user with this phone number already exists. Please use a different number.");
                resp.sendRedirect(req.getContextPath() + "/admin/profile");
                return;
            }
            if (userDAO.emailExists(email, userId)) {
                session.setAttribute(SessionKeys.FLASH_ERROR, "This email address is already registered.");
                resp.sendRedirect(req.getContextPath() + "/admin/profile");
                return;
            }
            userDAO.updateProfile(userId, fullName.trim(), phone.trim(), email.trim().toLowerCase());
            session.setAttribute(SessionKeys.USER_NAME, fullName.trim());
            session.setAttribute(SessionKeys.USER_EMAIL, email.trim().toLowerCase());
            if (!ValidationUtil.isBlank(newPassword)) {
                if (ValidationUtil.isBlank(currentPassword) || !PasswordUtil.matches(currentPassword, u.getPassword())) {
                    session.setAttribute(SessionKeys.FLASH_ERROR, "Current password is incorrect.");
                    resp.sendRedirect(req.getContextPath() + "/admin/profile");
                    return;
                }
                userDAO.updatePassword(userId, PasswordUtil.sha256Hex(newPassword));
            }
            session.setAttribute(SessionKeys.FLASH_SUCCESS, "Profile updated.");
            resp.sendRedirect(req.getContextPath() + "/admin/profile");
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
