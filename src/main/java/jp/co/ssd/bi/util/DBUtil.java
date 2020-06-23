package jp.co.ssd.bi.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DBUtil {
	
	@Value("${jp.co.sdd.bi.url}")
	private String url;
	@Value("${jp.co.sdd.bi.username}")
    private String username;
	@Value("${jp.co.sdd.bi.password}")
    private String password;
	private Connection connection = null;

	public Connection getConn() {
        try {
            Class.forName("org.postgresql.Driver").newInstance();
            connection = DriverManager.getConnection(url, username, password);
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return connection;
    }
	
	public void queryUpdate(Connection conn, String sql) throws Exception {
        PreparedStatement pStatement = null;
        pStatement = conn.prepareStatement(sql);
        pStatement.executeUpdate();
    }	
}
