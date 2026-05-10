</main>

<!-- =========================
     SITE FOOTER
========================= -->
<footer class="site-footer">

    <!-- Footer navigation links -->
    <nav>
        <a href="${pageContext.request.contextPath}/about.jsp">About</a>
        <a href="${pageContext.request.contextPath}/contact.jsp">Contact</a>
    </nav>

    <!-- Dynamic footer text -->
    <!-- serverName dynamically shows current host (e.g., localhost) -->
    <p>
        &copy; ${pageContext.request.serverName} - KharchaBook |
        CS5054NT | Team L2C1 Boys
    </p>

</footer>

</body>
</html>