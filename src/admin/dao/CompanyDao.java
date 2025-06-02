package admin.dao;

import global.db.DBConnection;
import global.entity.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
	public void update(Company t) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Long id) throws SQLException {
		// TODO Auto-generated method stub
		
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