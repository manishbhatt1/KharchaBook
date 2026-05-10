package com.kharchabook.servlet;

import com.kharchabook.dao.CategoryDAO;
import com.kharchabook.dao.TransactionDAO;
import com.kharchabook.dao.UserDAO;
import com.kharchabook.model.Category;
import com.kharchabook.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LocalDate now = LocalDate.now();
        int y = now.getYear();
        int m = now.getMonthValue();
        int prevM = m == 1 ? 12 : m - 1;
        int prevY = m == 1 ? y - 1 : y;
        try {
            req.setAttribute("totalUsers", userDAO.countRegularUsers());
            req.setAttribute("totalTransactions", transactionDAO.countTotalInSystem());
            int topCatId = transactionDAO.topCategoryByTransactionCount();
            if (topCatId > 0) {
                Category c = categoryDAO.findById(topCatId);
                req.setAttribute("topCategoryName", c != null ? c.getName() : "—");
            } else {
                req.setAttribute("topCategoryName", "—");
            }
            List<Object[]> topUsers = userDAO.topActiveUsers(5);
            req.setAttribute("topActiveUsers", topUsers);
            req.setAttribute("regThisMonth", userDAO.countRegistrationsInMonth(y, m));
            req.setAttribute("regPrevMonth", userDAO.countRegistrationsInMonth(prevY, prevM));
            List<User> recent = userDAO.findAllUsers(null);
            if (recent.size() > 8) {
                recent = recent.subList(0, 8);
            }
            req.setAttribute("recentUsers", recent);
            req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
