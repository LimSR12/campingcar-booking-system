package admin.dao;

import global.db.DBConnection;
import global.entity.InternalMaintenance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InternalMaintenanceDao implements CrudDao<InternalMaintenance> {

	// 테이블을 list 형식으로 만들어서 반환
    @Override
    public List<InternalMaintenance> findAll() throws SQLException {
        String sql = "SELECT * FROM internal_maintenance ORDER BY id";
        List<InternalMaintenance> list = new ArrayList<>();
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
    public void insert(InternalMaintenance v) throws SQLException {
        String sql = "INSERT INTO internal_maintenance (car_id, part_id, staff_id, repair_date, duration, description) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, v.getCarId());
            ps.setLong(2, v.getPartId());
            ps.setLong(3, v.getStaffId());
            // LocalDateTime인 repair date를 Timestamp로 변환해 삽입
            ps.setTimestamp(7, Timestamp.valueOf(v.getRepairDate()));
            ps.setInt(5, v.getDuration());
            ps.setString(6, v.getDescription());
            ps.executeUpdate();
        }
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"ID","Car ID","Part ID","Staff ID","Repair Date","Duration","Description"};
    }

    @Override
    public Object[] toRow(InternalMaintenance m) {
        return new Object[]{
            m.getId(), m.getCarId(), m.getPartId(), m.getStaffId(),
            m.getRepairDate(), m.getDuration(), m.getDescription()
        };
    }

    // SQL문의 결과값을 인스턴스 형식으로 만들어서 반환
    private InternalMaintenance map(ResultSet r) throws SQLException {
        InternalMaintenance m = new InternalMaintenance();
        m.setId(r.getLong("id"));
        m.setCarId(r.getLong("car_id"));
        m.setPartId(r.getLong("part_id"));
        m.setStaffId(r.getLong("staff_id"));
        Timestamp ts = r.getTimestamp("repair_date");
        if (ts != null) m.setRepairDate(ts.toLocalDateTime());
        m.setDuration(r.getInt("duration"));
        m.setDescription(r.getString("description"));
        return m;
    }

	@Override
	public void update(InternalMaintenance t) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Long id) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}