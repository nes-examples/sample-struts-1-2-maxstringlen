<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html><body>
<h1>Edit</h1>
<div id="errors"><html:errors/></div>
<p>Form fields after this round trip: title=[<%= request.getAttribute("editForm") == null ? "" : ((example.EditForm) request.getAttribute("editForm")).getTitle() %>]</p>
<p>MaxLengthExceeded=<%= request.getAttribute("org.apache.struts.upload.MaxLengthExceeded") %>
   MaxStringLengthExceeded=<%= request.getAttribute("org.apache.struts.upload.MaxStringLengthExceeded") %></p>
</body></html>
