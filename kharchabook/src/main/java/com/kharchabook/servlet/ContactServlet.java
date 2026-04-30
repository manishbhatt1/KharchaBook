package com.kharchabook.servlet;

import com.kharchabook.util.SessionKeys;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/contact")
public class ContactServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String message = req.getParameter("message");
        HttpSession session = req.getSession(true);
        if (name == null || name.trim().isEmpty() || message == null || message.trim().isEmpty()) {
            session.setAttribute(SessionKeys.FLASH_ERROR, "This field is required. Please fill in all required fields before submitting.");
        } else {
            session.setAttribute(SessionKeys.FLASH_SUCCESS, "Thank you — we have received your message.");
        }
        resp.sendRedirect(req.getContextPath() + "/contact.jsp");
    }
}
