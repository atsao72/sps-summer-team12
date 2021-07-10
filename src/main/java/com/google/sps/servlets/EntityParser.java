package com.google.sps.servlets;

import java.lang.String;
import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.AnalyzeEntitySentimentRequest;
import com.google.cloud.language.v1.AnalyzeEntitySentimentResponse;
import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.AnalyzeSyntaxRequest;
import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import com.google.cloud.language.v1.ClassificationCategory;
import com.google.cloud.language.v1.ClassifyTextRequest;
import com.google.cloud.language.v1.ClassifyTextResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.EntityMention;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.cloud.language.v1.Token;
import java.util.List;
import java.util.Map;

/** Class that serves to represent parse string text for relevant topics/entities */
public final class EntityParser {
    //attributes
    private String inputText;



    public EntityParser(String inputText){
        this.inputText = inputText;
    }


    /**
    * Function used to parse text for all topics/entities mentioned
    */
    public ArrayList<String> getAllEntities(){
        ArrayList<String> entities = new ArrayList<String>();

        // Instantiate the Language client com.google.cloud.language.v1.LanguageServiceClient
        try (LanguageServiceClient language = LanguageServiceClient.create()) {
            Document doc = Document.newBuilder().setContent(inputText).setType(Type.PLAIN_TEXT).build();
            AnalyzeEntitySentimentRequest request =
                AnalyzeEntitySentimentRequest.newBuilder()
                    .setDocument(doc)
                    .setEncodingType(EncodingType.UTF16)
                    .build();
            // detect entity sentiments in the given string
            AnalyzeEntitySentimentResponse response = language.analyzeEntitySentiment(request);
            // Print the response
            for (Entity entity : response.getEntitiesList()) {
                entities.add(entity.getName());
            }
        }
        catch(IOException e){
            System.out.println("Failed to load text");
        }

        return entities;
    }

    /**
    * Function used to parse text for relevant entities/topics mentioned
    */
    public ArrayList<String> getRelevantEntities(){
        ArrayList<String> entities = new ArrayList<String>();

        // Instantiate the Language client com.google.cloud.language.v1.LanguageServiceClient
        try (LanguageServiceClient language = LanguageServiceClient.create()) {
            Document doc = Document.newBuilder().setContent(inputText).setType(Type.PLAIN_TEXT).build();
            AnalyzeEntitySentimentRequest request =
                AnalyzeEntitySentimentRequest.newBuilder()
                    .setDocument(doc)
                    .setEncodingType(EncodingType.UTF16)
                    .build();
            // detect entity sentiments in the given string
            AnalyzeEntitySentimentResponse response = language.analyzeEntitySentiment(request);
            // Print the response
            for (Entity entity : response.getEntitiesList()) {
                if(Double.compare(entity.getSalience(), .1) > 0)
                    entities.add(entity.getName());
            }
        }
        catch(IOException e){
            System.out.println("Failed to load text");
        }

        return entities;
    }
}
