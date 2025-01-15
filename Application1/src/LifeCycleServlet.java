import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = "/lcs")
public class LifeCycleServlet extends HttpServlet {
    //1
    public LifeCycleServlet() {
        System.out.println("Inside LifeCycleServlet ONNA OBJECT EKAK HADUNA");
    }
    //2 methods run wenn patan gnnwa
    //3
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Life Cycle Servlet doGet ONNA MATA GET REQUEST EKA LABUNA");
    }
    //1 servlet eke behaviours tika daa gannawa
//request response handling
    //2
    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("Inside init ONNA MAMA SERVLET EKAK UNA");
    }

    @Override
    public void destroy() {
        System.out.println("Inside destroy");
    }
}
