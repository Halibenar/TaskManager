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
import javafx.scene.control.TextField;
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
	private Boolean addMode;
	private HBox pickBox;
	private Button deleteCategoryButton;
	private HBox addBox;

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
		
		//Add category button
		Button addCategoryButton = new Button("A");
		addCategoryButton.setMinSize(34, 34);
		addCategoryButton.setMaxSize(34, 34);
		addCategoryButton.setStyle("-fx-border-color: grey; -fx-border-width: 1 1 0 0;");
//		Image addImage = new Image(getClass().getResourceAsStream("/icons/ButtonNew.png"), 34, 34, true, true);
//		ImageView addImageView = new ImageView(addImage);
//		addImageView.fitHeightProperty().bind(addCategoryButton.heightProperty());
//		addImageView.fitWidthProperty().bind(addCategoryButton.widthProperty());
//		addCategoryButton.setGraphic(addImageView);
		addCategoryButton.setOnAction(e -> {
			this.setAddMode(!this.getAddMode());
		});
		
		//HBox for category selection
		this.pickBox = new HBox();
		this.pickBox.setAlignment(Pos.CENTER_LEFT);
		this.pickBox.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 1 0;");
		
		//Category selection box
		ObservableList<Category> categoryList = FXCollections.observableList(Category.getCategoryList());
		this.category = new Category (-1, "All");
		categoryList.add(this.category);
		categoryList.sort(null);
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
				try {
					return categoryBox.getItems().stream().filter(category -> 
					category.getName().equals(string)).findFirst().orElse(null);
				} catch (Exception e) {
					return null;
				}
			}
		});
		categoryBox.setValue(this.category);
		categoryBox.setVisibleRowCount(5);
		categoryBox.setOnAction(e -> {
			if (categoryBox.getValue() != null) {
				if (categoryBox.getValue().getID() <= 0) {
					this.deleteCategoryButton.setDisable(true);
				} else {
					this.deleteCategoryButton.setDisable(false);
				}
				this.getTasks(categoryBox.getValue());
			}
		});
		
		//Delete category button
		this.deleteCategoryButton = new Button("D");
		this.deleteCategoryButton.setMinSize(34, 34);
		this.deleteCategoryButton.setMaxSize(34, 34);
		this.deleteCategoryButton.setStyle("-fx-border-color: grey; -fx-border-width: 1 1 0 0;");
//		Image addImage = new Image(getClass().getResourceAsStream("/icons/ButtonNew.png"), 34, 34, true, true);
//		ImageView addImageView = new ImageView(addImage);
//		addImageView.fitHeightProperty().bind(addTaskButton.heightProperty());
//		addImageView.fitWidthProperty().bind(addTaskButton.widthProperty());
//		addTaskButton.setGraphic(addImageView);
		this.deleteCategoryButton.setOnAction(e -> {
			if (categoryBox.getValue() != null) {
				if (categoryBox.getValue().getID() > 0) {
					categoryBox.getValue().deleteSQL();
					categoryList.remove(categoryBox.getValue());
					this.editTaskBox.setPickList();
				}
			}
			this.category = categoryList.get(0);
			this.getTasks(this.category);
		});
		this.deleteCategoryButton.setDisable(true);
		
		this.pickBox.getChildren().addAll(categoryBox, this.deleteCategoryButton);
		
		//HBox for new category
		this.addBox = new HBox();
		this.addBox.setAlignment(Pos.CENTER_LEFT);
		this.addBox.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 1 0;");
		
		//Add category textfield
		TextField addCategoryField = new TextField();
		
		//Confirm add category button
		Button confirmCategoryButton = new Button("Y");
		confirmCategoryButton.setMinSize(34, 34);
		confirmCategoryButton.setMaxSize(34, 34);
		confirmCategoryButton.setStyle("-fx-border-color: grey; -fx-border-width: 1 1 0 0;");
//		Image addImage = new Image(getClass().getResourceAsStream("/icons/ButtonNew.png"), 34, 34, true, true);
//		ImageView addImageView = new ImageView(addImage);
//		addImageView.fitHeightProperty().bind(confirmCategoryButton.heightProperty());
//		addImageView.fitWidthProperty().bind(confirmCategoryButton.widthProperty());
//		confirmCategoryButton.setGraphic(addImageView);
		confirmCategoryButton.setOnAction(e -> {
			this.category = new Category (addCategoryField.getText());
			this.category.updateSQL();
			categoryList.add(this.category);
			categoryList.sort(null);
			this.editTaskBox.setPickList();
			categoryBox.setItems(categoryList);
			addCategoryField.setText("");
			this.setAddMode(false);
		});
		
		//Cancel add category button
		Button cancelCategoryButton = new Button("N");
		cancelCategoryButton.setMinSize(34, 34);
		cancelCategoryButton.setMaxSize(34, 34);
		cancelCategoryButton.setStyle("-fx-border-color: grey; -fx-border-width: 1 1 0 0;");
//		Image addImage = new Image(getClass().getResourceAsStream("/icons/ButtonNew.png"), 34, 34, true, true);
//		ImageView addImageView = new ImageView(addImage);
//		addImageView.fitHeightProperty().bind(cancelCategoryButton.heightProperty());
//		addImageView.fitWidthProperty().bind(cancelCategoryButton.widthProperty());
//		cancelCategoryButton.setGraphic(addImageView);
		cancelCategoryButton.setOnAction(e -> {
			addCategoryField.setText("");
			this.setAddMode(false);
		});
		
		this.addBox.getChildren().addAll(addCategoryField, confirmCategoryButton, cancelCategoryButton);
		
		controlBar.getChildren().addAll(addTaskButton, addCategoryButton, this.pickBox, this.addBox);
		this.setAddMode(false);

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
		//Category.getCategoryList().stream().filter(c -> c.getID() == 0).forEach(Category -> { this.category = Category; });

		//Clear
		this.daysBox.getChildren().clear();
		this.getTasks(this.category);
	}

	public void getTasks(Category category) {
		this.category = category;

		ArrayList<Category> categoryDisplayList = new ArrayList<Category>();

		this.daysBox.getChildren().clear();
		
		if (this.category.getID() == -1) {
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
	
	public Boolean getAddMode() {
		return addMode;
	}

	public void setAddMode(Boolean addMode) {
		this.addMode = addMode;
		
		this.addBox.setVisible(addMode);
		this.addBox.setManaged(addMode);
		
		this.pickBox.setVisible(!addMode);
		this.pickBox.setManaged(!addMode);
	}
}
