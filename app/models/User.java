package models;

import java.util.*;
import javax.persistence.*;
import controllers.Application.Login;
import controllers.routes;
import play.data.Form;
import play.db.ebean.*;
import play.mvc.Result;

@Entity
public class User extends Model{
	
	@Id
	public String userId;
    public String name;
	public String email;
    public String password;
	
	public User(String userId){
		this.userId = userId;
		this.name ="";
		this.email="";
		this.password="";
	}
	
	public User(String userId, String name, String email, String password){
		this.userId = userId;
		this.name =name;
		this.email=email;
		this.password=password;
	}
	public void setUserId(String userId){
		this.userId = userId;
	}	
	public String getUserId(){
		return this.userId;
	}
	
	public static User authenticate(String userId, String password) {
    	System.out.println("IN USER authenticate userID: "+ userId+" password: "+password);		
        return find.where().eq("userId", userId).eq("password", password).findUnique();
		//return(new User(userId));
    }
	
	@Override
	public boolean equals(Object other){
		if(this == other) return true;
	    
		if(other == null || (this.getClass() != other.getClass())){
			return false;
	    }
	    User u = (User) other;
	    return this.userId.equals(u.getUserId());
	}
	@Override
	public int hashCode(){
		int result = 0;
		result = 31*result + (userId !=null ? userId.hashCode() : 0);
		return result;
	}
	public static Finder<String,User> find = new Finder<String,User>(String.class, User.class); 
}
