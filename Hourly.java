/** Hourly.java:  An Employee Database GUI program. 
@author Sukkrishvar Vijay Santhana Krishnan 
@since 03/21/2024
*/

import java.util.*;

public class Hourly extends Employee {
	
	private double hoursWorked;

	public Hourly(String lg, String nm, double slry, byte[] password) {
		super(lg, nm, slry, password);
	}

	public Hourly(String lg, String nm, Date ent, int empID, double slry, byte[] password) {
		super(lg, nm, ent, empID, slry, password);
	}
	
	@Override
	public void setHours(double hours) {
		this.hoursWorked = hours;
	}

	@Override
	public double getPay() {
		double pay = eSalary * hoursWorked;
		return pay;
	}

}
