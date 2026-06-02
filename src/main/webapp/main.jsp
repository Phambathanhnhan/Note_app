<%@ page import="dao.NoteDAO, model.Note, java.util.List, java.net.URLEncoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%
    Integer uId = (Integer) session.getAttribute("userId");
    if (uId == null) { response.sendRedirect("index.jsp"); return; }

    String keyword = request.getParameter("keyword"); if (keyword == null) keyword = "";
    String viewParam = request.getParameter("view"); if (viewParam == null) viewParam = "notes";
    String labelParam = request.getParameter("lbl"); if (labelParam == null) labelParam = "";

    NoteDAO dao = new NoteDAO();
    List<Note> list = dao.getnotes(uId, keyword, viewParam, labelParam);
    List<String> userLabels = dao.getUniqueLabels(uId);

    String pageTitle = "My Notes"; String pageIcon = "lightbulb";
    if ("reminders".equals(viewParam)) { pageTitle = "Reminders"; pageIcon = "notifications_none"; }
    else if ("archive".equals(viewParam)) { pageTitle = "Archive"; pageIcon = "archive"; }
    else if ("trash".equals(viewParam)) { pageTitle = "Trash"; pageIcon = "delete_outline"; }
    else if ("label".equals(viewParam)) { pageTitle = labelParam; pageIcon = "label"; }
%>
<html>
<head>
    <title><%= pageTitle %></title>
    <link rel="stylesheet" href="style.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
</head>
<body class="app-body">
<div class="header">
    <div class="header-left">
        <i class="material-icons" style="cursor:pointer;" onclick="toggleSidebar()">menu</i>
        <i class="material-icons" style="color: var(--primary); font-size: 28px;"><%= pageIcon %></i>
        <span><%= pageTitle %></span>
    </div>
    <div class="search-bar">
        <i class="material-icons" style="color: var(--text-muted);">search</i>
        <form action="main.jsp" method="get" style="width: 100%; margin: 0;">
            <input type="hidden" name="view" value="<%= viewParam %>">
            <input type="hidden" name="lbl" value="<%= labelParam %>">
            <input type="text" name="keyword" value="<%= keyword %>" placeholder="Search...">
        </form>
    </div>
    <div class="header-right">
        <form action="file" method="post" enctype="multipart/form-data" style="margin: 0; display:flex; align-items:center; margin-right: 10px;">
            <input type="file" name="xmlFile" accept=".xml" id="xmlUpload" style="display:none;" onchange="this.form.submit()">
            <label for="xmlUpload" class="btn-text" style="display:flex; align-items:center; gap:6px; cursor:pointer; background: var(--primary-light); color: var(--primary); padding: 8px 16px; border-radius: 8px;" title="Import XML">
                <i class="material-icons" style="font-size: 18px;">cloud_upload</i> Import
            </label>
        </form>
        <a href="file?action=backupXml" class="btn-text" style="display:flex; align-items:center; gap:6px; text-decoration:none; background: #10B981; color: white; padding: 8px 16px; border-radius: 8px;" title="Export XML">
            <i class="material-icons" style="font-size: 18px;">cloud_download</i> Export
        </a>

        <form action="note" method="post" style="margin: 0; margin-left: 15px;">
            <input type="hidden" name="action" value="logout">
            <button type="submit" class="btn-icon material-icons" title="Logout">logout</button>
        </form>
    </div>
</div>

<div class="app-container">
    <div class="sidebar" id="appSidebar">
        <a href="main.jsp?view=notes" class="sidebar-item <%= "notes".equals(viewParam) ? "active" : "" %>">
            <i class="material-icons">lightbulb_outline</i><span>Notes</span>
        </a>
        <a href="main.jsp?view=reminders" class="sidebar-item <%= "reminders".equals(viewParam) ? "active" : "" %>">
            <i class="material-icons">notifications_none</i><span>Reminders</span>
        </a>

        <% for(String lbl : userLabels) { %>
        <a href="main.jsp?view=label&lbl=<%= URLEncoder.encode(lbl, "UTF-8") %>" class="sidebar-item <%= ("label".equals(viewParam) && lbl.equals(labelParam)) ? "active" : "" %>">
            <i class="material-icons">label_outline</i><span><%= lbl %></span>
        </a>
        <% } %>

        <a href="#" class="sidebar-item" onclick="openLabelsModal()">
            <i class="material-icons">edit</i><span>Edit labels</span>
        </a>

        <a href="main.jsp?view=archive" class="sidebar-item <%= "archive".equals(viewParam) ? "active" : "" %>">
            <i class="material-icons">archive</i><span>Archive</span>
        </a>
        <a href="main.jsp?view=trash" class="sidebar-item <%= "trash".equals(viewParam) ? "active" : "" %>">
            <i class="material-icons">delete_outline</i><span>Trash</span>
        </a>
    </div>

    <div class="main-content">
        <% if("trash".equals(viewParam)) { %>
        <div style="text-align: center; font-style: italic; color: var(--text-muted); font-size: 15px; margin-bottom: 24px;">
            Notes in Trash are deleted after 7 days.
        </div>
        <% } %>

        <% if("notes".equals(viewParam) || "reminders".equals(viewParam) || "label".equals(viewParam)) { %>
        <div class="note-input-wrapper" id="noteWrapper">
            <div class="note-input-collapsed" id="collapsedView">
                <span>Take a note...</span><i class="material-icons">check_box</i><i class="material-icons">image</i>
            </div>
            <div class="note-input-expanded" id="expandedView">
                <form action="note" method="post" id="addNoteForm" style="margin: 0;">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="view" value="<%= viewParam %>">
                    <input type="hidden" name="lbl" value="<%= labelParam %>">

                    <input type="text" name="title" id="noteTitle" placeholder="Title">
                    <textarea name="content" id="noteContent" rows="3" placeholder="Take a note..."></textarea>

                    <input type="text" name="label" class="note-label-input" placeholder="+ Add label (e.g. Work)" value="<%= "label".equals(viewParam) ? labelParam : "" %>">
                    <input type="datetime-local" name="reminder" id="reminderInput" class="reminder-input" title="Set a reminder">

                    <div class="note-input-actions">
                        <div style="display:flex; gap:16px; color:var(--text-muted);">
                            <i class="material-icons" style="font-size:20px; cursor:pointer;" onclick="document.getElementById('reminderInput').style.display='block'" title="Add reminder">add_alert</i>
                        </div>
                        <button type="submit" class="btn-text">Close</button>
                    </div>
                </form>
            </div>
        </div>
        <% } %>

        <% if(list.isEmpty()) { %>
        <div class="empty-state">
            <i class="material-icons"><%= "trash".equals(viewParam) ? "delete" : ("archive".equals(viewParam) ? "archive" : ("label".equals(viewParam) ? "label" : "lightbulb_outline")) %></i>
            <p>No notes here</p>
        </div>
        <% } else { %>
        <div class="notes-grid">
            <% for(Note n : list) {
                String safeTitle = URLEncoder.encode(n.title, "UTF-8");
                String safeContent = URLEncoder.encode(n.content, "UTF-8");
                String safeLabel = (n.label != null) ? n.label : "";
                String safeReminder = (n.reminder != null) ? n.reminder : "";
            %>
            <div class="note-card">
                <div class="note-title"><%= n.title %></div>
                <div class="note-content"><%= n.content %></div>

                <div style="display:flex; gap: 8px; flex-wrap: wrap;">
                    <% if(n.reminder != null && !n.reminder.isEmpty()) { %><div class="reminder-badge"><i class="material-icons">alarm</i> <%= n.reminder.replace("T", " ") %></div><% } %>
                    <% if(n.label != null && !n.label.trim().isEmpty()) { %><div class="note-label-badge"><%= n.label %></div><% } %>
                </div>

                <div style="display:flex; gap:5px; margin-top: auto; border-top: 1px solid var(--border-color); padding-top: 10px;">
                    <button onclick="openEditModal('<%= n.id %>', '<%= n.title.replace("'", "\\'") %>', '<%= n.content.replace("'", "\\'").replace("\n", "\\n") %>', '<%= safeLabel.replace("'", "\\'") %>', '<%= safeReminder %>')" class="btn-icon material-icons" style="font-size:18px;" title="Edit Note">edit</button>
                    <a href="file?action=exportTxt&title=<%= safeTitle %>&content=<%= safeContent %>" class="btn-icon material-icons" style="text-decoration:none; font-size:18px;" title="Export TXT">get_app</a>

                    <form action="note" method="post" style="margin:0; display:flex; gap:5px; margin-left: auto;">
                        <input type="hidden" name="id" value="<%= n.id %>">
                        <input type="hidden" name="view" value="<%= viewParam %>">
                        <input type="hidden" name="lbl" value="<%= labelParam %>">
                        <% if("trash".equals(viewParam)) { %>
                        <button type="submit" name="action" value="restore" class="btn-icon material-icons" title="Restore">restore</button>
                        <button type="submit" name="action" value="delete" class="btn-icon material-icons" title="Delete permanently">delete_forever</button>
                        <% } else if("archive".equals(viewParam)) { %>
                        <button type="submit" name="action" value="unarchive" class="btn-icon material-icons" title="Unarchive">unarchive</button>
                        <button type="submit" name="action" value="trash" class="btn-icon material-icons" title="Trash">delete</button>
                        <% } else { %>
                        <button type="submit" name="action" value="archive" class="btn-icon material-icons" title="Archive">archive</button>
                        <button type="submit" name="action" value="trash" class="btn-icon material-icons" title="Trash">delete</button>
                        <% } %>
                    </form>
                </div>
            </div>
            <% } %>
        </div>
        <% } %>
    </div>
</div>

<% if ("true".equals(request.getParameter("conflict"))) { %>
<div class="modal-overlay show" id="conflictModal">
    <div class="modal-content" style="width: 400px; padding: 20px;">
        <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 16px; border-bottom: 1px solid var(--border-color); padding-bottom: 12px;">
            <i class="material-icons" style="color: #F59E0B; font-size: 24px;">warning</i>
            <div style="font-weight: 600; font-size: 16px; color: var(--text-main);">Resolve Conflicts</div>
        </div>

        <form action="file" method="post" style="margin: 0;">
            <input type="hidden" name="action" value="resolveConflict">
            <div style="max-height: 260px; overflow-y: auto; margin-bottom: 16px; padding-right: 5px;">
                <%
                    List<Note> conflicts = (List<Note>) session.getAttribute("conflictNotes");
                    if (conflicts != null) {
                        for(int i = 0; i < conflicts.size(); i++) {
                            Note cNote = conflicts.get(i);
                %>
                <div style="margin-bottom: 12px; padding: 12px; background: #F9FAFB; border-radius: 8px; border: 1px solid var(--border-color);">
                    <div style="font-weight: 600; font-size: 14px; margin-bottom: 8px; color: var(--text-main); white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" title="<%= cNote.title %>">
                        <%= cNote.title %>
                    </div>
                    <select name="choice_<%= i %>" style="width: 100%; padding: 8px; border-radius: 6px; border: 1px solid var(--border-color); font-family: 'Inter'; font-size: 13px; outline: none; cursor: pointer;">
                        <option value="replace" selected>Replace existing</option>
                        <option value="keep">Keep both (Create copy)</option>
                        <option value="skip">Skip (Do not import)</option>
                    </select>
                </div>
                <%      }
                }
                %>
            </div>

            <div style="text-align: right;">
                <button type="submit" class="btn-text" style="background: var(--primary); color: white; padding: 8px 24px;">Confirm</button>
            </div>
        </form>
    </div>
</div>
<% } %>

<div class="modal-overlay" id="editModal">
    <div class="modal-content note-input-expanded" style="display:flex; margin:0; box-shadow:none;">
        <form action="note" method="post" style="margin: 0;">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="view" value="<%= viewParam %>">
            <input type="hidden" name="lbl" value="<%= labelParam %>">
            <input type="hidden" name="id" id="modal-id">
            <input type="text" name="title" id="modal-title" placeholder="Title" required>
            <textarea name="content" id="modal-content" rows="4" placeholder="Take a note..." required></textarea>
            <input type="text" name="label" id="modal-label" class="note-label-input" placeholder="+ Add label">
            <input type="datetime-local" name="reminder" id="modal-reminder" class="reminder-input" style="display:block;">
            <div class="note-input-actions">
                <div style="color:var(--text-muted); font-size: 13px; font-weight: 500;">Editing Note</div>
                <div>
                    <button type="button" onclick="closeEditModal()" class="btn-text" style="color: var(--text-muted); margin-right: 10px;">Cancel</button>
                    <button type="submit" class="btn-text">Update</button>
                </div>
            </div>
        </form>
    </div>
</div>

<div class="modal-overlay" id="labelsModal">
    <div class="modal-content" style="width: 320px; padding: 0; border-radius: 8px;">
        <div style="padding: 16px; font-weight: 600; font-size: 16px; color: var(--text-main);">Edit labels</div>

        <form action="note" method="post" style="margin:0; display:flex; align-items:center; padding: 10px 16px; border-top: 1px solid var(--border-color); border-bottom: 1px solid var(--border-color);">
            <input type="hidden" name="action" value="create_label">
            <input type="hidden" name="view" value="<%= viewParam %>">
            <input type="hidden" name="lbl" value="<%= labelParam %>">
            <i class="material-icons" style="color:var(--text-muted); font-size:20px; margin-right:15px; cursor:pointer;" onclick="this.closest('form').submit()">add</i>
            <input type="text" name="newLabel" placeholder="Create new label" required style="border:none; outline:none; flex:1; font-family:'Inter'; font-size: 14px;">
            <button type="submit" class="btn-icon material-icons" style="font-size:20px; padding:4px;" title="Create">check</button>
        </form>

        <div style="max-height: 250px; overflow-y: auto;">
            <% for(String lbl : userLabels) { %>
            <div style="display:flex; align-items:center; padding: 5px 16px; transition: background 0.2s;" onmouseover="this.style.background='#F3F4F6'" onmouseout="this.style.background='transparent'">
                <form action="note" method="post" style="margin:0;">
                    <input type="hidden" name="action" value="delete_label">
                    <input type="hidden" name="view" value="<%= viewParam %>">
                    <input type="hidden" name="lbl" value="<%= labelParam %>">
                    <input type="hidden" name="oldLabel" value="<%= lbl %>">
                    <button type="submit" class="btn-icon material-icons" style="font-size:20px; padding:4px; margin-right:5px;" title="Delete label">delete</button>
                </form>

                <form action="note" method="post" style="margin:0; display:flex; flex:1; align-items:center;">
                    <input type="hidden" name="action" value="rename_label">
                    <input type="hidden" name="view" value="<%= viewParam %>">
                    <input type="hidden" name="lbl" value="<%= labelParam %>">
                    <input type="hidden" name="oldLabel" value="<%= lbl %>">
                    <input type="text" name="newLabel" value="<%= lbl %>" required style="border:none; outline:none; flex:1; font-family:'Inter'; font-size:14px; background:transparent; border-bottom: 1px solid var(--border-color); margin-right: 8px; padding: 4px 0;">
                    <button type="submit" class="btn-icon material-icons" style="font-size:20px; padding:4px; color: var(--primary);" title="Save">check</button>
                </form>
            </div>
            <% } %>
        </div>

        <div style="padding: 10px 16px; text-align: right; border-top: 1px solid var(--border-color);">
            <button type="button" onclick="closeLabelsModal()" style="background:none; border:none; color:var(--text-main); font-weight:600; cursor:pointer; font-family:'Inter'; font-size:14px; padding: 6px 12px; border-radius: 4px;">Done</button>
        </div>
    </div>
</div>

<script>
    function toggleSidebar() { document.getElementById('appSidebar').classList.toggle('collapsed'); }

    const collapsedView = document.getElementById('collapsedView');
    const expandedView = document.getElementById('expandedView');
    const noteWrapper = document.getElementById('noteWrapper');

    if(collapsedView) {
        collapsedView.addEventListener('click', function() {
            collapsedView.style.display = 'none'; expandedView.style.display = 'flex'; document.getElementById('noteContent').focus();
        });
        document.addEventListener('click', function(event) {
            if (!noteWrapper.contains(event.target) && expandedView.style.display === 'flex') {
                if (document.getElementById('noteTitle').value.trim() !== '' || document.getElementById('noteContent').value.trim() !== '') {
                    document.getElementById('addNoteForm').submit();
                } else {
                    collapsedView.style.display = 'flex'; expandedView.style.display = 'none';
                }
            }
        });
    }

    function openEditModal(id, title, content, label, reminder) {
        document.getElementById('modal-id').value = id; document.getElementById('modal-title').value = title;
        document.getElementById('modal-content').value = content; document.getElementById('modal-label').value = label;
        document.getElementById('modal-reminder').value = reminder; document.getElementById('editModal').classList.add('show');
    }
    function closeEditModal() { document.getElementById('editModal').classList.remove('show'); }

    function openLabelsModal() { document.getElementById('labelsModal').classList.add('show'); }
    function closeLabelsModal() { document.getElementById('labelsModal').classList.remove('show'); }

    window.onclick = function(event) {
        if (event.target == document.getElementById('editModal')) closeEditModal();
        if (event.target == document.getElementById('labelsModal')) closeLabelsModal();
    }
</script>
</body>
</html>