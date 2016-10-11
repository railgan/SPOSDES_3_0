package ch.ice.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.configuration.ConfigurationException;

import ch.ice.controller.MainController;
import ch.ice.exceptions.InternalFormatException;
import ch.ice.exceptions.MissingCustomerRowsException;
import ch.ice.model.Customer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SegmentationController {
	
	@FXML
	private Button btnStartSegmentation;
	
	@FXML
	private Button btnCancel;
	
	@FXML
	private TextField txtSaveTo;
	
	@FXML
	private TextField txtSelectFile;
	
	@FXML
	private Button btnChangeDir;
	
	@FXML
	private Button btnSelectFile;
	
	/**
	 * path for storage of file
	 */
	public static String path;
	/**
	 * path for selected file
	 */
	public static String chosenPath;

	private File pathFile;
	
	/**
	 * ActionListener for Change Directory Button
	 */
	
	
	public void startSearchFile() {
		
	// FileChooser
			FileChooser fc = new FileChooser();
			
			// Pfad vordefinieren
			fc.setInitialDirectory(new File ("C:\\Users\\Kevin"));
			// Nur Excel oder csv Dateien anzeigen
			fc.getExtensionFilters().addAll(
							new FileChooser.ExtensionFilter(
									"Excel-File (*.xlsx)", "*.xlsx"));
			
			// // FileChooser Dialog �ffnen und ausgew�hlte Datei in SelecteFile speichern
			File selectedFile = fc.showOpenDialog(null);
			
					
			// Wenn etwas ausgew�hlt wurde, in Liste adden und Textbox schreiben
			if (selectedFile != null){
				txtSelectFile.setText(selectedFile.getAbsolutePath());
			}
		}
	
	public void startChangeDir() {
		
		// DirectoryChooser
		DirectoryChooser dc = new DirectoryChooser();
				
		// Pfad vordefinieren
		dc.setInitialDirectory(new File ("C:\\"));
	
		// Dialog �ffnen
		File selectedFile = dc.showDialog(null);
				
						
		// wenn etwas ausgew�hlt wurde, Pfad in TextField schreiben
		if (selectedFile != null){
			txtSaveTo.setText(selectedFile.getAbsolutePath());
		}
	}
		
		/*
		DirectoryChooser directoryChooser = new DirectoryChooser();

		//@Override
		public void handle(ActionEvent event) {
			// TODO Auto-generated method stub
			// Stage stage = new Stage();
			Node source = (Node) event.getSource();
			Stage stage = (Stage) source.getScene().getWindow();
			try {
				if (!path.isEmpty() && pathFile.exists() == true) {
					File initial = new File(path);
					directoryChooser.setInitialDirectory(initial);

				}
				pathFile = directoryChooser.showDialog(stage);

				if (pathFile != null && pathFile.exists() == true) {
					setSaveProperties(pathFile.getAbsolutePath(),
							chosenPath);
					config.save();
					getSaveProperties(startSearchButton);
					txtSaveTo.setText(path);
					txtSaveTo.setStyle("-fx-text-inner-color: black;");
					checkAll();

				} else if (pathFile == null) {
					txtSaveTo.setText(path);
					txtSaveTo.setStyle("-fx-text-inner-color: black;");
					pathFile = new File(path);
					checkAll();

				} else {

					txtSaveTo.setText("Illegal Directory for Saving");
					txtSaveTo.setStyle("-fx-text-inner-color: red;");
					checkAll();
				}
				// if there is no path selected
			} catch (NullPointerException | ConfigurationException e) {
				logger.error(e);
				e.printStackTrace();
				System.out.println("No Path selected");
				txtSaveTo.setText("No Directory selected");
				txtSaveTo.setStyle("-fx-text-inner-color: red;");
				setSaveProperties("", chosenPath);
				checkAll();
				try {
					config.save();
				} catch (ConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					logger.error(e1);
				}
				getSaveProperties(startSearchButton);
				checkAll();

				// if Directory is invalid
			} catch (IllegalArgumentException e) {
				logger.error(e);
				e.printStackTrace();
				txtSaveTo.setText("Illegal Directory for Saving");
				txtSaveTo.setStyle("-fx-text-inner-color: red;");
				setSaveProperties("", chosenPath);
				checkAll();
				try {
					config.save();
				} catch (ConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					logger.error(e1);
				}
				getSaveProperties(startSearchButton);
				checkAll();

			}
		}

	});*/
	
	public void startStartmenue() throws IOException{
		Stage primaryStage1 = new Stage();
		Parent root = FXMLLoader.load(getClass().getResource("/ch/ice/view/Startmenue.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("WebCrawler.css").toExternalForm());
		primaryStage1.setScene(scene);
		primaryStage1.show();
		
		// close current window
		Stage currentStage = (Stage) btnCancel.getScene()
				.getWindow();
		currentStage.close();
		
		
	}
	
}
