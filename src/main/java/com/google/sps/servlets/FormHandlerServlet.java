package com.google.sps.servlets;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.lang.String;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Responsible for collecting and storing location-post.html form data
 */
@WebServlet("/form-handler")
@MultipartConfig
public class FormHandlerServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) 
      throws ServletException, IOException {

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

    // Get the message entered by the user.
    String message = request.getParameter("message");

    // Get the file chosen by the user.
    Part filePart = request.getPart("image");
    String fileName = filePart.getSubmittedFileName();
    InputStream fileInputStream = filePart.getInputStream();

    // Upload the file and get its URL
    String imageURL;
    boolean hasImage;
    
    try{
        imageURL = uploadToCloudStorage(fileName, fileInputStream);
        hasImage = true;
    }
    catch(Exception e){
        imageURL = "";
        hasImage = false;
    }

    //TODO: Save data to database
    //TODO: Sentiment analysis for textbox

    //Creating a post class
    Post newPost = new Post(locationName, category, parking, ratingScore, noiseScore, spaceScore, userReview, imageURL, hasImage);

    //Printing data to confirm that form contents have been read
    String form = "Location: " + "[" + locationName + "]" + ", Category: " + category + ", Parking Available: " + parking + ", Overall Rating: "+ ratingScore + ", Noise Rating: " + noiseScore + ", Space Rating: " + spaceScore  + ", Image Uploaded: " + hasImage + ", Image URL: " + "[" + imageURL + "]" + ", User Review: " + "[" + userReview +"]";
    System.out.println(form);

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

  /** Uploads a file to Cloud Storage and returns the uploaded file's URL. */
  private static String uploadToCloudStorage(String fileName, InputStream fileInputStream) {
    String projectId = "summer21-sps-12";
    String bucketName = "summer21-sps-12.appspot.com";
    Storage storage =
        StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    BlobId blobId = BlobId.of(bucketName, fileName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

    // Upload the file to Cloud Storage.
    Blob blob = storage.create(blobInfo, fileInputStream);

    // Return the uploaded file's URL.
    return blob.getMediaLink();
  }
}
