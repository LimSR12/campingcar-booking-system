package admin.dao;

import global.db.DBConnection;
import global.entity.ExternalMaintenance;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	public int updateByCondition(Map<String, Object> newValues, String condition) throws SQLException {
        if (newValues == null || newValues.isEmpty() || condition == null || condition.trim().isEmpty()) {
            return 0;
        }

        // 1) SET 절 조립
        StringBuilder setClause = new StringBuilder();
        List<Object> params = new ArrayList<>();
        for (Map.Entry<String, Object> entry : newValues.entrySet()) {
            if (setClause.length() > 0) {
                setClause.append(", ");
            }
            setClause.append(entry.getKey()).append(" = ?");
            params.add(entry.getValue());
        }

        // 2) SQL 완성
        String sql = "UPDATE external_maintenance SET " + setClause + " WHERE " + condition;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            return ps.executeUpdate();
        }
	}
	
	/*
    * 7) 조건식 기반 삭제
    *    - 자식 테이블이 없으므로 바로 본 테이블 삭제
    * @param condition  WHERE 뒤에 바로 붙일 삭제 조건 예: "repair_fee < 50000 AND company_id = 3"
    * @return Map&lt;테이블명, 삭제건수&gt;, 여기서는 {"external_maintenance"=삭제건수}
    */
   public Map<String, Integer> deleteByCondition(String condition) throws SQLException {
       if (condition == null || condition.trim().isEmpty()) {
           return Collections.emptyMap();
       }

       Map<String, Integer> deletedCounts = new LinkedHashMap<>();

       try (Connection conn = DBConnection.getConnection()) {
           conn.setAutoCommit(false);

           try {
               // 1) 삭제할 행들 ID 목록 조회
               String sqlSelectIds = "SELECT id FROM external_maintenance WHERE " + condition;
               List<Long> ids = new ArrayList<>();
               try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(sqlSelectIds)) {
                   while (rs.next()) {
                       ids.add(rs.getLong("id"));
                   }
               }

               if (ids.isEmpty()) {
                   conn.rollback();
                   return Collections.emptyMap();
               }

               // 2) 본 테이블(external_maintenance)에서 해당 ID들 삭제
               String sqlDel = "DELETE FROM external_maintenance WHERE id = ?";
               int countDel = 0;
               try (PreparedStatement ps = conn.prepareStatement(sqlDel)) {
                   for (Long id : ids) {
                       ps.setLong(1, id);
                       countDel += ps.executeUpdate();
                   }
               }
               deletedCounts.put("external_maintenance", countDel);

               conn.commit();
               return deletedCounts;

           } catch (SQLException ex) {
               conn.rollback();
               throw ex;
           } finally {
               conn.setAutoCommit(true);
           }
       }
   }
}
