package com.google.sps.servlets;

import java.lang.String;
import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Class that serves to represent an employer */
public final class Post {
    //attributes
    private String userReview;
    private boolean parking;
    private int noiseScore;
    private int spaceScore;
    private int ratingScore;
    private String locationName;
    private String category;
    private String imageURL;
    private boolean hasImage;
    private String imageName;
    private ArrayList<String> imageTags;



    public Post(String locationName, String category, boolean parking, int ratingScore, int noiseScore, int spaceScore, String userReview, String imageName, String imageURL, boolean hasImage, ArrayList<String> imageTags){
        this.locationName = locationName;
        this.category = category;
        this.parking = parking;
        this.ratingScore = ratingScore;
        this.noiseScore = noiseScore;
        this.spaceScore = spaceScore;
        this.userReview = userReview;
        this.imageURL = imageURL;
        this.hasImage = hasImage;
        this.imageName = imageName;
        this.imageTags = imageTags;
    }

    /**
    * Function used to generate a Post debug string
    */
    public String generateDebugString(){
        //Printing data to confirm that form contents have been read
        String form = "Location: " + "[" + locationName + "]" + ", Category: " + category + ", Parking Available: " + parking + ", Overall Rating: "+ ratingScore + ", Noise Rating: " + noiseScore + ", Space Rating: " + spaceScore + ", User Review: " + "[" + userReview + "]"  + ", Image Uploaded: " + hasImage + ", Image Name: [" + imageName + "]" + ", Image URL: " + "[" + imageURL + "]";
        form += ", Image Tags: [";

        int lastIndex = imageTags.size()-1;
        for(String tag: imageTags){
            form += tag;

            //Prevents comma being added on last displayed tag
            if(tag != imageTags.get(lastIndex))
                form += ", ";
        }

        form += "]";

        return form;
  }

    /**
    * Function used to display Post debug string to console
    */
    public void printDebugString(){
        System.out.println(generateDebugString());
    }
}
