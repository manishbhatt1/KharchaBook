package com.kharchabook.servlet;

import com.kharchabook.dao.FinancialTipDAO;
import com.kharchabook.model.FinancialTip;
import com.kharchabook.util.SessionKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Handles financial tip browsing.
 * Loads and filters financial tips for the logged-in user.
 */
@WebServlet("/user/tips")
public class TipBrowseServlet extends HttpServlet {

    // DAO object for financial tip operations
    private final FinancialTipDAO tipDAO = new FinancialTipDAO();

    /**
     * Loads financial tips page with optional search filters.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get current session and logged-in user ID
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);

        // Read search filters
        String q = req.getParameter("q");
        String cat = req.getParameter("category");

        try {
            // Search tips using keyword and category filter
            List<FinancialTip> tips = tipDAO.search(q, cat, userId);

            // Send result and filter values to JSP
            req.setAttribute("tips", tips);
            req.setAttribute("searchQ", q);
            req.setAttribute("searchCat", cat);

            // Forward to tips page
            req.getRequestDispatcher("/user/tips.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}