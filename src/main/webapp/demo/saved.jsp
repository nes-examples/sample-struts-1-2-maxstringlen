<%@ page pageEncoding="UTF-8" import="example.DemoInfo, example.EditForm" %>
<% EditForm f = (EditForm) request.getAttribute("saved");
   boolean hasFile = f.getAttachment() != null && f.getAttachment().getFileName() != null && f.getAttachment().getFileName().length() > 0;
   boolean empty = f.getTitle() == null && f.getDescription() == null && f.getAttachment() == null; %>
<% if (empty) { %>
  <p class="note"><b>The save ran with an empty form.</b> A text field was over <code>maxStringLen</code>, so Struts
  dropped it and skipped copying any field into the form. With <code>validate="false"</code>, nothing stops the action,
  so it saved nulls.</p>
<% } else { %>
  <p class="ok">The action ran and received these values.</p>
<% } %>
<table class="running">
  <tr><th>Title</th><td><%= f.getTitle() == null ? "<i>null</i>" : DemoInfo.escape(f.getTitle()) %></td></tr>
  <tr><th>Description</th><td><%= f.getDescription() == null ? "<i>null</i>"
      : String.format("%,d characters, %,d bytes in UTF-8", f.getDescription().length(), f.getDescription().getBytes("UTF-8").length) %></td></tr>
  <tr><th>Attachment</th><td><%= f.getAttachment() == null ? "<i>null</i>"
      : hasFile ? DemoInfo.escape(f.getAttachment().getFileName()) + String.format(" (%,d bytes)", f.getAttachment().getFileSize())
      : "none chosen" %></td></tr>
</table>
<p style="margin-top:16px"><a href="form.do">Back to the form</a></p>
