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
        // Retrieve the current user session.
        HttpSession session = req.getSession();

        // Extract the logged-in user's ID from the session.
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);

        try {
            // Query the database for the user's profile information.
            User u = userDAO.findById(userId);

            // Make the user object available to the JSP for display.
            req.setAttribute("profileUser", u);

            // Forward the request to the profile JSP page.
            req.getRequestDispatcher("/user/profile.jsp").forward(req, resp);

        } catch (SQLException e) {
            // Wrap database exceptions in a ServletException for proper error handling.
            throw new ServletException(e);
        }
    }

    /**
     * Handles profile update form submission.
     * Updates user info and password if provided.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Retrieve the current user session.
        HttpSession session = req.getSession();

        // Extract the logged-in user's ID from the session.
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);

        // Extract form parameters submitted by the user.
        String fullName = req.getParameter("fullName");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String newPassword = req.getParameter("newPassword");
        String currentPassword = req.getParameter("currentPassword");

        try {
            // Retrieve the current user record from the database for validation.
            User u = userDAO.findById(userId);

            // If the user no longer exists, force a logout to prevent inconsistencies.
            if (u == null) {
                resp.sendRedirect(req.getContextPath() + "/logout");
                return;
            }

            // Validate that all required fields are provided and not empty.
            if (ValidationUtil.isBlank(fullName) || ValidationUtil.isBlank(phone) || ValidationUtil.isBlank(email)) {
                session.setAttribute(SessionKeys.FLASH_ERROR, "This field is required. Please fill in all required fields before submitting.");
                resp.sendRedirect(req.getContextPath() + "/user/profile");
                return;
            }

            // Ensure the full name contains only valid characters (letters and spaces).
            if (!ValidationUtil.isValidFullName(fullName)) {
                session.setAttribute(SessionKeys.FLASH_ERROR, "Full name must contain letters only. Please enter a valid name.");
                resp.sendRedirect(req.getContextPath() + "/user/profile");
                return;
            }

            // Check for duplicate phone numbers among other users.
            if (userDAO.phoneExists(phone, userId)) {
                session.setAttribute(SessionKeys.FLASH_ERROR, "A user with this phone number already exists. Please use a different number.");
                resp.sendRedirect(req.getContextPath() + "/user/profile");
                return;
            }

            // Check for duplicate email addresses among other users.
            if (userDAO.emailExists(email, userId)) {
                session.setAttribute(SessionKeys.FLASH_ERROR, "This email address is already registered. Please log in or use a different email.");
                resp.sendRedirect(req.getContextPath() + "/user/profile");
                return;
            }

            // Update the user's profile information in the database.
            userDAO.updateProfile(userId, fullName.trim(), phone.trim(), email.trim().toLowerCase());

            // Refresh the session attributes with the updated user details.
            session.setAttribute(SessionKeys.USER_NAME, fullName.trim());
            session.setAttribute(SessionKeys.USER_EMAIL, email.trim().toLowerCase());

            // Handle password update only if a new password is provided.
            if (!ValidationUtil.isBlank(newPassword)) {

                // Verify the current password matches before allowing the change.
                if (ValidationUtil.isBlank(currentPassword) || !PasswordUtil.matches(currentPassword, u.getPassword())) {
                    session.setAttribute(SessionKeys.FLASH_ERROR, "Current password is incorrect.");
                    resp.sendRedirect(req.getContextPath() + "/user/profile");
                    return;
                }

                // Hash and save the new password securely.
                userDAO.updatePassword(userId, PasswordUtil.sha256Hex(newPassword));
            }

            // Set a success message and redirect back to the profile page.
            session.setAttribute(SessionKeys.FLASH_SUCCESS, "Profile updated.");
            resp.sendRedirect(req.getContextPath() + "/user/profile");

        } catch (SQLException e) {
            // Wrap database exceptions in an IOException for consistent error handling.
            throw new IOException(e);
        }
    }
}