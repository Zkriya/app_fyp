package models.db2;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;


@Entity
@Table(name="User")
public class User extends Model{
		@Id
		@Column(name="userId")
		public Integer userId;
		@Column(name="firstName")
		public String firstName;
		@Column(name="lastName")
		public String lastName;
		@Column(name="isMale")
		public int isMale;
		@Column(name="email")
		public String email;
		@Column(name="password")
		public String password;
		public Birthday birthday;
		private static Integer generatedIds = 0;
		public User(){
		}
		public User(Integer userId){
			this.userId = userId;
		}
		public User(String firstName, String lastName, int isMale, String email, String password, int day, int month, int year){
			this.userId = ++generatedIds;
			this.firstName = firstName;
			this.lastName = lastName;
			this.isMale = isMale;
			this.email = email;
			this.password = password;
			Birthday birth = new Birthday();
			birth.day = day;
			birth.month = month;
			birth.year = year;
			this.birthday = birth;
		}
		public static User authenticate(String email, String password){
			return find.where().eq("email", email).eq("password", password).findUnique();
		}
	@Override
	public boolean equals(Object other){
		if(this == other) return true;
	    
		if(other == null || (this.getClass() != other.getClass())){
			return false;
	    }
	    User u = (User) other;
	    return this.userId.equals(u.userId);
	}
	@Override
	public int hashCode(){
		int result = 0;
		result = 31*result + (userId !=null ? userId.hashCode() : 0);
		return result;
	}
	public static Model.Finder<Integer,User> find = new Finder<Integer,User>(Integer.class, User.class);
}
