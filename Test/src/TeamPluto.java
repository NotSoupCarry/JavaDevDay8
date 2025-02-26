import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
                        cercaCitta(scanner, conn);
                        break;
                    case 3:
                        inserisciCitta(scanner, conn);
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

    // Metodo per controllare l'input intero
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

    // Metodo per controllare che l'input stringa non sia vuoto
    public static String controlloInputStringhe(Scanner scanner) {
        String valore;
        do {
            valore = scanner.nextLine().trim();
            if (valore.isEmpty()) {
                System.out.print("Input non valido. Inserisci un testo: ");
            }
        } while (valore.isEmpty());
        return valore;
    }

    // Stampa tutte le città
    public static void stampaCitta(Statement statement) {
        try {
            String query = "SELECT * FROM city";
            ResultSet rs = statement.executeQuery(query);
            ResultSetMetaData metadata = rs.getMetaData();
            int columnCount = metadata.getColumnCount();

            System.out.println("\n--- Elenco delle città ---");

            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metadata.getColumnName(i) + "\t");
            }
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Errore SQL: " + e.getMessage());
        }
    }

    // Cerca una città per nome
    public static void cercaCitta(Scanner scanner, Connection conn) {
        System.out.print("Inserisci il nome della città: ");
        String cityName = controlloInputStringhe(scanner);

        String query = "SELECT * FROM city WHERE Name = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, cityName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("\n--- Dettagli della città ---");
                System.out.println("ID: " + rs.getInt("ID"));
                System.out.println("Nome: " + rs.getString("Name"));
                System.out.println("Codice Paese: " + rs.getString("CountryCode"));
                System.out.println("Distretto: " + rs.getString("District"));
                System.out.println("Popolazione: " + rs.getInt("Population"));
            } else {
                System.out.println("Città non trovata.");
            }
        } catch (SQLException e) {
            System.out.println("Errore SQL: " + e.getMessage());
        }
    }

    // Inserisci città
    public static void inserisciCitta(Scanner scanner, Connection conn) {
        System.out.print("Inserisci il nome della città: ");
        String name = controlloInputStringhe(scanner);

        System.out.print("Inserisci il Codice Paese: ");
        String cc = controlloCodicePaese(scanner, conn);

        System.out.print("Inserisci il distretto: ");
        String district = controlloInputStringhe(scanner);

        System.out.print("Inserisci il numero della popolazione: ");
        int popolazione = controlloInputInteri(scanner);

        String query = "INSERT INTO city (Name, CountryCode, District, Population) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, cc);
            pstmt.setString(3, district);
            pstmt.setInt(4, popolazione);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Città inserita con successo!");
            } else {
                System.out.println("Errore nell'inserimento della città.");
            }
        } catch (SQLException e) {
            System.out.println("Errore SQL: " + e.getMessage());
        }
    }

    // Filtra città per popolazione
    public static void filtraPopolazione(Scanner scanner, Statement statement) {
        System.out.print("Inserisci il numero soglia: ");
        int soglia = controlloInputInteri(scanner);

        System.out.print("Inserisci quanti dati vuoi vedere: ");
        int limit = controlloInputInteri(scanner);

        String query = "SELECT Name, Population FROM city WHERE Population >= " + soglia + " LIMIT " + limit;

        try {
            ResultSet resultSet = statement.executeQuery(query);
            System.out.println("\n--- Città con popolazione superiore a " + soglia + " ---");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("Name") + ": " + resultSet.getInt("Population"));
            }
        } catch (SQLException e) {
            System.out.println("Errore SQL: " + e.getMessage());
        }
    }

    // Media aspettativa di vita per continente
    public static void calcolaAspettativaMedia(Statement statement) {
        String query = "SELECT Continent, AVG(LifeExpectancy) AS VitaMedia FROM country GROUP BY Continent";

        try {
            ResultSet rs = statement.executeQuery(query);
            System.out.println("\n--- Aspettativa di Vita Media per Continente ---");
            while (rs.next()) {
                System.out.println(rs.getString("Continent") + ": " + rs.getInt("VitaMedia"));
            }
        } catch (SQLException e) {
            System.out.println("Errore SQL: " + e.getMessage());
        }
    }

    // Metodo per controllare che il CountryCode esista
    public static String controlloCodicePaese(Scanner scanner, Connection conn) {
        String countryCode;
        boolean exist;

        do {
            System.out.print("Inserisci il Codice Paese: ");
            countryCode = scanner.nextLine().trim().toUpperCase(); // Converto in maiuscolo per uniformità

            // Query per controllare se il codice esiste nella tabella country
            String query = "SELECT COUNT(*) FROM country WHERE Code = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, countryCode);
                ResultSet rs = pstmt.executeQuery();

                rs.next();
                exist = rs.getInt(1) > 0; // Se COUNT(*) > 0 allora il codice esiste

                if (!exist) {
                    System.out.println("Errore: Il codice paese inserito non esiste. Riprova.");
                }
            } catch (SQLException e) {
                System.out.println("Errore SQL: " + e.getMessage());
                exist = false;
            }
        } while (!exist);

        return countryCode; // Restituisce un codice valido
    }

}
