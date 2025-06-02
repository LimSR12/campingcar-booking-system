package admin.dao;

import global.db.DBConnection;
import global.entity.ExternalMaintenance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExternalMaintenanceDao implements CrudDao<ExternalMaintenance> {

    @Override
    public List<ExternalMaintenance> findAll() throws SQLException {
        String sql = "SELECT * FROM external_maintenance ORDER BY id";
        List<ExternalMaintenance> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public void insert(ExternalMaintenance v) throws SQLException {
        String sql =
            "INSERT INTO external_maintenance " +
            "(car_id, center_id, customer_id, license_number, company_id, " +
            "repair_details, repair_date, repair_fee, fee_due_date, extra_details) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, v.getCarId());
            ps.setLong(2, v.getCenterId());
            ps.setLong(3, v.getCustomerId());
            ps.setString(4, v.getLicenseNumber());
            ps.setLong(5, v.getCompanyId());
            ps.setString(6, v.getRepairDetails());
            // repair_date as LocalDateTime
            ps.setTimestamp(7, Timestamp.valueOf(v.getRepairDate()));
            // repair_fee as double
            ps.setDouble(8, v.getRepairFee());
            // fee_due_date as LocalDate
            ps.setDate(9, Date.valueOf(v.getFeeDueDate()));
            ps.setString(10, v.getExtraDetails());
            ps.executeUpdate();
        }
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{
            "ID","Car ID","Center ID","Customer ID","License Number","Company ID",
            "Repair Details","Repair Date","Repair Fee","Fee Due Date","Extra Details"
        };
    }

    @Override
    public Object[] toRow(ExternalMaintenance e) {
        return new Object[]{
            e.getId(), e.getCarId(), e.getCenterId(), e.getCustomerId(),
            e.getLicenseNumber(), e.getCompanyId(), e.getRepairDetails(),
            e.getRepairDate(), e.getRepairFee(), e.getFeeDueDate(), e.getExtraDetails()
        };
    }

    private ExternalMaintenance map(ResultSet r) throws SQLException {
        ExternalMaintenance e = new ExternalMaintenance();
        e.setId(r.getLong("id"));
        e.setCarId(r.getLong("car_id"));
        e.setCenterId(r.getLong("center_id"));
        e.setCustomerId(r.getLong("customer_id"));
        e.setLicenseNumber(r.getString("license_number"));
        e.setCompanyId(r.getLong("company_id"));
        e.setRepairDetails(r.getString("repair_details"));
        // map repair_date to LocalDateTime
        Timestamp ts = r.getTimestamp("repair_date");
        if (ts != null) e.setRepairDate(ts.toLocalDateTime());
        // map repair_fee to double
        e.setRepairFee(r.getDouble("repair_fee"));
        // map fee_due_date to LocalDate
        Date dd = r.getDate("fee_due_date");
        if (dd != null) e.setFeeDueDate(dd.toLocalDate());
        e.setExtraDetails(r.getString("extra_details"));
        return e;
    }

	@Override
	public void update(ExternalMaintenance t) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Long id) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}
