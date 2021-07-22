package com.google.sps.servlets;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;
import com.google.sps.servlets.Post;
import com.google.sps.servlets.Filter;
import java.io.IOException;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for listing posts. */
@WebServlet("/list-posts")
public class ListPostServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    
    // Gets the category to be filtered by
    Query<Entity> query = Query.newEntityQueryBuilder().setKind("Filter").setOrderBy(OrderBy.desc("timestamp")).build();
    QueryResults<Entity> results = datastore.run(query);
    Entity entity1 = results.next();
    String filter = entity1.getString("filter1");
    String categories = entity1.getString("category");
    System.out.println(filter);
    System.out.println(categories);
    Query<Entity> query2 =
        Query.newEntityQueryBuilder().setKind("Post").setFilter(PropertyFilter.eq("category", categories)).setOrderBy(OrderBy.desc(filter)).build();
    QueryResults<Entity> results2 = datastore.run(query2);

    List<Post> posts = new ArrayList<>();
    while (results2.hasNext()) {
      Entity entity = results2.next();

      String userReview = entity.getString("userReview");
      boolean parking = entity.getBoolean("parking");
      int noiseScore = (int)entity.getLong("noiseScore");
      int spaceScore = (int)entity.getLong("spaceScore");
      int ratingScore = (int)entity.getLong("ratingScore");
      String locationName = entity.getString("locationName");
      String category = entity.getString("category");
      String allReviewTags = entity.getString("allReviewTags");
      String relevantReviewTags = entity.getString("relevantReviewTags");
      String imageName = entity.getString("imageName");
      String imageURL = entity.getString("imageURL");
      boolean hasImage = entity.getBoolean("hasImage");
      String imageTags = entity.getString("imageTags");
      String timeStamp = entity.getString("timeStamp");
      
      Post post = new Post(locationName, category, parking, ratingScore, noiseScore, spaceScore, userReview, allReviewTags, relevantReviewTags, imageName, imageURL, hasImage, imageTags, timeStamp);
      posts.add(post);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(posts));
  }
}
