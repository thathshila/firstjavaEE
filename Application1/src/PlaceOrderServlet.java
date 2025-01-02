import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
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

            String orderId = req.getParameter("OrderId");
            String customerContact = req.getParameter("customer_Contact");
            String date = req.getParameter("date");
            String itemCode = req.getParameter("item_code");
            String quantityStr = req.getParameter("quantity");
            String totalPriceStr = req.getParameter("total_price");
            String customerId = req.getParameter("customer_id");

            if (orderId == null || customerContact == null || date == null || itemCode == null ||
                    quantityStr == null || totalPriceStr == null || customerId == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters.");
                return;
            }

            int quantity;
            double totalPrice;

            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid quantity format.");
                return;
            }

            try {
                totalPrice = Double.parseDouble(totalPriceStr);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid total price format.");
                return;
            }

            String query = "INSERT INTO placeorder (OrderId, customer_Contact, date, item_code, quantity, total_price, customer_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, orderId);
            preparedStatement.setString(2, customerContact);
            preparedStatement.setString(3, date);
            preparedStatement.setString(4, itemCode);
            preparedStatement.setInt(5, quantity);
            preparedStatement.setDouble(6, totalPrice);
            preparedStatement.setString(7, customerId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                resp.setContentType("application/json");
                resp.getWriter().write("{\"status\":\"success\"}");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to save the order.");
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

}
