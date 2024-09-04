import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DatabaseTest {

    @Test
    public void TestConnection() {
        Connection con = null;

        try {
            String url = "jdbc:sqlite::resource:Scribble.db";
            con = DriverManager.getConnection(url, "sa", "sa");

            assertNotNull(con);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

}
