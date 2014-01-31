package models.db2;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Birthday {
	@Column(name="day")
	public int day;
	@Column(name="month")
	public int month;
	@Column(name="year")
	public int year;
}

