package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import models.db2.Location;
import models.db2.User;

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
	public static Result viewLocation(String locId){
		models.db2.Location location = models.db2.Location.find.byId(locId);
		String[] photos = null;
		if (location.photoUrl != null){
		 photos = location.photoUrl.split(",");
		}
		return ok(showLocation.render(location, photos));
	}
	public static Result visitedLocations(){
		String email = session("email");
		if(email == null){
			return redirect(routes.Application.login());
		}
		User user = User.find.where().eq("email", email).findUnique();
		List<models.db2.CheckIn> checkIns = models.db2.CheckIn.find.where().eq("userId", user.userId).findList();
		HashMap<Location, Integer> countMap = new HashMap<Location, Integer>();
		for (int i=0; i < checkIns.size(); i++){
			Location loc = Location.find.where().eq("locId", checkIns.get(i).locId).findUnique();
			if (!countMap.containsKey(loc)){
				Integer val = Integer.valueOf(1);
				countMap.put(loc, val);
			}
			else{
				Integer val = countMap.get(loc);
				val++;
				countMap.put(loc, val);
			}
		}
		System.out.println(countMap);
		return ok(visitedLocations.render(countMap));
	}
	public static Result recommendMe(){
		ArrayList<models.db2.Location> output = new ArrayList<models.db2.Location>();
		List<models.db2.Location> dbLocation = models.db2.Location.find.all();
		for (int i=0; i < 10; i++){
			output.add(dbLocation.get(i));
		}
		JSONObject featureCollection = new JSONObject();
		String result="";
		Integer index=0;
		try{
			featureCollection.put("type", "FeatureCollection");
			JSONArray featureList = new JSONArray();
			for (models.db2.Location loc : output){
				index += 1;
				JSONObject point = new JSONObject();
				point.put("type", "Point");
				// construct a JSONArray from a string; can also use an array or list
	            JSONArray coord = new JSONArray("["+loc.longitude+","+loc.latitude+"]");
	            point.put("coordinates", coord);
	            JSONObject properties = new JSONObject();
	            String str = index + ". " + loc.name;
	            properties.put("title", str);
	            properties.put("description", loc.address);
	            properties.put("index", index);
	            properties.put("marker-id", "marker-" + index);
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
		return ok(map.render(output, featureCollection.toString(),"recommendMe"));
	}
	
}
