package application;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class TaskList {

	//Viewmode for viewing the agenda per day or week, used as parameter in CalendarPane
	public static enum ReadMode {
		forDate, overDue
	}
	
	public static VBox getTaskListBox(LocalDate date, ReadMode readMode) {
		ArrayList<MainTask> taskList = TaskList.getTaskList(date, readMode);
		
		//Title label
		HBox titleBox = new HBox();
		
		if (readMode == ReadMode.forDate) {
			Label dayOfWeekLabel = new Label(date.getDayOfWeek().toString().substring(0,3));
			dayOfWeekLabel.setMinWidth(40);
			Label dateLabel = new Label(date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
			titleBox.getChildren().addAll(dayOfWeekLabel, dateLabel);
		} else if (readMode == ReadMode.overDue) {
			Label overdueLabel = new Label("OVERDUE");
			overdueLabel.setFont(new Font(overdueLabel.getFont().getName(), 12));
			titleBox.getChildren().add(overdueLabel);
		}
		
		VBox taskListBox = new VBox();
		taskListBox.setPadding(new Insets(0, 3, 3, 3));
		taskListBox.setStyle("-fx-border-color: grey; -fx-border-width: 1;");
		
		taskListBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		GridPane.setHgrow(taskListBox, Priority.ALWAYS);
		GridPane.setVgrow(taskListBox, Priority.ALWAYS);

		//Labels for tasks with spacing of 3 between tasks and between following add task button
		VBox taskBox = new VBox();
		taskBox.setSpacing(3);
		taskBox.setPadding(new Insets(0, 0, 3, 0));
		
		//Add taskPanes
		taskList.sort(null);
		for (MainTask task : taskList) {
			taskBox.getChildren().add(task.taskPane);
		}

		taskListBox.getChildren().addAll(titleBox, taskBox);
		return taskListBox;
	}
	
	public static ArrayList<MainTask> getTaskList(LocalDate date, ReadMode readMode) {
		//New tasklist
		ArrayList<MainTask> taskList = new ArrayList<MainTask>();
		
		//Set SQLString
		String SQLString = "SELECT * FROM tasks WHERE Date ";
		if (readMode == ReadMode.forDate) {
			SQLString += "=";
		} else if (readMode == ReadMode.overDue) {
			SQLString += "<";
		}
		SQLString += "'" + date.toString() + "'";

		//Get tasks from database
		SQLConnector.read(SQLString, rs -> {
			try {
				while (rs.next()) {
					//Parse string result from time query to LocalTime if it's not null
					LocalTime taskTime = null;
					if (rs.getString("Time") != null) {
						taskTime = LocalTime.parse(rs.getString("Time"));
					}

					//Create new main task
					MainTask newMainTask = new MainTask(rs.getInt("ID"), rs.getString("Name"), date, taskTime, Boolean.parseBoolean(rs.getString("Completed")), Boolean.parseBoolean(rs.getString("Expanded")));

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
					taskList.add(newMainTask);
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
		});
		return taskList;
	}
}
