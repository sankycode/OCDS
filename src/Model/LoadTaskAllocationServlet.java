package Model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/LoadTaskAllocationServlet")
public class LoadTaskAllocationServlet extends HttpServlet {
	
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

        request.setAttribute("TAList", new ArrayList<TaskAllocationModel>());
        
		request.getRequestDispatcher("TaskAllocation.jsp").forward(request, response);
	}
}
