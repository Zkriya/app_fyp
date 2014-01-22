package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class Checkin extends Model{
	@Id
	public String userId;
	@Id
	public String locId;
	public short hour;
	
	public Checkin(String userId, String locId, Short hour ){
		this.userId=userId;
		this.locId=locId;
		this.hour=hour;
	}
	
	public static Finder<String,Checkin> find = new Finder<String,Checkin>(String.class, Checkin.class);
	
	public static List<Checkin> findCheckin(String user){
		return find.where().eq("userId", user).findList();
	}
}
