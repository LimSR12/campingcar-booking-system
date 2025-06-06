package admin.dao;

import global.db.DBConnection;
import global.entity.InternalMaintenance;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
	public int updateByCondition(Map<String, Object> newValues, String condition) throws SQLException {
        if(newValues==null||newValues.isEmpty()||condition==null||condition.isBlank()) return 0;
        StringBuilder sb=new StringBuilder(); List<Object> ps=new ArrayList<>();
        for(var e:newValues.entrySet()){ if(sb.length()>0) sb.append(", "); sb.append(e.getKey()).append("=?"); ps.add(e.getValue());}
        String sql="UPDATE internal_maintenance SET "+sb+" WHERE "+condition;
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)){
            for(int i=0;i<ps.size();i++) p.setObject(i+1,ps.get(i));
            return p.executeUpdate();
        }
	}

    @Override
    public Map<String,Integer> deleteByCondition(String cond)throws SQLException{
        if(cond==null||cond.isBlank()) return Collections.emptyMap();
        String sql="DELETE FROM internal_maintenance WHERE "+cond;
        try(Connection c=DBConnection.getConnection(); Statement st=c.createStatement()){
            int n=st.executeUpdate(sql);
            return Collections.singletonMap("internal_maintenance",n);
        }
    }
    
    public List<InternalMaintenance> findByCarId(long carId) throws SQLException {
        String sql = "SELECT * FROM internal_maintenance WHERE car_id=? ORDER BY repair_date DESC";
        List<InternalMaintenance> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, carId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

}