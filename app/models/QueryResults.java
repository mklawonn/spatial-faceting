package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QueryResults {

    public String json;
    public ArrayList<Document> the_docs = new ArrayList<Document>();

    public QueryResults () {} 

    public QueryResults (String json) {
        this.json = json;
        //System.out.println(json);
        // create an ObjectMapper instance.
        ObjectMapper mapper = new ObjectMapper();
        // use the ObjectMapper to read the json string and create a tree
        JsonNode node = null;
		try {
			node = mapper.readTree(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO Get the document list from the json
		//TODO Then add to a list
		JsonNode documents = node.get("response").get("docs");
		Iterator<JsonNode> doc_iterator = documents.iterator();
		while (doc_iterator.hasNext()){
			JsonNode doc = doc_iterator.next();
			String id = doc.get("id").asText();
			String started_at = null;
			try {
				started_at = doc.get("started_at").asText();
			} catch (Exception e) {
				started_at = "";
			}
			String ended_at = null;
			try {
				ended_at = doc.get("ended_at").asText();;
			} catch (Exception e) {
				ended_at = "";
			}
			String observation = doc.get("observation").asText();
			String deployment = doc.get("deployment").asText();
			String platform_label = null;
			try {
				doc.get("platform_label").asText();
			} catch (Exception e) {
				platform_label = "";
			}
			String secured = doc.get("secured").asText();
			String nr_measurements = doc.get("nr_measurements").asText();
			List<JsonNode> characteristic = doc.findValues("characteristic");
			ArrayList<String> characteristics = new ArrayList<String>();
			for (JsonNode c : characteristic){
				characteristics.add(c.asText());
			}
			the_docs.add(new Document(id, started_at, ended_at, observation, deployment, platform_label, secured, nr_measurements, characteristics));
		}
		//System.out.println(the_docs.size());
    }
}
