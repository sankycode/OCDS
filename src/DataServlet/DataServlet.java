package DataServlet;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
//import org.json.simple.JSONObject;

//import com.vsat.beans.DataBeann;
//import com.mysql.jdbc.PreparedStatement;
//import com.mysql.jdbc.Statement;
//import com.vsat.beans.DataBeann;
//import com.vsat.beans.DataBae;
//import com.vsat.dao.DBConnection;

public class DataServlet extends HttpServlet {
	private ServletConfig config;
	// Setting JSP page

	String page = "Home.jsp";
	//String userName = request.getParameter("username");
	//String pass = request.getParameter("pass");
	
	// private String mymsg;
	public void init(ServletConfig config) throws ServletException {
		this.config = config;
		System.out.println("inside Init");

	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection con = null;
		try {

			Properties props = new Properties();
			String input = request.getServletContext().getRealPath("/");
			System.out.println("Classpath: " + input);
			FileInputStream in = new FileInputStream(input + "/WEB-INF/dbmysql.properties");
			props.load(in);
			in.close();
			String driver = props.getProperty("jdbc.driver");
			if (driver != null) {
				Class.forName(driver);
			}
			String url = props.getProperty("jdbc.url");
			String username = props.getProperty("jdbc.username");
			String password = props.getProperty("jdbc.password");
			con = DriverManager.getConnection(url, username, password);
			System.out.println("MySQL database successfull");
		} catch (Exception e) {
			// logger.error("Unable to load logging property :");
			System.out.println("MySQL DB Error");
			e.printStackTrace();
		}
		// public static Connection con1 = null;

		/*
		 * String pathmysql = application.getRealPath("/WEB-INF/dbmysql.properties");
		 * String path = application.getRealPath("/WEB-INF/db.properties");
		 */
		/*
		 * PrintWriter out = response.getWriter(); String stat =
		 * request.getParameter("status"); String zonv = request.getParameter("Zone");
		 * String rov = request.getParameter("RO"); System.out.println("vsatstatus: " +
		 * stat + "  Zone: " + zonv + " RO : " + rov);
		 * response.setContentType("text/html"); List<DataBae> dataList = new
		 * ArrayList();
		 */

		try {
			/*
			 * PrintWriter out = response.getWriter(); try{ String action =
			 * request.getParameter("action"); String json = request.getParameter("json");
			 * JSONObject jsonData = (JSONObject) JSONValue.parse(json); String name =
			 * (String)jsonData.get("name"); String password = (String)
			 * jsonData.get("password"); System.out.println(name + password); }
			 * catch(Exception e) { e.printStackTrace();
			 * 
			 * }
			 */
			
			
			
		} catch (Exception ex) {
			System.out.println("Exception is ;" + ex);
		}
		request.setAttribute("data", "cONNECTION sUCCESSFUL");
		// Disptching request

		RequestDispatcher dispatcher = request.getRequestDispatcher(page);
		if (dispatcher != null) {
			System.out.println("Dispatcher not null");
			dispatcher.forward(request, response);
		}

		// RequestDispatcher view =
		// getServletContext().getRequestDispatcher("/DataPage.jsp");
		// view.forward(request,response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		// Leaving empty. Use this if you want to perform
		// something at the end of Servlet life cycle.
	}
}