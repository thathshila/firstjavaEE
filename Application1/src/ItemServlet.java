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

@WebServlet(urlPatterns = "/item")
public class ItemServlet extends HttpServlet {
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234");
//            ResultSet resultSet = connection.prepareStatement("Select * from item").executeQuery();
//
//            JsonArrayBuilder allItems = Json.createArrayBuilder();
//            while (resultSet.next()) {
//                String code = resultSet.getString("code");
//                String name = resultSet.getString("name");
//                double price = resultSet.getDouble("price");
//                int qty = resultSet.getInt("qty");
//                JsonObjectBuilder item = Json.createObjectBuilder();
//                item.add("code", code);
//                item.add("name", name);
//                item.add("price", price);
//                item.add("qty", qty);
//                allItems.add(item);
//            }
//            resp.setContentType("application/json");
//            resp.getWriter().write(allItems.build().toString());
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234");

            String code = req.getParameter("code");
            String name = req.getParameter("name");
            double price = Double.parseDouble(req.getParameter("price"));
            int qty = Integer.parseInt(req.getParameter("qty"));

            String query = "INSERT INTO item (code, name, price, qty) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, code);
            preparedStatement.setString(2, name);
            preparedStatement.setDouble(3, price);
            preparedStatement.setInt(4, qty);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                resp.setContentType("application/json");
                resp.getWriter().write("{\"status\":\"success\"}");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to save the item.");
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234");

            BufferedReader reader = req.getReader();
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            String json = jsonBuilder.toString();
            JsonObject jsonObject = Json.createReader(new StringReader(json)).readObject();

            String code = jsonObject.getString("code");
            String name = jsonObject.getString("name");
            double price = jsonObject.getJsonNumber("price").doubleValue();
            int qty = jsonObject.getInt("qty");

            String query = "UPDATE item SET name = ?, price = ?, qty = ? WHERE code = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, price);
            preparedStatement.setInt(3, qty);
            preparedStatement.setString(4, code);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                resp.setContentType("application/json");
                resp.getWriter().write("{\"status\":\"success\"}");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update the item.");
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");

        if (code == null || code.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"ID is required.\"}");
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234")) {
            String sql = "DELETE FROM item WHERE code = ?";
            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setString(1, code);
                int rowsAffected = pst.executeUpdate();

                if (rowsAffected > 0) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("{\"message\": \"Item deleted successfully.\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\": \"Item not found.\"}");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"An internal error occurred.\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234");

            String nameOnly = req.getParameter("nameOnly");
            String itemName = req.getParameter("name");

            String query;
            PreparedStatement preparedStatement;

            if (nameOnly != null && nameOnly.equals("true")) {
                query = "SELECT name FROM item";
                preparedStatement = connection.prepareStatement(query);
            } else if (itemName != null) {
                query = "SELECT name, price, qty FROM item WHERE name = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, itemName);
            } else {
                query = "SELECT * FROM item";
                preparedStatement = connection.prepareStatement(query);
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            resp.setContentType("application/json");

            if (nameOnly != null && nameOnly.equals("true")) {
                JsonArrayBuilder nameArray = Json.createArrayBuilder();
                while (resultSet.next()) {
                    nameArray.add(resultSet.getString("name"));
                }
                resp.getWriter().write(nameArray.build().toString());
            } else if (itemName != null) {
                JsonObjectBuilder itemDetails = Json.createObjectBuilder();
                if (resultSet.next()) {
                    itemDetails.add("name", resultSet.getString("name"));
                    itemDetails.add("price", resultSet.getDouble("price"));
                    itemDetails.add("qty", resultSet.getInt("qty"));
                } else {
                    itemDetails.add("error", "Item not found");
                }
                resp.getWriter().write(itemDetails.build().toString());
            } else {
                JsonArrayBuilder allItems = Json.createArrayBuilder();
                while (resultSet.next()) {
                    JsonObjectBuilder item = Json.createObjectBuilder();
                    item.add("code", resultSet.getString("code"));
                    item.add("name", resultSet.getString("name"));
                    item.add("price", resultSet.getDouble("price"));
                    item.add("qty", resultSet.getInt("qty"));
                    allItems.add(item);
                }
                resp.getWriter().write(allItems.build().toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Database error occurred.\"}");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Driver error occurred.\"}");
        }
    }

}
