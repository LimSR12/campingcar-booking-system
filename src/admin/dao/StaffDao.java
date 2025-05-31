package admin.dao;

import global.db.DBConnection;
import global.entity.Staff;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDao implements CrudDao<Staff> {

    @Override
    public List<Staff> findAll() throws SQLException {
        String sql = "SELECT * FROM staff ORDER BY id";
        List<Staff> list = new ArrayList<>();
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
    public void insert(Staff v) throws SQLException {
        String sql = "INSERT INTO staff (name,phone,address,salary,family_num,department,role) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getName());
            ps.setString(2, v.getPhone());
            ps.setString(3, v.getAddress());
            ps.setDouble(4, v.getSalary());
            ps.setInt(5, v.getFamilyNum());
            ps.setString(6, v.getDepartment());
            ps.setString(7, v.getRole());
            ps.executeUpdate();
        }
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"ID","Name","Phone","Address","Salary","Family Num","Department","Role"};
    }

    @Override
    public Object[] toRow(Staff s) {
        return new Object[]{
            s.getId(), s.getName(), s.getPhone(), s.getAddress(),
            s.getSalary(), s.getFamilyNum(), s.getDepartment(), s.getRole()
        };
    }

    private Staff map(ResultSet r) throws SQLException {
        Staff s = new Staff();
        s.setId(r.getLong("id"));
        s.setName(r.getString("name"));
        s.setPhone(r.getString("phone"));
        s.setAddress(r.getString("address"));
        s.setSalary(r.getInt("salary"));
        s.setFamilyNum(r.getInt("family_num"));
        s.setDepartment(r.getString("department"));
        s.setRole(r.getString("role"));
        return s;
    }

	@Override
	public void update(Staff t) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Long id) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}