import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.io.StringReader;
import java.sql.*;

@WebServlet(urlPatterns = "/placeOrder")
public class PlaceOrderServlet extends HttpServlet {
        @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234");

            String query = "SELECT OrderId FROM placeorder ORDER BY OrderId DESC LIMIT 1";
            ResultSet resultSet = connection.prepareStatement(query).executeQuery();

            String latestOrderId = null;
            if (resultSet.next()) {
                latestOrderId = resultSet.getString("id");
            }

            JsonObjectBuilder responseJson = Json.createObjectBuilder();
            responseJson.add("latestOrderId", latestOrderId != null ? latestOrderId : "");

            resp.setContentType("application/json");
            resp.getWriter().write(responseJson.build().toString());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Unable to fetch the latest Order ID.\"}");
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234");

            // Parse JSON payload from the request
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = req.getReader().readLine()) != null) {
                sb.append(line);
            }
            String jsonPayload = sb.toString();

            // Parse JSON to extract data
            JsonObject jsonObject = Json.createReader(new StringReader(jsonPayload)).readObject();
            String orderId = jsonObject.getString("orderId");
            String orderDate = jsonObject.getString("orderDate");
            String customerId = jsonObject.getString("customerId");
            double totalPrice = jsonObject.getJsonNumber("totalPrice").doubleValue();

            // Extract cart items array
            JsonArray cartItems = jsonObject.getJsonArray("cartItems");

            // Insert the order
            String orderQuery = "INSERT INTO placeorder (OrderId, customer_Contact, date, total_price, customer_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement orderStmt = connection.prepareStatement(orderQuery);
            orderStmt.setString(1, orderId);
            orderStmt.setString(2, jsonObject.getString("customerTel"));
            orderStmt.setString(3, orderDate);
            orderStmt.setDouble(4, totalPrice);
            orderStmt.setString(5, customerId);


            int orderInserted = orderStmt.executeUpdate();
            if (orderInserted == 0) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to place the order.");
                return;
            }

            // Insert cart items
            String itemQuery = "INSERT INTO orderdetail (orderId, item_code, quantity, unit_price) VALUES (?, ?, ?, ?)";
            PreparedStatement itemStmt = connection.prepareStatement(itemQuery);
            itemStmt.setString(1, orderId);
            itemStmt.setString(2, jsonObject.getString("itemCode"));
            itemStmt.setInt(3, jsonObject.getInt("quantity"));
            itemStmt.setDouble(4, totalPrice);

            // Update available quantity in items table
            String updateQuantityQuery = "UPDATE item SET qty = available_quantity - ? WHERE item_code = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateQuantityQuery);

            for (int i = 0; i < cartItems.size(); i++) {
                JsonObject item = cartItems.getJsonObject(i);

                // Insert order details
                itemStmt.setString(1, orderId);
                itemStmt.setString(2, item.getString("itemCode"));
                itemStmt.setInt(3, item.getInt("qty"));
                itemStmt.setDouble(4, item.getJsonNumber("unitPrice").doubleValue());
                itemStmt.addBatch();

                // Update available quantity
                updateStmt.setInt(1, item.getInt("qty"));
                updateStmt.setString(2, item.getString("itemCode"));
                updateStmt.addBatch();
            }

            // Execute batch updates
            itemStmt.executeBatch();
            updateStmt.executeBatch();

            resp.setContentType("application/json");
            resp.getWriter().write("{\"status\":\"success\"}");

            // Close resources
            orderStmt.close();
            itemStmt.close();
            updateStmt.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//
//            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234");
//
//            String orderId = req.getParameter("OrderId");
//            String customerContact = req.getParameter("customer_Contact");
//            String date = req.getParameter("date");
//            String itemCode = req.getParameter("item_code");
//            String quantityStr = req.getParameter("quantity");
//            String totalPriceStr = req.getParameter("total_price");
//            String customerId = req.getParameter("customer_id");
//
//            if (orderId == null || customerContact == null || date == null || itemCode == null ||
//                    quantityStr == null || totalPriceStr == null || customerId == null) {
//                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters.");
//                return;
//            }
//
//            int quantity;
//            double totalPrice;
//
//            try {
//                quantity = Integer.parseInt(quantityStr);
//            } catch (NumberFormatException e) {
//                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid quantity format.");
//                return;
//            }
//
//            try {
//                totalPrice = Double.parseDouble(totalPriceStr);
//            } catch (NumberFormatException e) {
//                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid total price format.");
//                return;
//            }
//
//            String query = "INSERT INTO placeorder (OrderId, customer_Contact, date, item_code, quantity, total_price, customer_id) " +
//                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
//            PreparedStatement preparedStatement = connection.prepareStatement(query);
//
//            preparedStatement.setString(1, orderId);
//            preparedStatement.setString(2, customerContact);
//            preparedStatement.setString(3, date);
//            preparedStatement.setString(4, itemCode);
//            preparedStatement.setInt(5, quantity);
//            preparedStatement.setDouble(6, totalPrice);
//            preparedStatement.setString(7, customerId);
//
//            int rowsAffected = preparedStatement.executeUpdate();
//
//            if (rowsAffected > 0) {
//                resp.setContentType("application/json");
//                resp.getWriter().write("{\"status\":\"success\"}");
//            } else {
//                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to save the order.");
//            }
//
//            preparedStatement.close();
//            connection.close();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
//        }
//    }
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//
//            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234");
//
//            // Parse JSON payload from the request
//            StringBuilder sb = new StringBuilder();
//            String line;
//            while ((line = req.getReader().readLine()) != null) {
//                sb.append(line);
//            }
//            String jsonPayload = sb.toString();
//
//            // Parse JSON to extract data
//            JsonObject jsonObject = Json.createReader(new StringReader(jsonPayload)).readObject();
//            String orderId = jsonObject.getString("orderId");
//            String orderDate = jsonObject.getString("orderDate");
//            String customerId = jsonObject.getString("customerId");
//            double totalPrice = jsonObject.getJsonNumber("totalPrice").doubleValue();
//
//            // Extract cart items array
//            JsonArray cartItems = jsonObject.getJsonArray("cartItems");
//
//            // Insert the order
//            String orderQuery = "INSERT INTO placeorder (OrderId, customer_Contact, date, total_price, customer_id) VALUES (?, ?, ?, ?, ?)";
//            PreparedStatement orderStmt = connection.prepareStatement(orderQuery);
//            orderStmt.setString(1, orderId);
//            orderStmt.setString(2, jsonObject.getString("customerTel"));
//            orderStmt.setString(3, orderDate);
//            orderStmt.setDouble(4, totalPrice);
//            orderStmt.setString(5, customerId);
//
//
//            int orderInserted = orderStmt.executeUpdate();
//            if (orderInserted == 0) {
//                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to place the order.");
//                return;
//            }
//
//            // Insert cart items
//            String itemQuery = "INSERT INTO orderdetail (orderId, item_code, quantity, unit_price) VALUES (?, ?, ?, ?)";
//
//            PreparedStatement itemStmt = connection.prepareStatement(itemQuery);
//
////            for (int i = 0; i < cartItems.size(); i++) {
////                JsonObject item = ((JsonArray) cartItems).getJsonObject(i);
////                itemStmt.setString(1, orderId);
////                itemStmt.setString(2, item.getString("itemName")); // Ensure your DB expects item_name or similar.
////                itemStmt.setInt(3, item.getInt("qty"));
////                itemStmt.setDouble(4, item.getJsonNumber("unitPrice").doubleValue());
////                itemStmt.addBatch();
////            }
//            for (int i = 0; i < cartItems.size(); i++) {
//                JsonObject item = cartItems.getJsonObject(i);
//                System.out.println("Item Code: " + item.getString("itemCode"));
//                System.out.println("Quantity: " + item.getInt("qty"));
//                System.out.println("Unit Price: " + item.getJsonNumber("unitPrice").doubleValue());
//
//                itemStmt.setString(1, orderId);
//                itemStmt.setString(2, item.getString("itemCode")); // Ensure this matches the DB column
//                itemStmt.setInt(3, item.getInt("qty")); // Check this value is non-null and valid
//                itemStmt.setDouble(4, item.getJsonNumber("unitPrice").doubleValue());
//                itemStmt.addBatch();
//            }
//
//            itemStmt.executeBatch();
//
//            resp.setContentType("application/json");
//            resp.getWriter().write("{\"status\":\"success\"}");
//
//            orderStmt.close();
//            itemStmt.close();
//            connection.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
//        }
//    }

}
