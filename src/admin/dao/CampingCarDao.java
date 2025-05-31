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
    @Override 
    public void update(CampingCar v) throws SQLException { /* TODO */ }
    @Override 
    public void delete(Long id)        throws SQLException { /* TODO */ }

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
