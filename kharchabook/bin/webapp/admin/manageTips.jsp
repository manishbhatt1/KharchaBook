<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Tips" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="page-hero">
    <div>
        <h1 class="page-title">Financial tips</h1>
        <p class="lead">Review published tips, update content and remove old entries.</p>
    </div>
    <div class="hero-actions">
        <a href="${pageContext.request.contextPath}/admin/tips?action=add" class="btn btn-primary">Publish new tip</a>
    </div>
</div>

<jsp:include page="/includes/flash.jsp"/>

<div class="layout-split">
    <div class="stack">
        <div class="table-wrap">
            <table class="data">
                <thead><tr><th>Title</th><th>Category</th><th>Wishlists</th><th>Posted</th><th></th></tr></thead>
                <tbody>
                <c:forEach var="t" items="${tips}">
                    <tr>
                        <td>${t.title}</td>
                        <td>${t.category}</td>
                        <td>${t.wishlistCount}</td>
                        <td>${t.postedDate}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/tips?action=edit&id=${t.id}" class="btn btn-secondary btn-sm">Edit</a>
                            <form method="post" style="display:inline" action="${pageContext.request.contextPath}/admin/tips" onsubmit="return confirm('Delete this tip?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${t.id}">
                                <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <div class="stack">
        <div class="soft-panel">
            <h2 class="panel-title">Writing tips</h2>
            <ul class="summary-list">
                <li>Use clear titles that explain the topic quickly.</li>
                <li>Keep the content short and practical.</li>
                <li>Place tips in the most relevant category.</li>
            </ul>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
