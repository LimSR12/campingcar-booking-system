package admin.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface CrudDao<T> {
    List<T> findAll()     throws SQLException;
    void    insert(T t)   throws SQLException;
    int updateByCondition(Map<String, Object> newValues, String condition) throws SQLException;
    Map<String, Integer> deleteByCondition(String condition) throws SQLException;

    String[] getColumnNames(); // JTable 헤더
    Object[] toRow(T t);       // 엔티티 → JTable 한 줄
}
