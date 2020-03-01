package libServ;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import javax.servlet.RequestDispatcher;
/**
 *
 * @author Jitendra
 */
public class CreateStuAc extends HttpServlet {

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		
		try (PrintWriter out = response.getWriter()) {
			String name   = request.getParameter("name");
			String gender = request.getParameter("gender");
			String branch = request.getParameter("branch");
			String quota  = request.getParameter("quota");
			try{
				Class.forName("oracle.jdbc.driver.OracleDriver");
				Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "system");
				
				String query = "insert into student values(s_seq.NextVal,'" + 
						name + "',sysdate,'" + branch + "'," + quota + ",0,'" + gender + "')";
				
				PreparedStatement ps = con.prepareStatement(query, new String[]{"s_id"});
				Long s_id = null;
				
				// caution 
				// getGeneratedKeys() return only AUTO GENERATED KEYS
				// such as generated from sequence
				// the data type of these keys is "Long"
				
				if(ps.executeUpdate() > 0){
					ResultSet rs = ps.getGeneratedKeys();
					if(rs != null && rs.next()){
						s_id = rs.getLong(1);
					}
					else throw new SQLException("Some error occured.");
				}else	throw new SQLException("Some error occured.");
				// if all goes well
				ps = con.prepareStatement("commit");
				ps.execute();
				ps.close();
				// sending confirmation
				/*
				Statement stet = con.createStatement();
				ResultSet rs = stet.executeQuery("select s_id, name from student where s_id = " + s_id);/**/
				
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/LibCreateStudentAc.html");
				rd.include(request, response);
				out.print("<script>document.getElementById(\"form_wrapper\").innerHTML = \"<p>The student account ");
				
				out.print("of <strong>" + name + "</strong> with <em>ID</em> <strong>" + s_id + "</strong>");
				out.print(" has been created.</p><p><a href=\'LibCreateStudentAc.html\'>Back</a></p>\"</script>");
				
			}catch(ClassNotFoundException | SQLException e){
				String error = e.toString();
				if(error.contains("java.sql.SQLException: ") ) error = error.substring("java.sql.SQLException: ".length());
				else error = e.toString();
				
				if(error.contains("\n")){
					error = error.substring(0, error.indexOf('\n'));
				}
				
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/LibCreateStudentAc.html");
				rd.include(request, response);
				out.println("<script>document.getElementById(\"error\").innerHTML = ");
				out.println("'" + error + "';</script>");
			}
		}
	}

	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}

}
