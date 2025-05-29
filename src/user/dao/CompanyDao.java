package user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import global.db.DBConnection;

public class CompanyDao {

    public String getCompanyNameByCarId(Long id) {
        String sql = "SELECT co.name FROM company co " +
                     "JOIN camping_car ca ON co.id = ca.company_id " +
                     "WHERE ca.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "회사명 없음";
    }
    
    public Long getCompanyIdByCarId(Long id) {
        String sql = "SELECT co.id FROM company co " +
                     "JOIN camping_car ca ON co.id = ca.company_id " +
                     "WHERE ca.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}