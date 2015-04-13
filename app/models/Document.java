package models;

import java.util.ArrayList;

public class Document{
	
	public String id;
	public String started_at;
	public String ended_at;
	public String observation;
	public String deployment;
	public String platform_label;
	public String secured;
	public String nr_measurements;
	public ArrayList<String> characteristic = new ArrayList<String>();
	
	public Document() {}
	
	public Document(String id, String started_at, String ended_at, String observation,
					String deployment, String platform_label, String secured, String nr_measurements,
					ArrayList<String> characteristic) {
		this.id = id;
		this.started_at = started_at;
		this.ended_at = ended_at;
		this.observation = observation;
		this.deployment = deployment;
		this.platform_label = platform_label;
		this.secured = secured;
		this.nr_measurements = nr_measurements;
		
		for (String c : characteristic){
			if (c == null) continue;
			this.characteristic.add(c);
		}
		
	}
	
	public Document(String id, String observation,
			String deployment, String platform_label, String secured, String nr_measurements,
			ArrayList<String> characteristic) {
		
		this.id = id;
		this.started_at = "";
		this.ended_at = "";
		this.observation = observation;
		this.deployment = deployment;
		this.platform_label = platform_label;
		this.secured = secured;
		this.nr_measurements = nr_measurements;

		for (String c : characteristic){
			if (c == null) continue;
			this.characteristic.add(c);
		}

	}
	
}