import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart; 
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ChartPanel;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import java.awt.*;  
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsHelper {
    private Connection connection;
    public AnalyticsHelper(Connection connection) {
        this.connection = connection;
    }

    // Method to create enrollment bar chart
    public JPanel createEnrollmentBarChart(){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT course_name, COUNT(*) as count FROM Enrollments GROUP BY course_name");
            while(resultSet.next()){
                dataset.addValue(resultSet.getInt("count"), "Enrollments", resultSet.getString("course_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarchart(
            "Student Enrollment Bar Chart", //Title of the chart
            "Courses", // X-axis label  
            "Number of Students", // Y-axis label
            dataset); // Dataset

            return new ChartPanel(chart);
    }

    // Method to get grades pie chart
    public JPanel createGradesPieChart(){
        DefaultPieDataset dataset = new DefaultPieDataset();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT grade, COUNT(*) as count FROM Enrollments GROUP BY grade");
            while(resultSet.next()){
                dataset.setValue(resultSet.getString("grade"), resultSet.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        

        JFreeChart chart = ChartFactory.createPieChart(
            "Grade Distribution Pie Chart", // Title
            dataset, // Dataset

            true, // Show legend
            true, // Use tooltips
            false); // Configure chart to generate URLs

            return new ChartPanel(chart);
    }

        // Method to create average grades table
        public JPanel createAverageGradesTable(){
            // Creates a table using JTable
            Map<String, Double> averageGrades = new HashMap<>();

            try {
                statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT course_name, AVG(grade) as average FROM Enrollments GROUP BY course_name");

                while(resultSet.next()){
                    averageGrades.put(resultSet.getString("course_name"), resultSet.getDouble("average_grade"));
                }
            } catch (SQLException e) {
                e.printStackTrace();

             }
            }
            // Create a table model
            Object[][] data = new Object[averageGrades.size()][2];
            Object[] columnNames = {"Course Name", "Average Grade"};

            int i = 0;  
            for(Map.Entry<String, Double> entry : averageGrades.entrySet()){
                data[i][0] = entry.getKey();
                data[i][1] = entry.getValue();
                i++;
            }   

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            table.setFillsViewportHeight(true);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.setBorder(BorderFactory.createTitledBorder("Average Grades by Course"));

            return panel;
}
