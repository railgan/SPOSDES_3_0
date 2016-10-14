package ch.ice.view;

import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class StartmenueController {
	
	@FXML
	private Button btnEnrich;
	
	@FXML
	private Button btnSegment;
	
	@FXML
	private Button btnCloseWindow;
	
	@FXML
	private Button btnLowerWindow;
	
	GUIMain mainGUI = new GUIMain();
	
	public void closeWindow(){
		System.exit(0);
	}
	
	public void lowerWindow(){
		Stage currentStage = (Stage) btnLowerWindow.getScene().getWindow();
		currentStage.setIconified(true);
	}
	
	
	// On Button Click, the Segmentation windows opens

		public void startSegmentation(ActionEvent event) throws Exception{
				
				Stage primaryStage = new Stage();
				primaryStage.initStyle(StageStyle.UNDECORATED);
				Parent root = FXMLLoader.load(getClass().getResource("/ch/ice/view/Segmentation.fxml"));
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("Webcrawler.css").toExternalForm());
				primaryStage.setScene(scene);
				primaryStage.show();
				
				// close current window
				Stage currentStage = (Stage) btnSegment.getScene()
						.getWindow();
				currentStage.close();
				
		}
		
		// On Button Click, the Enrich-Part of SPOSDES starts

			public void startEnrichment() throws Exception{
					
					// Open just the predefined stuff from SPOSDES 2.0
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
						"/ch/ice/view/Schurter.fxml"));
				Parent root1;
				try {
					// Welcome
					Parent root = FXMLLoader.load(getClass().getResource("Welcome.fxml"));

					Scene scene = new Scene(root);

					
					Stage primaryStage = new Stage();
					primaryStage 
							.setTitle("Schurter POS-Data-Enhancement System (SPOSDES) ");
					primaryStage.setScene(scene);
					primaryStage.initStyle(StageStyle.UNDECORATED);
					primaryStage.show();
					Timeline timeline = new Timeline();
					timeline.getKeyFrames().add(
							new KeyFrame(Duration.seconds(4),
									new EventHandler<ActionEvent>() {

										public void handle(ActionEvent event) {
											primaryStage.hide();
											loadMain();
										}
									}));
					timeline.play();
					
					

					
					// close current window
					Stage currentStage = (Stage) btnEnrich.getScene()
							.getWindow();
					currentStage.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//logger.error(e);

				}

			
				
			}
			
			
			// SPOSDES Version 3.0
			public void loadMain() {
				mainGUI.loadMain();
				
			}

}
