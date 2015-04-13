package views.formdata;

import models.FacetsWithCategories;

import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

public class FacetFormData {

    public String named_geographic_location;
    public String spatial_predicate; 

    //These are just lists of strings
    //because when the query gets submitted the "category" (e.g "characteristic")
    //they fall into is irrelevant
    //public ArrayList<String> field_facets; 
    //public ArrayList<String> query_facets;
    //public ArrayList<String> pivot_facets;
    //public ArrayList<String> range_facets;
    //public ArrayList<String> cluster_facets;

    public FacetsWithCategories field_facets = new FacetsWithCategories();
    public FacetsWithCategories query_facets = new FacetsWithCategories();
    public FacetsWithCategories pivot_facets = new FacetsWithCategories();
    public FacetsWithCategories range_facets = new FacetsWithCategories();
    public FacetsWithCategories cluster_facets = new FacetsWithCategories();

    public FacetFormData() {}

    /*
    public FacetFormData(String named_geographic_location, String spatial_predicate,
                         FacetsWithCategories field_facets, FacetsWithCategories query_facets, FacetsWithCategories pivot_facets, FacetsWithCategories range_facets,
                         FacetsWithCategories cluster_facets){

        this.named_geographic_location = named_geographic_location;
        this.spatial_predicate = spatial_predicate;

        for (String subfacet : field_facets.all_facets) {
            this.field_facets.add(subfacet);
        } 

        for (String subfacet : query_facets.all_facets) {
            this.query_facets.add(subfacet);
        } 

        for (String subfacet : pivot_facets.all_facets) {
            this.pivot_facets.add(subfacet);
        } 

        for (String subfacet : range_facets.all_facets) {
            this.range_facets.add(subfacet);
        } 

        for (String subfacet : cluster_facets.all_facets) {
            this.cluster_facets.add(subfacet);
        } 


    }
    */

    public FacetFormData(String named_geographic_location, String spatial_predicate,
                         FacetsWithCategories field_facets, FacetsWithCategories query_facets, FacetsWithCategories pivot_facets, FacetsWithCategories range_facets,
                         FacetsWithCategories cluster_facets){

        this.named_geographic_location = named_geographic_location;
        this.spatial_predicate = spatial_predicate;
        //this.field_facets = field_facets;
        //this.query_facets = query_facets;
        
        for (String category : field_facets.facets.keySet() ) {
            for (String facet : field_facets.facets.get(category).keySet()){
                this.field_facets.addFacet(category, facet);
            }
        }

        for (String category : query_facets.facets.keySet() ) {
            for (String facet : query_facets.facets.get(category).keySet()){
                this.query_facets.addFacet(category, facet);
            }
        }

        for (String category : pivot_facets.facets.keySet() ) {
            for (String facet : pivot_facets.facets.get(category).keySet()){
                this.pivot_facets.addFacet(category, facet);
            }
        }

        for (String category : range_facets.facets.keySet() ) {
            for (String facet : range_facets.facets.get(category).keySet()){
                this.range_facets.addFacet(category, facet);
            }
        }

        for (String category : cluster_facets.facets.keySet() ) {
            for (String facet : cluster_facets.facets.get(category).keySet()){
                this.cluster_facets.addFacet(category, facet);
            }
        }

    }

    public List<ValidationError> validate() {
        return null;
    }


}
