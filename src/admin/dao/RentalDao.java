package admin.dao;

import global.db.DBConnection;
import global.entity.Rental;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalDao implements CrudDao<Rental> {

    @Override
    public List<Rental> findAll() throws SQLException {
        String sql = "SELECT * FROM rental ORDER BY id";
        List<Rental> list = new ArrayList<>();
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
    public void insert(Rental v) throws SQLException {
        String sql = "INSERT INTO rental (car_id,customer_id,license_number,company_id,start_date,return_date,rental_days,rental_fee,fee_due_date,extra_details,extra_fee) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, v.getCarId());
            ps.setLong(2, v.getCustomerId());
            ps.setString(3, v.getLicenseNumber());
            ps.setLong(4, v.getCompanyId());
            ps.setTimestamp(5, Timestamp.valueOf(v.getStartDate()));
            ps.setTimestamp(6, Timestamp.valueOf(v.getReturnDate()));
            ps.setInt(7, v.getRentalDays());
            ps.setDouble(8, v.getRentalFee());
            ps.setTimestamp(9, Timestamp.valueOf(v.getFeeDueDate()));
            ps.setString(10, v.getExtraDetails());
            ps.setDouble(11, v.getExtraFee());
            ps.executeUpdate();
        }
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{
            "ID","Car ID","Customer ID","License Number","Company ID",  
            "Start Date","Return Date","Rental Days","Rental Fee","Fee Due Date","Extra Details","Extra Fee"
        };
    }

    @Override
    public Object[] toRow(Rental r) {
        return new Object[]{
            r.getId(), r.getCarId(), r.getCustomerId(), r.getLicenseNumber(), r.getCompanyId(),
            r.getStartDate(), r.getReturnDate(), r.getRentalDays(), r.getRentalFee(), r.getFeeDueDate(), r.getExtraDetails(), r.getExtraFee()
        };
    }

    private Rental map(ResultSet r) throws SQLException {
        Rental rnt = new Rental();
        rnt.setId(r.getLong("id"));
        rnt.setCarId(r.getLong("car_id"));
        rnt.setCustomerId(r.getLong("customer_id"));
        rnt.setLicenseNumber(r.getString("license_number"));
        rnt.setCompanyId(r.getLong("company_id"));
        Timestamp startD = r.getTimestamp("start_date");
        if(startD != null) rnt.setStartDate(startD.toLocalDateTime());
        Timestamp returnD = r.getTimestamp("return_date");
        if(returnD != null) rnt.setReturnDate(returnD.toLocalDateTime());
        rnt.setRentalDays(r.getInt("rental_days"));
        rnt.setRentalFee(r.getDouble("rental_fee"));
        Timestamp due = r.getTimestamp("fee_due_date");
        if(due != null) rnt.setFeeDueDate(due.toLocalDateTime());
        rnt.setExtraDetails(r.getString("extra_details"));
        rnt.setExtraFee(r.getDouble("extra_fee"));
        return rnt;
    }

	@Override
	public void update(Rental t) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Long id) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}
