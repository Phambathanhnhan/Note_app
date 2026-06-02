package controller;
import dao.UserDAO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
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
            if (ok) {
                resp.sendRedirect("index.jsp?msg=Register success! Please login.");
            } else {
                resp.sendRedirect("register.jsp?err=Username already exists!");
            }
        } else {
            int userId = dao.login(u, p);
            if (userId != -1) {
                HttpSession session = req.getSession();
                session.setAttribute("userId", userId);
                session.setAttribute("username", u);
                resp.sendRedirect("main.jsp");
            } else {
                resp.sendRedirect("index.jsp?err=Invalid username or password!");
            }
        }
    }
}