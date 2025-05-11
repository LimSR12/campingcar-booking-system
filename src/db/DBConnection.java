package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
	// 일단 이렇게 사용하고, 추후에 변경
    public static Connection connect() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/camping", "root", "1234"
        );
    }
}
