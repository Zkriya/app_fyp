package controllers;

import java.util.ArrayList;
import java.util.List;
import org.json.*;

import com.avaje.ebean.config.dbplatform.DB2Platform;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
public class Engine extends Controller{
	public static Result getRecommendation(Integer userId){
		Recommender eng = new Recommender();
		ArrayList<String> result = eng.run(userId);
		ArrayList<models.db2.Location> output = new ArrayList<models.db2.Location>();
		for (int i=0; i < result.size(); i++){
			models.db2.Location loc = models.db2.Location.find.byId(result.get(i));
			output.add(loc);
		}
		return ok(testShow.render(output));
	}
	public static Result getMap(){
		ArrayList<models.db2.Location> output = new ArrayList<models.db2.Location>();
		List<models.db2.Location> dbLocation = models.db2.Location.find.all();
		for (int i=0; i < 10; i++){
			output.add(dbLocation.get(i));
		}
		JSONObject featureCollection = new JSONObject();
		String result="";
		try{
			featureCollection.put("type", "FeatureCollection");
			JSONArray featureList = new JSONArray();
			for (models.db2.Location loc : output){
				JSONObject point = new JSONObject();
				point.put("type", "Point");
				// construct a JSONArray from a string; can also use an array or list
	            JSONArray coord = new JSONArray("["+loc.longitude+","+loc.latitude+"]");
	            point.put("coordinates", coord);
	            JSONObject properties = new JSONObject();
	            if (loc.description != null){
	            	properties.put("title", loc.description);
	            }
	            else{
	            	properties.put("tutle", "Description not available");
	            }
	            properties.put("marker-color", "#f00");
	            JSONObject feature = new JSONObject();
	            feature.put("geometry", point);
	            feature.put("properties", properties);
	            feature.put("type", "Feature");
	            featureList.put(feature);
	            featureCollection.put("features", featureList);
			}
		}
		catch(JSONException ex){
			ex.printStackTrace();
		}
		return ok(map.render(output, featureCollection.toString()));
	}
	
}
