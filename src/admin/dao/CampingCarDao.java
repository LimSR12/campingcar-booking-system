package admin.dao;

import global.db.DBConnection;
import global.entity.CampingCar;

import java.sql.*;
import java.util.*;

public class CampingCarDao implements CrudDao<CampingCar> {

    // READ
    @Override
    public List<CampingCar> findAll() throws SQLException {
        String sql = "SELECT * FROM camping_car ORDER BY id";
        List<CampingCar> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             Statement  st = c.createStatement();
             ResultSet  rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // CREATE
    @Override
    public void insert(CampingCar v) throws SQLException {
        String sql = """
          INSERT INTO camping_car
            (company_id, name, plate_number, capacity,
             image, detail_info, rental_price, registration_date)
          VALUES (?,?,?,?,?,?,?,?)
        """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong      (1, v.getCompanyId());
            ps.setString    (2, v.getName());
            ps.setString    (3, v.getPlateNumber());
            ps.setInt       (4, v.getCapacity());
            ps.setString    (5, v.getImage());
            ps.setString    (6, v.getDetailInfo());
            ps.setInt(7, v.getRentalPrice());
            ps.setString (8, v.getRegistrationDate());
            ps.executeUpdate();
        }
    }

    // UPDATE & DELETE
    
    /** 조건식 기반 수정 */
    @Override
    public int updateByCondition(Map<String, Object> newValues, String condition) throws SQLException {
        if (newValues.isEmpty() || condition == null || condition.trim().isEmpty()) {
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
        // 2) 최종 SQL
        String sql = "UPDATE camping_car SET " + setClause + " WHERE " + condition;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            return ps.executeUpdate();
        }
    }
    
    /*
     * @param condition SQL where 절 바로 뒤에 붙일 조건 문자열 (예: rental_price < 50000 and capacity >= 4)
     */
	@Override
	public Map<String, Integer> deleteByCondition(String condition) throws SQLException {
		if (condition == null || condition.trim().isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Integer> deletedCounts = new LinkedHashMap<>();
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1) 삭제 대상 camping_car.id 목록 조회
                String sqlSelectIds = "SELECT id FROM camping_car WHERE " + condition;
                List<Long> idsToDelete = new ArrayList<>();
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery(sqlSelectIds)) {
                    while (rs.next()) {
                        idsToDelete.add(rs.getLong("id"));
                    }
                }
                if (idsToDelete.isEmpty()) {
                    conn.rollback();
                    return Collections.emptyMap();
                }

                // 2-1) 자식 테이블 external_maintenance 부터 삭제
                String sqlDelExt = "DELETE FROM external_maintenance WHERE car_id = ?";
                int countExt = 0;
                try (PreparedStatement psExt = conn.prepareStatement(sqlDelExt)) {
                    for (Long carId : idsToDelete) {
                        psExt.setLong(1, carId);
                        countExt += psExt.executeUpdate();
                    }
                }
                deletedCounts.put("external_maintenance", countExt);

                // 2-2) internal_maintenance 삭제
                String sqlDelInt = "DELETE FROM internal_maintenance WHERE car_id = ?";
                int countInt = 0;
                try (PreparedStatement psInt = conn.prepareStatement(sqlDelInt)) {
                    for (Long carId : idsToDelete) {
                        psInt.setLong(1, carId);
                        countInt += psInt.executeUpdate();
                    }
                }
                deletedCounts.put("internal_maintenance", countInt);

                // 2-3) rental 삭제
                String sqlDelRent = "DELETE FROM rental WHERE car_id = ?";
                int countRent = 0;
                try (PreparedStatement psRent = conn.prepareStatement(sqlDelRent)) {
                    for (Long carId : idsToDelete) {
                        psRent.setLong(1, carId);
                        countRent += psRent.executeUpdate();
                    }
                }
                deletedCounts.put("rental", countRent);

                // 3) 부모 테이블(camping_car)에서 삭제
                String sqlDelCar = "DELETE FROM camping_car WHERE id = ?";
                int countCar = 0;
                try (PreparedStatement psCar = conn.prepareStatement(sqlDelCar)) {
                    for (Long carId : idsToDelete) {
                        psCar.setLong(1, carId);
                        countCar += psCar.executeUpdate();
                    }
                }
                deletedCounts.put("camping_car", countCar);

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

    // JTable 헬퍼
    @Override 
    public String[] getColumnNames() {
        return new String[]{
            "ID","회사","이름","번호판","인원","가격","등록일"
        };
    }
    @Override 
    public Object[] toRow(CampingCar c) {
        return new Object[]{
            c.getId(), c.getCompanyId(), c.getName(), c.getPlateNumber(),
            c.getCapacity(), c.getRentalPrice(), c.getRegistrationDate()
        };
    }
    
    /*
    @param plate number 문자열
    @return 동일한 plate number을 가진 차가 DB에 하나라도 있으면 true, 없으면 false
    */
   public boolean existsByPlateNumber(String plateNumber) throws SQLException {
       String sql = "SELECT COUNT(*) FROM customer WHERE plate_number = ?";
       try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
           ps.setString(1, plateNumber.trim());
           try (ResultSet rs = ps.executeQuery()) {
               if (rs.next()) {
                   return rs.getInt(1) > 0;
               }
           }
       }
       return false;
   }

    /* ---------- ResultSet→Entity ---------- */
    private CampingCar map(ResultSet r) throws SQLException {
        CampingCar c = new CampingCar();
        c.setId              (r.getLong ("id"));
        c.setCompanyId       (r.getLong ("company_id"));
        c.setName            (r.getString("name"));
        c.setPlateNumber     (r.getString("plate_number"));
        c.setCapacity        (r.getInt   ("capacity"));
        c.setImage           (r.getString("image"));
        c.setDetailInfo      (r.getString("detail_info"));
        c.setRentalPrice     (r.getInt("rental_price"));
        c.setRegistrationDate(r.getString("registration_date"));
        return c;
    }
}
