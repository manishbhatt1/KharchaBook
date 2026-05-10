package com.kharchabook.servlet;

import com.kharchabook.dao.CategoryDAO;
import com.kharchabook.dao.FinancialTipDAO;
import com.kharchabook.dao.TransactionDAO;
import com.kharchabook.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/admin/reports")
public class AdminReportServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final FinancialTipDAO financialTipDAO = new FinancialTipDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LocalDate now = LocalDate.now();
        int y = now.getYear();
        int m = now.getMonthValue();
        int prevM = m == 1 ? 12 : m - 1;
        int prevY = m == 1 ? y - 1 : y;
        try {
            req.setAttribute("regThisMonth", userDAO.countRegistrationsInMonth(y, m));
            req.setAttribute("regPrevMonth", userDAO.countRegistrationsInMonth(prevY, prevM));
            int topCatId = transactionDAO.topCategoryByTransactionCount();
            if (topCatId > 0) {
                req.setAttribute("mostUsedCategory", categoryDAO.findById(topCatId));
            }
            req.setAttribute("topActiveUsers", userDAO.topActiveUsers(5));
            req.setAttribute("totalTransactions", transactionDAO.countTotalInSystem());
            req.setAttribute("registrationTrend", userDAO.registrationCountsForYear(y));
            req.setAttribute("trendYear", y);
            req.setAttribute("mostWishlistedTips", financialTipDAO.mostWishlistedTips(10));
            req.getRequestDispatcher("/admin/reports.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
