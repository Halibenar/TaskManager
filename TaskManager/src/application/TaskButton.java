package application;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TaskButton extends Button{
	
	private int[] size;

	public TaskButton(String imagePath) {
		super();
		this.setSize(34, 34);
		this.setStyle("-fx-border-color: grey; -fx-border-width: 0;");
		this.setImage(imagePath);
	}
	
	public TaskButton(Node node) {
		super();
		this.setSize(34, 34);
		this.setStyle("-fx-border-color: grey; -fx-border-width: 0;");
		this.setGraphic(node);
	}

	public void setSize(int width, int height) {
		this.size = new int[] {width, height};
		this.setMinSize(width, height);
		this.setMaxSize(width, height);
	}
	
	public void setImage(String imagePath) {
		Image image = new Image(getClass().getResourceAsStream(imagePath), size[0], size[1], true, true);
		ImageView imageView = new ImageView(image);
		imageView.fitHeightProperty().bind(this.heightProperty());
		imageView.fitWidthProperty().bind(this.widthProperty());
		this.setGraphic(imageView);
	}
}