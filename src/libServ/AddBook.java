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
public class AddBook extends HttpServlet {

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
			String title  = request.getParameter("title");
			String author = request.getParameter("author");
			String year   = request.getParameter("year");
			String publisher = request.getParameter("publisher");
			String isbn = request.getParameter("isbn");
			String user = request.getParameter("user");
			
			String query = "insert into book values(b_seq.NextVal, '" + title + "','" + author + "','" + isbn + "'," + year + 
					",'" + publisher + "', 1, null, null)";
			
			try{
				Class.forName("oracle.jdbc.driver.OracleDriver");
				Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "system");
				
				PreparedStatement ps = con.prepareStatement(query, new String[]{"book_id"});
				Long book_id = null;
				
				if(ps.executeUpdate() > 0){
					ResultSet rs = ps.getGeneratedKeys();
					if(rs != null && rs.next()){
						book_id = rs.getLong(1);
					}else throw new SQLException("Some error occured.");
				}else throw new SQLException("Some error occured.");
				
				// if all goes well
				ps = con.prepareStatement("commit");
				ps.execute();
				ps.close();
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/LibAddBook.html");
				rd.include(request, response);
				out.print("<script>document.getElementById(\"form_wrapper\").innerHTML = \"<p>The book with title ");
				
				out.print("<strong>" + title + "</strong> has been add to database with <em>ID</em> <strong>" + book_id + "</strong>");
				out.print(".</p><p><a href=\'LibAddBook.html\'>Back</a></p>\"</script>");
				
			}catch(ClassNotFoundException | SQLException e){
				String error = e.toString();
				if(error.contains("java.sql.SQLException: ") ) error = error.substring("java.sql.SQLException: ".length());
				else error = e.toString();
				
				if(error.contains("\n")){
					error = error.substring(0, error.indexOf('\n'));
				}
			
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/LibAddBook.html");
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
