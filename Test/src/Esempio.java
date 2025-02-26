import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class Esempio {
    public static void main(String[] args) throws Exception {
        String DB_DRIVER = "com.mysql.jdbc.Driver";
        String DB_URL = "jdbc:mysql://localhost:3306/world";
        String DB_USERNAME = "root";
        String DB_PASSWORD = "root";
        Connection conn = null;

        try {
            Class.forName(DB_DRIVER);

            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            if (conn != null){
                System.out.println("CONNESSO");
                Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                String query = "SELECT * from city limit 5";
                ResultSet rs = stmt.executeQuery(query);
                ResultSetMetaData metaData = rs.getMetaData();
                System.out.println("numero colonne: " + metaData.getColumnCount());
                System.out.println(metaData.getColumnName(1) + "| " + metaData.getColumnName(2) + "| " + metaData.getColumnName(3));

                while (rs.next()) {
                    System.out.println(rs.getString(1) + " | " + rs.getString(2) + " | " + rs.getString(3));
                }
                rs.beforeFirst(); // il cursore torna in cima
                rs.next(); // il cursore è nel primo record {rs.absolute(1)}
                System.out.println(rs.getString("Name"));

            }
            else
                System.out.println("NON CONNESSO");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
