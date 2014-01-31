package models.db1;


import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Location extends Model{
	@Id
	public String locId;
	public double latitude;
	public double longitude;
	
	public Location(){
	}
	public Location(String locId, double latitude, double longitude){
		this.locId = locId;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	@Override
	public boolean equals(Object other){
		if(this == other) return true;
	    
		if(other == null || (this.getClass() != other.getClass())){
			return false;
	    }
	    Location loc = (Location) other;
	    return this.locId.equals(loc.locId);
	}
	@Override
	public int hashCode(){
		int result = 0;
		result = 31*result + this.locId.hashCode();
		return result;
	}
}
