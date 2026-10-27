import java.sql.*;

public class SalesAnalysis {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/SalesAnalysis";
        String user = "root";
        String password = "Godpro@12";

        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Products (" +
                    "product_id INT PRIMARY KEY, " +
                    "product_name VARCHAR(100) NOT NULL, " +
                    "price DECIMAL(10,2) NOT NULL)");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Sales (" +
                    "sale_id INT PRIMARY KEY, " +
                    "product_id INT, " +
                    "quantity INT NOT NULL, " +
                    "sale_date DATE NOT NULL, " +
                    "FOREIGN KEY (product_id) REFERENCES Products(product_id))");

            stmt.executeUpdate("INSERT IGNORE INTO Products VALUES " +
                    "(1,'Laptop',50000.00)," +
                    "(2,'Mobile',20000.00)," +
                    "(3,'Headphones',2000.00)," +
                    "(4,'Keyboard',1500.00)");

            stmt.executeUpdate("INSERT IGNORE INTO Sales VALUES " +
                    "(101,1,2,'2026-10-20')," +
                    "(102,2,5,'2026-10-20')," +
                    "(103,3,10,'2026-10-21')," +
                    "(104,1,1,'2026-10-21')," +
                    "(105,2,3,'2026-10-22')," +
                    "(106,4,8,'2026-10-22')," +
                    "(107,3,6,'2026-10-23')");

            System.out.println("Revenue Per Product");

            String revenueQuery =
                    "SELECT p.product_name, SUM(s.quantity) AS total_quantity, " +
                            "SUM(s.quantity * p.price) AS revenue " +
                            "FROM Products p JOIN Sales s ON p.product_id = s.product_id " +
                            "GROUP BY p.product_id, p.product_name " +
                            "ORDER BY revenue DESC";

            ResultSet rs = stmt.executeQuery(revenueQuery);

            while (rs.next()) {
                System.out.println(
                        rs.getString("product_name") +
                                " | Quantity Sold: " + rs.getInt("total_quantity") +
                                " | Revenue: " + rs.getDouble("revenue"));
            }

            System.out.println("\nBest-Selling Product");

            String bestQuery =
                    "SELECT p.product_name, SUM(s.quantity) AS total_quantity " +
                            "FROM Products p JOIN Sales s ON p.product_id = s.product_id " +
                            "GROUP BY p.product_id, p.product_name " +
                            "ORDER BY total_quantity DESC LIMIT 1";

            rs = stmt.executeQuery(bestQuery);

            if (rs.next()) {
                System.out.println(
                        rs.getString("product_name") +
                                " | Quantity Sold: " + rs.getInt("total_quantity"));
            }

            System.out.println("\nDate-Wise Sales Summary");

            String dateQuery =
                    "SELECT s.sale_date, SUM(s.quantity) AS total_quantity, " +
                            "SUM(s.quantity * p.price) AS daily_revenue, " +
                            "COUNT(s.sale_id) AS total_transactions " +
                            "FROM Sales s JOIN Products p ON s.product_id = p.product_id " +
                            "GROUP BY s.sale_date ORDER BY s.sale_date";

            rs = stmt.executeQuery(dateQuery);

            while (rs.next()) {
                System.out.println(
                        rs.getDate("sale_date") +
                                " | Items Sold: " + rs.getInt("total_quantity") +
                                " | Revenue: " + rs.getDouble("daily_revenue") +
                                " | Transactions: " + rs.getInt("total_transactions"));
            }

            System.out.println("\nOverall Sales Summary");

            String summaryQuery =
                    "SELECT COUNT(*) AS total_transactions, " +
                            "SUM(s.quantity) AS total_items, " +
                            "SUM(s.quantity * p.price) AS total_revenue, " +
                            "AVG(s.quantity * p.price) AS average_transaction " +
                            "FROM Sales s JOIN Products p ON s.product_id = p.product_id";

            rs = stmt.executeQuery(summaryQuery);

            if (rs.next()) {
                System.out.println("Total Transactions: " +
                        rs.getInt("total_transactions"));
                System.out.println("Total Items Sold: " +
                        rs.getInt("total_items"));
                System.out.println("Total Revenue: " +
                        rs.getDouble("total_revenue"));
                System.out.println("Average Transaction Value: " +
                        rs.getDouble("average_transaction"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}