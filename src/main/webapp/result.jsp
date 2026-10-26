<%@ page import="example.EditForm" %>
<% EditForm f = (EditForm) request.getAttribute("saved"); %>
<html><body>
<h1>Saved</h1>
<p>title=[<%= f.getTitle() %>]</p>
<p>descriptionLength=[<%= f.getDescription() == null ? "null" : String.valueOf(f.getDescription().length()) %>]</p>
<p>attachment=[<%= f.getAttachment() == null ? "null" : f.getAttachment().getFileName() %>]</p>
<p>MaxLengthExceeded=<%= request.getAttribute("org.apache.struts.upload.MaxLengthExceeded") %>
   MaxStringLengthExceeded=<%= request.getAttribute("org.apache.struts.upload.MaxStringLengthExceeded") %></p>
</body></html>
