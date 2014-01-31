package controllers;

import java.util.ArrayList;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

public class Engine extends Controller{
	public static Result getRecommendation(Integer userId){
		Recommender eng = new Recommender();
		ArrayList<String> output = eng.run(userId);
		return ok(testShow.render(output));
	}
	public static Result getMap(){
		return ok(map.render());
	}
}
