package user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import global.db.DBConnection;
import global.entity.Customer;

public class CustomerDao {

    public Customer getCustomerById(Long id) {
        String sql = "SELECT * FROM customer WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Customer customer = new Customer();
                customer.setName(rs.getString("name"));
                customer.setLicenseNumber(rs.getString("license_number"));
                customer.setPhone(rs.getString("phone"));
                return customer;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
