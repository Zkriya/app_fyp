package controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Ebean;

import models.db2.User;
import play.data.*;
import play.db.DB;
import play.mvc.*;
import views.html.*;
import static play.data.Form.*;

public class Application extends Controller {

    public static Result index() {
        return ok(login.render(form(Login.class)));
    }
    
    public static Result login() {
        return ok(login.render(form(Login.class)));
    }
    
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(routes.Application.index());
    }
    
    public static Result authenticate() {
    	Form<Login> loginForm = form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("email", loginForm.get().email);
            return redirect(routes.Application.getHome());
        }
    }
    
    public static Result getHome(){
		String email = session("email");		
		if(email != null) {
			User user = User.find.where().eq("email", email).findUnique();
			return ok(home.render(user));
		} else {
			return redirect(routes.Application.login());
		}
    }
    
    public static Result newUser(){
    	return ok(newUser.render(form(User.class)));
    }
    
    public static Result createUser() throws Exception{
    	Form<User> userForm = form(User.class);
        /* 
    	Map<String, String[]> formData = request().body().asFormUrlEncoded();
    	String email = formData.get("email")[0];
    	String password = formData.get("password")[0];
    	String firstName = formData.get("firstName")[0];
    	String lastName = formData.get("lastName")[0];
    	int isMale = Integer.parseInt(formData.get("gender")[0]);
    	int day = Integer.parseInt(formData.get("day")[0]);
    	int month = Integer.parseInt(formData.get("month")[0]);
    	int year = Integer.parseInt(formData.get("year")[0]);
    	User newUser = new User(firstName, lastName, isMale, email, password, day, month, year);
    	*/
        return redirect(routes.Application.login());
    }
    public static class Login {
        public String email;
        public String password;
        
        public String validate() {
            if (User.authenticate(email, password) == null) {
              return "Invalid user or password";
            }
            return null;
        }

    }
}