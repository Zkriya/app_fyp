package models.db2;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;


@Entity
public class Location {
	@Id
	@Column(name="locId")
	public String locId;
	@Column(name="latitude")
	public double latitude;
	@Column(name="longitude")
	public double longitude;
	@Column(name="address")
	public String address;
	@Column(name="description", columnDefinition="TEXT")
	public String description;
	@Column(name="photoUrl")
	public String photoUrl;
	@Column(name="categories")
	public String categories;
	public static Model.Finder<String,Location> find = new Finder<String,Location>("main", String.class, Location.class); 
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
