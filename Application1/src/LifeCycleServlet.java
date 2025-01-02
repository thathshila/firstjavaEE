import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = "/lcs")
public class LifeCycleServlet extends HttpServlet {
    public LifeCycleServlet() {
        System.out.println("Inside LifeCycleServlet");
    }
    //2 methods run wenn patan gnnwa
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Life Cycle Servlet doGet");
    }
    //1 servlet eke behaviours tika daa gannawa
//request response handling
    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("Inside init");
    }

    @Override
    public void destroy() {
        System.out.println("Inside destroy");
    }
}
