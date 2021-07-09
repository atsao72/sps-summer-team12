package com.google.sps.servlets;

import java.util.ArrayList;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.KeyFactory;

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
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import java.util.List;



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
    String relevantReviewTags = convertArrayListToString(new EntityParser(userReview).getRelevantEntities());
    String allReviewTags = convertArrayListToString(new EntityParser(userReview).getAllEntities());
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
    String imageName = filePart.getSubmittedFileName();
    InputStream fileInputStream = filePart.getInputStream();

    // Upload the image and get its URL
    String imageURL = "";
    ArrayList<String> imageTags = new ArrayList<String>();
    
    // Upload the image and save metadata if provided
    if(!imageName.isEmpty()) {
        try {
            //Making image name unique to prevent overrides from same file name uploads
            imageName = System.currentTimeMillis() + imageName;
            imageURL = uploadToCloudStorage(imageName, fileInputStream);

            //Get the labels of the image that the user uploaded.
            byte[] imageBytes = filePart.getInputStream().readAllBytes();
            for (EntityAnnotation label : getImageLabels(imageBytes)) {
                imageTags.add(label.getDescription());
            }
        } catch (Exception e) {
            throw e;
        } 
    }

    //Checking if image was uploaded
    boolean hasImage = imageURL != "";

    //Creating a post object
    Post newPost = new Post(locationName, category, parking, ratingScore, noiseScore, spaceScore, userReview, allReviewTags, relevantReviewTags, imageName, imageURL, hasImage, convertArrayListToString(imageTags));
    
    //Printing data to confirm that form contents have been read
    newPost.printDebugString();

    //Storing information to datastore
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Post");
    FullEntity taskEntity =
        Entity.newBuilder(keyFactory.newKey())
            .set("locationName", locationName)
            .set("category", category)
            .set("parking", parking)
            .set("ratingScore", ratingScore)
            .set("noiseScore", noiseScore)
            .set("spaceScore", spaceScore)
            .set("userReview", userReview)
            .set("relevantReviewTags", relevantReviewTags)
            .set("allReviewTags", allReviewTags)
            .set("hasImage", hasImage)
            .set("imageName", imageName)
            .set("imageURL", imageURL)
            .set("imageTags", convertArrayListToString(imageTags))
            .build();
    datastore.put(taskEntity);


    //TODO: Sentiment analysis for textbox


    response.sendRedirect("index.html");

  }

  //Helper function used to determine user input of radio button ratings
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
  private static String uploadToCloudStorage(String imageName, InputStream fileInputStream) {
    String projectId = "summer21-sps-12";
    String bucketName = "summer21-sps-12.appspot.com";
    Storage storage =
        StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    BlobId blobId = BlobId.of(bucketName, imageName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

    // Upload the file to Cloud Storage.
    Blob blob = storage.create(blobInfo, fileInputStream);

    // Return the uploaded file's URL.
    return blob.getMediaLink();
  }

  /**
  * Uses the Google Cloud Vision API to generate a list of labels that apply to the image
  * represented by the binary data stored in imageBytes.
  */
  private List<EntityAnnotation> getImageLabels(byte[] imageBytes) throws IOException {
    ByteString byteString = ByteString.copyFrom(imageBytes);
    Image image = Image.newBuilder().setContent(byteString).build();

    Feature feature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
    List<AnnotateImageRequest> requests = new ArrayList<>();
    requests.add(request);

    ImageAnnotatorClient client = ImageAnnotatorClient.create();
    BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
    client.close();
    List<AnnotateImageResponse> imageResponses = batchResponse.getResponsesList();
    AnnotateImageResponse imageResponse = imageResponses.get(0);

    if (imageResponse.hasError()) {
      System.err.println("Error getting image labels: " + imageResponse.getError().getMessage());
      return null;
    }

    return imageResponse.getLabelAnnotationsList();
  }

  private String convertArrayListToString(ArrayList<String> imageTags){
      String form = "";
        int lastIndex = imageTags.size()-1;
        for(String tag: imageTags){
            form += tag;

            //Prevents comma being added on last displayed tag
            if(tag != imageTags.get(lastIndex))
                form += "-";
        }
        return form;
  }

}
