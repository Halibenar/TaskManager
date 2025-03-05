package application;

import java.sql.SQLException;
import java.util.ArrayList;

public class Category implements Comparable<Category> {

	private int iD;
	private String name;
	
	public Category(String name) {
		this.iD = 0;
		this.name = name;
	}
	
	public Category(int iD, String name) {
		this.iD = iD;
		this.name = name;
	}
	
	public void updateSQL() {
		//Get data to insert
		String[] data = new String[1];
		data[0] = this.getName();

		//If new task, add to database
		if (this.getID() == 0) {
			String insertString = "INSERT INTO categories (Name) VALUES (?)";
			this.setID(SQLConnector.insert(insertString, data));

		//If already exists, update records
		} else {
			String updateString = "UPDATE tasks SET Name = ? WHERE ID = " + this.getID();
			SQLConnector.update(updateString, data);
		}

		//Update UI
		Main.toDoPane.getTasks(Main.toDoPane.getCategory());
		Main.calendarPane.setPlanDate(Main.calendarPane.getPlanDate());
	}
	
	@Override
	public int compareTo(Category another) {
		if (another == null) {
			return 1;
		} else {
			return this.name.compareTo(another.name);
		}
	}
	
	public static ArrayList<Category> getCategoryList() {
		//New tasklist
		ArrayList<Category> categoryList = new ArrayList<Category>();
		
		//Set SQLString
		String SQLString = "SELECT * FROM categories";

		//Get tasks from database
		SQLConnector.read(SQLString, rs -> {
			try {
				while (rs.next()) {

					//Create new category
					Category newCategory = new Category(rs.getInt("ID"), rs.getString("Name"));
					
					//Add category to categorylist
					categoryList.add(newCategory);
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
		});
		return categoryList;
	}

	public int getID() {
		return iD;
	}

	public void setID(int iD) {
		this.iD = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
