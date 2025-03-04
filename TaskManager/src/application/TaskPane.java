package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Extends VBox. Belongs to a Task. Displays Task name and completion and allows it to be edited and deleted.
 * TaskPanes of SubTasks are grouped on the UI under the MainTaskPane of the MainTask.
 */
public class TaskPane extends VBox {

	protected Task task;

	protected HBox taskBox = new HBox();
	protected Label taskNameLabel = new Label();
	protected CheckBox completeCheckBox = new CheckBox();

	/**
	 * Creates TaskPane to display information on a SubTask.
	 * @param task SubTask to display
	 */
	public TaskPane(Task task) {
		this.task = task;
		this.setPadding(new Insets(4,0,4,9));	
		this.setStyle("-fx-border-color: grey; -fx-border-width: 1 0 0 1;");
		HBox.setHgrow(this, Priority.ALWAYS);
		this.setMaxWidth(Double.MAX_VALUE);

		//HBox for task
		this.taskBox = new HBox(10);
		this.taskBox.setAlignment(Pos.CENTER_LEFT);
		this.taskBox.setMinHeight(25);
		this.taskBox.setMaxHeight(25);
		this.getChildren().add(this.taskBox);

		//Name label
		this.taskNameLabel.setAlignment(Pos.CENTER_LEFT);
		this.taskNameLabel.setPadding(new Insets(0,7,0,7));
		HBox.setHgrow(this.taskNameLabel, Priority.ALWAYS);
		this.taskNameLabel.setMaxWidth(Double.MAX_VALUE);

		//Complete checkbox
		this.completeCheckBox.setOnAction(e -> {
			task.setCompleted(!task.isCompleted());
			e.consume();
		});
		
		this.taskBox.getChildren().addAll(this.completeCheckBox, this.taskNameLabel);
		
		//Set name, completed
		this.setName(task.getName());
		if (!(this instanceof MainTaskPane)) {
			this.setCompleted(task.isCompleted());
		}
	}
	
	public void setName(String name) {
		this.taskNameLabel.setText(name);
	}
	
	public void setCompleted(Boolean completed) {
		this.completeCheckBox.setSelected(completed);
		
		//Name label strikethrough if completed
		if (completed) {
			this.taskNameLabel.getStyleClass().clear();
			this.taskNameLabel.getStyleClass().add("labelStrikethrough");
		} else {
			this.taskNameLabel.getStyleClass().clear();
		}
		
		//rebuild subtasklist to change order
		if (this.task instanceof SubTask) {
			((MainTaskPane)((SubTask)this.task).getMainTask().taskPane).addSubTaskPanes();
		}
	}
	
	/**
	 * Sets TaskPane as invisible and deletes Task from SQLite database.
	 */
	public void delete() {
		this.task.deleteSQL();
		this.setVisible(false);
		this.setManaged(false);
		
		if (this.task instanceof SubTask) {
			((SubTask)this.task).getMainTask().removeFromSubTaskList((SubTask)this.task);
		}
	}
}
