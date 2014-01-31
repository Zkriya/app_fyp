package models.db2;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;


@Entity
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
		
		public User(){
		}
		public User(Integer userId){
			this.userId = userId;
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
	public static Model.Finder<Integer,User> find = new Finder<Integer,User>("main", Integer.class, User.class);
	
}
