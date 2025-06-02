package user.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import global.db.DBConnection;
import global.entity.ExternalMaintenance;

public class ExternalMaintenanceDao {

	public boolean insert(ExternalMaintenance m) {
	    String sql = "INSERT INTO external_maintenance (car_id, center_id, customer_id, license_number, company_id, repair_details, repair_date, repair_fee, fee_due_date, extra_details) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setLong(1, m.getCarId());
	        pstmt.setLong(2, m.getCenterId());
	        pstmt.setLong(3, m.getCustomerId());
	        pstmt.setString(4, m.getLicenseNumber());
	        pstmt.setLong(5, m.getCompanyId());
	        pstmt.setString(6, m.getRepairDetails());
	        pstmt.setTimestamp(7, Timestamp.valueOf(m.getRepairDate()));
	        pstmt.setDouble(8, m.getRepairFee());
	        pstmt.setDate(9, Date.valueOf(m.getFeeDueDate()));
	        pstmt.setString(10, m.getExtraDetails());

	        return pstmt.executeUpdate() > 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	public List<ExternalMaintenanceDto> getRepairsByCustomerId(Long customerId) {
        List<ExternalMaintenanceDto> list = new ArrayList<>();
        String sql = "SELECT em.id, ec.name AS center_name, em.repair_details, em.repair_date, em.repair_fee " +
                     "FROM external_maintenance em " +
                     "JOIN external_center ec ON em.center_id = ec.id " +
                     "WHERE em.customer_id = ? " +
                     "ORDER BY em.repair_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Long id = rs.getLong("id");
                String centerName = rs.getString("center_name");
                String details = rs.getString("repair_details");
                LocalDateTime repairDate = rs.getTimestamp("repair_date").toLocalDateTime();
                double fee = rs.getDouble("repair_fee");

                list.add(new ExternalMaintenanceDto(id, centerName, details, repairDate, fee));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
