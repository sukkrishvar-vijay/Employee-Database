/** Payroll.java:  An Employee Database GUI program. 
@author Sukkrishvar Vijay Santhana Krishnan 
@since 03/21/2024
 */


import java.util.*;
import java.io.*;
import java.security.*;
import java.time.LocalDate;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import javafx.stage.Screen;
import javafx.event.ActionEvent;

// Payroll class
public class Payroll extends Application {
	private ArrayList<Employee> listOfEmployees; // attribute to store list of employees
	private ArrayList<Employee> listOfFiredEmployees; // attribute to store list of employees who quit or were fired
	private Employee currentUser; // attribute to store reference to employee object
	private Scanner fileScanner; // attribute to store reference to file scanner object
	private FileOutputStream fos;
	private ObjectOutputStream objectFileWriter; // attribute to store reference to object-output-stream object
	private FileInputStream fis;
	private ObjectInputStream objectFileReader;
	private PrintWriter payrollFileWriter; // attribute to store reference to print-writer object
	private int currentUserID; // attribute to store current user ID
	private static final String DATABASEFILE = "employeedatabase.txt";
	private static final String PAYROLLFILE = "payroll.txt";

	private Stage primaryStage;
	private boolean Passmatch;

	private TextField nameTextField;
	private TextField loginTextField;
	private PasswordField passwordField;
	private PasswordField rePasswordField;
	private TextField salaryTextField;
	private RadioButton salariedRadioButton;
	private RadioButton hourlyRadioButton;
	private ToggleGroup typeToggleGroup;

	private TextField loginUsernameTextField;
	private PasswordField loginPasswordField;

	private TextArea employeeListTextArea;

	private TextArea employeeDataTextArea;

	private TextField idTextField;
	private TextField changeNameTextField;
	private TextField changeSalaryTextField;

	private TextArea payrollDataTextArea;

	// default constructor
	public Payroll() {
	}

	// function to initialize variables
	private void initializePayroll() {
		listOfEmployees = new ArrayList<Employee>(); // initialize arraylist for the list of employees
		listOfFiredEmployees = new ArrayList<Employee>(); // initialize arraylist for the list of employees who quit or were fired
		currentUser = null; // reset the current user
		currentUserID = -1; // reset the current user ID
	}

	// function to create Login Screen
	private void loginScreen() {
		buildGui(1, primaryStage);
	}

	// function to create Boss Screen
	private void bossScreen() {
		buildGui(2, primaryStage);
	}

	// function to create Create New Employee Screen
	private void createNewEmployeeScreen() {
		buildGui(3, primaryStage);
	}

	// function to create Change Employee Screen
	private void changeEmployeeScreen() {
		buildGui(4, primaryStage);
	}

	// function to create Payroll Data Screen
	private void payrollScreen() {
		buildGui(5, primaryStage);
	}

	// function to create Non Boss Screen
	private void employeeScreen() {
		buildGui(6, primaryStage);
	}

	// function to do login
	private class DoLoginAction implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {
			boolean employeeFound = false;
			
			// loop to look up the list of employees to find the employee with entered login name and do login
			for (Employee e : listOfEmployees) {
				try {
					if (loginUsernameTextField.getText().equals(e.getEmpLoginName()) && Arrays.toString(computeHash(loginPasswordField.getText())).equals(Arrays.toString(e.getEmpPassword()))) {
						currentUser = e; // set current user
						currentUserID = e.getEmpID(); // set current user ID
						employeeFound = true; // employee record found
						if (currentUserID == 0) {
							bossScreen(); // go to this screen if current user is Boss
						} else {
							employeeScreen();  // go to this screen if current user is other employee
						}
						break;
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if (!employeeFound) {
				popUp("Error", "Employee record not found. Please enter correct login name and password.");
			}
		}
	}

	// Class and function to create new employee 
	private class CreateNewEmployeeAction implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			// check if all necessary fields are filled in or not
			if (nameTextField.getText().isEmpty() || loginTextField.getText().isEmpty()
					|| passwordField.getText().isEmpty() || rePasswordField.getText().isEmpty()) {
				popUp("Error", "Fill all the data fields.");
				typeToggleGroup.selectToggle(null);
				return;
			}
			
			// get user input from fields
			String fullName = nameTextField.getText();
			String loginName = loginTextField.getText();
			double salary = Double.parseDouble(salaryTextField.getText());
			byte[] password = getNewPassword();
			String employeeType = ((RadioButton) typeToggleGroup.getSelectedToggle()).getText();

			// look for duplicate login name
			boolean duplicateLoginNameFound = false;
			for (Employee e : listOfEmployees) {
				if (e.getEmpLoginName().equals(loginName)) {
					duplicateLoginNameFound = true;
					break;
				}
			}

			// reset employee type radio buttons if duplicate login name found
			if (duplicateLoginNameFound) {
				typeToggleGroup.selectToggle(null);
				popUp("Error", "Login name already exists! Try another one.");
				return;
			}

			// check if password and retyped password matches or not
			if (!Passmatch) {
				typeToggleGroup.selectToggle(null);
				popUp("Error", "Passwords do not match. Try again.");
				return;
			}

			// create new employee based on the selected type
			Employee newEmployee = null;
			if ("Salaried".equals(employeeType)) {
				newEmployee = new Salaried(loginName, fullName, salary, password);
			} else if ("Hourly".equals(employeeType)) {
				newEmployee = new Hourly(loginName, fullName, salary, password);
			}

			if (newEmployee != null) {
				listOfEmployees.add(newEmployee);
				if (newEmployee.getEmpID() == 0) {
					popUp("Success", "Boss record created successfully.");
				} else {
					popUp("Success", "Employee record created successfully.");
				}

				// redirect to intended screens
				if (currentUser == null) {
					loginScreen();
				} else {
					bossScreen();
				}

			}
		}
	}

	// function to display employee records
	private void listEmployees() {
		// display all employee records if the current user is the Boss
		if (currentUserID == 0) {
			employeeListTextArea.setText("List Of Employees:\n");
			for (Employee e : listOfEmployees) {
				employeeListTextArea.appendText(e.toString() + "\n");
			}
		}
		// display only the current user's information if the user is not the Boss
		else {
			employeeDataTextArea.setText("Your Information:\n");
			for (Employee e : listOfEmployees) {
				if (currentUserID == e.getEmpID()) {
					employeeDataTextArea.appendText(e.toString());
					break;
				}
			}
		}
	}
	
	public class FindEmployeeAction implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			int employeeId = Integer.parseInt(idTextField.getText()); // get employee ID data from field
			boolean employeeIDFound = false;
			
			for (Employee e : listOfEmployees) {
				if (e.getEmpID() == employeeId) {
					changeNameTextField.setText(e.getEmpLoginName());
					changeSalaryTextField.setText(String.valueOf(e.getEmpBaseSalary()));
					employeeIDFound = true;
					break;
				}
			}
			if (!employeeIDFound) {
				changeNameTextField.setText("");
				changeSalaryTextField.setText("");
				popUp("Employee not found", "Enter valid employee ID.");
			}
			
		}

	}

	// Class and function to update existing employee record
	private class ChangeEmployeeAction implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			int employeeId = Integer.parseInt(idTextField.getText()); // get employee ID data from field
			boolean employeeIDFound = false;

			// loop to find the employee record
			for (Employee e : listOfEmployees) {
				if (e.getEmpID() == employeeId) {
					String newName = changeNameTextField.getText();
					if (!newName.isEmpty()) {
						e.setName(newName); // update employee name
					}

					double salary = Double.parseDouble(changeSalaryTextField.getText());
					if (salary > -1) {
						e.setSalary(salary); // update employee salary
					}
					employeeIDFound = true;
					popUp("Success", "Employee record updated successfully.");
					bossScreen();
					break;
				}
			}
			if (!employeeIDFound) {
				changeNameTextField.setText("");
				changeSalaryTextField.setText("");
				popUp("Employee not found", "Enter valid employee ID.");
			}
		}
	}

	// helper method to write all employee data to file
	private void writeAllEmployeeRecordsToFile() throws IOException {
		fos = new FileOutputStream(DATABASEFILE);
		objectFileWriter = new ObjectOutputStream(fos);
		for (Employee e : listOfEmployees) {
			objectFileWriter.writeObject(e);
		}
	}

	// Class and function to terminate an employee or an employee quits by himself
	private class TerminateEmployeeAction implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (currentUserID != 0) {
				// employee quit the job by himself and reset attributes
				for (Employee e : listOfEmployees) {
					if (e.getEmpID() == currentUserID) {
						listOfFiredEmployees.add(e);
						listOfEmployees.remove(e);
						currentUser = null;
						currentUserID = -1;
						popUp("Success", "You have quit the job successfully. Logging out.");
						logout();
						break;
					}
				}
			} else {
				int employeeId = Integer.parseInt(idTextField.getText()); // get employee ID data from field

				boolean employeeIDFound = false;

				// terminate employee and reset attributes
				for (Employee e : listOfEmployees) {
					if (e.getEmpID() == employeeId) {
						employeeIDFound = true;
						listOfFiredEmployees.add(e);
						listOfEmployees.remove(e);
						popUp("Success", "Employee terminated successfully.");
						bossScreen();
						break;
					}
				}
				if (!employeeIDFound) {
					popUp("Error", "No employee record found with the entered employee ID.");
				}
			}
		}
	}

	// Class and function to process payroll data
	private class PayEmployeesAction implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {
			payrollScreen(); // redirect to payroll data screen
			payrollFileWriter.println("Payroll Report (" + LocalDate.now() + ")");
			payrollDataTextArea.setText("\nPayroll Report (" + LocalDate.now() + ")\n");

			// write Payroll Report table headers to file
			payrollFileWriter.println(String.format("%-20s%-20s%-20s", "Employee ID", "Employee Name", "Pay Amount"));
			
			// display Payroll Report table headers on the text area
			payrollDataTextArea
					.appendText(String.format("%-20s%-20s%-20s\n", "Employee ID", "Employee Name", "Pay Amount"));

			// display ID, name and pay amount for all employees
			for (Employee emp : listOfEmployees) {
				double pay;
				if (emp instanceof Salaried) {
					pay = emp.getPay();
				} 
				else {
					
				boolean validInput = true;
				while(validInput) {
					TextInputDialog dialog = new TextInputDialog();
				    dialog.setTitle("Hours Worked");
				    dialog.setHeaderText(String.format("Enter hours worked during this pay period for %s :", emp.getEmpLoginName()));
				    dialog.setContentText("Hours Worked:");

				    // the dialog will be displayed and wait for the user's input
				    Optional<String> hours = dialog.showAndWait();
				    double hoursWorked = 0;

				    if (hours.isPresent()) {
				        try {
				            hoursWorked = Double.parseDouble(hours.get());
				            validInput = false;
				        } catch (NumberFormatException e) {
				            popUp("Error", "Invalid input! Please enter a valid number.");
				        }
				    }
				    emp.setHours(hoursWorked);
				}
					pay = emp.getPay();
				}
				// write to payroll file
				payrollFileWriter
						.println(String.format("%05d%15s%-20s%.2f", emp.getEmpID(), "", emp.getEmpName(), pay));
				// print on text area
				payrollDataTextArea.appendText(String
						.format(String.format("%05d%15s%-20s%.2f\n", emp.getEmpID(), "", emp.getEmpName(), pay)));
			}
		}
	}
	
	// function to show a dialog box for employees who were terminated or quit when exiting the system
	private void showFiredEmployeeListDialog(String content) {
	    // Create a new dialog
	    Dialog<String> dialog = new Dialog<>();
	    dialog.setTitle("List of Employees Who Have Quit or Been Fired");

	    // Set up a TextArea inside the dialog
	    TextArea employeesFiredorQuit = new TextArea(content);
	    employeesFiredorQuit.setStyle("-fx-font-family: 'Arial';");
	    employeesFiredorQuit.setEditable(false);
	    employeesFiredorQuit.setWrapText(true);
	    employeesFiredorQuit.setPrefWidth(900);
	    employeesFiredorQuit.setMaxWidth(Double.MAX_VALUE);
	    employeesFiredorQuit.setMaxHeight(Double.MAX_VALUE);
	    GridPane.setVgrow(employeesFiredorQuit, Priority.ALWAYS);
	    GridPane.setHgrow(employeesFiredorQuit, Priority.ALWAYS);

	    // Grid Pane
	    GridPane grid = new GridPane();
	    grid.setMaxWidth(Double.MAX_VALUE);
	    grid.add(employeesFiredorQuit, 0, 0);

	    // Set the dialog pane content
	    dialog.getDialogPane().setContent(grid);

	    // Add a button to close the dialog
	    ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
	    dialog.getDialogPane().getButtonTypes().add(closeButton);

	    // Show the dialog and wait for the user to close it
	    dialog.showAndWait();
	}


	// function to logout of the system
	private void logout() {
		// display the list of employees who quit or been fired as soon as the program exits
		if (!listOfFiredEmployees.isEmpty()) {
			popUp("Info","Employee has been terminated");
			String s = "";
			for (Employee e : listOfFiredEmployees) {
				s = s + e.toString() + "\n";
			}
			showFiredEmployeeListDialog(s);
		} 
		
		// close all file read and write objects and scanners
		try {
			writeAllEmployeeRecordsToFile(); // write data back to object file
			if (fileScanner != null)
				fileScanner.close();
			if (fos != null)
				fos.close();
			if (fis != null)
				fis.close();
			if (objectFileWriter != null)
				objectFileWriter.close();
			if (objectFileReader != null)
				objectFileReader.close();
			if (payrollFileWriter != null)
				payrollFileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		popUp("Info", "Exiting the program.");
		System.exit(0);
	}

	// Class and function to logout
	private class LogoutAction implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {
			logout();
		}
	}
	
	private byte[] computeHash(String password) {
		MessageDigest msg = null;
		try {
			msg = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return msg.digest(password.getBytes());
	}
	
	private byte[] getNewPassword() {
		byte enterdPassword[] = computeHash(passwordField.getText());
		byte reEnteredPassword[] = computeHash(rePasswordField.getText());
		if (Arrays.toString(enterdPassword).equals(Arrays.toString(reEnteredPassword))) {
			Passmatch = true;
			return enterdPassword;
		}
		else {
			Passmatch = false;
			return null;
		}
	}


	// function to build screens
	private void buildGui(int menu, Stage primaryStage) {
		switch (menu) {
		// Login Screen
		case 1: {
			primaryStage.setTitle("Login Screen");

			// Grid Pane
			GridPane gridPane = new GridPane();
			gridPane.setAlignment(Pos.CENTER);
			gridPane.setHgap(10);
			gridPane.setVgap(10);

			// Login name label and text field
			Label usernameLabel = new Label("User Name:");
			loginUsernameTextField = new TextField();
			gridPane.add(usernameLabel, 0, 0);
			gridPane.add(loginUsernameTextField, 1, 0);

			// Password label and password field
			Label passwordLabel = new Label("Password:");
			loginPasswordField = new PasswordField();
			gridPane.add(passwordLabel, 0, 1);
			gridPane.add(loginPasswordField, 1, 1);

			// Login button with an action handler to execute logging in into the system
			Button loginButton = new Button("Login");
			gridPane.add(loginButton, 1, 2);
			loginButton.setOnAction(new DoLoginAction());

			// Set the scene and show the stage
			Scene scene = new Scene(gridPane, 350, 250);
			primaryStage.setScene(scene);
			primaryStage.show();
			centerStageOnScreen(primaryStage);
			break;
		}
		// Boss Screen
		case 2: {
			primaryStage.setTitle("Boss Screen");

			// Border Pane
			BorderPane borderPane = new BorderPane();

			// Text area for displaying employees and is set as scrollable and non-editable
			employeeListTextArea = new TextArea();
			employeeListTextArea.setStyle("-fx-font-family: 'Arial';");
			employeeListTextArea.setEditable(false);
			employeeListTextArea.setWrapText(true);
			listEmployees(); // function to list all employees when this screen loads

			// Buttons for going to screens for creating new employee, update employee, do payroll and logout
			Button createEmployeeButton = new Button("Create New Employee");
			Button employeeUpdateButton = new Button("Update Employee");
			Button payrollButton = new Button("Pay Employees");
			Button logoutButton = new Button("Logout");
			
			// action handler to go to create new employee screen
			createEmployeeButton.setOnAction(event -> {
				createNewEmployeeScreen();
			});

			// action handler to go to update employee screen
			employeeUpdateButton.setOnAction(event -> {
				changeEmployeeScreen();
			});

			// action handler to go to payroll screen
			payrollButton.setOnAction(new PayEmployeesAction());
			
			// action handler to logout
			logoutButton.setOnAction(new LogoutAction());

			// HBox for layout of buttons
			HBox buttonBox = new HBox(10, createEmployeeButton, employeeUpdateButton,
					payrollButton, logoutButton);
			buttonBox.setAlignment(Pos.CENTER);
			buttonBox.setPadding(new Insets(15, 12, 15, 12));

			// add components to the main layout
			borderPane.setCenter(employeeListTextArea);
			borderPane.setBottom(buttonBox);

			// set the scene and show the stage
			Scene scene = new Scene(borderPane, 900, 400);
			primaryStage.setScene(scene);
			primaryStage.show();
			centerStageOnScreen(primaryStage);
			break;
		}
		// Create New Employee Screen
		case 3: {
			primaryStage.setTitle("Create New Employee");

			// Grid Pane
			GridPane gridPane = new GridPane();
			gridPane.setAlignment(Pos.CENTER);
			gridPane.setVgap(10);
			gridPane.setHgap(10);
			gridPane.setPadding(new Insets(10, 10, 10, 10));

			// Full name label and text field
			Label nameLabel = new Label("Full Name:");
			nameTextField = new TextField();
			gridPane.add(nameLabel, 0, 0);
			gridPane.add(nameTextField, 1, 0);

			// Login name label and text field
			Label loginLabel = new Label("Login Name:");
			loginTextField = new TextField();
			gridPane.add(loginLabel, 0, 1);
			gridPane.add(loginTextField, 1, 1);

			// Password label and password field
			Label passwordLabel = new Label("Password:");
			passwordField = new PasswordField();
			gridPane.add(passwordLabel, 0, 2);
			gridPane.add(passwordField, 1, 2);

			// Retype-Password label and password field
			Label retypePasswordLabel = new Label("Retype Password:");
			rePasswordField = new PasswordField();
			gridPane.add(retypePasswordLabel, 0, 3);
			gridPane.add(rePasswordField, 1, 3);

			// Salary label and text field
			Label salaryLabel = new Label("Salary:");
			salaryTextField = new TextField();
			gridPane.add(salaryLabel, 0, 4);
			gridPane.add(salaryTextField, 1, 4);

			// Employee type and radio buttons with toggle group
			Label typeLabel = new Label("Employee Type:");
			salariedRadioButton = new RadioButton("Salaried");
			hourlyRadioButton = new RadioButton("Hourly");
			typeToggleGroup = new ToggleGroup();
			salariedRadioButton.setToggleGroup(typeToggleGroup);
			hourlyRadioButton.setToggleGroup(typeToggleGroup);
			HBox radioBox = new HBox(10, hourlyRadioButton, salariedRadioButton);
			gridPane.add(typeLabel, 0, 5);
			gridPane.add(radioBox, 1, 5);
			
			Button btn = new Button("Create");
			HBox hbBtn = new HBox(10);
			hbBtn.setAlignment(Pos.CENTER);
			hbBtn.getChildren().add(btn);
			gridPane.add(hbBtn, 1, 6);
			btn.setOnAction(new CreateNewEmployeeAction());

			// set the scene and show the stage
			Scene scene = new Scene(gridPane, 600, 400);
			primaryStage.setScene(scene);
			primaryStage.show();
			centerStageOnScreen(primaryStage);
			break;
		}
		// Edit Employee Screen
		case 4: {
			primaryStage.setTitle("Update Employee Information");

			// Grid Pane
			GridPane gridPane = new GridPane();
			gridPane.setAlignment(Pos.CENTER);
			gridPane.setVgap(10);
			gridPane.setHgap(10);
			gridPane.setPadding(new Insets(10, 10, 10, 10));

			// Employee ID label and text field
			Label idLabel = new Label("Employee ID:");
			idTextField = new TextField();
			gridPane.add(idLabel, 0, 0);
			gridPane.add(idTextField, 1, 0);
			
			Button searchButton = new Button("Search");
			searchButton.setOnAction(new FindEmployeeAction());
			HBox h1 = new HBox(10, searchButton);
			h1.setAlignment(Pos.CENTER_RIGHT);
			gridPane.add(h1, 1, 1);

			// Employee Name label and text field
			Label nameLabel = new Label("Employee New Name:");
			changeNameTextField = new TextField();
			gridPane.add(nameLabel, 0, 2);
			gridPane.add(changeNameTextField, 1, 2);

			// Employee Salary label and text field
			Label salaryLabel = new Label("Employee New Salary:");
			changeSalaryTextField = new TextField();
			gridPane.add(salaryLabel, 0, 3);
			gridPane.add(changeSalaryTextField, 1, 3);

			// Button to terminate employee
			Button fireEmpButton = new Button("Fire Employee");
			fireEmpButton.setOnAction(new TerminateEmployeeAction());
			HBox h2 = new HBox(10, fireEmpButton);
			h2.setAlignment(Pos.CENTER);
			gridPane.add(h2, 0, 4);

			// OK Button to confirm changes and return to Boss screen
			Button changeEmpOkButton = new Button("OK");
			changeEmpOkButton.setOnAction(new ChangeEmployeeAction());
			HBox h3 = new HBox(10, changeEmpOkButton);
			h3.setAlignment(Pos.CENTER);
			gridPane.add(h3, 1, 4);
			
			

			// Set the scene and show the stage
			Scene scene = new Scene(gridPane, 600, 400);
			primaryStage.setScene(scene);
			primaryStage.show();
			centerStageOnScreen(primaryStage);
			break;
		}
		// Payroll Data Screen
		case 5: {
			primaryStage.setTitle("Payroll Screen");

			// Border Pane
			BorderPane borderPane = new BorderPane();

			// Scrollable and non-editable text area for displaying payroll data
			payrollDataTextArea = new TextArea();
			payrollDataTextArea.setStyle("-fx-font-family: 'Arial';");
			payrollDataTextArea.setEditable(false); 
			payrollDataTextArea.setWrapText(true); 

			// OK Button to return to Boss screen
			Button okButton = new Button("OK");
			okButton.setOnAction(event -> {
				bossScreen();
			});

			// Logout button
			Button logoutButton = new Button("Logout");
			logoutButton.setOnAction(new LogoutAction());

			// VBox for the OK button
			HBox buttonBox = new HBox(10);
			buttonBox.getChildren().setAll(okButton, logoutButton);
			buttonBox.setAlignment(Pos.CENTER);
			buttonBox.setPadding(new Insets(10, 0, 10, 0));

			// Adding components to the layout
			borderPane.setCenter(payrollDataTextArea);
			borderPane.setBottom(buttonBox);

			// Set the scene and show the stage
			Scene scene = new Scene(borderPane, 600, 400);
			primaryStage.setScene(scene);
			primaryStage.show();
			centerStageOnScreen(primaryStage);
			break;
		}
		// Employee Data Screen
		case 6: {
			primaryStage.setTitle("Employee Data");

			// VBox layout
			VBox vbox = new VBox(10);
			vbox.setAlignment(Pos.CENTER);
			vbox.setPadding(new Insets(20, 20, 20, 20));

			// Non-editable text area for displaying employee data
			employeeDataTextArea = new TextArea();
			employeeDataTextArea.setStyle("-fx-font-family: 'Arial';");
			employeeDataTextArea.setEditable(false);
			employeeDataTextArea.setWrapText(true);
			listEmployees(); // list the current employee's data

			// Quit Button and action handler to quit the company
			Button quitButton = new Button("Quit");
			quitButton.setOnAction(new TerminateEmployeeAction());

			// Logout button
			Button logoutButton = new Button("Logout");
			logoutButton.setOnAction(new LogoutAction());
			
			HBox h1 = new HBox(10, quitButton, logoutButton);
			h1.setAlignment(Pos.CENTER);

			// Adding components to the VBox layout
			vbox.getChildren().addAll(employeeDataTextArea, h1);

			// Setting the scene and showing the stage
			Scene scene = new Scene(vbox, 900, 500);
			primaryStage.setScene(scene);
			primaryStage.show();
			centerStageOnScreen(primaryStage);
			break;
		}
		default: {
			break;
		}
		}
	}

	// Helper function to show Alerts
	private void popUp(String title, String content) {
		// Create an alert with no default alert type (NONE)
		Alert alert = new Alert(Alert.AlertType.NONE);
		alert.setTitle(title);
		alert.setContentText(content);
		alert.setHeaderText(null);

		// Create a 'Close' button for the alert
		ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
		
		// Add the 'Close' button to the alert
		alert.getButtonTypes().setAll(closeButton);

		// Display the alert and wait for the user to close it
		alert.showAndWait();
	}

	// Helper function to ensure screens are displayed at the center of the screen
	private void centerStageOnScreen(Stage stage) {
		// Get the primary screen's bounds
		javafx.geometry.Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

		// Calculate the center position
		double centerXPos = screenBounds.getMinX() + (screenBounds.getWidth() - stage.getWidth()) / 2;
		double centerYPos = screenBounds.getMinY() + (screenBounds.getHeight() - stage.getHeight()) / 2;

		// Set the stage's position to the center calculated above
		stage.setX(centerXPos);
		stage.setY(centerYPos);
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.initializePayroll();
		try {
			// try block to open employee record file and read data from it
			try {
				fis = new FileInputStream(DATABASEFILE);
				objectFileReader = new ObjectInputStream(fis);
				File file = new File(PAYROLLFILE);
				payrollFileWriter = new PrintWriter(file);

				// loop to read employee data from file
				while (true) {
					try {
						Employee emp = (Employee) objectFileReader.readObject();
						if (emp instanceof Salaried) {
							Employee e = new Salaried(emp.getEmpLoginName(), emp.getEmpName(), emp.getEmpDateJoined(), emp.getEmpID(), emp.getEmpBaseSalary(),
									emp.getEmpPassword());
							listOfEmployees.add(e);
						} else {
							Employee e = new Hourly(emp.getEmpLoginName(), emp.getEmpName(), emp.getEmpDateJoined(), emp.getEmpID(), emp.getEmpBaseSalary(),
									emp.getEmpPassword());
							listOfEmployees.add(e);
						}
					} catch (EOFException e) {
						loginScreen();
						break;
					}
				}
			}
			// catch block if file not found on first run
			catch (FileNotFoundException e) {
				popUp("Not Found", "Database File not found");
				createNewEmployeeScreen();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
