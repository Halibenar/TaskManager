package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Extends VBox. Belongs to a Task. Displays Task name and completion and allows it to be edited and deleted.
 * TaskPanes of SubTasks are grouped on the UI under the MainTaskPane of the MainTask.
 */
public class TaskPane extends VBox {

	protected Task task;
	protected Boolean editMode = false;

	protected HBox taskBox = new HBox();
	protected Label taskNameLabel = new Label();
	protected TextField taskNameField = new TextField();
	protected CheckBox completeCheckBox = new CheckBox();
	protected Button deleteButton = new Button();

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

		//Name field
		this.taskNameField.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(this.taskNameField, Priority.ALWAYS);
		this.taskNameField.setMaxWidth(Double.MAX_VALUE);

		//Disable fields by default
		this.taskNameField.setVisible(false);
		this.taskNameField.setManaged(false);

		//Complete checkbox
		this.completeCheckBox.setOnAction(e -> {
			task.setCompleted(!task.isCompleted());
			e.consume();
		});
		
		//Delete button deletes subtask or task
		this.deleteButton.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 0 1;");
		this.deleteButton.setMinSize(34, 33);
		this.deleteButton.setMaxSize(34, 33);
		this.deleteButton.setOnAction(e -> {
			this.delete();
		});
		Image cancelImage = new Image(getClass().getResourceAsStream("ButtonCancel.png"));
		ImageView cancelImageView = new ImageView(cancelImage);
		cancelImageView.fitHeightProperty().bind(this.deleteButton.heightProperty());
		cancelImageView.fitWidthProperty().bind(this.deleteButton.widthProperty());
		this.deleteButton.setGraphic(cancelImageView);
		this.deleteButton.setVisible(false);
		this.deleteButton.setManaged(false);
		
		this.taskBox.getChildren().addAll(this.completeCheckBox, this.taskNameLabel, this.taskNameField, this.deleteButton);
		
		//Set name, completed
		this.setName(task.getName());
		if (!(this instanceof MainTaskPane)) {
			this.setCompleted(task.isCompleted());
		}
	}
	
	public void setName(String name) {
		this.taskNameLabel.setText(name);
		this.taskNameField.setText(name);
	}
	
	public void setCompleted(Boolean completed) {
		this.completeCheckBox.setSelected(completed);
		
		//Name label strikethrough if completed
		if (completed) {
			this.taskNameLabel.getStyleClass().clear();
			this.taskNameLabel.getStyleClass().add("labelStrikethrough");
			this.completeCheckBox.setSelected(true);
		} else {
			this.taskNameLabel.getStyleClass().clear();
			this.completeCheckBox.setSelected(false);
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

	/**
	 * Sets TaskPane editMode. Hides labels and shows fields to enable editing of name.
	 * @param editMode
	 */
	public void setEditMode(Boolean editMode) {
		this.editMode = editMode;

		//Set visibility
		this.taskNameLabel.setVisible(!editMode);
		this.taskNameLabel.setManaged(!editMode);
		this.taskNameField.setVisible(editMode);
		this.taskNameField.setManaged(editMode);
		this.deleteButton.setVisible(editMode);
		this.deleteButton.setManaged(editMode);
	}
}
