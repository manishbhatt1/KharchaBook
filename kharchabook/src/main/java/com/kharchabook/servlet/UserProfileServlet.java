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

/**
 * Handles user profile view and update actions.
 * Allows logged-in users to view and update profile details.
 */
@WebServlet("/user/profile")
public class UserProfileServlet extends HttpServlet {

    // DAO object used to access user-related database operations
    private final UserDAO userDAO = new UserDAO();

    /**
     * Loads the logged-in user's profile page.
     * Fetches user details and sends them to profile.jsp.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get current session
        HttpSession session = req.getSession();

        // Get logged-in user ID from session
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);

        try {
            // Fetch user details from database
            User u = userDAO.findById(userId);

            // Send user data to JSP
            req.setAttribute("profileUser", u);

            // Forward request to profile page
            req.getRequestDispatcher("/user/profile.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Handles profile update form submission.
     * Updates user info and password if provided.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Get current session
        HttpSession session = req.getSession();

        // Get logged-in user ID
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);

        // Read form values
        String fullName = req.getParameter("fullName");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String newPassword = req.getParameter("newPassword");
        String currentPassword = req.getParameter("currentPassword");

        try {
            // Get current user record from database
            User u = userDAO.findById(userId);

            // If user is missing, force logout
            if (u == null) {
                resp.sendRedirect(req.getContextPath() + "/logout");
                return;
            }

            // Check required fields
            if (ValidationUtil.isBlank(fullName) || ValidationUtil.isBlank(phone) || ValidationUtil.isBlank(email)) {
                session.setAttribute(SessionKeys.FLASH_ERROR, "This field is required. Please fill in all required fields before submitting.");
                resp.sendRedirect(req.getContextPath() + "/user/profile");
                return;
            }

            // Validate full name format
            if (!ValidationUtil.isValidFullName(fullName)) {
                session.setAttribute(SessionKeys.FLASH_ERROR, "Full name must contain letters only. Please enter a valid name.");
                resp.sendRedirect(req.getContextPath() + "/user/profile");
                return;
            }

            // Check if phone already exists for another user
            if (userDAO.phoneExists(phone, userId)) {
                session.setAttribute(SessionKeys.FLASH_ERROR, "A user with this phone number already exists. Please use a different number.");
                resp.sendRedirect(req.getContextPath() + "/user/profile");
                return;
            }

            // Check if email already exists for another user
            if (userDAO.emailExists(email, userId)) {
                session.setAttribute(SessionKeys.FLASH_ERROR, "This email address is already registered. Please log in or use a different email.");
                resp.sendRedirect(req.getContextPath() + "/user/profile");
                return;
            }

            // Update profile information
            userDAO.updateProfile(userId, fullName.trim(), phone.trim(), email.trim().toLowerCase());

            // Update session values after successful profile update
            session.setAttribute(SessionKeys.USER_NAME, fullName.trim());
            session.setAttribute(SessionKeys.USER_EMAIL, email.trim().toLowerCase());

            // Update password only if new password is entered
            if (!ValidationUtil.isBlank(newPassword)) {

                // Verify current password before allowing password change
                if (ValidationUtil.isBlank(currentPassword) || !PasswordUtil.matches(currentPassword, u.getPassword())) {
                    session.setAttribute(SessionKeys.FLASH_ERROR, "Current password is incorrect.");
                    resp.sendRedirect(req.getContextPath() + "/user/profile");
                    return;
                }

                // Save new encrypted password
                userDAO.updatePassword(userId, PasswordUtil.sha256Hex(newPassword));
            }

            // Success message
            session.setAttribute(SessionKeys.FLASH_SUCCESS, "Profile updated.");
            resp.sendRedirect(req.getContextPath() + "/user/profile");

        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}