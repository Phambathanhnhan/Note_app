package controller;
import dao.UserDAO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        String u = req.getParameter("username");
        String p = req.getParameter("password");
        UserDAO dao = new UserDAO();

        if ("register".equals(action)) {
            boolean ok = dao.register(u, p);
            resp.getWriter().write(ok ? "success" : "fail");
        } else {
            int userId = dao.login(u, p);
            resp.getWriter().write(String.valueOf(userId));
        }
    }
}