package admin.dao;

import global.db.DBConnection;
import global.entity.Staff;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public int updateByCondition(Map<String,Object> v,String cond)throws SQLException{
        if(v==null||v.isEmpty()||cond==null||cond.isBlank()) return 0;
        StringBuilder sb=new StringBuilder(); List<Object> ps=new ArrayList<>();
        for(var e:v.entrySet()){ if(sb.length()>0) sb.append(", "); sb.append(e.getKey()).append("=?"); ps.add(e.getValue());}
        String sql="UPDATE staff SET "+sb+" WHERE "+cond;
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)){
            for(int i=0;i<ps.size();i++) p.setObject(i+1,ps.get(i));
            return p.executeUpdate();
        }
    }

    /** staff 삭제 시 → 자식 internal_maintenance 먼저 */
    @Override
    public Map<String,Integer> deleteByCondition(String cond)throws SQLException{
        if(cond==null||cond.isBlank()) return Collections.emptyMap();
        Map<String,Integer> out=new LinkedHashMap<>();
        try(Connection conn=DBConnection.getConnection()){
            conn.setAutoCommit(false);
            try{
                // 대상 staff id
                List<Long> ids=new ArrayList<>();
                try(Statement st=conn.createStatement();
                    ResultSet rs=st.executeQuery("SELECT id FROM staff WHERE "+cond)){
                    while(rs.next()) ids.add(rs.getLong("id"));
                }
                if(ids.isEmpty()){ conn.rollback(); return Collections.emptyMap(); }

                // 자식 internal_maintenance
                int delIM=0;
                try(PreparedStatement ps=conn.prepareStatement(
                        "DELETE FROM internal_maintenance WHERE staff_id=?")){
                    for(Long id:ids){ ps.setLong(1,id); delIM+=ps.executeUpdate(); }
                }
                out.put("internal_maintenance",delIM);

                // 부모 staff
                int delStaff=0;
                try(PreparedStatement ps=conn.prepareStatement(
                        "DELETE FROM staff WHERE id=?")){
                    for(Long id:ids){ ps.setLong(1,id); delStaff+=ps.executeUpdate(); }
                }
                out.put("staff",delStaff);

                conn.commit(); return out;
            }catch(SQLException ex){ conn.rollback(); throw ex; }
            finally{ conn.setAutoCommit(true);}
        }
    }
}