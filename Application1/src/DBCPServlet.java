import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/dbcp")
public class DBCPServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        BasicDataSource basicDataSource = new BasicDataSource();
//        basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        basicDataSource.setUrl("jdbc:mysql://localhost:3306/thogakade");
//        basicDataSource.setUsername("root");
//        basicDataSource.setPassword("1234");
//        basicDataSource.setMaxTotal(5);
//        basicDataSource.setInitialSize(5);

        //common interface to all servlets
//        ServletContext servletContext = req.getServletContext();
//        servletContext.setAttribute("dataSource", basicDataSource);
        ServletContext servletContext = req.getServletContext();
        BasicDataSource basicDataSource = (BasicDataSource) servletContext.getAttribute("dataSource");
        try {
            Connection connection = basicDataSource.getConnection();
            ResultSet resultSet = connection.prepareStatement("Select * from customer").executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String tel = resultSet.getString("tel");
                String email = resultSet.getString("email");
                System.out.println(id + " " + name + " " + address + " " + tel + " " + email);
            }
            connection.close();
            resp.setContentType("application/json");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("DBCPServlet doGet");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = req.getServletContext();
        BasicDataSource basicDataSource = (BasicDataSource) servletContext.getAttribute("dataSource");
        try {
            Connection connection = basicDataSource.getConnection();
            ResultSet resultSet = connection.prepareStatement("Select * from customer").executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String tel = resultSet.getString("tel");
                String email = resultSet.getString("email");
                System.out.println(id + " " + name + " " + address + " " + tel + " " + email);
            }
            connection.close();
            resp.setContentType("application/json");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("DBCPServlet doGet");
    }
}
