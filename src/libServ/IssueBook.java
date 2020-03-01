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
public class IssueBook extends HttpServlet {

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
			/* TODO output your page here. You may use following sample code. */
			String b_id = request.getParameter("b_id");
			String s_id = request.getParameter("s_id");
			String user = request.getParameter("user");
			boolean validb = false, valids = false;
			
			try{
				Class.forName("oracle.jdbc.driver.OracleDriver");
				Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "system");
				
				Statement stet = con.createStatement();
				
				ResultSet rsb = stet.executeQuery("select * from book where book_id = '" + b_id + "'");
				
				if(rsb != null && rsb.next()){ // if book exists in database
					if(rsb.getInt("availability")== 1){
						validb = true;
					} else throw new SQLException("Book is already issued.");
				}else throw new SQLException("Book does not exist."); // if book does not exist
				
				ResultSet rss = stet.executeQuery("select * from student where s_id = '" + s_id + "'");
				
				if(rss != null && rss.next()){
					if(rss.getInt("book_count") < rss.getInt("quota")){
						valids = true;
					}else throw new SQLException("Maximum number of books have been issued.");
				}else throw new SQLException("Student <strong>ID</strong> does not exist.");
				
				if(valids && validb){
					//setting book_count of student
					int c = stet.executeUpdate("update student set book_count = " + (rss.getInt("book_count")+1) +
							" where s_id = " + s_id);
					if(c == 1){
						// setting book availability
						c = stet.executeUpdate("update book set availability = 0, issued_to = " + s_id
								+ ", issued_by = '" + user + "'" + " where book_id = " + b_id);
						if(c == 0){
							stet.executeUpdate("rollback");
							throw new SQLException("Some error(2) occured " + c + ".");
						}
						// if all goes well
						stet.executeUpdate("commit");
						stet.close();
						RequestDispatcher rd = getServletContext().getRequestDispatcher("/LibIssueBook.html");
						rd.include(request, response);
						out.print("<script>document.getElementById(\"form_wrapper\").innerHTML = ");
						out.print("\"<p>Book(ID "+ b_id +") has been issued successfully to student(ID "+ s_id +").</p>\" + ");
						out.print("\"<a href='LibIssueBook.html'>Back</a>\"</script>");
					}else throw new SQLException("Some error(1) occured.");
				}
				
			}catch(ClassNotFoundException | SQLException e){
				String error = e.toString();
				if(error.contains("java.sql.SQLException: ") ) error = error.substring("java.sql.SQLException: ".length());
				else error = e.toString();
				
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/LibIssueBook.html");
				rd.include(request, response);
				out.println("<script>document.getElementById('error').innerHTML = '" + error + "';</script>");
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
