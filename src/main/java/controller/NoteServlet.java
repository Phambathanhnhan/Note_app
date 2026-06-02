package controller;
import dao.NoteDAO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/note")
public class NoteServlet extends HttpServlet {
    private NoteDAO dao = new NoteDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) { resp.sendRedirect("index.jsp"); return; }

        String action = req.getParameter("action");
        String label = req.getParameter("label"); if(label == null) label = "";
        String reminder = req.getParameter("reminder"); if(reminder == null) reminder = "";

        String view = req.getParameter("view"); if(view == null) view = "notes";
        String lblParam = req.getParameter("lbl"); if(lblParam == null) lblParam = "";

        int id = req.getParameter("id") != null && !req.getParameter("id").isEmpty() ? Integer.parseInt(req.getParameter("id")) : 0;

        if ("add".equals(action)) { dao.addnote(userId, req.getParameter("title"), req.getParameter("content"), label, reminder); }
        else if ("update".equals(action)) { dao.updatenote(id, req.getParameter("title"), req.getParameter("content"), label, reminder); }
        else if ("archive".equals(action)) { dao.changeStatus(id, true, false); }
        else if ("unarchive".equals(action)) { dao.changeStatus(id, false, false); }
        else if ("trash".equals(action)) { dao.changeStatus(id, false, true); }
        else if ("restore".equals(action)) { dao.changeStatus(id, false, false); }
        else if ("delete".equals(action)) { dao.deletePermanently(id); }

        else if ("create_label".equals(action)) {
            String newLabel = req.getParameter("newLabel");
            if(newLabel != null && !newLabel.trim().isEmpty()) {
                dao.createEmptyLabel(userId, newLabel.trim());
            }
        }
        else if ("rename_label".equals(action)) {
            String oldL = req.getParameter("oldLabel");
            String newL = req.getParameter("newLabel");
            dao.renameLabel(userId, oldL, newL);
            if (lblParam.equals(oldL)) { lblParam = newL; }
        }
        else if ("delete_label".equals(action)) {
            String oldL = req.getParameter("oldLabel");
            dao.deleteLabel(userId, oldL);
            if (lblParam.equals(oldL)) { lblParam = ""; view = "notes"; }
        }

        else if ("logout".equals(action)) { session.invalidate(); resp.sendRedirect("index.jsp"); return; }

        if ("label".equals(view) && !lblParam.isEmpty()) {
            resp.sendRedirect("main.jsp?view=label&lbl=" + URLEncoder.encode(lblParam, "UTF-8"));
        } else {
            resp.sendRedirect("main.jsp?view=" + view);
        }
    }
}