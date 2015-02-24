package ajax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CallExternalDomain
 */
@WebServlet("/CallExternalDomain")
public class CallExternalDomain extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CallExternalDomain() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		try {
			URL url = new java.net.URL(request.getParameter("address"));
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String next = null;
			StringBuilder responseStr = new StringBuilder();
			while ((next = reader.readLine()) != null)
				responseStr.append(next);
			response.getWriter().write(responseStr.toString());
		} catch (Exception e) {
			System.out.println("An error occurred connecting to external resource" + e.getMessage());
		} finally {
			try {
				if (conn != null)
					conn.disconnect();
				if (reader != null)
					reader.close();
			} catch (Exception e) {
				System.out.println("An error occurred releasing resources " + e.getMessage());
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
}
