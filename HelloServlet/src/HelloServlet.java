import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/hello")              //Extract mapping
//@WebServlet(urlPatterns = "/")                 //Default mapping
//@WebServlet(urlPatterns = "/items/*")            //Wildcard mapping
//@WebServlet(urlPatterns = "*.ijse")                 //Extension mapping
//@WebServlet(urlPatterns = {"/ab", "/cd"})         //multiple url patterns thiyenn puluwan
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
  //      System.out.println("Hello Servlet");
//        PrintWriter out = resp.getWriter();
//        out.println("DO GET: Hello World from HelloServlet");
       // out.println("<h1 styles= \"color:blue\">Hello World</h1>");
        String servletPath = req.getServletPath();
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String method = req.getMethod();
        String pathInfo = req.getPathInfo();
        String remoteUser = req.getRemoteUser();

        System.out.println("servlet path    "+servletPath);
        System.out.println("requestURI      "+requestURI);
        System.out.println("contextPath     "+contextPath);
        System.out.println("request method   "+method);
        System.out.println("pathInfo        "+pathInfo);
        System.out.println("remote User      "+remoteUser);

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.println("DO POST :Hello World from HelloServlet");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.println("DO PUT :Hello World from HelloServlet");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.println("DO DELETE :Hello World from HelloServlet");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.println("DO OPTIONS :Hello World from HelloServlet");
    }
}