import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.json.*;
import java.io.IOException;

@WebServlet(urlPatterns = "/json")
public class JSONServlet extends HttpServlet {
        //request eke json ekk awoth kiyawana widiya saha response eke body eke json ekk yawana widiya

    //Response body - json send
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       // response.setContentType("application/json");
       // response.setHeader("Accept", "application/json");

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        objectBuilder.add("id", "1");
        objectBuilder.add("address", "123 Main Street");
        objectBuilder.add("email", "john@doe.com");
        objectBuilder.add("name", "John Doe");
        objectBuilder.add("tel", "1234567890");


        JsonObjectBuilder address1 = Json.createObjectBuilder();
        address1.add("street", "Main Street");
        address1.add("city", "New York");

        JsonObjectBuilder address2 = Json.createObjectBuilder();
        address2.add("street", "New York");
        address2.add("city", "New York");

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        arrayBuilder.add(address1);
        arrayBuilder.add(address2);

        objectBuilder.add("address", arrayBuilder);

        response.setContentType("application/json");
        response.getWriter().write(objectBuilder.build().toString());
    }

    //Request body - JSON read

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            JsonReader reader = Json.createReader(req.getReader());
            JsonObject jsonObject = reader.readObject();
//            System.out.println(jsonObject.getString("id"));
//            System.out.println(jsonObject.getString("address"));
//            System.out.println(jsonObject.getString("email"));
//            System.out.println(jsonObject.getString("name"));
//            System.out.println(jsonObject.getString("tel"));
            String id = jsonObject.getString("id");
            String address = jsonObject.getString("address");
            String email = jsonObject.getString("email");
            String name = jsonObject.getString("name");
            String tel = jsonObject.getString("tel");
            System.out.println("id: " + id + " address: " + address + " email: " + email + " name: " + name + " tel: " + tel);

            //data save on database

            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("status",HttpServletResponse.SC_CREATED);
            response.add("message","Created");
            response.add("data",jsonObject);

        } catch (Exception e){
           // throw new RuntimeException(e);
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("status",HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.add("message","Error");
            response.add("data",""); //error ekk yaddi data ynne nee ne e nis ethana blank ekk
        }
    }
}
