package com.kharchabook.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kharchabook.dao.UserDAO;
import com.kharchabook.model.User;
import com.kharchabook.util.PasswordUtil;
import com.kharchabook.util.SessionKeys;
import com.kharchabook.util.ValidationUtil;

/**
 * Servlet handling admin profile view and update actions.
 *
 * Allows logged-in admins to view and update their profile details,
 * including password changes with current password verification.
 */
@WebServlet("/admin/profile")
public class AdminProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Retrieve the current admin session and user ID.
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);

        try {
            // Fetch the admin's profile data from the database.
            User u = userDAO.findById(userId);
            req.setAttribute("profileUser", u);

            // Forward to the admin profile JSP.
            req.getRequestDispatcher("/admin/profile.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Retrieve the current admin session and user ID.
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);

        // Extract form parameters.
        String fullName = req.getParameter("fullName");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String newPassword = req.getParameter("newPassword");
        String currentPassword = req.getParameter("currentPassword");

        try {
            // Fetch the current admin user record.
            User u = userDAO.findById(userId);
            if (u == null) {
                // If admin no longer exists, force logout.
                resp.sendRedirect(req.getContextPath() + "/logout");
                return;
            }

            // Validate required fields.
            if (ValidationUtil.isBlank(fullName) || ValidationUtil.isBlank(phone) || ValidationUtil.isBlank(email)) {
                session.setAttribute(SessionKeys.FLASH_ERROR, "This field is required. Please fill in all required fields before submitting.");
                resp.sendRedirect(req.getContextPath() + "/admin/profile");
                return;
            }

            // Validate full name format.
            if (!ValidationUtil.isValidFullName(fullName)) {
                session.setAttribute(SessionKeys.FLASH_ERROR, "Full name must contain letters only. Please enter a valid name.");
                resp.sendRedirect(req.getContextPath() + "/admin/profile");
                return;
            }

            // Check for duplicate phone number.
            if (userDAO.phoneExists(phone, userId)) {
                session.setAttribute(SessionKeys.FLASH_ERROR, "A user with this phone number already exists. Please use a different number.");
                resp.sendRedirect(req.getContextPath() + "/admin/profile");
                return;
            }

            // Check for duplicate email address.
            if (userDAO.emailExists(email, userId)) {
                session.setAttribute(SessionKeys.FLASH_ERROR, "This email address is already registered.");
                resp.sendRedirect(req.getContextPath() + "/admin/profile");
                return;
            }

            // Update profile information.
            userDAO.updateProfile(userId, fullName.trim(), phone.trim(), email.trim().toLowerCase());
            session.setAttribute(SessionKeys.USER_NAME, fullName.trim());
            session.setAttribute(SessionKeys.USER_EMAIL, email.trim().toLowerCase());

            // Handle password update if provided.
            if (!ValidationUtil.isBlank(newPassword)) {
                // Verify current password before allowing change.
                if (ValidationUtil.isBlank(currentPassword) || !PasswordUtil.matches(currentPassword, u.getPassword())) {
                    session.setAttribute(SessionKeys.FLASH_ERROR, "Current password is incorrect.");
                    resp.sendRedirect(req.getContextPath() + "/admin/profile");
                    return;
                }
                // Update password with hash.
                userDAO.updatePassword(userId, PasswordUtil.sha256Hex(newPassword));
            }

            // Set success message and redirect.
            session.setAttribute(SessionKeys.FLASH_SUCCESS, "Profile updated.");
            resp.sendRedirect(req.getContextPath() + "/admin/profile");
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
