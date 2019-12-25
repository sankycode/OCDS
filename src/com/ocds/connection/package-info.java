package com.ocds.connection;

import java.sql.*;

class MysqlCon{
	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/ocds","root","admin");
			// here the database name ocds, root is the username and admin is the password
			Statement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select * from login");
			while(rs.next())
				System.out.println(rs.getInt(1)+" "+rs.getString(2)+" "+rs.getString(3));
			con.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		//https://youtu.be/3o0lQkyNuGo
	}
	
}