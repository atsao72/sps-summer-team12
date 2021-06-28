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
 * Responsible for collecting and storing contact-me.html form data
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
    
    //Checking which radio button is selected for noise level
    switch(request.getParameter("noise").charAt(0)){
        case '1':
            noiseScore = 1;
            break;
        case '2':
            noiseScore = 2;
            break;
        case '3':
            noiseScore = 3;
            break;
        case '4':
            noiseScore = 4;
            break;
        case '5':
            noiseScore = 5;
            break;
    }

    //Checking which radio button is selected for noise level
    switch(request.getParameter("space").charAt(0)){
        case '1':
            spaceScore = 1;
            break;
        case '2':
            spaceScore = 2;
            break;
        case '3':
            spaceScore = 3;
            break;
        case '4':
            spaceScore = 4;
            break;
        case '5':
            spaceScore = 5;
            break;
    }

    //Checking if parking is available
    if("1".equals(request.getParameter("parking")))
        parking = true;


    //Printing data to confirm that form contents have been read
    String form = "Noise Rating: " + noiseScore + ", Space Rating: " + spaceScore + ", Parking Available: " + parking + ", User Review: " + userReview;
    System.out.println(form);

    //TODO: Save data to database

    response.sendRedirect("index.html");

  }
}
