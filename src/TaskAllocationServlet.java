import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import Model.Status;
import Model.TaskAllocationModel;

@WebServlet("/TaskAllocationServlet")
public class TaskAllocationServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        try {
        	String idtaskAllocation=request.getParameter("idtaskAllocation");
            String TileID=request.getParameter("TileID");
        	String FeatureID=request.getParameter("FeatureID");
        	String KMs=request.getParameter("KMs");
        	String status=request.getParameter("status");
        	
        	System.out.println("----- " + idtaskAllocation);
        	
        	Class.forName("com.mysql.jdbc.Driver");

            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/shan", "shan", "K6P_BQz5SmEy#.j"); // gets a new connection
            
            LocalDate date = LocalDate.now();
            
            if(status.compareTo("RFQC")==0) {
	            PreparedStatement ps = c.prepareStatement("update taskallocation set KMs=?,Status=?,SubTask=?,CoderUserID=?,CodingDate=? where idtaskAllocation=?");
	            
	            ps.setString(1, KMs);
	            ps.setString(2, status);
	            ps.setString(3, "QC");
	            ps.setString(4, "coderuserid");
	            ps.setString(5, date.toString());
	            ps.setString(6, idtaskAllocation);
	
	            ps.executeUpdate();
            }else {
            	if(status.compareTo("PASS")==0) {
	            	PreparedStatement ps = c.prepareStatement("update taskallocation set KMs=?,Status=?,SubTask=?,QCerUserID=?,QCDate=? where idtaskAllocation=?");
		            
		            ps.setString(1, KMs);
		            ps.setString(2, status);
		            ps.setString(3, "Completed");
		            ps.setString(4, "qcuserid");
		            ps.setString(5, date.toString());
		            ps.setString(6, idtaskAllocation);
		
		            ps.executeUpdate();
            	}else {
            		if(status.compareTo("FAIL")==0) {
    	            	PreparedStatement ps = c.prepareStatement("update taskallocation set KMs=?,Status=?,SubTask=?,QCerUserID=?,QCDate=? where idtaskAllocation=?");
    		            
    		            ps.setString(1, KMs);
    		            ps.setString(2, status);
    		            ps.setString(3, "Coding");
    		            ps.setString(4, "qcuserid");
    		            ps.setString(5, date.toString());
    		            ps.setString(6, idtaskAllocation);
    		
    		            ps.executeUpdate();
                	}else {
	            		PreparedStatement ps = c.prepareStatement("update taskallocation set KMs=?,Status=?,SubTask=?,CoderUserID=?,CodingDate=? where idtaskAllocation=?");
	     	            
	     	            ps.setString(1, KMs);
	     	            ps.setString(2, status);
	     	            ps.setString(3, "QC");
	     	            ps.setString(4, "coderuserid");
	     	            ps.setString(5, date.toString());
	     	            ps.setString(6, idtaskAllocation);
	     	
	     	            ps.executeUpdate();
                	}
            	}
            }
            
        }catch (Exception e) {
			// TODO: handle exception
		}
	}
	
    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        try {
        	String projectTag=request.getParameter("ProjectTag");
            String subTask=request.getParameter("SubTask");
        	String user=request.getParameter("User");
            String status="Assigned";
            int assignedTaskSize = 5;
                        
            int prvAssignedTaskSize = getPrvAssingedTask(projectTag, subTask, user, status);
            
            Class.forName("com.mysql.jdbc.Driver");

            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/shan", "shan", "K6P_BQz5SmEy#.j"); // gets a new connection

            PreparedStatement ps = c.prepareStatement("update taskallocation set User=?,Status=? where idtaskAllocation IN (select * from (select idtaskAllocation FROM taskallocation where ProjectTag=? and SubTask=? LIMIT " + (assignedTaskSize-prvAssignedTaskSize) + ") as ta) ");
            
            ps.setString(1, user);
            ps.setString(2, status);
            ps.setString(3, projectTag);
            ps.setString(4, subTask);

            ps.executeUpdate();
            
            ArrayList<Status> arrayListStatus = new ArrayList<Status>();
            
            Status statusModelAssigned = new Status();
            statusModelAssigned.status = "Assigned";

            arrayListStatus.add(statusModelAssigned);
            
            Status statusModelRFQC = new Status();
            statusModelRFQC.status = "RFQC";

            arrayListStatus.add(statusModelRFQC);
            
            Status statusModelPASS = new Status();
            statusModelPASS.status = "PASS";

            arrayListStatus.add(statusModelPASS);
            
            Status statusModelFAIL = new Status();
            statusModelFAIL.status = "FAIL";

            arrayListStatus.add(statusModelFAIL);
            
            Status statusModelBlocked = new Status();
            statusModelBlocked.status = "Blocked";

            arrayListStatus.add(statusModelBlocked);
            
            request.setAttribute("StatusList", arrayListStatus);

            request.setAttribute("TAList", getAssingedTask(projectTag, subTask, user, status));
            
            request.getRequestDispatcher("TaskAllocation.jsp").forward(request, response);  
            return;
        } catch (ClassNotFoundException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private int getPrvAssingedTask(String projectTag,String subTask,String user,String status){
    	try {
            Class.forName("com.mysql.jdbc.Driver");

            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/shan", "shan", "K6P_BQz5SmEy#.j"); // gets a new connection

            PreparedStatement ps = c.prepareStatement("select COUNT(*) AS rowcount FROM taskallocation where User=? and Status=? and ProjectTag=? and SubTask=?");
            ps.setString(1, user);
            ps.setString(2, status);
            ps.setString(3, projectTag);
            ps.setString(4, subTask);

            ResultSet rs = ps.executeQuery();
            
            rs.next();
            
            int count = rs.getInt("rowcount");
            
            return count;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private ArrayList<TaskAllocationModel> getAssingedTask(String projectTag,String subTask,String user,String status){
    	try {
    		ArrayList<TaskAllocationModel> allocationModels = new ArrayList<TaskAllocationModel>();
    		
            Class.forName("com.mysql.jdbc.Driver");

            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/shan", "shan", "K6P_BQz5SmEy#.j"); // gets a new connection

            PreparedStatement ps = c.prepareStatement("select * FROM taskallocation where User=? and Status=? and ProjectTag=? and SubTask=?");
            ps.setString(1, user);
            ps.setString(2, status);
            ps.setString(3, projectTag);
            ps.setString(4, subTask);

            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
            	TaskAllocationModel allocationModel = new TaskAllocationModel();
            	allocationModel.CoderUserID =rs.getString("CoderUserID");
            	allocationModel.CodingDate =rs.getString("CodingDate");
            	allocationModel.Comments =rs.getString("Comments");
            	allocationModel.Date =rs.getString("Date");
            	allocationModel.FeatureID =rs.getString("FeatureID");
            	allocationModel.idtaskAllocation =rs.getString("idtaskAllocation");
            	allocationModel.KMs =rs.getString("KMs");
            	allocationModel.ProjectTag =rs.getString("ProjectTag");
            	allocationModel.QCDate =rs.getString("QCDate");
            	allocationModel.QCerUserID =rs.getString("QCerUserID");
            	allocationModel.Status =rs.getString("Status");
            	allocationModel.SubTask =rs.getString("SubTask");
            	allocationModel.TileID =rs.getString("TileID");
            	allocationModel.User =rs.getString("User");
            	
            	allocationModels.add(allocationModel);
            }
            
            return allocationModels;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
