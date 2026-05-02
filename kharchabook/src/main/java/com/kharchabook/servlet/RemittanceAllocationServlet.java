package com.kharchabook.servlet;

import com.kharchabook.dao.RemittanceAllocationDAO;
import com.kharchabook.model.RemittanceAllocation;
import com.kharchabook.util.SessionKeys;
import com.kharchabook.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/user/remittance")
public class RemittanceAllocationServlet extends HttpServlet {

    private final RemittanceAllocationDAO remittanceDAO = new RemittanceAllocationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);
        
        try {
            String action = req.getParameter("action");
            if ("edit".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                RemittanceAllocation editAllocation = remittanceDAO.findById(id, userId);
                req.setAttribute("editAllocation", editAllocation);
            }
            
            LocalDate today = LocalDate.now();
            List<RemittanceAllocation> allocations = remittanceDAO.findByUser(userId);
            req.setAttribute("remittanceAllocations", allocations);
            
            BigDecimal monthTotal = remittanceDAO.sumTotalByUserAndMonth(userId, today.getYear(), today.getMonthValue());
            req.setAttribute("monthRemittanceTotal", monthTotal);
            
            req.getRequestDispatcher("/user/remittanceAllocation.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/user/remittance");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);
        String action = req.getParameter("action");
        
        try {
            if ("create".equals(action)) {
                String err = saveAllocation(req, userId, false);
                session.setAttribute(err == null ? SessionKeys.FLASH_SUCCESS : SessionKeys.FLASH_ERROR,
                        err == null ? "Remittance allocation saved successfully." : err);
                resp.sendRedirect(req.getContextPath() + "/user/remittance");
                return;
            }
            
            if ("update".equals(action)) {
                String err = saveAllocation(req, userId, true);
                session.setAttribute(err == null ? SessionKeys.FLASH_SUCCESS : SessionKeys.FLASH_ERROR,
                        err == null ? "Remittance allocation updated." : err);
                String id = req.getParameter("id");
                resp.sendRedirect(req.getContextPath() + "/user/remittance" + (id != null ? "?action=edit&id=" + id : ""));
                return;
            }
            
            if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                remittanceDAO.delete(id, userId);
                session.setAttribute(SessionKeys.FLASH_SUCCESS, "Remittance allocation removed.");
                resp.sendRedirect(req.getContextPath() + "/user/remittance");
                return;
            }
            
            if ("allocate".equals(action)) {
                String err = saveAllocation(req, userId, false);
                if (err == null) {
                    session.setAttribute(SessionKeys.FLASH_SUCCESS, "Your remittance has been allocated successfully! The amounts are now protected in their respective categories.");
                } else {
                    session.setAttribute(SessionKeys.FLASH_ERROR, err);
                }
                resp.sendRedirect(req.getContextPath() + "/user/remittance");
                return;
            }
            
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    private String saveAllocation(HttpServletRequest req, int userId, boolean isUpdate) throws SQLException {
        String idRaw = req.getParameter("id");
        String totalAmountRaw = req.getParameter("totalAmount");
        String rentAmountRaw = req.getParameter("rentAmount");
        String foodAmountRaw = req.getParameter("foodAmount");
        String savingsAmountRaw = req.getParameter("savingsAmount");
        String otherAmountRaw = req.getParameter("otherAmount");
        String description = req.getParameter("description");
        String allocationDateRaw = req.getParameter("allocationDate");
        String status = req.getParameter("status");

        if (ValidationUtil.isBlank(totalAmountRaw)) {
            return "Total amount is required.";
        }

        BigDecimal totalAmount = ValidationUtil.parseAmount(totalAmountRaw, new StringBuilder());
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return "Please enter a valid positive total amount.";
        }

        BigDecimal rentAmount = ValidationUtil.parseAmount(rentAmountRaw, new StringBuilder());
        BigDecimal foodAmount = ValidationUtil.parseAmount(foodAmountRaw, new StringBuilder());
        BigDecimal savingsAmount = ValidationUtil.parseAmount(savingsAmountRaw, new StringBuilder());
        BigDecimal otherAmount = ValidationUtil.parseAmount(otherAmountRaw, new StringBuilder());

        BigDecimal allocatedTotal = BigDecimal.ZERO;
        if (rentAmount != null) allocatedTotal = allocatedTotal.add(rentAmount);
        if (foodAmount != null) allocatedTotal = allocatedTotal.add(foodAmount);
        if (savingsAmount != null) allocatedTotal = allocatedTotal.add(savingsAmount);
        if (otherAmount != null) allocatedTotal = allocatedTotal.add(otherAmount);

        if (allocatedTotal.compareTo(totalAmount) > 0) {
            return "Allocated amounts cannot exceed the total remittance amount.";
        }

        LocalDate allocationDate;
        try {
            allocationDate = LocalDate.parse(allocationDateRaw.trim());
        } catch (Exception e) {
            allocationDate = LocalDate.now();
        }

        RemittanceAllocation allocation = new RemittanceAllocation();
        if (isUpdate) {
            allocation.setId(Integer.parseInt(idRaw));
        }
        
        allocation.setUserId(userId);
        allocation.setTotalAmount(totalAmount);
        allocation.setRentAmount(rentAmount);
        allocation.setFoodAmount(foodAmount);
        allocation.setSavingsAmount(savingsAmount);
        allocation.setOtherAmount(otherAmount);
        allocation.setDescription(ValidationUtil.isBlank(description) ? null : description.trim());
        allocation.setAllocationDate(allocationDate);
        allocation.setStatus(ValidationUtil.isBlank(status) ? "active" : status.toLowerCase());
        allocation.setCreatedAt(LocalDateTime.now());

        if (isUpdate) {
            remittanceDAO.update(allocation);
        } else {
            remittanceDAO.insert(allocation);
        }
        
        return null;
    }
}
