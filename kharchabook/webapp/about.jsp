<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="About Us" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="wrap about-page">
    <section class="about-hero">
        <div class="about-hero-text">
            <span class="about-eyebrow">About KharchaBook</span>
            <h1 class="page-title">A simple way to understand where your money goes.</h1>
            <p class="lead">KharchaBook is built for people who want to keep a clear record of income, expenses, budgets and savings without using complicated finance software.</p>
            <p>Many people remember spending only after the money is gone. This system gives users a place to write down daily transactions, set monthly limits and follow savings goals step by step.</p>
            <div class="hero-cta-row">
                <a href="${pageContext.request.contextPath}/register.jsp" class="btn btn-primary">Register</a>
                <a href="${pageContext.request.contextPath}/contact.jsp" class="btn btn-secondary">Contact</a>
            </div>
        </div>
        <div class="about-hero-visual image-panel">
            <img src="${pageContext.request.contextPath}/images/finance-flatlay.jpg"
                 alt="Calculator, notebook and money on a desk">
        </div>
    </section>

    <section class="about-section">
        <div class="about-section-text">
            <span class="about-eyebrow">Why It Helps</span>
            <h2>Small records can make better habits.</h2>
            <p>The system is focused on everyday money decisions: pocket money, salary, rent, food, transport, remittance, festivals and savings. Users can check their monthly totals instead of guessing.</p>
            <ul class="about-check-list">
                <li>Track income and expenses by category.</li>
                <li>Set a monthly spending limit for important categories.</li>
                <li>Follow saving goals such as a laptop, emergency fund or bike purchase.</li>
                <li>Read short financial tips and save useful ones for later.</li>
            </ul>
        </div>
        <div class="about-section-visual">
            <h2 class="section-heading">Common Categories</h2>
            <ul class="about-visual-list">
                <li>Salary and pocket money</li>
                <li>Remittance received</li>
                <li>Food and groceries</li>
                <li>Transport and rent</li>
                <li>Education and health</li>
                <li>Dashain and Tihar expenses</li>
            </ul>
        </div>
    </section>

    <section class="about-features-section">
        <span class="about-eyebrow">Features</span>
        <h2 class="section-heading">What users can do</h2>
        <div class="about-features-grid">
            <div class="about-feat-card">
                <div class="feat-icon-big">T</div>
                <h3>Transactions</h3>
                <p>Add, edit and filter income or expense records.</p>
            </div>
            <div class="about-feat-card">
                <div class="feat-icon-big">B</div>
                <h3>Budgets</h3>
                <p>Set monthly limits and see warning messages before overspending.</p>
            </div>
            <div class="about-feat-card">
                <div class="feat-icon-big">S</div>
                <h3>Savings Goals</h3>
                <p>Create a target and update the saved amount as progress is made.</p>
            </div>
            <div class="about-feat-card">
                <div class="feat-icon-big">F</div>
                <h3>Finance Tips</h3>
                <p>Browse short tips about saving, budgeting and avoiding debt.</p>
            </div>
        </div>
    </section>

    <section class="about-team-section">
        <span class="about-eyebrow">Team</span>
        <h2 class="section-heading">L2C1 Boys</h2>
        <div class="team-grid">
            <div class="team-card"><div class="team-avatar">A</div><h4>Ajay Bidari</h4></div>
            <div class="team-card"><div class="team-avatar">A</div><h4>Aayush Khadka</h4></div>
            <div class="team-card"><div class="team-avatar">A</div><h4>Anjal Phuyal</h4></div>
            <div class="team-card"><div class="team-avatar">M</div><h4>Manish Bhattarai</h4><span class="team-role">Team leader</span></div>
            <div class="team-card"><div class="team-avatar">S</div><h4>Saugat Bhujel</h4></div>
            <div class="team-card"><div class="team-avatar">S</div><h4>Samir Rai</h4></div>
        </div>
    </section>
</div>

<jsp:include page="/includes/footer.jsp"/>
