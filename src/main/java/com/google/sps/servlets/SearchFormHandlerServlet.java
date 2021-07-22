package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.KeyFactory;

/**
 * Responsible for collecting and storing data from the home page
 */
@WebServlet("/search-form-handler")
public class SearchFormHandlerServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String zipCode = Jsoup.clean(request.getParameter("zip-code"), Whitelist.none());
    String category = Jsoup.clean(request.getParameter("categories"), Whitelist.none());
    String filter1 = Jsoup.clean(request.getParameter("filter1"), Whitelist.none());
    String filter2 = Jsoup.clean(request.getParameter("filter2"), Whitelist.none());
    String filter3 = Jsoup.clean(request.getParameter("filter3"), Whitelist.none());
    long timestamp = System.currentTimeMillis();    

    Filter newFilter = new Filter(zipCode, category, filter1, filter2, filter3, timestamp);
    newFilter.printSearchFilterString();


    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Filter");
    FullEntity taskEntity =
        Entity.newBuilder(keyFactory.newKey())
            .set("zipCode", zipCode)
            .set("category", category)
            .set("filter1", filter1)
            .set("filter2", filter2)
            .set("filter3", filter3)
            .set("timestamp", timestamp)
            .build();
    datastore.put(taskEntity);

    response.sendRedirect("/index2.html");

    }
}
