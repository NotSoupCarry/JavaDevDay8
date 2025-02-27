import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Esempio2 {
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
                String query = "SELECT * from city limit ?";
                int limite = 5; // varibile del limit per la query

                PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                pstmt.setInt(1, limite); // setta il primo "?" a limite

                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    System.out.println(rs.getString("Name"));
                }

            }
            else
                System.out.println("NON CONNESSO");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
