package application;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;

import application.Main.ViewMode;
import application.Task.ReadMode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Extends VBox. Shows Tasks for a PlanDate. Holds a bar with buttons for PlanDate selection and a stackpane.
 * Stackpane holds a DatePicker and a GridPane with TaskPanes for Tasks on the selected PlanDate.
 */
public class CalendarPane extends VBox {

	private LocalDate planDate = LocalDate.now();
	private LocalDate[] weekDates = new LocalDate[7];
	private VBox daysBox;
	private Button weekButton;
	private Button[] dayButtons;
	private ViewMode currentViewMode;
	public EditTaskBox editTaskBox;

	public CalendarPane() {
		
		//HBox for controls
		HBox controlBar = new HBox();
		controlBar.setAlignment(Pos.CENTER_LEFT);
		controlBar.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 1 0;");

		//HBox for week display and next/previous buttons
		HBox dateBar = new HBox();
		dateBar.setAlignment(Pos.CENTER_LEFT);
		dateBar.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 1 0;");
		
		//HBox for day of week selection buttons
		HBox dayButtonBar = new HBox();
		dayButtonBar.setAlignment(Pos.CENTER_LEFT);
		dayButtonBar.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 1 0;");
		
		//Stackpane for tasks and datepicker
		StackPane dateStack = new StackPane();
		VBox.setVgrow(dateStack, Priority.ALWAYS);
		dateStack.setAlignment(Pos.TOP_LEFT);

		//DatePicker layer, set invisible at start
		DatePicker datePicker = new DatePicker(this);
		datePicker.setVisible(false);
		
		//EditTaskBox for editing tasks
		this.editTaskBox = new EditTaskBox();
		this.editTaskBox.setVisible(false);
		this.editTaskBox.setManaged(false);
		
		//Add elements
		this.getChildren().addAll(controlBar, dateBar, dayButtonBar, dateStack, this.editTaskBox);
		
		//Add task button
		Button addTaskButton = new TaskButton("/icons/ButtonNew.png");
		addTaskButton.setOnAction(e -> {
			MainTask newTask = new MainTask(this.getPlanDate());
			this.editTaskBox.setTask(newTask);
		});
		
		//Today button
		Button todayButton = new TaskButton("/icons/ButtonToday.png");
		todayButton.setOnAction(e -> {
			this.setPlanDate(LocalDate.now());
		});
		
		//Datepicker button, sets datePicker as visible in dateStack
		Button datePickerButton = new TaskButton("/icons/ButtonPicker.png");
		datePickerButton.setOnAction(e -> {
			datePicker.pickerDate = getPlanDate();
			datePicker.Update();
			datePicker.setVisible(!datePicker.isVisible());
		});
		
		//Add to controlBar
		controlBar.getChildren().addAll(addTaskButton, todayButton, datePickerButton);
		
		//Central date button, sets viewmode to week
		this.weekButton = new Button();
		Font titleFont = new Font(weekButton.getFont().getName(), 15);
		this.weekButton.setFont(titleFont);
		this.weekButton.setMaxHeight(34);
		this.weekButton.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(this.weekButton, Priority.ALWAYS);
		this.weekButton.setOnAction(e -> {
			this.setViewMode(ViewMode.week);
			this.setPlanDate(this.getPlanDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
		});
		
		//Previous date button
		Button previousDateButton = new TaskButton("/icons/ButtonPrevious.png");
		previousDateButton.setOnAction(e -> {
			this.setViewMode(ViewMode.week);
			this.setPlanDate(this.weekDates[0].minusDays(7));
		});

		//Next date button
		Button nextDateButton = new TaskButton("/icons/ButtonNext.png");
		nextDateButton.setOnAction(e -> {
			this.setViewMode(ViewMode.week);
			this.setPlanDate(this.weekDates[0].plusDays(7));
		});
		
		//Add to dateBar
		dateBar.getChildren().addAll(previousDateButton, this.weekButton, nextDateButton);
		
		//Buttons for day selection
		this.dayButtons = new Button[7];
		for (int i = 0; i < 7; i++) {
			this.dayButtons[i] = new Button();
			if (i < 6) {
				this.dayButtons[i].setStyle("-fx-border-color: grey; -fx-border-width: 0 1 0 0;");
			}
			this.dayButtons[i].setPadding(new Insets(0,0,0,0));
			this.dayButtons[i].setMinSize(30, 42);
			this.dayButtons[i].setMaxSize(Double.MAX_VALUE, 42);
			HBox.setHgrow(this.dayButtons[i], Priority.ALWAYS);
			dayButtonBar.getChildren().add(this.dayButtons[i]);
		}

		//VBox with tasks for each day
		this.daysBox = new VBox(1);
		this.daysBox.setPadding(new Insets(0, 1, 0, 0));

		//Enclose VBox in ScrollPane
		ScrollPane scrollPane = new ScrollPane(daysBox);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);

		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		dateStack.getChildren().addAll(scrollPane, datePicker);
	}

	/**
	 * Updates CalendarPane to show Tasks for the current PlanDate.
	 * @param viewMode ViewMode for the CalendarPane; options are ViewMode.day for one day or ViewMode.week for seven days.
	 */
	void setPlanDate(LocalDate planDate) {
		//Set planDate and viewMode
		this.planDate = planDate;

		//Clear tasks
		this.daysBox.getChildren().clear();

		//Set labels and actions for day buttons
		for (int i = 0; i < 7; i++) {
			this.weekDates[i] = this.getPlanDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusDays(i);
			VBox buttonDisplay = new VBox();
			buttonDisplay.setAlignment(Pos.BASELINE_CENTER);
			Label buttonDayLabel = new Label(this.weekDates[i].getDayOfWeek().toString().substring(0,3));
			Label buttonDateLabel = new Label(this.weekDates[i].format(DateTimeFormatter.ofPattern("dd")));
			buttonDisplay.getChildren().addAll(buttonDayLabel, buttonDateLabel);
			this.dayButtons[i].setGraphic(buttonDisplay);
			LocalDate lambdaDate = this.weekDates[i];
			this.dayButtons[i].setOnAction(e -> {
				this.setViewMode(ViewMode.day);
				this.setPlanDate(lambdaDate);
			});
		}
		
		//Set label for week button to display date in format WEEK [weeknumber], [month] [year]
		this.weekButton.setText("WEEK " + this.weekDates[0].get(WeekFields.of(DayOfWeek.MONDAY, 4).weekOfWeekBasedYear()) +
				", " + this.weekDates[0].getMonth().toString() + " " + this.weekDates[0].format(DateTimeFormatter.ofPattern("yyyy")));
		
		//Set days to show tasks for
		LocalDate[] showDates;
		//If one day is shown, set to show tasks for one day
		if (this.getViewMode() == ViewMode.day) {
			showDates = new LocalDate[1];
			showDates[0] = this.getPlanDate();
		//If one week is shown, set to show tasks for the week
		} else {
			showDates = this.weekDates;
		}

		if (this.getViewMode() == ViewMode.week) {
			//Get list of overdue tasks from database, display tasklistbox if there are any
			ArrayList<MainTask> taskList = MainTask.getMainTaskList(LocalDate.now(), Task.ReadMode.lessThan, null);
			taskList.sort(null);
			if (taskList.size() > 0) {
				LocalDate lastTaskDate = LocalDate.of(0000,01,01);
				VBox taskBox = new VBox();
				for (MainTask task : taskList) {
					if (!Arrays.asList(showDates).contains(task.getPlanDate())) {
						if (!task.getPlanDate().isEqual(lastTaskDate)) {

							//Title label
							HBox titleBox = new HBox();
							Label dayOfWeekLabel = new Label(task.getPlanDate().getDayOfWeek().toString().substring(0,3));
							dayOfWeekLabel.setMinWidth(40);
							Label dateLabel = new Label(task.getPlanDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
							titleBox.getChildren().addAll(dayOfWeekLabel, dateLabel);

							VBox taskListBox = new VBox();
							taskListBox.setPadding(new Insets(0, 3, 3, 3));
							taskListBox.setStyle("-fx-border-color: grey; -fx-border-width: 1;");
							taskListBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
							GridPane.setHgrow(taskListBox, Priority.ALWAYS);
							GridPane.setVgrow(taskListBox, Priority.ALWAYS);

							//Labels for tasks with spacing of 3 between tasks and between following add task button
							taskBox = new VBox();
							taskBox.setSpacing(3);
							taskBox.setPadding(new Insets(0, 0, 3, 0));
							taskListBox.getChildren().addAll(titleBox, taskBox);
							this.daysBox.getChildren().add(taskListBox);
							lastTaskDate = task.getPlanDate();
						}

						//Add taskPanes
						taskBox.getChildren().add(task.taskPane);

					}
				}
			}
		}

		//Display tasklistbox with tasks for every day
		for (LocalDate showDate : showDates) {
			ArrayList<MainTask> taskList = MainTask.getMainTaskList(showDate, ReadMode.equals, null);

			//Title label
			HBox titleBox = new HBox();
			Label dayOfWeekLabel = new Label(showDate.getDayOfWeek().toString().substring(0,3));
			dayOfWeekLabel.setMinWidth(40);
			Label dateLabel = new Label(showDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
			titleBox.getChildren().addAll(dayOfWeekLabel, dateLabel);

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
	
	public LocalDate getPlanDate() {
		return this.planDate;
	}
	
	public void setViewMode(ViewMode viewMode) {
		this.currentViewMode = viewMode;
	}
	
	public ViewMode getViewMode() {
		return this.currentViewMode;
	}
}