package ch.ice.view.segmentation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.Timer;

import ch.ice.SegmentationMain;
import ch.ice.controller.MainController;
import ch.ice.controller.threads.SegmentationThread;
import ch.ice.exceptions.InternalFormatException;
import ch.ice.exceptions.MissingCustomerRowsException;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import sun.awt.RepaintArea;

public class ProgressSegmentationController extends Thread implements Initializable{

	@FXML
	private Button btnCancel;

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

	public boolean working = true;
	
	public Timer timer;
	int i = 0;


	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("we doing");

		 timer = new Timer(100,new ActionListener() {
		       	@Override
				public void actionPerformed(ActionEvent e) {
		       		Platform.runLater(new Runnable() {
							@Override
							public void run() {
		       		lblStatusObject.setText(SegmentationMain.progressText.toString());
		            if(i==100){
		            	if(SegmentationMain.progressText.equals("Done")){
		            		 timer.stop();
		            	} else{
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
	
}
	
	
