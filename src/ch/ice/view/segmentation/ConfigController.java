package ch.ice.view.segmentation;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.poi.util.SystemOutLogger;

import ch.ice.utils.Config;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ConfigController {
	
	@FXML
	private Button btnSafe;
	
	@FXML
	private Button btnCancel;
	
	@FXML
	private CheckBox chbRemoveSpecialCharakters;
	
	@FXML
	private CheckBox chbDeduplicate;
	
	@FXML
	private TextField txtSegmentMargin;
	
	@FXML
	private Label lblMargin;
	
	public static PropertiesConfiguration config = Config.PROPERTIES;
	
	// Properties laden
	public void initialize(){

		// Read the configurtaion out of app.properties and set controls
		// Textbox
		String margin = (String) config.getProperty("segmentation.segmentmargin");
		
		
		
		
		
		// Read properties for setting the Checkboxes
		String deduplicate = (String) config.getProperty("segmentation.deduplicate");
		String removeSpecial = (String) config.getProperty("segmentation.removeSpecialCharakters");
		
		
		
		if (deduplicate.equals("true")){
		chbDeduplicate.setSelected(true);
		
		}else{
			chbDeduplicate.setSelected(false);
			System.out.println("false");
		}
		
		if (removeSpecial.equals("true")){
				chbRemoveSpecialCharakters.setSelected(true);
				}else{
					chbRemoveSpecialCharakters.setSelected(false);
					
				}
		
		handleChbRemove();
		handleChbDeduplicate();
	}
	
	
	// Method to change Text of Checkboxes on click
	
	public void handleChbRemove() {

		if (chbRemoveSpecialCharakters.isSelected() == false) {
			chbRemoveSpecialCharakters.setText("Removing Special Charakters is deactivated");
			chbRemoveSpecialCharakters.setTextFill(Color.RED);
		} else{
			chbRemoveSpecialCharakters.setText("Removing Special Charakters is activated");
			chbRemoveSpecialCharakters.setTextFill(Color.GREEN);
		}
	}
	
	// Method to change Text of Checkboxes on click
	public void handleChbDeduplicate() {

		if (chbDeduplicate.isSelected() == false) {
			chbDeduplicate.setText("Deduplicating is deactivated");
			chbDeduplicate.setTextFill(Color.RED);
		} else{
			chbDeduplicate.setText("Deduplicating is activated");
			chbDeduplicate.setTextFill(Color.GREEN);
		}
	}
	
	
	// Method to safe configuration changes into the .XML file
	public void cancelConfiguration() throws IOException{
		// Load Segmentation window
				Stage primaryStage = new Stage();
				primaryStage.initStyle(StageStyle.UNDECORATED);
				Parent root = FXMLLoader.load(getClass().getResource("/ch/ice/view/Segmentation.fxml"));
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("/ch/ice/view/Webcrawler.css").toExternalForm());
				primaryStage.setScene(scene);
				primaryStage.show();
				
				// close current window
				Stage currentStage = (Stage) btnSafe.getScene()
						.getWindow();
				currentStage.close();
		
	}
	
	// Back to Segmentation-Window
	public void safeConfiguration() throws IOException, NumberFormatException, ConfigurationException{
	
		// Save configuration to XML File
				try {
					// Overall Check
					String margin = txtSegmentMargin.getText();
					
					// First check if it is a number in the TextField
					double dbl_margin = Double.parseDouble(margin);
					
					if(dbl_margin <= 1.000 && dbl_margin >=0.000){
						
						// Safe Checkboxes
						if (chbDeduplicate.isSelected() == true){
							config.setProperty("segmentation.deduplicate", "true");
						}	else{
								config.setProperty("segmentation.deduplicate", "false");
						}
						
						if (chbRemoveSpecialCharakters.isSelected()){
							config.setProperty("segmentation.removeSpecialCharakters", "true");
						}	else{
								config.setProperty("segmentation.removeSpecialCharakters", "false");
						}
						// Safe Textbox
						config.setProperty("segmentation.segmentmargin", txtSegmentMargin.getText());
						config.save();
						startSegmentationMenue();
					}else{
						
					}
					
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					lblMargin.setText("Margin value has to be a number between 0.00 and 1.00");
					lblMargin.setTextFill(Color.RED);
				}
	}
	public void startSegmentationMenue() throws IOException{
		// Load Segmentation window
		Stage primaryStage = new Stage();
		primaryStage.initStyle(StageStyle.UNDECORATED);
		Parent root = FXMLLoader.load(getClass().getResource("/ch/ice/view/Segmentation.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/ch/ice/view/Webcrawler.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
		
		// close current window
		Stage currentStage = (Stage) btnSafe.getScene()
				.getWindow();
		currentStage.close();
	}
		
}
