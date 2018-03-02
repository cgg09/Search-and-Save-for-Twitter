package tfg.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
	
	public static void main (String args[]){
	}
		
	public void doGet(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String oauthVerifier = request.getParameter("oauth_verifier");
/*		
		int day = Integer.parseInt(request.getParameter("day"));
		int month = Integer.parseInt(request.getParameter("month"));
		int year = Integer.parseInt(request.getParameter("year"));
			
		LocalDate birthDate = new LocalDate(year, month, day);	
		LocalDate currentDate = new LocalDate();
		
		Years age = Years.yearsBetween(birthDate, currentDate);

		
		String docType = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n";
		out.println(docType +
					"<html>\n" +
					" <head><title>Seminar 1</title></head>\n" +
	                "<body>\n" +
					" Years: " + age.getYears() +
					" Name: " + request.getParameter("name") +
	                " <h1>Please, go back and fill the form :)</h1>\n" +
	                "</body></html>");
	*/	
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
	
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String oauthVerifier = request.getParameter("oauth_verifier");
		
		/*
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String referer = request.getHeader("Referer");
		if (referer == null) referer = "<I>none</I>";
		
		int day = Integer.parseInt(request.getParameter("day"));
		int month = Integer.parseInt(request.getParameter("month"));
		int year = Integer.parseInt(request.getParameter("year"));
			
		LocalDate birthDate = new LocalDate(year, month, day);	
		LocalDate currentDate = new LocalDate();
		
		Years age = Years.yearsBetween(birthDate, currentDate);

		String docType = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n";
		out.println(docType +
					"<html>\n" +
					"<head><title>Seminar 1</title></head>\n" +
					"<body>\n" +
						"Hi " + request.getParameter("name") + "! You are now " + age.getYears() + " years!" +
						"<ul>\n " +
	 						"<li>URI: " + request.getRequestURI() +
	 						"<li>Referer: " + request.getHeader("referer") +
	 						"<li>User agent: " + request.getHeader("user-agent") +
	 					"</ul>\n" +
					"</body>" +
					"</html>");
	*/	 }
		
		private boolean contains(String mainString, String subString) {
			 return(mainString.indexOf(subString) != -1);
		}

}
