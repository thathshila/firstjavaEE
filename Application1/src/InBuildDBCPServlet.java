import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/inBuildDBCP")
public class InBuildDBCPServlet extends HttpServlet {

    @Resource(name="java:comp/env/jdbc/pool")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getParameter("id");
        String username = req.getHeader("username");
       try {
           Connection connection = dataSource.getConnection();
           ResultSet resultSet = connection.prepareStatement("SELECT * FROM customer").executeQuery();
           while (resultSet.next()) {
               String id = resultSet.getString("id");
               String address = resultSet.getString("address");
               String email = resultSet.getString("email");
               String name = resultSet.getString("name");
               String tel = resultSet.getString("tel");
               System.out.println(id+" "+address+" "+email+" "+name+" "+tel);
           }
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
    }
}
