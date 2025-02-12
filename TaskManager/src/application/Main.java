package application;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

/**
 * Holds the sql database file location and static references to panes for different views and a menubar.
 */
public class Main extends Application {

	//SQL database file location
	static String url = "jdbc:sqlite:taskbase.db";
	
	//Viewmode for viewing the agenda per day or week, used as parameter in CalendarPane
	public static enum ViewMode {
		day, week
	}
	
	//Root pane, menubar, and panes for different views
	BorderPane root = new BorderPane();
	static CalendarPane calendarPane;
	static MainMenuBar mainMenuBar;

	/**
	 *Checks database for correct tables, creates the root window and panes for different views.
	 * @param primaryStage Stage object acting as the primary window.
	 */
	@Override
	public void start(Stage primaryStage) {

		//Set SQLite database url
		SQLConnector.setUrl(Main.url);
		
		//Check database for tables
		ArrayList<String> tableStrings = new ArrayList<String>();
		
		//Task table
		tableStrings.add("CREATE TABLE IF NOT EXISTS tasks (" +
				  "	ID INTEGER PRIMARY KEY," +
				  "	Name VARCHAR(100) NOT NULL," +
				  "	Date DATE NOT NULL," +
				  "	Time TEXT," +
				  "	Completed INTYINT(1) NOT NULL," +
				  "	Expanded INTYINT(1) NOT NULL," +
				  "	Editmode INTYINT(1) NOT NULL" +
				  ");");
		//Subtask table
		tableStrings.add("CREATE TABLE IF NOT EXISTS subtasks (" +
				  "	ID INTEGER PRIMARY KEY," +
				  "	Name VARCHAR(100) NOT NULL," +
				  "	MainTaskID INT NOT NULL," +
				  "	Completed INTYINT(1) NOT NULL" +
				  ");");

		SQLConnector.checkTables(tableStrings);
		
		//Create panes and menubar
		Main.calendarPane = new CalendarPane();
		Main.mainMenuBar = new MainMenuBar();
		
		//Set menu at top
		this.root.setTop(Main.mainMenuBar);

		//Create window and scene
		primaryStage.setTitle("TaskManager");

		//Set style
		Scene scene = new Scene(root,400,700);
		this.root.setStyle("-fx-border-color: grey; -fx-border-width: 1;");
		scene.getStylesheets().add(getClass().getResource("/TaskPaneStyle.css").toExternalForm());
		
		//Set min size
		primaryStage.setMinHeight(400);
		primaryStage.setMinWidth(350);
		
		//Show window
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Launches application.
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Extends HBox. Menu bar with buttons for switching between views and labels for current time and date.
	 */
	class MainMenuBar extends HBox {
		
		HBox buttonBox;
		Label currentDateLabel;
		Label currentTimeLabel;

		MainMenuBar() {
			//Set alignment
			this.setAlignment(Pos.CENTER_LEFT);

			//Current date and time labels
			this.currentDateLabel = new Label();
			currentDateLabel.setPadding(new Insets(4, 8, 0, 3));
			this.currentTimeLabel = new Label();
			currentTimeLabel.setPadding(new Insets(4, 3, 0, 0));
			//Set current date and time
			this.setDateTime();
			
			//Check time and date every 0.5 seconds and change date and time label text to match
			Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), _ -> {
				this.setDateTime();
			}));
			timeline.setCycleCount(Animation.INDEFINITE);
			timeline.playFromStart();
			
			//HBox for time and date
			HBox dateBox = new HBox();
			dateBox.setMaxWidth(Double.MAX_VALUE);
			HBox.setHgrow(dateBox, Priority.ALWAYS);
			dateBox.setAlignment(Pos.BASELINE_RIGHT);
			dateBox.setStyle("-fx-border-color: grey; -fx-background-color: white; -fx-border-width: 0 0 1 0;");
			dateBox.getChildren().addAll(this.currentDateLabel, this.currentTimeLabel);
			
			//Buttons
			int buttonWidth = 54;
			Button agendaButton = new Button("Agenda");
			agendaButton.setMinWidth(buttonWidth);
			agendaButton.setMaxWidth(Double.MAX_VALUE);
			HBox.setHgrow(agendaButton, Priority.ALWAYS);
			agendaButton.setPadding(new Insets(4, 2, 4, 2));
			agendaButton.setOnAction(e -> {
				menuBarButtonEvent((Button)e.getSource(), calendarPane);
			});
			//HBox for buttons
			this.buttonBox = new HBox();
			this.buttonBox.setAlignment(Pos.TOP_LEFT);
			this.buttonBox.setMaxWidth(Double.MAX_VALUE);
			HBox.setHgrow(this.buttonBox, Priority.ALWAYS);
			this.buttonBox.getChildren().addAll(agendaButton);
			
			//Add boxes to bar
			this.getChildren().addAll(this.buttonBox, dateBox);
			
			//Set agendaButton style as pressed and calendarPane as default view
			agendaButton.fire();
		}
		
		/**
		 * When button is pessed, set button style as pressed, style of other buttons as not pressed, and set the pane as the current view.
		 * @param button Button on the MainMenuBar corresponding with the pane
		 * @param pane Main pane set as viewed by this method
		 */
		public void menuBarButtonEvent(Button button, Pane pane) {
			for (Node node : this.buttonBox.getChildren()) {
				if (node instanceof Button) {
					if (node == button) {
						node.setStyle("-fx-border-color: grey; -fx-background-color: transparent; -fx-border-width: 0 1 0 0;");
					} else {
						node.setStyle("-fx-border-color: grey; -fx-background-color: white; -fx-border-width: 0 1 1 0;");
					}
				}
			}
			root.setCenter(pane);
		}
		
		/**
		 * Sets date and time labels to display the current date and time.
		 */
		public void setDateTime() {
			this.currentDateLabel.setText(LocalDate.now().getDayOfWeek().toString().substring(0,3) + " " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
			this.currentTimeLabel.setText(LocalTime.now().toString().substring(0,8));
		}
	}
}
