import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.sql.ResultSetMetaData;

public class App {
    private static final String URL = "jdbc:mysql://localhost:3306/world";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD)) {
            Class.forName(DB_DRIVER);
            Statement statement = conn.createStatement();

            boolean exit = false;
            int scelta;

            while (!exit) {
                System.out.println("\n--- MENU ---");
                System.out.println("1. Stampare tutte le città");
                System.out.println("2. Cercare una città per nome");
                System.out.println("3. Inserire una nuova città");
                System.out.println("4. Stampare paesi con popolazione superiore a una soglia");
                System.out.println("5. Media aspettativa di vita per continente");
                System.out.println("6. Esci");
                System.out.print("Scelta: ");
                scelta = controlloInputInteri(scanner);

                scanner.nextLine(); 

                switch (scelta) {
                    case 1:
                        stampaCitta(statement);
                        break;
                    case 2:
                        // cercaCitta(scanner, statement);
                        break;
                    case 3:
                        // inserisciCitta(scanner, statement);
                        break;
                    case 4:
                        filtraPopolazione(scanner, statement);
                        break;
                    case 5:
                        calcolaAspettativaMedia(statement);
                        break;
                    case 6:
                        System.out.println("CIAOOOOO");
                        exit = true;
                        break;
                    default:
                        System.out.println("Scelta non valida!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        scanner.close();
    }

    // Metodo per controllare l'input intero e assicurarsi che sia un numero non negativo
    public static int controlloInputInteri(Scanner scanner) {
        int valore;
        do {
            while (!scanner.hasNextInt()) {
                System.out.print("Devi inserire un numero intero. Riprova ");
                scanner.next();
            }
            valore = scanner.nextInt();
            if (valore < 0) {
                System.out.print("Il numero non può essere negativo. Riprova: ");
            }
        } while (valore < 0);
        return valore;
    }
    
    // Stampa tutte le città
    public static void stampaCitta(Statement statement) throws SQLException {
        String query = "SELECT * FROM city";
        ResultSet rs = statement.executeQuery(query);
        ResultSetMetaData metadata = rs.getMetaData();
        
        int columnCount = metadata.getColumnCount();
    
        System.out.println("\n--- Elenco delle città ---");
    
        // Stampa intestazione delle colonne
        for (int i = 1; i <= columnCount; i++) {
            System.out.print(metadata.getColumnName(i) + "\t");
        }
        System.out.println(); // Nuova riga
    
        // Stampa le righe della tabella
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rs.getString(i) + "\t");
            }
            System.out.println(); // Nuova riga dopo ogni città
        }
    }
    

    // Data la popolazione in input stampare solo le città con popolazione maggiore di quella inserita 
    public static void filtraPopolazione(Scanner scanner, Statement statement) throws SQLException {
        // Chiedere input per soglia
        System.out.print("Inserisci il numero soglia: ");
        int soglia = controlloInputInteri(scanner);
    
        // Chiedere quanti risultati visualizzare
        System.out.print("Inserisci quanti dati vuoi vedere: ");
        int limit = controlloInputInteri(scanner);
        
        // Query per filtrare la popolazione
        String query = "SELECT Name, Population FROM city WHERE Population >= " + soglia + " LIMIT " + limit;
        ResultSet resultSet = statement.executeQuery(query);
    
        // Stampare i risultati
        System.out.println("\n--- Città con popolazione superiore a " + soglia + " ---");
        while (resultSet.next()) {
            System.out.println(resultSet.getString("Name") + ": " + resultSet.getInt("Population"));
        }
    }
    

    // Media aspettativa di vita per continente
    public static void calcolaAspettativaMedia(Statement statement) throws SQLException {
        String query = "SELECT Continent, AVG(LifeExpectancy) AS VitaMedia FROM country GROUP BY Continent";
        ResultSet rs = statement.executeQuery(query);
        System.out.println("\n--- Aspettativa di Vita Media per Continente ---");
        while (rs.next()) {
            System.out.println(rs.getString("Continent") + ": " + rs.getInt("VitaMedia"));
        }
    }
}
