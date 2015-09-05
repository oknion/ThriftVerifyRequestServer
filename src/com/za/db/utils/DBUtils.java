package com.za.db.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;

import com.za.verify.AppDetail;

public class DBUtils {
	private static Connection conn;
	static String host = "localhost";
	static String dbname = "zanalytics";
	static String username = "root";
	static String password = "123456";

	public DBUtils() {

	}

	public static ConcurrentHashMap<String, AppDetail> getProductIds() {
		Statement stmt = null;
		ResultSet rs = null;
		String query = "SELECT id,url,rpm FROM app WHERE status='enable' ";

		ConcurrentHashMap<String, AppDetail> productIDs = new ConcurrentHashMap<>();
		String connectionString = "jdbc:mysql://" + host + "/" + dbname + "?" + "user=" + username + "&password="
				+ password;
		try {
			conn = DriverManager.getConnection(connectionString);
			stmt = conn.createStatement();

			if (stmt.execute(query)) {
				rs = stmt.getResultSet();
			}
			while (rs.next()) {
				productIDs.put(rs.getString("id"), new AppDetail(rs.getString("url"), rs.getInt("rpm")));
			}
			return productIDs;
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return null;
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				} // ignore

				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				} // ignore

				stmt = null;
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		System.out.println("abc".equals(null));
	}
}
