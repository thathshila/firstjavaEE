import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
     //   System.out.println("Hello Servlet");
        PrintWriter out = resp.getWriter();
        out.println("Hello World from HelloServlet");
//        out.println("<h1 style=\"color:red\">Hello World from HelloServlet</h1>");
//        String servletPath = req.getServletPath();
//        out.println("Servlet path: " + servletPath);
//        String method = req.getMethod();
//        out.println("Method: " + method);
//        String pathInfo = req.getPathInfo();
//        out.println("PathInfo: " + pathInfo);
//        String contextPath = req.getContextPath();
//        out.println("ContextPath: " + contextPath);
    }
}