<%@ page pageEncoding="UTF-8" import="example.DemoInfo" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<% DemoInfo info = new DemoInfo(request, application);
   boolean exceeded = Boolean.TRUE.equals(request.getAttribute("org.apache.struts.upload.MaxStringLengthExceeded")); %>
<html:errors/>
<% if (exceeded) { %>
  <p class="note">Struts set <code>MaxStringLengthExceeded</code> on this request: a text field was over
  <code>maxStringLen</code>, so none of the submitted fields were copied into the form.
  <% if (!info.isAfter()) { %>Any "required" message above is a side effect of that, not a real missing field.<% } %>
  The form below comes back empty for the same reason.</p>
<% } %>

<form method="post" enctype="multipart/form-data" accept-charset="UTF-8" action="saveValidated.do">
  <input type="hidden" name="method" value="save">
  <label for="title">Title</label>
  <input type="text" id="title" name="title" value="Example">

  <label for="description">Description</label>
  <textarea id="description" name="description"></textarea>
  <div id="counter" class="counter"></div>
  <div class="row">Fill with:
    <button type="button" data-ch="x" data-n="100">100 characters</button>
    <button type="button" data-ch="x" data-n="5000">5,000 characters</button>
    <button type="button" data-ch="x" data-n="40000">40,000 characters</button>
    <button type="button" data-ch="&#x4e2d;" data-n="1400">1,400 Chinese characters</button>
    <button type="button" data-ch="&#x4e2d;" data-n="12000">12,000 Chinese characters</button>
  </div>

  <label for="attachment">Attachment (optional)</label>
  <input type="file" id="attachment" name="attachment">

  <div class="row" style="margin-top:16px">
    <button class="primary" type="submit" formaction="saveValidated.do">Save with validate="true"</button>
    <button class="primary" type="submit" formaction="saveUnvalidated.do">Save with validate="false"</button>
  </div>
</form>

<h3>Things to try</h3>
<% if (info.isAfter()) { %>
<ol>
  <li>Fill 5,000 characters and save. It saves, because the limit is now 32K.</li>
  <li>Fill 40,000 characters and save with either button. You come back here with the "too long" message, and nothing is saved.</li>
  <li>Fill 12,000 Chinese characters. That's 36,000 bytes, over 32K, even though it's fewer characters.</li>
</ol>
<% } else { %>
<ol>
  <li>Fill 5,000 characters and save with <code>validate="true"</code>. You get "Title is required." even though the title was sent.</li>
  <li>Do the same with <code>validate="false"</code>. The save runs, and every field, including the title and attachment, arrives empty.</li>
  <li>Fill 1,400 Chinese characters. That's 4,200 bytes, over 4K, even though it's fewer characters.</li>
  <li>Switch to "After: recommended settings" at the top to try the fix.</li>
</ol>
<% } %>

<script>
  var limit = <%= info.maxStringLenBytes() %>;
  var d = document.getElementById('description'), c = document.getElementById('counter');
  function update() {
    var bytes = new TextEncoder().encode(d.value).length;
    c.textContent = d.value.length.toLocaleString() + ' characters, ' + bytes.toLocaleString() +
      ' bytes in UTF-8. This copy accepts up to ' + limit.toLocaleString() + ' bytes per text field.' +
      (bytes > limit ? ' Over the limit.' : '');
    c.className = 'counter' + (bytes > limit ? ' over' : '');
  }
  document.querySelectorAll('[data-n]').forEach(function (b) {
    b.addEventListener('click', function () { d.value = b.dataset.ch.repeat(+b.dataset.n); update(); });
  });
  d.addEventListener('input', update);
  update();
</script>
