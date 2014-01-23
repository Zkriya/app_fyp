package controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Checkin;
import models.Location;
import models.User;
import play.data.*;
import play.mvc.*;
import views.html.*;
import static play.data.Form.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render(form(Login.class),"ok"));
        //return ok(login.render();
    }
    
    public static Result login() {
        return ok(login.render(form(Login.class),"ok"));
    }
    
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(routes.Application.index());
    }
    
    public static Result authenticate() {
    	System.out.println("authenticatinggggg");
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
        	System.out.println("error!!!!!!!!!");
            return badRequest(login.render(loginForm,"bad"));
        } else {
            session().clear();
            session("userId", loginForm.get().userId);
            return redirect(routes.Application.getHome());
        }
    }
    
    public static Result getHome(){
		String user = session("userId");
		//ArrayList<String> listofCheckin= new ArrayList<String>();
		//testcode
		//User bob = User.find.where().eq("email", "bob@gmail.com").findUnique();
		
		if(user != null) {
			return ok(home.render(user));
		} else {
			return unauthorized("Oops, you are not connected. Please log in");
		}
    }
    
    public static Result newUser(){
    	System.out.println("IN NEW USER");
    	return ok(newUser.render("welcome",null));
    }
    
    public static Result createUser(){
    	System.out.println("Creating USER");
    	Map<String, String[]> formData = request().body().asFormUrlEncoded();
    	String name = formData.get("name")[0];
    	System.out.println(name);
    	String email = formData.get("email")[0];
    	System.out.println(email);
    	String password = formData.get("password")[0];
    	System.out.println(password);
    	User newUser = new User("user_1", name, email, password);
    	newUser.save();
    	return redirect(routes.Application.index	());
    }
	    
    public static Result getResult(){
    	//test code on retreiving form data
    	//Map<String, String[]> formData = request().body().asFormUrlEncoded();
    	//String name = formData.get("name")[0];
    	//System.out.println(name);
    	Recommender a = new Recommender();
    	String name =session("userId");
    	//System.out.println("inGetResult");
		String dataFile= "app/controllers/tune.txt";
		ArrayList<String> result = a.run(dataFile, new User(name));
		System.out.println(result);
    	return ok(listResult.render("resultset",result));
    }
    public static Result getHistory(){
    	if(session("userId")==null){
			return unauthorized("Oops, you are not connected. Please log in");    		
    	}else{
			List<Checkin> c = Checkin.findCheckin(session("userId"));
			return ok(history.render("",c));
    	}
    }
    public static Result getUser(){
    	ArrayList<User> usr= new ArrayList<User>();
    	ArrayList<Location> allLocs = new ArrayList<Location>();
    	try{
			BufferedReader in = new BufferedReader(new FileReader("app/controllers/tune.txt"));
			String inLine;
			while((inLine = in.readLine()) != null){
					String[] arrStr = inLine.split("\t");
					//get user
					User user = new User(arrStr[0]);
					//get location
					Location l = new Location();
					l.locId = arrStr[1];
					String[] loc_parts = arrStr[2].split(",");
					l.latitude = Double.parseDouble(loc_parts[0]);
					l.longitude = Double.parseDouble(loc_parts[1]);
					Short timeOfCheckIn = Short.parseShort(arrStr[3]);
					//if new user create a default object and save
					if (!usr.contains(user)){
				    	User newUser = new User(user.userId, "JohnDoe", "email@email.com", "password");				    	
				    	try{
				    		newUser.save();
				    	}catch(Exception ex){
							//ex.printStackTrace();
						}				    	
						usr.add(user);
					}
					
					//if new location create a default location object
					if (!allLocs.contains(l)){
						Location newLocation = new Location(l.locId, l.latitude,l.longitude);
						try{
							newLocation.save();
				    	}catch(Exception ex){
							//ex.printStackTrace();
						}	
						allLocs.add(l);
					}
					
					//add checkin for user
					Checkin newCheckin = new Checkin(user.userId, l.locId, timeOfCheckIn);					
					try{
						newCheckin.save();
			    	}catch(Exception ex){
						//ex.printStackTrace();
					}
					
			}
			in.close();
    	}
		catch (IOException ex){
				ex.printStackTrace();
		}
    	return ok(listUser.render("hello",usr));
    }
    
    
    
    public static class Login {
        public String userId;
        public String password;
        
        public String validate() {
            if (User.authenticate(userId, password) == null) {
            	System.out.println("no userfound");
              return "Invalid user or password";
            }
        	System.out.println("userfound");
            return null;
        }

    }
}