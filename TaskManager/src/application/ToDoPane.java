package application;

import java.time.LocalDate;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ToDoPane extends VBox {
	
	private VBox daysBox;
	public EditTaskBox editTaskBox;

	public ToDoPane() {
		//Add task button
		Button addTaskButton = new Button();
		addTaskButton.setMinSize(34, 34);
		addTaskButton.setMaxSize(34, 34);
		addTaskButton.setStyle("-fx-border-color: grey; -fx-border-width: 1 1 0 0;");
		Image addImage = new Image(getClass().getResourceAsStream("/icons/ButtonNew.png"), 34, 34, true, true);
		ImageView addImageView = new ImageView(addImage);
		addImageView.fitHeightProperty().bind(addTaskButton.heightProperty());
		addImageView.fitWidthProperty().bind(addTaskButton.widthProperty());
		addTaskButton.setGraphic(addImageView);
		addTaskButton.setOnAction(e -> {
			MainTask newTask = new MainTask((LocalDate)null);
			this.editTaskBox.setTask(newTask);
		});

		//VBox with tasks for each day
		this.daysBox = new VBox(1);
		this.daysBox.setPadding(new Insets(0, 1, 0, 0));

		//Enclose VBox in ScrollPane
		ScrollPane scrollPane = new ScrollPane(this.daysBox);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);

		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		
		VBox.setVgrow(scrollPane, Priority.ALWAYS);
		
		//EditTaskBox for editing tasks
		this.editTaskBox = new EditTaskBox();
		this.editTaskBox.setVisible(false);
		this.editTaskBox.setManaged(false);
		
		this.getChildren().addAll(addTaskButton, scrollPane, this.editTaskBox);
		this.getTasks();
	}

	public void getTasks() {
		//clear
		this.daysBox.getChildren().clear();
		
		//Display tasklistbox with tasks for every day
		this.daysBox.getChildren().add(Task.getTaskListBox(null, Task.ReadMode.toDo));
	}
}
