package com.google.sps.servlets;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.KeyFactory;
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
    String locationName = Jsoup.clean(request.getParameter("location"), Whitelist.none());
    String category = request.getParameter("categories");
    
    //Checking which radio button is selected for noise level
    noiseScore = getScore(request.getParameter("noise").charAt(0));

    //Checking which radio button is selected for space level
    spaceScore = getScore(request.getParameter("space").charAt(0));

    //Checking if parking is available
    parking = request.getParameter("parking").equals("1");

    //Checking which radio button is selected for overall review
    ratingScore = getScore(request.getParameter("rating").charAt(0));

    //Printing data to confirm that form contents have been read
    String form = "Location: " + "[" + locationName + "]" + ", Category: " + category + ", Parking Available: " + parking + ", Overall Rating: "+ ratingScore + ", Noise Rating: " + noiseScore + ", Space Rating: " + spaceScore + ", User Review: " + userReview;
    System.out.println(form);

    //Creating a post class
    Post newPost = new Post(locationName, category, parking, ratingScore, noiseScore, spaceScore, userReview);

    //TODO: Save data to database
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Post");
    FullEntity taskEntity =
        Entity.newBuilder(keyFactory.newKey())
            .set("locationName", title)
            .set("category", timestamp)
            .set("parking", parking)
            .set("ratingScore", ratingScore)
            .set("noiseScore", noiseScore)
            .set("spaceScore", spaceScore)
            .set("userReview", userReview)
            .build();
    datastore.put(taskEntity);
    //TODO: Sentiment analysis for textbox

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
