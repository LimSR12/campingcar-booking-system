package admin.dao;

import global.db.DBConnection;
import global.entity.PartInventory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartInventoryDao implements CrudDao<PartInventory> {

    @Override
    public List<PartInventory> findAll() throws SQLException {
        String sql = "SELECT * FROM part_inventory ORDER BY id";
        List<PartInventory> list = new ArrayList<>();
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
    public void insert(PartInventory v) throws SQLException {
        String sql = "INSERT INTO part_inventory (name,price,quantity,received_date,supplier_name) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getName());
            ps.setDouble(2, v.getPrice());
            ps.setInt(3, v.getQuantity());
            ps.setTimestamp(4, Timestamp.valueOf(v.getReceivedDate()));
            ps.setString(5, v.getSupplierName());
            ps.executeUpdate();
        }
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"ID","Name","Price","Quantity","Received Date","Supplier Name"};
    }

    @Override
    public Object[] toRow(PartInventory p) {
        return new Object[]{
            p.getId(), p.getName(), p.getPrice(),
            p.getQuantity(), p.getReceivedDate(), p.getSupplierName()
        };
    }

    private PartInventory map(ResultSet r) throws SQLException {
        PartInventory p = new PartInventory();
        p.setId(r.getLong("id"));
        p.setName(r.getString("name"));
        p.setPrice(r.getDouble("price"));
        p.setQuantity(r.getInt("quantity"));
        Timestamp ts = r.getTimestamp("received_date");
        if (ts != null) p.setReceivedDate(ts.toLocalDateTime());
        p.setSupplierName(r.getString("supplier_name"));
        return p;
    }

	@Override
	public void update(PartInventory t) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Long id) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}