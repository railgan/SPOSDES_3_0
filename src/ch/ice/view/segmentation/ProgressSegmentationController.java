package ch.ice.view.segmentation;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.Timer;

import ch.ice.SegmentationMain;
import ch.ice.controller.threads.SegmentationThread;
import ch.ice.view.SegmentationController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Runs besides the main program constantly showing the progress of it
 * 
 * @author Mike & Kevin
 *
 */
public class ProgressSegmentationController extends Thread implements Initializable {

	@FXML
	private Button btnOpenFile;

	@FXML
	private Button btnClose;

	@FXML
	private ProgressBar progressbar;

	@FXML
	private Label lblStatusPhase;

	@FXML
	private Label lblStatusObject;
	
	@FXML
	private Label lblAmountSegmented;
	
	@FXML
	private Label lblAmountSegmented11;
	
	@FXML
	private Label lblAmountDuplicate;
	
	@FXML
	private Label lblAmountRows;
	
	@FXML
	private ImageView imgViewSchurter;

	public boolean working = true;

	public Timer timer;
	int i = 0;

	public void initialize(URL location, ResourceBundle resources) {
		
		// Buttons deactivated
		btnOpenFile.setDisable(true);
		
		btnClose.setText("Cancel");
		
		// Constatly updates the GUI
		timer = new Timer(100, new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						// Changes the Text of the Labels and updates the
						// progressbar
						lblStatusObject.setText(SegmentationMain.progressText.toString());
						
						if (SegmentationMain.progressText.equals("Done")) {
							// reactivate Buttons
							btnOpenFile.setDisable(false);
							btnClose.setDisable(false);
							btnClose.setText("Close");
							
						}
						
						
						progressbar.setProgress(SegmentationMain.progressPercent);
						
						// update labels 
						lblAmountSegmented.setText(""+SegmentationMain.amountSegmented);
						lblAmountDuplicate.setText(""+SegmentationMain.amountDuplicate );
						lblAmountRows.setText("" + SegmentationMain.currentRows+ " of "+SegmentationMain.amountRows);


						// Checks if the Program is done else Restarts
						if (i == 100) {
							if (SegmentationMain.progressText.equals("Done")) {
								timer.stop();
								
							} else {
								i = 0;
							}

						}
						i++;
					}
				});
			}
		});
		timer.start();
	}
	
	
		
	public void openPOSfile() throws IOException{
		// Open the segmented POS-file in Excel
		Desktop dt = Desktop.getDesktop();
		dt.open(new File(SegmentationController.POSfilePath));
	}
	
	public void close(){
		// Close programm
		System.exit(0);
	}
	

}
