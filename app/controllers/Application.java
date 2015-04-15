package controllers;

import http.GetSolrQuery;
import http.JsonHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import models.FacetsWithCategories;
import models.Query;
import models.QueryResults;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.formdata.FacetFormData;
import views.html.backup;
import views.html.index;

public class Application extends Controller {

    public static FacetFormData facet_form = new FacetFormData();
    public static FacetsWithCategories field_facets = new FacetsWithCategories();
    public static FacetsWithCategories query_facets = new FacetsWithCategories();
    public static FacetsWithCategories pivot_facets = new FacetsWithCategories();
    public static FacetsWithCategories range_facets = new FacetsWithCategories();
    public static FacetsWithCategories cluster_facets = new FacetsWithCategories();
    public static Map<String, Boolean> named_location = new HashMap<String, Boolean>();
    public static Map<String, Boolean> spatial_predicate = new HashMap<String, Boolean>();
    public static QueryResults query_results = new QueryResults();

    public static Result index() {
        return ok(backup.render("Your new application is ready."));
    }
    
    public static void getFacets(JsonHandler jh){
    	//Get the facets
        try {
            if (jh.getFieldCountJson()) {
                for (String key : jh.categories_and_facets.keySet()) {
                    for (String facet : jh.categories_and_facets.get(key)){
                        //HashMap<String, String> temp_map = new HashMap<String, String>();
                        //temp_map.put(facet, jh.categories_facets_and_counts.get(key).get(facet));
                        if (facet.equals("null")) {field_facets.addFacet(key, "missing"); continue;}
                        field_facets.addFacet(key, facet);
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Result test() {
    	Form<FacetFormData> formData = Form.form(FacetFormData.class).fill(facet_form);
        JsonHandler jh = new JsonHandler();
        //ArrayList<String> names = new ArrayList<String>();
        
        //Get query using http.GetSolrQuery
        Query query = new Query();
        GetSolrQuery query_submit = new GetSolrQuery(query);
        String query_json = null;
        try {
			query_json = query_submit.executeQuery();
		} catch (IllegalStateException | IOException e1) {
			e1.printStackTrace();
		}
        //Store the results in a models.QueryResults
        QueryResults query_results = new QueryResults(query_json);
        
        
        //Get the facets
        getFacets(jh);
  
        return ok(index.render(formData, field_facets, query_facets,
                    range_facets, pivot_facets, cluster_facets, 
                    formData.get().named_geographic_location, formData.get().spatial_predicate, query_results));
    }

    public static Result postIndex() {
        
    	JsonHandler jh = new JsonHandler();
    	String named_geographic_location = new String();
    	String spatial_predicate = "within";
    	DynamicForm formData = Form.form().bindFromRequest();
    	
    	FacetsWithCategories field_facet_for_query = new FacetsWithCategories();
    	
    	//Searching for the index of "[" is done here, because the way the views are set up
    	//The scala will add a number to each category so as to map
    	//The same category to more than 1 facet
    	//When creating the query, however, this number is not needed.
    	for (String category : formData.data().keySet()){
    		if (category.contains("[")) {
    			int index = category.indexOf("[");
    			field_facet_for_query.addFacet(category.substring(0,index), formData.data().get(category));
    		} else {
    			named_geographic_location = formData.data().get(category);
    		}
    	}
    	
    	Query query = new Query(named_geographic_location, spatial_predicate, field_facet_for_query, query_facets,
    						    pivot_facets, range_facets, cluster_facets);
    	
    	GetSolrQuery query_submit = new GetSolrQuery(query).addSpatialComponent(named_geographic_location, spatial_predicate);
        String query_json = null;
        try {
			query_json = query_submit.executeQuery();
		} catch (IllegalStateException | IOException e1) {
			e1.printStackTrace();
		}
        QueryResults query_results = new QueryResults(query_json);
    	
        //Get the facets
        getFacets(jh);
        
        //return ok("cool");
        Form<FacetFormData> fd = Form.form(FacetFormData.class).fill(facet_form);
        return ok(index.render(fd, field_facets, query_facets,
                range_facets, pivot_facets, cluster_facets, 
                named_geographic_location, spatial_predicate, query_results));
    }

}
