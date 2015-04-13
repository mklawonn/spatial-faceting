
package models;

import play.*;
import play.mvc.*;

import views.html.*;


import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class FacetsWithCategories {

    //Maps categories of field facets (e.g "characteristic") to a list of the facets falling in that category (e.g pH)
    public HashMap<String, HashMap<String, Boolean>> facets = new HashMap<String, HashMap<String, Boolean>>();
    //List of categories
    public Map<String, Boolean> categories = new HashMap<String, Boolean>();
    //Keep track of all facets regardless of category for use by the query form
    public ArrayList<String> all_facets = new ArrayList<String>();


    public FacetsWithCategories() {}
    
    //TODO update this to create facets Map
    public FacetsWithCategories(Map<String, Boolean> facet_categories, ArrayList<String> all_facets){
        this.categories = new HashMap<String, Boolean>(facet_categories);
        this.all_facets = new ArrayList<String>(all_facets);
    }

    //Consider adding to map from categories to list of strings
    public boolean addFacet(String category, String facet){
        if (categories.get(category) == null) {
            categories.put(category, true);
            facets.put(category, new HashMap<String, Boolean>());
        }
        //Note that this doesn't prevent the facet from being overwritten
        facets.get(category).put(facet, false);
        return true;
    }

}
