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
public class CreateLbr extends HttpServlet {

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
			String newun = request.getParameter("new_username");
			String pw =    request.getParameter("pw");
			String user =  request.getParameter("user");
			
			try{
				// step 1: load oracle driver
				Class.forName("oracle.jdbc.driver.OracleDriver");

				// step 2: create connection
				Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "system");

				// step 3: create statement
				Statement stet = con.createStatement();

				// step 4: execute query
				// checking whether user has admin priviledges
				String query = "select * from librarian where l_id = '" + user + "'";
				ResultSet rs = stet.executeQuery(query);
				if(rs.next()){
					if(rs.getString(3).equals("y")){
						query = "select * from librarian where l_id = '" + newun + "'";
						rs = stet.executeQuery(query);
						
						if(rs.next()){
							throw new SQLException("username already taken");
						}else{
							// if username is genuine
							// creatin account
							query = "insert into librarian values('" + newun + "','" + pw + "', 'n')";
							rs = stet.executeQuery(query);
							
							// sending confirmation
							RequestDispatcher rd = getServletContext().getRequestDispatcher("/LibCreateLibrarianAc.html");
							rd.include(request, response);
							out.println("<script>\ndocument.getElementById(\"form_wrapper\").innerHTML = \"<p>Librarian"
									+ " Accouont with username <strong>\"+\""+ newun +"\"+\"</strong> has been created successfully.</p>\";</script>");
							// if all goes well
							stet.execute("commit");
							stet.close();
						}
					}else{
						throw new SQLException("You do not have sufficient privilege to create <em>Librarian Account</em>.");
					}
				}else{
					throw new SQLException("Some error occured. Please try again.");
				}

			}catch(ClassNotFoundException | SQLException e){
				String error = e.toString();
				if(error.contains("java.sql.SQLException: ") ) error = error.substring("java.sql.SQLException: ".length());
				else error = e.toString();
				
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/LibCreateLibrarianAc.html");
				rd.include(request, response);
				out.println("<script>document.getElementById('error').innerHTML = '" + error + "';\n" +
"document.getElementById(\"new_username\").value = '" + newun + "';</script>");
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
