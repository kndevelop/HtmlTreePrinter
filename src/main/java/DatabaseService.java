package HtmlTreePrinter;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import java.sql.*;
import java.util.*;

@Slf4j
public class DatabaseService {
    
    private HikariDataSource dataSource;
    
    /**
     * DB接続初期化
     * @param host DB ホスト
     * @param port DB ポート
     * @param database データベース名
     * @param user ユーザー名
     * @param password パスワード
     */
    public void initialize(String host, int port, String database, String user, String password) {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(String.format("jdbc:postgresql://%s:%d/%s", host, port, database));
            config.setUsername(user);
            config.setPassword(password);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            
            this.dataSource = new HikariDataSource(config);
            log.info("Database connection pool initialized");
        } catch (Exception e) {
            log.error("Failed to initialize database connection", e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * テーブルからすべてのレコードを取得
     * @param tableName テーブル名
     * @return List of Maps（カラム名→値）
     */
    public List<Map<String, Object>> fetchAll(String tableName) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            ResultSetMetaData metadata = rs.getMetaData();
            int columnCount = metadata.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metadata.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }
            log.info("Fetched {} records from {}", results.size(), tableName);
        } catch (SQLException e) {
            log.error("Error fetching data from {}", tableName, e);
        }
        
        return results;
    }
    
    /**
     * WHERE条件でレコード取得
     * @param tableName テーブル名
     * @param whereClause WHERE句（例："id = ? AND status = ?"）
     * @param params パラメータ値
     * @return List of Maps
     */
    public List<Map<String, Object>> fetchWhere(String tableName, String whereClause, Object... params) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + " WHERE " + whereClause;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metadata = rs.getMetaData();
                int columnCount = metadata.getColumnCount();
                
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metadata.getColumnName(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    results.add(row);
                }
                log.info("Fetched {} records from {} with condition", results.size(), tableName);
            }
        } catch (SQLException e) {
            log.error("Error fetching data from {} with condition", tableName, e);
        }
        
        return results;
    }
    
    /**
     * カスタムクエリ実行
     */
    public List<Map<String, Object>> executeQuery(String sql, Object... params) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metadata = rs.getMetaData();
                int columnCount = metadata.getColumnCount();
                
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metadata.getColumnName(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            log.error("Error executing custom query", e);
        }
        
        return results;
    }
    
    /**
     * 接続クローズ
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("Database connection pool closed");
        }
    }
}
