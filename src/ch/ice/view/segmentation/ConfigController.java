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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
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
	
	public static PropertiesConfiguration config = Config.PROPERTIES;
	
	// Properties laden
	public void initialize(){

		// Read the configurtaion out of app.properties and set controls
		// Textbox
		String test = (String) config.getProperty("segmentation.segmentmargin");
		txtSegmentMargin.setText(test);
		
		
		
		// Checkboxes
		String deduplicate = (String) config.getProperty("segmentation.deduplicate");
		String removeSpecial = (String) config.getProperty("segmentation.removeSpecialCharakters");
		
		chbDeduplicate.setSelected(true);
		
		if ( deduplicate.equals("true")){
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
	public void safeConfiguration() throws IOException{
	
		// Save configuration to XML File
				try {
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
					
					
				} catch (ConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
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
