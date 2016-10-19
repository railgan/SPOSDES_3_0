package ch.ice.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.configuration.ConfigurationException;

import ch.ice.SegmentationMain;
import ch.ice.controller.MainController;
import ch.ice.controller.threads.SegmentationThread;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SegmentationController {
	
	
	public SegmentationController()  // Standardkonstruktor
	  {
	  }
	
	@FXML
	private Button btnStartSegmentation;
	
	@FXML
	private Button btnCancel;
	
	@FXML
	private TextField txtSaveTo;
	
	@FXML
	public TextField txtPOSfile;
	
	@FXML
	private Button btnChangeDir;
	
	@FXML
	private TextField txtSegmentationFile;
	
	@FXML
	private Button btnSelectSegmentation;
	
	@FXML
	private Button btnSelectPOSfile;
	
	@FXML
	private Button btnCloseWindow;
	
	@FXML
	private Button btnLowerWindow;
	
	@FXML
	private Label lblPOSFile;
	
	@FXML
	private Label lblSegmentationFile;
	
	@FXML
	private Label lblSaveTo;
	
	/**
	 * path for storage of file
	 */

	/**
	 * path for selected file
	 */
	public static String chosenPath;

	
	/* SPOSDES 3.0 */
	public static String POSfilePath;
	public static String saveToDirectoryPath;
	public static String SegmentationFilePath;

	private File pathFile;
	

	
	public void startSegmentation() throws IOException{
		
		
		// TextField variables
		POSfilePath = txtPOSfile.getText();
		saveToDirectoryPath = txtSaveTo.getText();
		SegmentationFilePath = txtSegmentationFile.getText();
		
		// Check if Textboxes are filled
		if(POSfilePath.equals("") | saveToDirectoryPath.equals("") | SegmentationFilePath.equals("")){
			
			if(POSfilePath.equals("")){
				lblPOSFile.setText("Please select a file!");
				lblPOSFile.setTextFill(Color.RED);
			}else{
				lblPOSFile.setText("");
			}
			
			if(SegmentationFilePath.equals("")){
				lblSegmentationFile.setText("Please select a file!");
				lblSegmentationFile.setTextFill(Color.RED);
				}else{
					lblSegmentationFile.setText("");
				}
			
			if(saveToDirectoryPath.equals("")){
				lblSaveTo.setText("Please select a folder!");
				lblSaveTo.setTextFill(Color.RED);
			}else{
				lblSaveTo.setText("");
			}
		}else{

		
		// Open Progress Window
		
		Stage primaryStage1 = new Stage();
		primaryStage1.initStyle(StageStyle.UNDECORATED);
		Parent root = FXMLLoader.load(getClass().getResource("/ch/ice/view/segmentation/ProgressSegmentation.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/ch/ice/view/WebCrawler.css").toExternalForm());
		primaryStage1.setScene(scene);
		primaryStage1.show();
		
		// close current window
		Stage currentStage = (Stage) btnCancel.getScene()
				.getWindow();
		currentStage.close();
		// Start Segmentation Function
		SegmentationThread.main(null);
		}
	}
	
	public void lowerWindow(){
		Stage currentStage = (Stage) btnLowerWindow.getScene().getWindow();
		currentStage.setIconified(true);
	}
	
	public void closeWindow(){
		System.exit(0);
	}
	
	public void startSearchFile() {
		
	// FileChooser
			FileChooser fc = new FileChooser();
			
			// Pfad vordefinieren
			fc.setInitialDirectory(new File ("C:\\"));
			// Nur Excel oder csv Dateien anzeigen
			fc.getExtensionFilters().addAll(
							new FileChooser.ExtensionFilter(
									"Excel-File (*.xlsx)", "*.xlsx"));
			
			// // FileChooser Dialog öffnen und ausgewählte Datei in SelecteFile speichern
			File selectedFile = fc.showOpenDialog(null);
			
					
			// Wenn etwas ausgewählt wurde, in Liste adden und Textbox schreiben
			if (selectedFile != null){
				txtPOSfile.setText(selectedFile.getAbsolutePath());
			}
		}
	
	public void startSearchSegmentation() {
		
		// FileChooser
				FileChooser fc = new FileChooser();
				
				// Pfad vordefinieren
				fc.setInitialDirectory(new File ("C:\\"));
				// Nur Excel oder csv Dateien anzeigen
				fc.getExtensionFilters().addAll(
								new FileChooser.ExtensionFilter(
										"Excel-File (*.xlsx)", "*.xlsx"));
				
				// // FileChooser Dialog öffnen und ausgewählte Datei in SelecteFile speichern
				File selectedFile = fc.showOpenDialog(null);
				
						
				// Wenn etwas ausgewählt wurde, in Liste adden und Textbox schreiben
				if (selectedFile != null){
					txtSegmentationFile.setText(selectedFile.getAbsolutePath());
				}
			}
	
	public void startChangeDir() {
		
		// DirectoryChooser
		DirectoryChooser dc = new DirectoryChooser();
				
		// Pfad vordefinieren
		dc.setInitialDirectory(new File ("C:\\"));
	
		// Dialog öffnen
		File selectedFile = dc.showDialog(null);
				
						
		// wenn etwas ausgewählt wurde, Pfad in TextField schreiben
		if (selectedFile != null){
			txtSaveTo.setText(selectedFile.getAbsolutePath());
		}
	}
		
	public void startConfiguration() throws IOException{
		Stage primaryStage1 = new Stage();
		primaryStage1.initStyle(StageStyle.UNDECORATED);
		Parent root = FXMLLoader.load(getClass().getResource("/ch/ice/view/segmentation/config.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("WebCrawler.css").toExternalForm());
		primaryStage1.setScene(scene);
		primaryStage1.show();
		
		// close current window
		Stage currentStage = (Stage) btnCancel.getScene()
				.getWindow();
		currentStage.close();
	}
	
	public void startStartmenue() throws IOException{
		Stage primaryStage1 = new Stage();
		primaryStage1.initStyle(StageStyle.UNDECORATED);
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
	
	public void startManual(){
		Desktop dt = Desktop.getDesktop();
		try {

			dt.open(new File("conf/SPOSDES_manual.pdf"));
		} catch (IOException e) {
			// TODO
			// Auto-generated
			// catch block
			e.printStackTrace();
		}
	}
	
}
