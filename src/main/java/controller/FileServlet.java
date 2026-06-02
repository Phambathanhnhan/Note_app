package controller;
import dao.NoteDAO;
import model.Note;
import utils.FileExporter;
import utils.XmlBackup;
import utils.XmlRestore;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/file")
@MultipartConfig
public class FileServlet extends HttpServlet {
    private NoteDAO dao = new NoteDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) { resp.sendRedirect("index.jsp"); return; }

        String action = req.getParameter("action");
        if ("exportTxt".equals(action)) {
            String title = req.getParameter("title");
            String content = req.getParameter("content");
            if (title != null && content != null) {
                resp.setContentType("text/plain; charset=UTF-8");
                resp.setHeader("Content-Disposition", "attachment; filename=\"note_" + URLEncoder.encode(title, "UTF-8") + ".txt\"");
                FileExporter.exporttxt(resp.getOutputStream(), title, content);
            }
        } else if ("backupXml".equals(action)) {
            List<Note> notes = dao.getnotes(userId, "", "all", "");
            resp.setContentType("application/xml; charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"notes_backup.xml\"");
            XmlBackup.exportxml(resp.getOutputStream(), notes);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) { resp.sendRedirect("index.jsp"); return; }

        String action = req.getParameter("action");

        if ("resolveConflict".equals(action)) {
            List<Note> conflicts = (List<Note>) session.getAttribute("conflictNotes");

            if (conflicts != null) {
                for (int i = 0; i < conflicts.size(); i++) {
                    Note n = conflicts.get(i);
                    String choice = req.getParameter("choice_" + i);

                    if ("keep".equals(choice)) {
                        dao.addnote(userId, n.title + " (Copy)", n.content, "", "");
                    } else if ("replace".equals(choice)) {
                        Note existing = dao.getNoteByExactTitle(userId, n.title);
                        if (existing != null) {
                            dao.updatenote(existing.id, n.title, n.content, "", "");
                        } else {
                            dao.addnote(userId, n.title, n.content, "", "");
                        }
                    }
                }
                session.removeAttribute("conflictNotes");
            }
            resp.sendRedirect("main.jsp");
            return;
        }

        try {
            Part filePart = req.getPart("xmlFile");
            if (filePart != null) {
                InputStream fileContent = filePart.getInputStream();
                List<Note> importedNotes = XmlRestore.importxml(fileContent);

                List<Note> conflictNotes = new ArrayList<>();
                for (Note n : importedNotes) {
                    Note existing = dao.getNoteByExactTitle(userId, n.title);
                    if (existing != null) {
                        conflictNotes.add(n);
                    } else {
                        dao.addnote(userId, n.title, n.content, "", "");
                    }
                }

                if (!conflictNotes.isEmpty()) {
                    session.setAttribute("conflictNotes", conflictNotes);
                    resp.sendRedirect("main.jsp?conflict=true");
                    return;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        resp.sendRedirect("main.jsp");
    }
}