package admin.dao;

import java.sql.SQLException;
import java.util.List;

public interface CrudDao<T> {
    List<T> findAll()     throws SQLException;
    void    insert(T t)   throws SQLException;
    void    update(T t)   throws SQLException;
    void    delete(Long id) throws SQLException;

    String[] getColumnNames(); // JTable 헤더
    Object[] toRow(T t);       // 엔티티 → JTable 한 줄
}
