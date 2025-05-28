package common.dao;

import java.sql.*;

import global.db.DBConnection;
import global.entity.Customer;
import global.util.DialogUtil;

public class LoginDao {
	
    public static Customer verifyLogin(String username, String password) {
        String sql = "SELECT * FROM customer WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getLong("id"));
//                customer.setUsername(rs.getString("username"));
//                customer.setName(rs.getString("name"));
//                customer.setLicenseNumber(rs.getString("license_number"));
//                customer.setPhone(rs.getString("phone_number"));
                return customer;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean adminLogin(String userId, String password) {
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
