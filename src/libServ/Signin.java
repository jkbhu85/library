package libServ;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

/**
 *
 * @author Jitendra
 */
public class Signin extends HttpServlet {

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
		
		// getting form data here
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		try (PrintWriter out = response.getWriter()) {
			try{
				// step 1: load the driver class
				Class.forName("oracle.jdbc.driver.OracleDriver");
				try( // step 2: create the connection object
					Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "system")){
					
					// step 3: create the statement object
					Statement stet = con.createStatement();
					
					// step 4: execute query
					ResultSet rs = stet.executeQuery("select * from librarian where l_id = '" + username
							+ "' and l_pw = '" + password+"'");
					
					if(rs.next()){ // rs is not empty
						//response.sendRedirect("/Library/LibHomePage.html");
						out.println("<!DOCTYPE html><head><meta charset='UTF-8'/><title>Redirect page</title>");
						out.println("<meta http-equiv='refresh' content='0;URL=LibHomePage.html' />");
						out.println("<script>function createCookie(name,value,days) {\n" +
"if (days) {\n" +
"var date = new Date();\n" +
"date.setTime(date.getTime()+(days*24*60*60*1000));\n" +
"var expires = \"; expires=\"+date.toGMTString();\n" +
"}\n" +
"else var expires = \"\";\n" +
"document.cookie = name+\"=\"+value+expires+\"; path=/\";}\n"
								+ "createCookie(\"libuser\",\""+ username+"\",1);</script></head>");
						out.println("<body>You are being redirected<br\\> <script>document.write(document.cookie);</script></body></html>");
					}else{
						RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.html");
						rd.include(request, response);
						out.println("<script>document.getElementById('error').innerHTML = 'Either username or password is wrong.'</script>");
						rs.close();
					}
				}
			}catch(ClassNotFoundException | SQLException e){
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.html");
				out.println("<script>document.getElementById('error').innerHTML = '" + e +"'</script>");
				rd.include(request, response);
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
