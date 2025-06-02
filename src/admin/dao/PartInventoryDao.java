package admin.dao;

import global.db.DBConnection;
import global.entity.PartInventory;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public int updateByCondition(Map<String,Object> v,String cond)throws SQLException{
        if(v==null||v.isEmpty()||cond==null||cond.isBlank()) return 0;
        StringBuilder sb=new StringBuilder(); List<Object> ps=new ArrayList<>();
        for(var e:v.entrySet()){ if(sb.length()>0) sb.append(", "); sb.append(e.getKey()).append("=?"); ps.add(e.getValue());}
        String sql="UPDATE part_inventory SET "+sb+" WHERE "+cond;
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)){
            for(int i=0;i<ps.size();i++) p.setObject(i+1,ps.get(i));
            return p.executeUpdate();
        }
    }

    /** part_inventory 삭제 시 → 자식 internal_maintenance 먼저, 그 다음 본 테이블 */
    @Override
    public Map<String,Integer> deleteByCondition(String cond)throws SQLException{
        if(cond==null||cond.isBlank()) return Collections.emptyMap();
        Map<String,Integer> out=new LinkedHashMap<>();
        try(Connection conn=DBConnection.getConnection()){
            conn.setAutoCommit(false);
            try{
                // 대상 part id
                List<Long> ids=new ArrayList<>();
                try(Statement st=conn.createStatement();
                    ResultSet rs=st.executeQuery("SELECT id FROM part_inventory WHERE "+cond)){
                    while(rs.next()) ids.add(rs.getLong("id"));
                }
                if(ids.isEmpty()){ conn.rollback(); return Collections.emptyMap(); }

                // 자식 internal_maintenance
                int delIM=0;
                try(PreparedStatement ps=conn.prepareStatement(
                        "DELETE FROM internal_maintenance WHERE part_id=?")){
                    for(Long id:ids){ ps.setLong(1,id); delIM+=ps.executeUpdate(); }
                }
                out.put("internal_maintenance",delIM);

                // 부모 part_inventory
                int delPart=0;
                try(PreparedStatement ps=conn.prepareStatement(
                        "DELETE FROM part_inventory WHERE id=?")){
                    for(Long id:ids){ ps.setLong(1,id); delPart+=ps.executeUpdate(); }
                }
                out.put("part_inventory",delPart);

                conn.commit(); return out;
            }catch(SQLException ex){ conn.rollback(); throw ex; }
            finally{ conn.setAutoCommit(true); }
        }
    }
    
    public PartInventory findById(long partId) throws SQLException {
        String sql = "SELECT * FROM part_inventory WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, partId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;   // map(...)은 이미 있는 private 메서드
            }
        }
    }
}