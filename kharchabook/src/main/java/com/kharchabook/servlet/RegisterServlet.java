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
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Logger logger = Logger.getLogger(RegisterServlet.class.getName());
        String fullName = req.getParameter("fullName");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        if (ValidationUtil.isBlank(fullName) || ValidationUtil.isBlank(phone) || ValidationUtil.isBlank(email) || ValidationUtil.isBlank(password)) {
            req.getSession(true).setAttribute(SessionKeys.FLASH_ERROR, "This field is required. Please fill in all required fields before submitting.");
            resp.sendRedirect(req.getContextPath() + "/register.jsp");
            return;
        }
        if (!ValidationUtil.isValidFullName(fullName)) {
            req.getSession(true).setAttribute(SessionKeys.FLASH_ERROR, "Full name must contain letters only. Please enter a valid name.");
            resp.sendRedirect(req.getContextPath() + "/register.jsp");
            return;
        }
        try {
            if (userDAO.phoneExists(phone, null)) {
                req.getSession(true).setAttribute(SessionKeys.FLASH_ERROR, "A user with this phone number already exists. Please use a different number.");
                resp.sendRedirect(req.getContextPath() + "/register.jsp");
                return;
            }
            if (userDAO.emailExists(email, null)) {
                req.getSession(true).setAttribute(SessionKeys.FLASH_ERROR, "This email address is already registered. Please log in or use a different email.");
                resp.sendRedirect(req.getContextPath() + "/register.jsp");
                return;
            }
            User u = new User();
            u.setFullName(fullName.trim());
            u.setPhone(phone.trim());
            u.setEmail(email.trim().toLowerCase());
            u.setPassword(PasswordUtil.sha256Hex(password));
            u.setRole("USER");
            u.setStatus("PENDING");
            userDAO.insert(u);
            // Use req.getSession() instead of req.getSession(true) if a session is expected to exist
            // or if you don't want to create a new one unnecessarily.
            // For registration, creating a new session is fine.
            req.getSession().setAttribute(SessionKeys.FLASH_SUCCESS, "Registration submitted. An admin must approve your account before you can log in.");
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error during user registration", e);
            req.getSession().setAttribute(SessionKeys.FLASH_ERROR, "An unexpected error occurred during registration. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/register.jsp"); // Redirect back to registration with an error
        }
    }
}
