/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package libServ;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import java.sql.*;
/**
 *
 * @author Jitendra
 */
public class StBookDt extends HttpServlet {

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
			String id = request.getParameter("id");
			String dtl = request.getParameter("d");
			String male = "m";
			
			/* TODO output your page here. You may use following sample code. */
			try{
				Class.forName("oracle.jdbc.driver.OracleDriver");
				Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "system");
				Statement stet = con.createStatement();
				
				ResultSet rs;
				if(dtl.compareTo("st") == 0){
					rs = stet.executeQuery("select * from student where s_id="+id);
					if(rs.next()){
						RequestDispatcher rd = getServletContext().getRequestDispatcher("/StBookDetails.html");
						rd.include(request, response);
						out.print("<script>");
						out.print("document.getElementById(\"form_wrapper\").innerHTML = ");
						out.print("'<table><tbody><tr>");
						out.print("<td>ID</td><td>" + rs.getInt(1) + "</td></tr><tr>");
						out.print("<td>Name</td><td>"+rs.getString(2)+"</td></tr><tr>");
						out.print("<td>Joining date</td><td>"+rs.getDate(3)+"</td></tr><tr>");
						out.print("<td>Branch</td><td>"+rs.getString(4)+"</td></tr><tr>");
						out.print("<td>Quota</td><td>"+rs.getInt(5)+"</td></tr><tr>");
						out.print("<td>Book Count</td><td>"+rs.getInt(6)+"</td></tr><tr>");
						if(male.compareTo((rs.getString(7))) == 0)
							out.print("<td>Gender</td><td>Male</td></tr><tr>");
						else
							out.print("<td>Gender</td><td>Female</td></tr><tr>");
						out.print("<td colspan=\"2\"><a href=\"StBookDetails.html\">Back</a></td></tr><tr>");
						out.print("</tr></tbody></table>';</script>");
					}else{
						stet.close();
						throw new SQLException("This student ID does not exist.");
					}
				}
				else if(dtl.equals("bo")){
					rs = stet.executeQuery("select * from book where book_id = " + id);
					if(rs.next()){
						RequestDispatcher rd = getServletContext().getRequestDispatcher("/StBookDetails.html");
						rd.include(request, response);
						out.print("<script>");
						out.print("document.getElementById(\"form_wrapper\").innerHTML = ");
						out.print("'<table><tbody><tr>");
						out.print("<td>Book ID</td><td>"+rs.getInt(1)+"</td></tr><tr>");
						out.print("<td>Title</td><td>"+rs.getString(2)+"</td></tr><tr>");
						out.print("<td>Author</td><td>"+rs.getString(3)+"</td></tr><tr>");
						out.print("<td>ISBN</td><td>"+rs.getString(4)+"</td></tr><tr>");
						out.print("<td>Year of publishing</td><td>"+rs.getInt(5)+"</td></tr><tr>");
						out.print("<td>Publisher</td><td>"+rs.getString(6)+"</td></tr><tr>");
						if(rs.getInt(7) == 1)
							out.print("<td>Availability</td><td>Available</td></tr><tr>");
						else
							out.print("<td>Availability</td><td>Issued</td></tr><tr>");
						
						String issuedto;
						String issuedby = rs.getString(9);
						if(rs.getInt(8) == 0) issuedto = "none";
						else issuedto = Long.toString(rs.getInt(8));
						if(rs.getString(9) == null) issuedby = "none";
							
						out.print("<td>Issued to</td><td>"+issuedto+"</td></tr><tr>");
						out.print("<td>Issued by</td><td>"+issuedby+"</td></tr><tr>");
						out.print("<td colspan=\"2\"><a href=\"StBookDetails.html\">Back</a></td>");
						out.print("</tr></tbody></table>'</script>");
					}else{
						throw new SQLException("This book ID does not exist.");
					}
				}
				else{
					stet.close();
					throw new SQLException("Some error(2) occured.");
				}
			
			}catch(ClassNotFoundException | SQLException e){
				String error = e.toString();
				if(error.contains("java.sql.SQLException: ") ) error = error.substring("java.sql.SQLException: ".length());
				else error = e.toString();
				if(error.contains("\n")) error = error.substring(0, error.indexOf("\n"));
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/StBookDetails.html");
				rd.include(request, response);
				out.println("<script>document.getElementById('error').innerHTML = '" + error + "';</script>");
			}
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
	}// </editor-fold>

}
