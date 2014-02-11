package models.db2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
@Table(name="CheckIn")
public class CheckIn extends Model{
	@Id 
	@Column(name="checkInId")
	public Integer checkInId;
	@Column(name="userId")
	public Integer userId;
	@Column(name="locId")
	public String locId;
	@Column(name="slot")
	public int slot;
	@Column(name="latitude")
	public double latitude;
	@Column(name="longitude")
	public double longitude;
	public static Model.Finder<Integer,CheckIn> find = new Finder<Integer,CheckIn>(Integer.class, CheckIn.class); 
}