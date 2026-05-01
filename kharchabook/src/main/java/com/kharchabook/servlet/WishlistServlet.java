package com.kharchabook.servlet;

import com.kharchabook.dao.WishlistDAO;
import com.kharchabook.model.FinancialTip;
import com.kharchabook.util.SessionKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet for handling Wishlist functionality
 * URL: /user/wishlist
 */
@WebServlet("/user/wishlist")
public class WishlistServlet extends HttpServlet {

    // DAO object for database operations
    private final WishlistDAO wishlistDAO = new WishlistDAO();

    /**
     * Handles GET request
     * Purpose: Display all saved wishlist tips for the logged-in user
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Get current session
        HttpSession session = req.getSession();

        // Retrieve logged-in user ID from session
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);

        try {
            // Fetch wishlist tips for this user
            List<FinancialTip> tips = wishlistDAO.findByUser(userId);

            // Send data to JSP
            req.setAttribute("tips", tips);

            // Forward to wishlist page
            req.getRequestDispatcher("/user/wishlist.jsp").forward(req, resp);

        } catch (SQLException e) {
            // Wrap SQL exception into ServletException
            throw new ServletException(e);
        }
    }

    /**
     * Handles POST request
     * Purpose: Add or remove tips from wishlist
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession();

        // Get logged-in user ID
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);

        // Determine action (add/remove)
        String action = req.getParameter("action");

        try {

            // ===== ADD TO WISHLIST =====
            if ("add".equals(action)) {
                int tipId = Integer.parseInt(req.getParameter("tipId"));

                // Save tip to wishlist
                wishlistDAO.add(userId, tipId);

                // Set success message
                session.setAttribute(SessionKeys.FLASH_SUCCESS,
                        "Tip saved to wishlist.");
            }

            // ===== REMOVE FROM WISHLIST =====
            else if ("remove".equals(action)) {
                int tipId = Integer.parseInt(req.getParameter("tipId"));

                // Remove tip from wishlist
                wishlistDAO.remove(userId, tipId);

                // Set success message
                session.setAttribute(SessionKeys.FLASH_SUCCESS,
                        "Removed from wishlist.");
            }

            // Determine where request came from
            String from = req.getParameter("from");

            // Redirect user accordingly
            if ("tips".equals(from)) {
                // Redirect back to tips page
                resp.sendRedirect(req.getContextPath() + "/user/tips");
            } else {
                // Default redirect to wishlist page
                resp.sendRedirect(req.getContextPath() + "/user/wishlist");
            }

        } catch (SQLException e) {
            // Convert SQL exception into IO exception
            throw new IOException(e);
        }
    }
}