package http;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Scanner;

import models.Query;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetSolrQuery {

    public StringBuffer solr_query = new StringBuffer();
    public String dataset_collection_url_base = "http://jeffersontest.tw.rpi.edu/solr/datasets/browse?wt=json";
    public String lidarsonar_collection_url_base = "http://matt:rophestorbeleara@jeffersontest.tw.rpi.edu/solr/lidarsonar/select?wt=json";
    
    
    public GetSolrQuery () {} 

    public GetSolrQuery (Query query) {
    	this.solr_query.append(dataset_collection_url_base);
        this.solr_query.append("&q=*:*");
        
        String quote = new String();
		try {
			quote = URLEncoder.encode("\"", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        
        
        for (String field_facet_category : query.field_facets.facets.keySet()){
            for (String field_facet : query.field_facets.facets.get(field_facet_category).keySet()){
                this.solr_query.append(String.format("&fq=%s:%s%s%s", field_facet_category.replace(" ", "%20"), quote,
                		field_facet.replace(" ", "%20"), quote));
            }
        }
        
        //System.out.println("Final Solr Query");
        //System.out.println(this.solr_query.toString());
    }

    
    //Preconditions: The GetSolrQuery object has been initialized by a Query object
    //Inputs: The named location and the predicate associated with it.
    //Output: Returns this object to allow for a builder design pattern to be applied.
    //		  Currently does not handle http errors (e.g 404) very well. Need to fix.
    //		  Especially to handle permission denied responses.
    //Postconditions: The member string solr_query is modified to contain the spatial filters.
    public GetSolrQuery addSpatialComponent(String named_geographic_location, String spatial_predicate) {
    	//Right now (4-14-15) the lidarsonar collection is the only collection with associate lats and longs
    	//In other words, this must be the collection queried to use the spatial faceting
    	if (named_geographic_location != null){
        	if (named_geographic_location.length() > 0){
        		solr_query = new StringBuffer(solr_query.toString().replace(dataset_collection_url_base, 
						  lidarsonar_collection_url_base));
            	//Get the polygon associated with the name
            	StringBuffer spatial_query = new StringBuffer();
            	String json = new String();
            	String polygon_string = new String();
            	try {
            		spatial_query.append("http://jeffersontest.tw.rpi.edu/solr/wikimapia/select?q=location_name");
            		spatial_query.append(URLEncoder.encode(":\"", "UTF-8"));
            		spatial_query.append(URLEncoder.encode(named_geographic_location, "UTF-8"));
            		spatial_query.append(URLEncoder.encode("\"", "UTF-8"));
            		spatial_query.append("&wt=json");
            		//System.out.println(spatial_query.toString().charAt(72));
            		
            	} catch (Exception e){
            		e.printStackTrace();
            	}
            	try
                {
                	HttpClient client = new DefaultHttpClient();
                	
                	System.out.println(spatial_query.toString());
                	HttpGet request = new HttpGet(spatial_query.toString().replace(" ", "%20"));
                	HttpResponse response = client.execute(request);
          
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(response.getEntity().getContent(), writer, "utf-8");

                    json = writer.toString();
                    
                } catch (IllegalStateException e) {
    				e.printStackTrace();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
            	
            	ObjectMapper mapper = new ObjectMapper();
            	JsonNode node = null;
        		try {
        			node = mapper.readTree(json);
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
            	
            	JsonNode documents = node.get("response").get("docs");
            	Iterator<JsonNode> doc_iterator = documents.iterator();
            	while (doc_iterator.hasNext()){
            		JsonNode doc = doc_iterator.next();
            		//For the SOLR query to work, the commas must be surrounded by spaces
            		polygon_string = doc.get("polygon_string").asText().replace(",", " , ");
            		String quote = new String();
            		try {
						quote = URLEncoder.encode("\"", "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
            		this.solr_query.append(String.format("&fq=point:%sIsWithin(%s)%sdistErrPct=0%s", quote, polygon_string, "%20", quote));
            	}
            }
    	}
    	//System.out.println("The spatial query:");
    	//System.out.println(this.solr_query.toString());
    	return this;
    }
    
    //Preconditions: The GetSolrQuery object has been initialized by a Query object
    //Inputs: None. Executes query based on the member string solr_query.
    //Output: Returns JSON in the form of a string. Currently does not handle http errors
    //		  very gracefully. Need to change this.
    //Postconditions: None
    public String executeQuery() throws IllegalStateException, IOException{
    	CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(dataset_collection_url_base);
        
        Scanner in = null;
        try
        {
        	HttpClient client = new DefaultHttpClient();
        	HttpGet request = new HttpGet(solr_query.toString().replace(" ", "%20"));
        	HttpResponse response = client.execute(request);
  
            StringWriter writer = new StringWriter();
            IOUtils.copy(response.getEntity().getContent(), writer, "utf-8");
            
            return writer.toString();
            
        } finally
        {
            //in.close();
            //request.close();
        }
    }
}
