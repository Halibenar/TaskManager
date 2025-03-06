package application;

import java.time.LocalDate;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

public class ToDoPane extends VBox {
	
	private VBox daysBox;
	private Category category;
	public EditTaskBox editTaskBox;

	public ToDoPane() {
		
		//HBox for controls
		HBox controlBar = new HBox();
		controlBar.setAlignment(Pos.CENTER_LEFT);
		controlBar.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 1 0;");
		
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
			newTask.setCategory(this.category);
			this.editTaskBox.setTask(newTask);
		});
		
		//Category selection list
		ObservableList<Category> categoryList = FXCollections.observableList(FXCollections.observableList(Category.getCategoryList()));
		categoryList.add(new Category(-1, "All"));
		
		ComboBox<Category> categoryBox = new ComboBox<>();
		categoryBox.setItems(categoryList);
		categoryBox.setConverter(new StringConverter<Category>() {

			@Override
			public String toString(Category object) {
				if (object != null) {
					return object.getName();
				} else {
					return "All";
				}
			}

			@Override
			public Category fromString(String string) {
				return categoryBox.getItems().stream().filter(ap -> 
				ap.getName().equals(string)).findFirst().orElse(null);
			}
		});
		categoryBox.setValue(Category.getCategoryList().get(0));
		
		//categoryBox.setEditable(true);
		categoryBox.setVisibleRowCount(3);
		categoryBox.setOnAction(e -> {
			this.getTasks(categoryBox.getValue());
		});
		
		controlBar.getChildren().addAll(addTaskButton, categoryBox);

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
		
		this.getChildren().addAll(controlBar, scrollPane, this.editTaskBox);
		Category.getCategoryList().stream().filter(c -> c.getID() == 0).forEach(Category -> { this.category = Category; });
		
		//Clear
		this.daysBox.getChildren().clear();
		this.getTasks(this.category);
	}

	public void getTasks(Category category) {
		this.category = category;
		
		ArrayList<Category> categoryDisplayList = new ArrayList<Category>();
		
		this.daysBox.getChildren().clear();

		if (this.category.getID() < 0) {
			for (Category displayCategory : Category.getCategoryList()) {
				if (displayCategory.getID() >= 0) {
					categoryDisplayList.add(displayCategory);
				}
			}
		} else {
			categoryDisplayList.add(this.category);
		}

		for (Category displayCategory : categoryDisplayList) {
			//Get tasks
			ArrayList<MainTask> taskList = MainTask.getMainTaskList(null, Task.ReadMode.equals, displayCategory);

			//Title label
			HBox titleBox = new HBox();
			String titleLabelString = "To Do";
			if (displayCategory != null) {
				titleLabelString = displayCategory.getName();
			}
			Label toDoLabel = new Label(titleLabelString);
			toDoLabel.setFont(new Font(toDoLabel.getFont().getName(), 12));
			titleBox.getChildren().add(toDoLabel);

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

			this.daysBox.getChildren().add(taskListBox);
		}
	}
	
	public Category getCategory() {
		return category;
	}
}
