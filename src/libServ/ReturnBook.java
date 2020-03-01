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
public class ReturnBook extends HttpServlet {

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
			String sid = request.getParameter("s_id");
			String bid = request.getParameter("b_id");
			
			try{
				Class.forName("oracle.jdbc.driver.OracleDriver");
				Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "system");
				Statement stet = con.createStatement();
				
				ResultSet rss = stet.executeQuery("select * from student where s_id = '" + sid + "'");
				if(!rss.next()){
					stet.close();
					throw new SQLException("Invalid student ID " + sid);
				}
				if(rss.getInt("book_count") == 0){
					stet.close();
					throw new SQLException("This student has no books issued.");
				}
				Statement stet1 = con.createStatement();
				ResultSet rsb = stet1.executeQuery("select * from book where book_id = '" + bid + "'");
				if(!rsb.next()){
					stet.close();
					throw new SQLException("Invalid book ID " + bid);
				}
				if(Long.valueOf(sid, 10) != rsb.getLong("issued_to")){
					stet.close();
					if(rsb.getInt("availability")==1) throw new SQLException("This book is not issued to any student");
					throw new SQLException("This book is not issued to this student");
				}
				
				// updating book and student
				int count = rss.getInt("count") - 1;
				stet.executeUpdate("update student set book_count = " + count + " where s_id = '"+sid+"'");
				stet1.executeUpdate("update book set availability = 1, issued_to = null, issued_by = null where book_id = '" + bid + "'");
				
				// if all goes well
				stet.execute("commit");
				stet.close();
				stet1.close();
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/LibReturnBook.html");
				rd.include(request, response);
				out.println("<script>");
				out.println("document.getElementById(\"form_wrapper\").innerHTML");
				out.println("  = \"<p>Book returned successfully.</p>\"+");
				out.println("\"<a href=\'LibReturnBook.html\'>Back</a>\";");
				out.println("</script>");/**/
				
			}catch(ClassNotFoundException | SQLException e){
				String error = e.toString();
				if(error.contains("java.sql.SQLException: ") ) error = error.substring("java.sql.SQLException: ".length());
				else error = e.toString();
				if(error.contains("\n")) error = error.substring(0, error.indexOf("\n"));
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/LibReturnBook.html");
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
	}

}
