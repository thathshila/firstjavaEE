import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.*;

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234");
//            ResultSet resultSet = connection.prepareStatement("Select * from customer").executeQuery();
//
//            //create json array
//            JsonArrayBuilder allCustomers = Json.createArrayBuilder();
//            while (resultSet.next()) {
//                String id = resultSet.getString("id");
//                String name = resultSet.getString("name");
//                String address = resultSet.getString("address");
//                String tel = resultSet.getString("tel");
//                String email = resultSet.getString("email");
//                // System.out.println(id+" "+name+" "+address+" "+tel+" "+email);
//                JsonObjectBuilder customer = Json.createObjectBuilder();
//                customer.add("id", id);
//                customer.add("name", name);
//                customer.add("address", address);
//                customer.add("tel", tel);
//                customer.add("email", email);
//                allCustomers.add(customer);
//            }
//            resp.setContentType("application/json");
//            resp.getWriter().write(allCustomers.build().toString());
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("application/json");
    try {
        Class.forName("com.mysql.jdbc.Driver");

        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String address = req.getParameter("address");
        String tel = req.getParameter("tel");
        String email = req.getParameter("email");

        if (id == null || name == null || address == null || tel == null || email == null ||
                id.isEmpty() || name.isEmpty() || address.isEmpty() || tel.isEmpty() || email.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"All fields are required.\"}");
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234")) {
            String sql = "INSERT INTO customer (id, name, address, tel, email) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setString(1, id);
                pst.setString(2, name);
                pst.setString(3, address);
                pst.setString(4, tel);
                pst.setString(5, email);

                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("{\"message\": \"Customer added successfully.\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write("{\"error\": \"Failed to add customer.\"}");
                }
            }
        }
    } catch (ClassNotFoundException | SQLException e) {
        e.printStackTrace();
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.getWriter().write("{\"error\": \"An internal error occurred.\"}");
    }
}

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");

        if (id == null || id.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"ID is required.\"}");
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234")) {
            String sql = "DELETE FROM customer WHERE id = ?";
            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setString(1, id);
                int rowsAffected = pst.executeUpdate();

                if (rowsAffected > 0) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("{\"message\": \"Customer deleted successfully.\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\": \"Customer not found.\"}");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"An internal error occurred.\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try (BufferedReader reader = req.getReader()) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            String jsonString = jsonBuilder.toString();
            JsonObject jsonObject = Json.createReader(new StringReader(jsonString)).readObject();

            String id = jsonObject.getString("id", null);
            String name = jsonObject.getString("name", null);
            String address = jsonObject.getString("address", null);
            String tel = jsonObject.getString("tel", null);
            String email = jsonObject.getString("email", null);

            if (id == null || name == null || address == null || tel == null || email == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"All fields are required.\"}");
                return;
            }

            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234")) {
                String sql = "UPDATE customer SET name = ?, address = ?, tel = ?, email = ? WHERE id = ?";
                try (PreparedStatement pst = connection.prepareStatement(sql)) {
                    pst.setString(1, name);
                    pst.setString(2, address);
                    pst.setString(3, tel);
                    pst.setString(4, email);
                    pst.setString(5, id);

                    int rowsAffected = pst.executeUpdate();
                    if (rowsAffected > 0) {
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().write("{\"message\": \"Customer updated successfully.\"}");
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write("{\"error\": \"Customer not found.\"}");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"An internal error occurred.\"}");
        }
    }

//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234");
//
//            String telOnly = req.getParameter("telOnly");
//            String query = telOnly != null && telOnly.equals("true")
//                    ? "SELECT tel FROM customer"
//                    : "SELECT * FROM customer";
//
//            ResultSet resultSet = connection.prepareStatement(query).executeQuery();
//            resp.setContentType("application/json");
//
//            if (telOnly != null && telOnly.equals("true")) {
//                // Create a JSON array for telephone numbers
//                JsonArrayBuilder telArray = Json.createArrayBuilder();
//                while (resultSet.next()) {
//                    telArray.add(resultSet.getString("tel"));
//                }
//                resp.getWriter().write(telArray.build().toString());
//            } else {
//                // Existing logic for all customer details
//                JsonArrayBuilder allCustomers = Json.createArrayBuilder();
//                while (resultSet.next()) {
//                    JsonObjectBuilder customer = Json.createObjectBuilder();
//                    customer.add("id", resultSet.getString("id"));
//                    customer.add("name", resultSet.getString("name"));
//                    customer.add("address", resultSet.getString("address"));
//                    customer.add("tel", resultSet.getString("tel"));
//                    customer.add("email", resultSet.getString("email"));
//                    allCustomers.add(customer);
//                }
//                resp.getWriter().write(allCustomers.build().toString());
//            }
//        } catch (ClassNotFoundException | SQLException e) {
//            e.printStackTrace();
//            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            resp.getWriter().write("{\"error\": \"An internal error occurred.\"}");
//        }
//    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234");

            String telOnly = req.getParameter("telOnly");
            String telParam = req.getParameter("tel");

            String query;
            PreparedStatement preparedStatement;

            if (telOnly != null && telOnly.equals("true")) {
                query = "SELECT tel FROM customer";
                preparedStatement = connection.prepareStatement(query);
            } else if (telParam != null) {
                query = "SELECT id FROM customer WHERE tel = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, telParam);
            } else {
                query = "SELECT * FROM customer";
                preparedStatement = connection.prepareStatement(query);
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            resp.setContentType("application/json");

            if (telOnly != null && telOnly.equals("true")) {

                JsonArrayBuilder telArray = Json.createArrayBuilder();
                while (resultSet.next()) {
                    telArray.add(resultSet.getString("tel"));
                }
                resp.getWriter().write(telArray.build().toString());
            } else if (telParam != null) {

                JsonObjectBuilder customerIdJson = Json.createObjectBuilder();
                if (resultSet.next()) {
                    customerIdJson.add("id", resultSet.getString("id"));
                } else {
                    customerIdJson.add("id", "");
                }
                resp.getWriter().write(customerIdJson.build().toString());
            } else {
                JsonArrayBuilder allCustomers = Json.createArrayBuilder();
                while (resultSet.next()) {
                    JsonObjectBuilder customer = Json.createObjectBuilder();
                    customer.add("id", resultSet.getString("id"));
                    customer.add("name", resultSet.getString("name"));
                    customer.add("address", resultSet.getString("address"));
                    customer.add("tel", resultSet.getString("tel"));
                    customer.add("email", resultSet.getString("email"));
                    allCustomers.add(customer);
                }
                resp.getWriter().write(allCustomers.build().toString());
                resp.setStatus(HttpServletResponse.SC_OK);
               // resp.sendError(400,"not found");
                resp.addHeader("IP-Address", "123.123.4.12");
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"An internal error occurred.\"}");
        }
    }
}

