import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import uoc.webserver.utils.Database;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TestRoute implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            try {
                // Initialize database connection
                Database db = new Database("jdbc:mysql://localhost:3306/yourdatabase", "root", "password");
                Connection conn = db.getConnection();

                // Fetch data from the database
                String query = "SELECT part, status, temperature, speed, last_update FROM car_data";
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                // Process the result set
                List<CarData> data = new ArrayList<>();
                while (rs.next()) {
                    data.add(new CarData(
                        rs.getString("part"),
                        rs.getString("status"),
                        rs.getString("temperature"),
                        rs.getString("speed"),
                        rs.getString("last_update")
                    ));
                }

                // Convert data to JSON
                Gson gson = new Gson();
                String response = gson.toJson(data);

                // Set response headers
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*"); // Enable CORS
                exchange.sendResponseHeaders(200, response.length());

                // Send response
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                String errorResponse = "Error fetching data";
                exchange.sendResponseHeaders(500, errorResponse.length());
                OutputStream os = exchange.getResponseBody();
                os.write(errorResponse.getBytes());
                os.close();
            }
        }
    }
}

// Data class to represent a row in the database
class CarData {
    String part;
    String status;
    String temperature;
    String speed;
    String lastUpdate;

    public CarData(String part, String status, String temperature, String speed, String lastUpdate) {
        this.part = part;
        this.status = status;
        this.temperature = temperature;
        this.speed = speed;
        this.lastUpdate = lastUpdate;
    }
}
