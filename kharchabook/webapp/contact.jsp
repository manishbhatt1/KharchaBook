<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Contact Us" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="wrap contact-page">
    <div class="contact-header">
        <span class="about-eyebrow">Contact</span>
        <h1 class="page-title">How can we help?</h1>
        <p class="lead">Send a message if you have feedback, find a problem, or need help with your account.</p>
    </div>

    <div class="contact-main">
        <div class="contact-form-col">
            <article class="content contact-form-card">
                <h2>Send a message</h2>
                <jsp:include page="/includes/flash.jsp"/>
                <form method="post" action="${pageContext.request.contextPath}/contact" id="contactForm">
                    <div class="form-row-split">
                        <div class="form-row">
                            <label for="name">Full name <span class="req">*</span></label>
                            <input type="text" id="name" name="name" required placeholder="Ram Bahadur Thapa"/>
                        </div>
                        <div class="form-row">
                            <label for="email">Email address <span class="req">*</span></label>
                            <input type="email" id="email" name="email" required placeholder="ram@example.com"/>
                        </div>
                    </div>
                    <div class="form-row">
                        <label for="phone">Phone number</label>
                        <input type="tel" id="phone" name="phone" placeholder="98XXXXXXXX"/>
                    </div>
                    <div class="form-row">
                        <label for="subject">Subject <span class="req">*</span></label>
                        <select id="subject" name="subject" required>
                            <option value="" disabled selected>Select a topic</option>
                            <option value="account">Account or login issue</option>
                            <option value="bug">Report a bug</option>
                            <option value="feature">Feature request</option>
                            <option value="feedback">General feedback</option>
                            <option value="other">Other</option>
                        </select>
                    </div>
                    <div class="form-row">
                        <label for="message">Message <span class="req">*</span></label>
                        <textarea id="message" name="message" required rows="6" placeholder="Write your message here"></textarea>
                    </div>
                    <div class="form-row contact-form-footer">
                        <p class="form-note">Fields marked <span class="req">*</span> are required.</p>
                        <button type="submit" class="btn btn-primary" id="sendBtn">Send message</button>
                    </div>
                </form>
            </article>
        </div>

        <div class="contact-side-col">
            <div class="contact-side-card">
                <h3>Contact details</h3>
                <p><strong>Email:</strong> kharchabook.team@example.com</p>
                <p><strong>Phone:</strong> +977 980-000-0000</p>
                <p><strong>Location:</strong> Kathmandu, Nepal</p>
            </div>

            <div class="contact-side-card">
                <h3>Before sending</h3>
                <ul class="response-list">
                    <li>Mention the page where the issue happened.</li>
                    <li>Describe what you were trying to do.</li>
                    <li>Include any message shown on the screen.</li>
                </ul>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
