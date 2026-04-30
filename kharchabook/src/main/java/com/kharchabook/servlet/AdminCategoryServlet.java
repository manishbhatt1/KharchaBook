package com.kharchabook.servlet;

import com.kharchabook.dao.CategoryDAO;
import com.kharchabook.model.Category;
import com.kharchabook.util.SessionKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/categories")
public class AdminCategoryServlet extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            if ("edit".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                Category c = categoryDAO.findById(id);
                req.setAttribute("editCategory", c);
            }
            List<Category> list = categoryDAO.findAll();
            req.setAttribute("categories", list);
            req.getRequestDispatcher("/admin/manageCategories.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Logger logger = Logger.getLogger(AdminCategoryServlet.class.getName());
        HttpSession session = req.getSession();
        int adminId = (Integer) session.getAttribute(SessionKeys.USER_ID);
        String action = req.getParameter("action");
        try {
            if ("create".equals(action)) {
                String name = req.getParameter("name");
                String type = req.getParameter("type");
                if (name == null || name.trim().isEmpty() || type == null || type.trim().isEmpty()) { // Added type.trim().isEmpty()
                    session.setAttribute(SessionKeys.FLASH_ERROR, "This field is required. Please fill in all required fields before submitting.");
                } else {
                    Category c = new Category();
                    c.setName(name.trim());
                    c.setType(type.trim()); // Trim type as well
                    c.setIcon(req.getParameter("icon"));
                    c.setCreatedBy(adminId);
                    categoryDAO.insert(c);
                    session.setAttribute(SessionKeys.FLASH_SUCCESS, "Category added.");
                }
            } else if ("update".equals(action)) {
                try {
                    int id = Integer.parseInt(req.getParameter("id"));
                    String name = req.getParameter("name");
                    String type = req.getParameter("type");
                    if (name == null || name.trim().isEmpty() || type == null || type.trim().isEmpty()) {
                        session.setAttribute(SessionKeys.FLASH_ERROR, "This field is required. Please fill in all required fields before submitting.");
                    } else {
                        categoryDAO.update(id, name.trim(), type.trim());
                        session.setAttribute(SessionKeys.FLASH_SUCCESS, "Category updated.");
                    }
                } catch (NumberFormatException e) {
                    logger.log(Level.WARNING, "Invalid category ID for update: " + req.getParameter("id"), e);
                    session.setAttribute(SessionKeys.FLASH_ERROR, "Invalid category ID provided for update.");
                }
            } else if ("delete".equals(action)) {
                try {
                    int id = Integer.parseInt(req.getParameter("id"));
                    categoryDAO.deleteIfUnused(id);
                    session.setAttribute(SessionKeys.FLASH_SUCCESS, "Category deleted.");
                } catch (SQLException ex) {
                    session.setAttribute(SessionKeys.FLASH_ERROR, "Cannot delete: category is in use.");
                }
            }
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error in AdminCategoryServlet", e);
            session.setAttribute(SessionKeys.FLASH_ERROR, "A database error occurred. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        }
    }
}
