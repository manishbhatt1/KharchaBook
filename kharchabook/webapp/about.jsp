<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!-- JSTL Core Tag Library -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Set dynamic page title -->
<c:set var="pageTitle" value="About Us" scope="request"/>

<!-- Include common header -->
<jsp:include page="/includes/header.jsp"/>

<div class="wrap about-page">

    <!-- =========================
         HERO SECTION (INTRO)
    ========================= -->
    <section class="about-hero">

        <!-- Left side: text -->
        <div class="about-hero-text">
            <span class="about-eyebrow">About KharchaBook</span>

            <!-- Main heading -->
            <h1 class="page-title">A simple way to understand where your money goes.</h1>

            <!-- Description -->
            <p class="lead">
                KharchaBook helps users track income, expenses, budgets and savings easily.
            </p>

            <p>
                Users can record daily transactions, set limits and follow savings goals step by step.
            </p>

            <!-- Call-to-action buttons -->
            <div class="hero-cta-row">
                <a href="${pageContext.request.contextPath}/register.jsp" class="btn btn-primary">Register</a>
                <a href="${pageContext.request.contextPath}/contact.jsp" class="btn btn-secondary">Contact</a>
            </div>
        </div>

        <!-- Right side: image -->
        <div class="about-hero-visual image-panel">
            <img src="${pageContext.request.contextPath}/images/finance-flatlay.jpg"
                 alt="Finance setup with calculator and notebook">
        </div>
    </section>

    <!-- =========================
         WHY IT HELPS SECTION
    ========================= -->
    <section class="about-section">

        <!-- Explanation text -->
        <div class="about-section-text">
            <span class="about-eyebrow">Why It Helps</span>
            <h2>Small records can make better habits.</h2>

            <p>
                Helps users manage everyday spending and avoid guessing monthly totals.
            </p>

            <!-- Feature list -->
            <ul class="about-check-list">
                <li>Track income and expenses.</li>
                <li>Set monthly spending limits.</li>
                <li>Follow saving goals.</li>
                <li>Save useful financial tips.</li>
            </ul>
        </div>

        <!-- Category examples -->
        <div class="about-section-visual">
            <h2 class="section-heading">Common Categories</h2>
            <ul class="about-visual-list">
                <li>Salary and pocket money</li>
                <li>Remittance received</li>
                <li>Food and groceries</li>
                <li>Transport and rent</li>
                <li>Education and health</li>
                <li>Festival expenses</li>
            </ul>
        </div>
    </section>

    <!-- =========================
         FEATURES SECTION
    ========================= -->
    <section class="about-features-section">

        <span class="about-eyebrow">Features</span>
        <h2 class="section-heading">What users can do</h2>

        <div class="about-features-grid">

            <!-- Transactions -->
            <div class="about-feat-card">
                <div class="feat-icon-big">T</div>
                <h3>Transactions</h3>
                <p>Add, edit and filter income or expenses.</p>
            </div>

            <!-- Budgets -->
            <div class="about-feat-card">
                <div class="feat-icon-big">B</div>
                <h3>Budgets</h3>
                <p>Set limits and get warnings before overspending.</p>
            </div>

            <!-- Savings -->
            <div class="about-feat-card">
                <div class="feat-icon-big">S</div>
                <h3>Savings Goals</h3>
                <p>Track progress towards financial goals.</p>
            </div>

            <!-- Tips -->
            <div class="about-feat-card">
                <div class="feat-icon-big">F</div>
                <h3>Finance Tips</h3>
                <p>Read and save useful financial advice.</p>
            </div>
        </div>
    </section>

    <!-- =========================
         TEAM SECTION
    ========================= -->
    <section class="about-team-section">

        <span class="about-eyebrow">Team</span>
        <h2 class="section-heading">L2C1 Boys</h2>

        <!-- Team members -->
        <div class="team-grid">
            <div class="team-card"><div class="team-avatar">A</div><h4>Ajay Bidari</h4></div>
            <div class="team-card"><div class="team-avatar">A</div><h4>Aayush Khadka</h4></div>
            <div class="team-card"><div class="team-avatar">A</div><h4>Anjal Phuyal</h4></div>

            <!-- Team leader -->
            <div class="team-card">
                <div class="team-avatar">M</div>
                <h4>Manish Bhattarai</h4>
                <span class="team-role">Team leader</span>
            </div>

            <div class="team-card"><div class="team-avatar">S</div><h4>Saugat Bhujel</h4></div>
            <div class="team-card"><div class="team-avatar">S</div><h4>Samir Rai</h4></div>
        </div>
    </section>

</div>

<!-- Include footer -->
<jsp:include page="/includes/footer.jsp"/>