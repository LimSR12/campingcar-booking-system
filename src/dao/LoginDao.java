package dao;

import java.sql.*;
import db.DBConnection;
import util.DialogUtil;

public class LoginDao {
    public static boolean verifyLogin(String userId, String password) {
        boolean result = false;

        try (
        		Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                		"SELECT * FROM customer WHERE username = ? AND password = ?"
                		)
        	){
        	
        	pstmt.setString(1, userId);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
