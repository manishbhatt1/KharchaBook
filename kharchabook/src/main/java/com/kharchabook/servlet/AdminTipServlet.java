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

@WebServlet("/admin/tips")
public class AdminTipServlet extends HttpServlet {

    private final FinancialTipDAO tipDAO = new FinancialTipDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            if ("add".equals(action)) {
                req.getRequestDispatcher("/admin/addTip.jsp").forward(req, resp);
                return;
            }
            if ("edit".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                FinancialTip t = tipDAO.findByIdForAdmin(id);
                req.setAttribute("tip", t);
                req.getRequestDispatcher("/admin/editTip.jsp").forward(req, resp);
                return;
            }
            List<FinancialTip> tips = tipDAO.findAllForAdmin();
            req.setAttribute("tips", tips);
            req.getRequestDispatcher("/admin/manageTips.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        int adminId = (Integer) session.getAttribute(SessionKeys.USER_ID);
        String action = req.getParameter("action");
        try {
            if ("create".equals(action)) {
                String title = req.getParameter("title");
                String content = req.getParameter("content");
                String category = req.getParameter("category");
                if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty() || category == null) {
                    session.setAttribute(SessionKeys.FLASH_ERROR, "This field is required. Please fill in all required fields before submitting.");
                } else {
                    FinancialTip tip = new FinancialTip();
                    tip.setTitle(title.trim());
                    tip.setContent(content.trim());
                    tip.setCategory(category.trim());
                    tip.setPostedBy(adminId);
                    tipDAO.insert(tip);
                    session.setAttribute(SessionKeys.FLASH_SUCCESS, "Tip published.");
                }
                resp.sendRedirect(req.getContextPath() + "/admin/tips");
                return;
            }
            if ("update".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                String title = req.getParameter("title");
                String content = req.getParameter("content");
                String category = req.getParameter("category");
                tipDAO.update(id, title.trim(), content.trim(), category.trim());
                session.setAttribute(SessionKeys.FLASH_SUCCESS, "Tip updated.");
                resp.sendRedirect(req.getContextPath() + "/admin/tips");
                return;
            }
            if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                tipDAO.delete(id);
                session.setAttribute(SessionKeys.FLASH_SUCCESS, "Tip deleted.");
                resp.sendRedirect(req.getContextPath() + "/admin/tips");
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
