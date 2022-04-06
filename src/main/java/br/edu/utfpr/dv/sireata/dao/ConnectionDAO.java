package br.edu.utfpr.dv.sireata.dao;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class ConnectionDAO {
	
	private String SERVER = "192.168.56.20";
	private String DATABASE = "diget";
	private String USER = "mysql";
	private String PASSWORD = "mysql";

	private Connection conn = null;
	private Statement stmt = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	
	private DataSource datasource = null;
	private static ConnectionDAO instance = null;

	private void createDataSource() throws SQLException{
		String user, password, server, database, driver, type; 
		
		try{
			Properties props = new Properties();
	        FileInputStream fis = new FileInputStream(this.getClass().getClassLoader().getResource("/dblocal.properties").getPath());
	        
	        props.load(fis);
	        
	        server = props.getProperty("DB_SERVER");
	        database = props.getProperty("DB_NAME");
	        user = props.getProperty("DB_USERNAME");
	        password = props.getProperty("DB_PASSWORD");
	        driver = props.getProperty("DB_DRIVER_CLASS");
	        type = props.getProperty("DB_TYPE");
		} catch(Exception e){
	    	server = SERVER;
	    	database = DATABASE;
	    	user = USER;
	    	password = PASSWORD;
	    	driver = "com.mysql.jdbc.Driver";
	    	type = "mysql";
		}
		
		PoolProperties p = new PoolProperties();
		p.setUrl("jdbc:" + type + "://" + server + "/" + database);
		p.setDriverClassName(driver);
		p.setUsername(user);
		p.setPassword(password);
		p.setJmxEnabled(true);
		p.setTestWhileIdle(false);
		p.setTestOnBorrow(true);
		p.setValidationQuery("SELECT 1");
		p.setTestOnReturn(false);
		p.setValidationInterval(30000);
		p.setTimeBetweenEvictionRunsMillis(30000);
		p.setMaxActive(1000);
		p.setInitialSize(10);
		p.setMaxWait(10000);
		p.setRemoveAbandonedTimeout(30);
		p.setMinEvictableIdleTimeMillis(30000);
		p.setMinIdle(10);
		p.setLogAbandoned(true);
		p.setRemoveAbandoned(true);
		p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
		
		datasource = new DataSource();
		datasource.setPoolProperties(p);
        
		if(type.equals("mysql")){
			Statement stmt = this.datasource.getConnection().createStatement();
			stmt.execute("SET GLOBAL max_allowed_packet=1024*1024*14;");	
		}
	}

	private Connection openConnection() throws SQLException {
		return ConnectionDAO.getInstance().getConnection();
	}

	private void closeConnection() throws SQLException {
		if((rs != null) && !rs.isClosed())
			rs.close();
		if((stmt != null) && !stmt.isClosed())
			stmt.close();
		if((conn != null) && !conn.isClosed())
			conn.close();

		// clear variables
		conn = null;
		stmt = null;
		pstmt = null;
		rs = null;
	}

	private ConnectionDAO(){}
	
	public static synchronized ConnectionDAO getInstance() throws SQLException{
		if((ConnectionDAO.instance == null) || (ConnectionDAO.instance.datasource == null)){
			ConnectionDAO.instance = new ConnectionDAO();
			ConnectionDAO.instance.createDataSource();
		}
		
		return ConnectionDAO.instance;
	}

	public Connection getConnection() throws SQLException{
		return this.datasource.getConnection();
	}

	public Statement getStatement() {
		return stmt;
	}

	public PreparedStatement getPreparedStatement() {
		return pstmt;
	}

	public ConnectionDAO query() throws SQLException{
		conn = openConnection();
		stmt = conn.createStatement();

		return this;
	}

	public void queryParam(String query) throws SQLException{
		conn = openConnection();
		pstmt = conn.prepareStatement(query);
	}

	public void queryParam(String query, int RETURN_GENERATED_KEYS) throws SQLException{
		conn = openConnection();
		pstmt = conn.prepareStatement(query, RETURN_GENERATED_KEYS);
	}

	public void setInt(int position, int value) throws SQLException{
		pstmt.setInt(position, value);
	}

	public void setString(int position, String value) throws SQLException{
		pstmt.setString(position, value);
	}

	public void setTimestamp(int position, Timestamp timestamp) throws SQLException{
		pstmt.setTimestamp(position, timestamp);
	}

	public void setDate(int position, Date date) throws SQLException{
		pstmt.setDate(position, date);
	}

	public void setBytes(int position, byte[] bytes) throws SQLException{
		pstmt.setBytes(position, bytes);
	}
	
	public ResultSet execute() throws SQLException{
		rs = pstmt.executeQuery();
		closeConnection();

		return rs;
	}

	public ResultSet executeQuery(String query) throws SQLException{
		rs = stmt.executeQuery(query);	
		closeConnection();

		return rs;
	}
}
