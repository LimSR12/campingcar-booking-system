package admin.dao;

import global.db.DBConnection;
import global.entity.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CompanyDao implements CrudDao<Company> {

	@Override
	public List<Company> findAll() throws SQLException {
		String sql = "SELECT * FROM company ORDER BY id";
		List<Company> list = new ArrayList<>();
		try(Connection conn = DBConnection.getConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql)) {
			
			while(rs.next()) {
				list.add(map(rs));
			}
		}
		
		return list;
	}
	
	public Company findById(Long long1) throws SQLException {
		String sql = "SELECT * FROM company WHERE id = " + long1;
		try(Connection conn = DBConnection.getConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql)) {
			
	        if (rs.next()) {
	            Company company = new Company();
	            company.setId(rs.getLong("id"));
	            company.setName(rs.getString("name"));
	            company.setAddress(rs.getString("address"));
	            company.setPhone(rs.getString("phone"));
	            company.setManagerName(rs.getString("manager_name"));
	            company.setManagerEmail(rs.getString("manager_email"));
	            return company;
	        } else {
	            return null;
	        }
		}
	}

	private Company map(ResultSet r) throws SQLException {
		Company c = new Company();
        c.setId           (r.getLong   ("id"));
        c.setName         (r.getString ("name"));
        c.setAddress      (r.getString ("address"));
        c.setPhone        (r.getString ("phone"));
        c.setManagerName  (r.getString ("manager_name"));
        c.setManagerEmail (r.getString ("manager_email"));
        return c;
	}

	@Override
	public void insert(Company v) throws SQLException {
        String sql = """
                INSERT INTO company
                  (name, address, phone, manager_name, manager_email)
                VALUES (?,?,?,?,?)
                """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

             ps.setString(1, v.getName());
             ps.setString(2, v.getAddress());
             ps.setString(3, v.getPhone());
             ps.setString(4, v.getManagerName());
             ps.setString(5, v.getManagerEmail());
             ps.executeUpdate();
        }
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
            if (setClause.length() > 0) setClause.append(", ");
            setClause.append(entry.getKey()).append(" = ?");
            params.add(entry.getValue());
        }
        String sql = "UPDATE company SET " + setClause + " WHERE " + condition;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            return ps.executeUpdate();
        }
	}

	@Override
	public Map<String, Integer> deleteByCondition(String condition) throws SQLException {
	    if (condition == null || condition.trim().isEmpty()) {
	        return Collections.emptyMap();
	    }

	    Map<String,Integer> deletedCounts = new LinkedHashMap<>();
	    try (Connection conn = DBConnection.getConnection()) {
	        conn.setAutoCommit(false);
	        try {
	            // 1) 삭제 대상 company ID 목록 조회
	            String sqlSelectIds = "SELECT id FROM company WHERE " + condition;
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

	            // ─────────────────────────────────────────────────────
	            // 2) 손자 테이블부터 순서대로 삭제
	            // ─────────────────────────────────────────────────────

	            // 2-1) internal_maintenance (camping_car.id 참조)
	            String sqlDelInternal = "DELETE FROM internal_maintenance WHERE car_id = ?";
	            int countInternal = 0;
	            try (PreparedStatement ps = conn.prepareStatement(sqlDelInternal)) {
	                for (Long companyId : ids) {
	                    // companyId → camping_car.id 목록 조회
	                    List<Long> carIds = new ArrayList<>();
	                    String sqlSelectCarIds = "SELECT id FROM camping_car WHERE company_id = ?";
	                    try (PreparedStatement ps2 = conn.prepareStatement(sqlSelectCarIds)) {
	                        ps2.setLong(1, companyId);
	                        try (ResultSet rs2 = ps2.executeQuery()) {
	                            while (rs2.next()) {
	                                carIds.add(rs2.getLong("id"));
	                            }
	                        }
	                    }
	                    // 삭제
	                    for (Long carId : carIds) {
	                        ps.setLong(1, carId);
	                        countInternal += ps.executeUpdate();
	                    }
	                }
	            }
	            deletedCounts.put("internal_maintenance", countInternal);

	            // 2-2) external_maintenance (camping_car.id 참조)
	            String sqlDelExternalMaint = "DELETE FROM external_maintenance WHERE car_id = ?";
	            int countExternal = 0;
	            try (PreparedStatement ps = conn.prepareStatement(sqlDelExternalMaint)) {
	                for (Long companyId : ids) {
	                    List<Long> carIds = new ArrayList<>();
	                    String sqlSelectCarIds = "SELECT id FROM camping_car WHERE company_id = ?";
	                    try (PreparedStatement ps2 = conn.prepareStatement(sqlSelectCarIds)) {
	                        ps2.setLong(1, companyId);
	                        try (ResultSet rs2 = ps2.executeQuery()) {
	                            while (rs2.next()) {
	                                carIds.add(rs2.getLong("id"));
	                            }
	                        }
	                    }
	                    for (Long carId : carIds) {
	                        ps.setLong(1, carId);
	                        countExternal += ps.executeUpdate();
	                    }
	                }
	            }
	            deletedCounts.put("external_maintenance", countExternal);

	            // 2-3) rental (camping_car.id 참조)  ← 이 부분을 camping_car 삭제보다 **먼저** 실행해야 한다!
	            String sqlDelRentalByCar = "DELETE FROM rental WHERE car_id = ?";
	            int countRentalByCar = 0;
	            try (PreparedStatement ps = conn.prepareStatement(sqlDelRentalByCar)) {
	                for (Long companyId : ids) {
	                    List<Long> carIds = new ArrayList<>();
	                    String sqlSelectCarIds = "SELECT id FROM camping_car WHERE company_id = ?";
	                    try (PreparedStatement ps2 = conn.prepareStatement(sqlSelectCarIds)) {
	                        ps2.setLong(1, companyId);
	                        try (ResultSet rs2 = ps2.executeQuery()) {
	                            while (rs2.next()) {
	                                carIds.add(rs2.getLong("id"));
	                            }
	                        }
	                    }
	                    for (Long carId : carIds) {
	                        ps.setLong(1, carId);
	                        countRentalByCar += ps.executeUpdate();
	                    }
	                }
	            }
	            deletedCounts.put("rental_by_car", countRentalByCar);

	            // ─────────────────────────────────────────────────────
	            // 3) camping_car (company_id 참조)
	            // ─────────────────────────────────────────────────────
	            String sqlDelCar = "DELETE FROM camping_car WHERE company_id = ?";
	            int countCar = 0;
	            try (PreparedStatement ps = conn.prepareStatement(sqlDelCar)) {
	                for (Long companyId : ids) {
	                    ps.setLong(1, companyId);
	                    countCar += ps.executeUpdate();
	                }
	            }
	            deletedCounts.put("camping_car", countCar);

	            // ─────────────────────────────────────────────────────
	            // 4) rental (company_id 참조)  ← car_id 외래키 삭제가 끝났으므로 이제 안전하게 company_id 기준 삭제
	            // ─────────────────────────────────────────────────────
	            String sqlDelRentalByCompany = "DELETE FROM rental WHERE company_id = ?";
	            int countRentalByCompany = 0;
	            try (PreparedStatement ps = conn.prepareStatement(sqlDelRentalByCompany)) {
	                for (Long companyId : ids) {
	                    ps.setLong(1, companyId);
	                    countRentalByCompany += ps.executeUpdate();
	                }
	            }
	            deletedCounts.put("rental_by_company", countRentalByCompany);

	            // ─────────────────────────────────────────────────────
	            // 5) 최종적으로 company 테이블 삭제
	            // ─────────────────────────────────────────────────────
	            String sqlDelCompany = "DELETE FROM company WHERE id = ?";
	            int countCompany = 0;
	            try (PreparedStatement ps = conn.prepareStatement(sqlDelCompany)) {
	                for (Long companyId : ids) {
	                    ps.setLong(1, companyId);
	                    countCompany += ps.executeUpdate();
	                }
	            }
	            deletedCounts.put("company", countCompany);

	            // 커밋
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

	@Override
	public String[] getColumnNames() {
        return new String[]{
                "ID", "Name", "Address", "Phone", "Manager Name", "Manager Email"
        };
	}

	@Override
	public Object[] toRow(Company c) {
        return new Object[]{
                c.getId(),
                c.getName(),
                c.getAddress(),
                c.getPhone(),
                c.getManagerName(),
                c.getManagerEmail()
        };
	}
	
}