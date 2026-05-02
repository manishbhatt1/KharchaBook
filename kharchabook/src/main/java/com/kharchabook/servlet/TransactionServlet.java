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

@WebServlet("/user/tips")
public class TipBrowseServlet extends HttpServlet {

    private final FinancialTipDAO tipDAO = new FinancialTipDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);
        String q = req.getParameter("q");
        String cat = req.getParameter("category");
        try {
            List<FinancialTip> tips = tipDAO.search(q, cat, userId);
            req.setAttribute("tips", tips);
            req.setAttribute("searchQ", q);
            req.setAttribute("searchCat", cat);
            req.getRequestDispatcher("/user/tips.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
