import org.knowm.xchart.*;

import java.sql.ResultSet;
import java.util.HashMap;

public class Visualizer {
    public static void showCitiesStats() {
        try {
            HashMap<String, Integer> cities = new HashMap<>();
            String query = "SELECT city, COUNT(city) as counter FROM students WHERE city IS NOT NULL GROUP BY city";
            ResultSet res = DataBaseManager.executeQuery(query);

            while (true) {
                assert res != null;
                if (!res.next()) break;
                String city = res.getString("city");
                int cityCounter = res.getInt("counter");
                cities.put(city, cityCounter);
            }

            PieChart chart = new PieChartBuilder().width(800).height(600).title("Статистика городов").build();
            cities.forEach(chart::addSeries);
            new SwingWrapper<>(chart).displayChart();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
