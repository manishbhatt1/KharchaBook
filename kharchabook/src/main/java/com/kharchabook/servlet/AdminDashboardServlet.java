package com.kharchabook.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kharchabook.dao.CategoryDAO;
import com.kharchabook.dao.TransactionDAO;
import com.kharchabook.dao.UserDAO;
import com.kharchabook.model.Category;
import com.kharchabook.model.User;

/**
 * Servlet handling the admin dashboard page.
 *
 * Aggregates system-wide statistics including user counts, transaction volumes,
 * top categories, and recent registrations for administrative overview.
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get current date for monthly calculations.
        LocalDate now = LocalDate.now();
        int y = now.getYear();
        int m = now.getMonthValue();
        int prevM = m == 1 ? 12 : m - 1;
        int prevY = m == 1 ? y - 1 : y;

        try {
            // Fetch total regular users count.
            req.setAttribute("totalUsers", userDAO.countRegularUsers());

            // Fetch total transactions in the system.
            req.setAttribute("totalTransactions", transactionDAO.countTotalInSystem());

            // Determine the top category by transaction count.
            int topCatId = transactionDAO.topCategoryByTransactionCount();
            if (topCatId > 0) {
                Category c = categoryDAO.findById(topCatId);
                req.setAttribute("topCategoryName", c != null ? c.getName() : "—");
            } else {
                req.setAttribute("topCategoryName", "—");
            }

            // Get top 5 most active users.
            List<Object[]> topUsers = userDAO.topActiveUsers(5);
            req.setAttribute("topActiveUsers", topUsers);

            // Count registrations this month and previous month.
            req.setAttribute("regThisMonth", userDAO.countRegistrationsInMonth(y, m));
            req.setAttribute("regPrevMonth", userDAO.countRegistrationsInMonth(prevY, prevM));

            // Fetch recent users (limit to 8 for display).
            List<User> recent = userDAO.findAllUsers(null);
            if (recent.size() > 8) {
                recent = recent.subList(0, 8);
            }
            req.setAttribute("recentUsers", recent);

            // Forward to the admin dashboard JSP.
            req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
