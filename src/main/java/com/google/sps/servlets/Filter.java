package com.google.sps.servlets;

import java.lang.String;
import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Class that serves to represent a filter */
public final class Filter {
    //attributes
    private String zipCode;
    private String category;
    private String filter1;
    private String filter2;
    private String filter3;




    public Filter(String zipCode, String category, String filter1, String filter2, String filter3){
        this.zipCode = zipCode;
        this.category = category;
        this.filter1 = filter1;
        this.filter2 = filter2;
        this.filter3 = filter3;
    }


	/**
    * Function used to generate a Filter debug string
    */
    public String generateSearchFilterString(){
        //Printing data to confirm that form contents have been read
        String form = "Zip Code: [" + zipCode + "], Category: " + category + 
        ", Filter 1: " + filter1 + ", Filter 2: "+ filter2 + ", Filter 3: " + filter3 + "]";

        return form;
  }

    /**
    * Function used to display Filter search string to console
    */
    public void printSearchFilterString(){
        System.out.println(generateSearchFilterString());
    }
}
