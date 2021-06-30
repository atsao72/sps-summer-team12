package com.google.sps.servlets;


import java.lang.String;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Responsible for collecting and storing location-post.html form data
 */
@WebServlet("/form-handler")
public class FormHandlerServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Getting values entered in the form.
    String userReview = Jsoup.clean(request.getParameter("review-input"), Whitelist.none());
    boolean parking = false;
    int noiseScore = 0;
    int spaceScore = 0;
    int ratingScore = 0;
    
    //Checking which radio button is selected for noise level
    noiseScore = getScore(request.getParameter("noise").charAt(0));

    //Checking which radio button is selected for space level
    spaceScore = getScore(request.getParameter("space").charAt(0));

    //Checking if parking is available
    parking = request.getParameter("parking").equals("1");

    //Checking which radio button is selected for overall review
    ratingScore = getScore(request.getParameter("rating").charAt(0));

    //Printing data to confirm that form contents have been read
    String form = "Noise Rating: " + noiseScore + ", Space Rating: " + spaceScore + ", Parking Available: " + parking + ", Overall Rating: "+ ratingScore + ", User Review: " + userReview;
    System.out.println(form);

    //TODO: Save data to database

    response.sendRedirect("index.html");

  }

  private int getScore(char parameterChar){
        switch(parameterChar){
        case '1':
            return 1;
        case '2':
            return 2;
        case '3':
            return 3;
        case '4':
            return 4;
        case '5':
            return 5;
        default:
            return 0;
    }
  }
}
