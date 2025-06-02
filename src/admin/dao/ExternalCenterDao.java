package admin.dao;

import global.db.DBConnection;
import global.entity.ExternalCenter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExternalCenterDao implements CrudDao<ExternalCenter> {

    @Override
    public List<ExternalCenter> findAll() throws SQLException {
        String sql = "SELECT * FROM external_center ORDER BY id";
        List<ExternalCenter> list = new ArrayList<>();
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
    public void insert(ExternalCenter v) throws SQLException {
        String sql = "INSERT INTO external_center (name,address,phone,manager_name,manager_email) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getName());
            ps.setString(2, v.getAddress());
            ps.setString(3, v.getPhone());
            ps.setString(4, v.getManagerName());
            ps.setString(5, v.getManagerEmail());
            ps.executeUpdate();
        }
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{
            "ID","Name","Address","Phone","ManagerName","ManagerEmail"
        };
    }

    @Override
    public Object[] toRow(ExternalCenter c) {
        return new Object[]{
            c.getId(), c.getName(), c.getAddress(),
            c.getPhone(), c.getManagerName(), c.getManagerEmail()
        };
    }

    private ExternalCenter map(ResultSet r) throws SQLException {
        ExternalCenter c = new ExternalCenter();
        c.setId(r.getLong("id"));
        c.setName(r.getString("name"));
        c.setAddress(r.getString("address"));
        c.setPhone(r.getString("phone"));
        c.setManagerName(r.getString("manager_name"));
        c.setManagerEmail(r.getString("manager_email"));
        return c;
    }

	@Override
	public int updateByCondition(Map<String, Object> newValues, String condition) throws SQLException {
        if (newValues == null || newValues.isEmpty() || condition == null || condition.trim().isEmpty()) {
            return 0;
        }

        // SET 절 조립
        StringBuilder setClause = new StringBuilder();
        List<Object> params = new ArrayList<>();
        for (Map.Entry<String, Object> entry : newValues.entrySet()) {
            if (setClause.length() > 0) {
                setClause.append(", ");
            }
            setClause.append(entry.getKey()).append(" = ?");
            params.add(entry.getValue());
        }

        String sql = "UPDATE external_center SET " + setClause + " WHERE " + condition;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            return ps.executeUpdate();
        }
	}

    /**
     * 7) 조건식 기반 삭제
     *    - 외래키 관계(= 자식 테이블인 external_maintenance)부터 먼저 지우고,
     *      마지막에 external_center 테이블에서 삭제
     * @param condition  WHERE 뒤에 직접 붙일 삭제 조건 예: "name = 'AAA' OR phone LIKE '02-%'"
     * @return 테이블별로 “삭제된 건수”를 맵으로 반환
     *         예: { "external_maintenance"=3, "external_center"=2 }
     */
    public Map<String, Integer> deleteByCondition(String condition) throws SQLException {
        if (condition == null || condition.trim().isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String,Integer> deletedCounts = new LinkedHashMap<>();

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1) 삭제 대상 external_center 레코드의 ID 목록을 미리 조회
                String sqlSelectIds = "SELECT id FROM external_center WHERE " + condition;
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

                // 2) 자식 테이블(external_maintenance)에서 먼저 삭제
                String sqlDelExtMaint = "DELETE FROM external_maintenance WHERE center_id = ?";
                int countExtMaint = 0;
                try (PreparedStatement ps = conn.prepareStatement(sqlDelExtMaint)) {
                    for (Long centerId : ids) {
                        ps.setLong(1, centerId);
                        countExtMaint += ps.executeUpdate();
                    }
                }
                deletedCounts.put("external_maintenance", countExtMaint);

                // 3) external_center 본 테이블에서 삭제
                String sqlDelCenter = "DELETE FROM external_center WHERE id = ?";
                int countCenter = 0;
                try (PreparedStatement ps = conn.prepareStatement(sqlDelCenter)) {
                    for (Long centerId : ids) {
                        ps.setLong(1, centerId);
                        countCenter += ps.executeUpdate();
                    }
                }
                deletedCounts.put("external_center", countCenter);

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
    
    public ExternalCenter findById(long centerId) throws SQLException {
        String sql = "SELECT * FROM external_center WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, centerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }
}