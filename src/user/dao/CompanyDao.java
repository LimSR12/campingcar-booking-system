package user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import global.db.DBConnection;

public class CompanyDao {

    public String getCompanyNameByCarPlate(String plateNumber) {
        String sql = "SELECT co.name FROM company co " +
                     "JOIN camping_car ca ON co.id = ca.company_id " +
                     "WHERE ca.plate_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, plateNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "회사명 없음";
    }
}