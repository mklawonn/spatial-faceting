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
    public String url_base = "http://jeffersontest.tw.rpi.edu/solr/datasets/browse?wt=json";

    public GetSolrQuery () {} 

    public GetSolrQuery (Query query) {
    	this.solr_query.append(url_base);
        this.solr_query.append("&q=*:*");
        //System.out.println(query.field_facets.facets.keySet());
        
        //Check for geospatial component
        if (query.named_geographic_location != null){
        	if (query.named_geographic_location.length() > 0){
            	//Get the polygon associated with the name
            	StringBuffer spatial_query = new StringBuffer();
            	String json = new String();
            	String polygon_string = new String();
            	try {
            		spatial_query.append("http://jeffersontest.tw.rpi.edu/solr/wikimapia/select?q=location_name");
            		spatial_query.append(URLEncoder.encode(":\"", "UTF-8"));
            		spatial_query.append(URLEncoder.encode(query.named_geographic_location, "UTF-8"));
            		spatial_query.append(URLEncoder.encode("\"", "UTF-8"));
            		spatial_query.append("&wt=json");
            		//System.out.println(spatial_query.toString().charAt(72));
            		
            	} catch (Exception e){
            		e.printStackTrace();
            	}
            	try
                {
                	HttpClient client = new DefaultHttpClient();
                	String encoded_query = URLEncoder.encode(spatial_query.toString(), "UTF-8");
                	System.out.println(spatial_query.toString());
                	HttpGet request = new HttpGet(spatial_query.toString().replace(" ", "%20"));
                	HttpResponse response = client.execute(request);
          
                    //in = new Scanner(response.getEntity().getContent());
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(response.getEntity().getContent(), writer, "utf-8");
                    //System.out.println(writer.toString());
                    //return writer.toString();
                    json = writer.toString();
                    //return IOUtils.toString(entity.getContent());
                    /*while (in.hasNext())
                    {
                        System.out.println(in.next());
                    }
                    EntityUtils.consume(entity);*/
                } catch (IllegalStateException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}finally {
                    //in.close();
                    //request.close();
                }
            	
            	ObjectMapper mapper = new ObjectMapper();
            	JsonNode node = null;
        		try {
        			node = mapper.readTree(json);
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
            	
            	JsonNode documents = node.get("response").get("docs");
            	Iterator<JsonNode> doc_iterator = documents.iterator();
            	while (doc_iterator.hasNext()){
            		JsonNode doc = doc_iterator.next();
            		polygon_string = doc.get("polygon_string").asText().replace(",", " , ");
            		String quote = new String();
            		try {
						quote = URLEncoder.encode("\"", "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		this.solr_query.append(String.format("&fq=point:%sIsWithin(%s)%sdistErrPct=0%s", quote, polygon_string, "%20", quote));
            		/*
            		try {
						this.solr_query.append(URLEncoder.encode(String.format("&fq=point:\"IsWithin(%s)%sdistErrPct=0\"", polygon_string, "%20"), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
            		//System.out.println(doc.get("polygon_string"));
            	}
            	//System.out.println(documents.asText());
            	//Add the filter to the query
            }
            
            for (String field_facet_category : query.field_facets.facets.keySet()){
                for (String field_facet : query.field_facets.facets.get(field_facet_category).keySet()){
                    this.solr_query.append(String.format("&fq=%s:%s", field_facet_category.replace(" ", "%20"), 
                    		field_facet.replace(" ", "%20")));
                }
            }
        }

        System.out.println("Final Solr Query");
        System.out.println(this.solr_query.toString());
        //this.solr_query.append("}");
    }

    //Should return JSON
    public String executeQuery() throws IllegalStateException, IOException{
    	CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(url_base);
        //List<NameValuePair> params = new ArrayList<>();
        //params.add(new BasicNameValuePair("task", "savemodel"));
        //params.add(new BasicNameValuePair("code", solr_query.toString()));
        //StringEntity postingString = new StringEntity(solr_query.toString());
        //CloseableHttpResponse response = null;
        Scanner in = null;
        try
        {
        	HttpClient client = new DefaultHttpClient();
        	HttpGet request = new HttpGet(solr_query.toString().replace(" ", "%20"));
        	HttpResponse response = client.execute(request);
  
            //in = new Scanner(response.getEntity().getContent());
            StringWriter writer = new StringWriter();
            IOUtils.copy(response.getEntity().getContent(), writer, "utf-8");
            //System.out.println(writer.toString());
            return writer.toString();
            //return IOUtils.toString(entity.getContent());
            /*while (in.hasNext())
            {
                System.out.println(in.next());
            }
            EntityUtils.consume(entity);*/
        } finally
        {
            //in.close();
            //request.close();
        }
    }
}
