import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import Model.Status;
import Model.TaskAllocationModel;

@WebServlet("/UploadTaskAllocationServlet")
@MultipartConfig
public class UploadTaskAllocationServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
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
        
		request.setAttribute("TAList", getTask());
		
		request.setAttribute("UploadSuccess", false);
		
		request.getRequestDispatcher("uploadTaskAllocation.jsp").forward(request, response);
	}
	
	private ArrayList<TaskAllocationModel> getTask() {
		try {
    		ArrayList<TaskAllocationModel> allocationModels = new ArrayList<TaskAllocationModel>();
    		
            Class.forName("com.mysql.jdbc.Driver");

            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/shan", "shan", "K6P_BQz5SmEy#.j"); // gets a new connection

            PreparedStatement ps = c.prepareStatement("select * FROM taskallocation");

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
            	System.out.println(allocationModel.Status);;
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
	
	@SuppressWarnings("deprecation")
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		String fileName = null;
		File file = null;
		InputStream inputStream = null;
		for (Part part : request.getParts()) {
            fileName = extractFileName(part);
            // refines the fileName in case it is an absolute path
            inputStream = part.getInputStream();
        }
        
         
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet firstSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = firstSheet.iterator();
        
        ArrayList<TaskAllocationModel> allocationModels = new ArrayList<TaskAllocationModel>();
        
        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            
            if (nextRow.getRowNum() == 0) {
                continue;
            }
            
            int i = 1;
            TaskAllocationModel allocationModel = new TaskAllocationModel();
            
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                cell.setCellType(Cell.CELL_TYPE_STRING);
                
                if(i == 1) {
            		allocationModel.ProjectTag = cell.getStringCellValue();
            	}else {
            		if(i == 2) {
                		allocationModel.SubTask = cell.getStringCellValue();
                	}else {
                		if(i == 3) {                    		
                    		allocationModel.FeatureID = cell.getStringCellValue();
                    	}else {
                    		if(i == 4) {
                        		allocationModel.TileID = cell.getStringCellValue();
                        	}else {
                        		if(i == 5) {
                        			allocationModel.KMs = cell.getStringCellValue();
                            	}else {
                            		if(i == 6) {
                                		allocationModel.Status = cell.getStringCellValue();
                                	}else {
                                		
                                	}
                            	}
                        	}
                    	}                		
                	}
            	}
                i++;
            }
            allocationModels.add(allocationModel);            
        }
        
        try {
			insertTask(allocationModels);
			request.setAttribute("UploadSuccess", true);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("UploadSuccess", false);
		}
        finally {
            workbook.close();
            inputStream.close();
            
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
            
    		request.setAttribute("TAList", getTask());
    		
    		request.getRequestDispatcher("uploadTaskAllocation.jsp").forward(request, response);
		}
    }
	/**
     * Extracts file name from HTTP header content-disposition
     */
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length()-1);
            }
        }
        return "";
    }

    public boolean isCellEmpty(Cell cell) {
        return true;
    }
    
    private void insertTask(ArrayList<TaskAllocationModel> allocationModels) throws SQLException, ClassNotFoundException {
    	Class.forName("com.mysql.jdbc.Driver");

        Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/shan", "shan", "K6P_BQz5SmEy#.j"); // gets a new connection
        
        LocalDate date = LocalDate.now();
        for (TaskAllocationModel taskAllocationModel : allocationModels) {
        	PreparedStatement ps = c.prepareStatement("insert into taskallocation(ProjectTag,SubTask,FeatureID,TileID,KMs,Status) values(?,?,?,?,?,?)");

            ps.setString(1, taskAllocationModel.ProjectTag);
            ps.setString(2, taskAllocationModel.SubTask);
            ps.setString(3, taskAllocationModel.FeatureID);
            ps.setString(4, taskAllocationModel.TileID);
            ps.setString(5, taskAllocationModel.KMs);
            ps.setString(6, taskAllocationModel.Status);

            ps.execute();
		}        
    }
}
