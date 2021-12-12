package driver;

import java.sql.*;

public class Driver {

	public static void main(String[] args) {
		
		//1. Get a connection to database
		
		try {
			
			Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/rmt-domaci2","root","admin");
			

			//2. Create a statement
			
			Statement myStmt = myConn.createStatement();
			
			//3. Execute SQL query
			
			myStmt.executeUpdate("insert into `rmt-domaci2`.appuser values('stefko5','stefanole123','Stefan','Radovic','5259010259','musko','stefan10@gmail.com')");
			
			//4. Process the result set
			/*
			while(myRs.next()) {
				System.out.println(myRs.getString("surname") + ", " + myRs.getString("name"));
			}
			*/
		} catch (SQLException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
	}

}
