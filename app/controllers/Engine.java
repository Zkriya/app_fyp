package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	public static Result viewLocation(String locId){
		models.db2.Location location = models.db2.Location.find.byId(locId);
		String[] photos = null;
		if (location.photoUrl != null){
		 photos = location.photoUrl.split(",");
		}
		return ok(showLocation.render(location, photos));
	}
	public static Result visitedLocations() throws Exception{
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
		ArrayList<Location> locs = new ArrayList<Location>();
		ArrayList<Integer> vals = new ArrayList<Integer>();
		
		JSONObject featureCollection = new JSONObject();
		String result="";
		Integer index=0;
			featureCollection.put("type", "FeatureCollection");
			JSONArray featureList = new JSONArray();
			for (Location loc : countMap.keySet()){
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
		for (Map.Entry<Location, Integer> entry : countMap.entrySet()){
			locs.add(entry.getKey());
			vals.add(entry.getValue());
		}
		return ok(visitedLocations.render(locs,vals, featureCollection.toString()));
	}
	public static Result recommendMe(Integer numb) throws Exception{
		String email = session("email");
		if(email == null){
			return redirect(routes.Application.login());
		}
		User user = User.find.where().eq("email", email).findUnique();
		Recommender eng = new Recommender();
		ArrayList<String> result = eng.run(user.userId);
		List<Location> allLocs = Location.find.all();
		ArrayList<Location> output = new ArrayList<Location>();
		for (int i=0; i < numb; i++){
			Location loc;
			for (int j=0; j < allLocs.size(); j++){
				if (allLocs.get(j).locId.equals(result.get(i))){
					loc = allLocs.get(j);
					output.add(loc);
					break;
				}
			}
		}
		JSONObject featureCollection = new JSONObject();
		Integer index=0;
			featureCollection.put("type", "FeatureCollection");
			JSONArray featureList = new JSONArray();
			for (Location loc : output){
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
		return ok(map.render(output, featureCollection.toString(),"recommendMe"));
	}
	
}
