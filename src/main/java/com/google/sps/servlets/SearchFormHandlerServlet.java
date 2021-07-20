package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.KeyFactory;

/**
 * Responsible for collecting and storing data from the home page
 */
@WebServlet("/search-form-handler")
public class SearchFormHandlerServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String zipCode = request.getParameter("zip-code");
    String category = request.getParameter("categories");
    String filter1 = request.getParameter("filter1");
    String filter2 = request.getParameter("filter2");
    String filter3 = request.getParameter("filter3");    

    Filter newFilter = new Filter(zipCode, category, filter1, filter2, filter3);
    newFilter.printSearchFilterString();


    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Filter");
    FullEntity taskEntity =
        Entity.newBuilder(keyFactory.newKey())
            .set("zipCode", zipCode)
            .set("category", category)
            .set("filter1", filter1)
            .set("filter1", filter2)
            .set("filter1", filter3)
            .build();
    datastore.put(taskEntity);

    response.sendRedirect("index.html");

    }
}


