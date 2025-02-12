package application;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Represents a date in the agenda. Holds a list of MainTasks.
 */
public class PlanDate {
	
	LocalDate date;
	ArrayList<MainTask> taskList = new ArrayList<MainTask>();
	VBox taskBox = new VBox();

	/**
	 * Creates PlanDate for the specified date.
	 * @param date
	 */
	public PlanDate(LocalDate date) {
		this.date = date;
	}

	/**
	 * Creates copy of a PlanDate.
	 * @param another PlanDate to copy
	 */
	public PlanDate (PlanDate another) {
		this.date = another.date;
		this.taskList = another.taskList;
	}
	
	@Override
	public String toString() {
		return this.date.toString();
	}
	
	
	/**
	 * Gets tasks for the date of this PlanDate from the SQLite database.
	 * Retrieved MainTasks are stored in taskList in this PlanDate. SubTasks are stored in subTaskList in their respective MainTasks.
	 */
	public void getTasks() {
		//Clear tasklist
		this.taskList.clear();

		//Get tasks from database
		SQLConnector.read("SELECT * FROM tasks WHERE Date = '" + this.toString() + "'", rs -> {
			try {
				while (rs.next()) {
					//Parse string result from time query to LocalTime if it's not null
					LocalTime taskTime = null;
					if (rs.getString("Time") != null) {
						taskTime = LocalTime.parse(rs.getString("Time"));
					}

					//Create new main task
					MainTask newMainTask = new MainTask(rs.getInt("ID"), rs.getString("Name"), this, taskTime, Boolean.parseBoolean(rs.getString("Completed")), Boolean.parseBoolean(rs.getString("Expanded")), Boolean.parseBoolean(rs.getString("Editmode")));

					//Get subtasks for main task
					SQLConnector.read("SELECT * FROM subtasks WHERE MainTaskID = " + newMainTask.getID(), rsSubTask -> {
						try {
							while (rsSubTask.next()) {
								//Create subtask
								SubTask newSubTask = new SubTask(newMainTask, rsSubTask.getInt("ID"), rsSubTask.getString("Name"), Boolean.parseBoolean(rsSubTask.getString("Completed")));
								//Add to subtasklist
								newMainTask.addToSubTaskList(newSubTask);
							}
						} catch (SQLException e) {
							System.out.println(e);
						}
					});
					
					//Add main task to tasklist
					this.taskList.add(newMainTask);
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
		});
	}
	
	/**
	 * Creates a UI element containing the MainTaskPane of all MainTasks in this PlanDate.
	 * The element VBox taskBox holds the MainTaskPanes and can be updated with updateTaskBox() when tasks are changed.
	 * @return VBox containing date label and MainTaskPanes
	 */
	public VBox createDayBox() {
		VBox dayBox = new VBox();
		dayBox.setPadding(new Insets(0, 3, 3, 3));
		dayBox.setStyle("-fx-border-color: grey; -fx-border-width: 1 0 1 1;");
		
		dayBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		GridPane.setHgrow(dayBox, Priority.ALWAYS);
		GridPane.setVgrow(dayBox, Priority.ALWAYS);

		//Day of week label
		Label dayOfWeekLabel = new Label(this.date.getDayOfWeek().toString().substring(0,3) + " " + this.toString().substring(8, 10));
		dayOfWeekLabel.setFont(new Font(dayOfWeekLabel.getFont().getName(), 12));

		//Labels for tasks with spacing of 3 between tasks and between following add task button
		this.taskBox = new VBox();
		this.taskBox.setSpacing(3);
		this.taskBox.setPadding(new Insets(0, 0, 3, 0));

		//Add task button
		Button addTaskButton = new Button("+");
		addTaskButton.setStyle("-fx-border-color: black; -fx-border-width: 1;");
		addTaskButton.setMinSize(35, 35);
		addTaskButton.setOnAction(_ -> {
			MainTask newTask = new MainTask(this);
			this.taskBox.getChildren().add(newTask.taskPane);
			((MainTaskPane)newTask.taskPane).setExpanded(true);
			newTask.taskPane.setEditMode(true);
		});
		dayBox.getChildren().addAll(dayOfWeekLabel, this.taskBox, addTaskButton);

		return dayBox;
	}
	
	/**
	 * Updates taskBox with MainTaskPanes of MainTasks in this PlanDate.
	 */
	public void updateTaskBox() {
		this.taskBox.getChildren().clear();
		for (MainTask task : this.taskList) {
			this.taskBox.getChildren().add(task.taskPane);
		}
	}
}