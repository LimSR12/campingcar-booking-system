package admin.dao;

import global.db.DBConnection;
import global.entity.ExternalCenter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
	public void update(ExternalCenter t) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Long id) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}