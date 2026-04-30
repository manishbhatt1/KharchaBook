package com.kharchabook.servlet;

import com.kharchabook.dao.UserDAO;
import com.kharchabook.model.User;
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

@WebServlet("/admin/users")
public class AdminUserServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("search");
        String idStr = req.getParameter("id");
        try {
            if (idStr != null && !idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);
                User u = userDAO.findById(id);
                if (u != null && "USER".equals(u.getRole())) {
                    req.setAttribute("detailUser", u);
                    req.setAttribute("txCount", userDAO.countTransactionsForUser(id));
                }
                req.getRequestDispatcher("/admin/userDetail.jsp").forward(req, resp);
                return;
            }
            List<User> users = userDAO.findAllUsers(search);
            req.setAttribute("users", users);
            req.setAttribute("search", search);
            req.getRequestDispatcher("/admin/manageUsers.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String action = req.getParameter("action");
        int id = Integer.parseInt(req.getParameter("id"));
        try {
            if ("approve".equals(action)) {
                userDAO.setStatus(id, "APPROVED");
                session.setAttribute(SessionKeys.FLASH_SUCCESS, "User approved.");
            } else if ("block".equals(action)) {
                userDAO.setStatus(id, "BLOCKED");
                session.setAttribute(SessionKeys.FLASH_SUCCESS, "User blocked.");
            } else if ("unblock".equals(action)) {
                userDAO.setStatus(id, "APPROVED");
                session.setAttribute(SessionKeys.FLASH_SUCCESS, "User unblocked.");
            }
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
