/** Salaried.java:  An Employee Database GUI program. 
@author Sukkrishvar Vijay Santhana Krishnan 
@since 03/21/2024
*/

import java.util.*;

public class Salaried extends Employee{

	public Salaried(String lg, String nm, double slry, byte[] password) {
		super(lg, nm, slry, password);
		
	}

	public Salaried(String lg, String nm, Date ent, int empID, double slry, byte[] password) {
		super(lg, nm, ent, empID, slry, password);
		
	}

	public double getPay() {
		return eSalary / 24;
	}

}
