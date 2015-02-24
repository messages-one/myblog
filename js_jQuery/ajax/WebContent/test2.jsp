<%@ page
import="java.io.BufferedReader,
java.io.DataOutputStream,
java.io.InputStream,
java.io.InputStreamReader,
java.net.HttpURLConnection,
java.net.URL,
java.io.OutputStreamWriter"%>
<%
	URL url;
	HttpURLConnection connection = null;
	try {
		//Create connection
		url = new java.net.URL(request.getParameter("address"));
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();		
		conn.setRequestMethod("GET");
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String next = null;
		StringBuilder responseStr = new StringBuilder();
		while ((next = reader.readLine()) != null)
			responseStr.append(next);		
		response.getWriter().write(responseStr.toString());
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if (connection != null) {
			connection.disconnect();
		}
	}
%>