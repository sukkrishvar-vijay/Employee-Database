/** Employee.java:  An Employee Database GUI program. 
@author Sukkrishvar Vijay Santhana Krishnan 
@since 03/21/2024
 */

import java.util.Date;
import java.io.Serializable;
import java.text.SimpleDateFormat;

// Class called Employee
public abstract class Employee implements Serializable {
	protected String eLoginName; // attribute to store employee's login name
	protected double eSalary; // attribute to store employee's base salary
	protected String eName; // attribute to store employee's name
	protected Date eJoinDate; // attribute to store employee's date entered into the system
	protected final int eID; // attribute to store employee's ID
	protected static int nextId; // static attribute to store ID for next employee ID
	protected byte[] ePassword; // attribute to store employee's password

	// non-default constructor with 3 parameters
	public Employee(String lg, String nm, double slry, byte[] pass) {
		this.eLoginName = lg;
		this.eSalary = slry;
		this.eName = nm;
		this.eJoinDate = new Date();
		this.eID = Employee.nextId;
		this.ePassword = pass;
		Employee.nextId++;
	}

	// non-default constructor with 5 parameters
	public Employee(String lg, String nm, Date ent, int empID, double slry, byte[] pass) {
		this.eLoginName = lg;
		this.eSalary = slry;
		this.eName = nm;
		this.eJoinDate = ent;
		this.ePassword = pass;
		this.eID = empID;
		Employee.nextId = empID;
		Employee.nextId++;
	}

	// mutator method to set employee's base salary
	public void setSalary(double slry) {
		this.eSalary = slry;
	}

	// mutator method to set employee's name
	public void setName(String name) {
		this.eName = name;
	}

	// overridden toString method
	public String toString() {
		return String.format("%05d%-15s%-20s%-20s%-20s%-20s%-1s", this.eID, "", this.eLoginName,
				this.eSalary, new SimpleDateFormat("MM/dd/yyyy").format(this.eJoinDate),
				this.eName,this.ePassword);
	}

	// accessor method to get employee's ID
	public int getEmpID() {
		return this.eID;
	}

	// accessor method to get employee's login name
	public String getEmpLoginName() {
		return this.eLoginName;
	}

	// accessor method to get employee's name
	public String getEmpName() {
		return this.eName;
	}

	// accessor method to get employee's date registered on
	public Date getEmpDateJoined() {
		return this.eJoinDate;
	}

	// accessor method to get employee's base salary
	public double getEmpBaseSalary() {
		return this.eSalary;
	}
	
	// accessor method to get employee's password
	public byte[] getEmpPassword() {
		return this.ePassword;
	}

	// abstract method to get employee's pay
	public abstract double getPay();
	
	// method to set work hours
	public void setHours(double hoursWorked) {
		
	}
}
