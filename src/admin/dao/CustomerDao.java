package admin.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import global.db.DBConnection;
import global.entity.Customer;

public class CustomerDao implements CrudDao<Customer> {
	
    @Override
    public List<Customer> findAll() throws SQLException {
        String sql = "SELECT * FROM customer ORDER BY id";
        List<Customer> list = new ArrayList<>();
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
    public void insert(Customer v) throws SQLException {
        String sql = "INSERT INTO customer (username,password,license_number,name,address,phone,email,prev_return_date,prev_car_type) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getUsername());
            ps.setString(2, v.getPassword());
            ps.setString(3, v.getLicenseNumber());
            ps.setString(4, v.getName());
            ps.setString(5, v.getAddress());
            ps.setString(6, v.getPhone());
            ps.setString(7, v.getEmail());
            ps.setTimestamp(8, v.getPrevReturnDate());
            ps.setString(9, v.getPrevCarType());
            ps.executeUpdate();
        }
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{
            "ID","Username","Password","LicenseNumber","Name","Address","Phone","Email","PrevReturnDate","PrevCarType"
        };
    }

    @Override
    public Object[] toRow(Customer c) {
        return new Object[]{
            c.getId(), c.getUsername(), c.getPassword(),
            c.getLicenseNumber(), c.getName(), c.getAddress(),
            c.getPhone(), c.getEmail(), c.getPrevReturnDate(), c.getPrevCarType()
        };
    }

    private Customer map(ResultSet r) throws SQLException {
        Customer c = new Customer();
        c.setId(r.getLong("id"));
        c.setUsername(r.getString("username"));
        c.setPassword(r.getString("password"));
        c.setLicenseNumber(r.getString("license_number"));
        c.setName(r.getString("name"));
        c.setAddress(r.getString("address"));
        c.setPhone(r.getString("phone"));
        c.setEmail(r.getString("email"));
        c.setPrevReturnDate(r.getTimestamp("prev_return_date"));
        c.setPrevCarType(r.getString("prev_car_type"));
        return c;
    }
    
    /*
     @param license 면허번호 문자열
     @return 동일한 license_number를 가진 고객이 DB에 하나라도 있으면 true, 없으면 false
     */
    public boolean existsByLicense(String license) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customer WHERE license_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, license.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    /*
    @param phone 문자열
    @return 동일한 phone을 가진 고객이 DB에 하나라도 있으면 true, 없으면 false
    */
   public boolean existsByPhone(String phone) throws SQLException {
       String sql = "SELECT COUNT(*) FROM customer WHERE phone = ?";
       try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
           ps.setString(1, phone.trim());
           try (ResultSet rs = ps.executeQuery()) {
               if (rs.next()) {
                   return rs.getInt(1) > 0;
               }
           }
       }
       return false;
   }
   
   /*
   @param email 문자열
   @return 동일한 email을 가진 고객이 DB에 하나라도 있으면 true, 없으면 false
   */
  public boolean existsByEmail(String email) throws SQLException {
      String sql = "SELECT COUNT(*) FROM customer WHERE email = ?";
      try (Connection conn = DBConnection.getConnection();
           PreparedStatement ps = conn.prepareStatement(sql)) {
          ps.setString(1, email.trim());
          try (ResultSet rs = ps.executeQuery()) {
              if (rs.next()) {
                  return rs.getInt(1) > 0;
              }
          }
      }
      return false;
  }

	@Override
	public int updateByCondition(Map<String, Object> newValues, String condition) throws SQLException {
        if (newValues == null || newValues.isEmpty() || condition == null || condition.trim().isEmpty()) {
            return 0;
        }

        // 1) SET 절 조립
        StringBuilder setClause = new StringBuilder();
        List<Object> params = new ArrayList<>();
        for (Map.Entry<String,Object> entry : newValues.entrySet()) {
            if (setClause.length() > 0) {
                setClause.append(", ");
            }
            setClause.append(entry.getKey()).append(" = ?");
            params.add(entry.getValue());
        }

        // 2) 최종 SQL
        String sql = "UPDATE customer SET " + setClause + " WHERE " + condition;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 순서대로 파라미터 바인딩
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
            // 트랜잭션 시작
            conn.setAutoCommit(false);
            try {
                // 1) 삭제 대상 customer ID 목록 조회
                String sqlSelectIds = "SELECT id FROM customer WHERE " + condition;
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
                
                // 2-1) external maintenance
                String sqlDelExt = "DELETE FROM external_maintenance WHERE customer_id = ?";
                int countExt = 0;
                try (PreparedStatement psExt = conn.prepareStatement(sqlDelExt)) {
                    for (Long custId : ids) {
                        psExt.setLong(1, custId);
                        countExt += psExt.executeUpdate();
                    }
                }
                deletedCounts.put("external_maintenance", countExt);
                
                // 2-2) rental
                String sqlDelRent = "DELETE FROM rental WHERE customer_id = ?";
                int countRent = 0;
                try (PreparedStatement psRent = conn.prepareStatement(sqlDelRent)) {
                    for (Long custId : ids) {
                        psRent.setLong(1, custId);
                        countRent += psRent.executeUpdate();
                    }
                }
                deletedCounts.put("rental", countRent);

                // 3) 최종적으로 customer 테이블 삭제
                String sqlDelCust = "DELETE FROM customer WHERE id = ?";
                int countCustomer = 0;
                try (PreparedStatement ps = conn.prepareStatement(sqlDelCust)) {
                    for (Long custId : ids) {
                        ps.setLong(1, custId);
                        countCustomer += ps.executeUpdate();
                    }
                }
                deletedCounts.put("customer", countCustomer);

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
	
}