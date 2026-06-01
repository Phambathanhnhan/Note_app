package controller;
import dao.NoteDAO;
import model.Note;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/note")
public class NoteServlet extends HttpServlet {
    private NoteDAO dao = new NoteDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        int userId = Integer.parseInt(req.getParameter("userid"));
        String keyword = req.getParameter("keyword");
        if(keyword == null) keyword = "";

        List<Note> list = dao.getnotes(userId, keyword);
        StringBuilder builder = new StringBuilder();
        for (Note n : list) {
            builder.append(n.id).append("|").append(n.title).append("|").append(n.content).append(";;");
        }
        resp.getWriter().write(builder.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        if ("add".equals(action)) {
            int userId = Integer.parseInt(req.getParameter("userid"));
            dao.addnote(userId, req.getParameter("title"), req.getParameter("content"));
        } else if ("update".equals(action)) {
            int noteId = Integer.parseInt(req.getParameter("noteid"));
            dao.updatenote(noteId, req.getParameter("title"), req.getParameter("content"));
        } else if ("delete".equals(action)) {
            int noteId = Integer.parseInt(req.getParameter("noteid"));
            dao.deletenote(noteId);
        }
        resp.getWriter().write("success");
    }
}