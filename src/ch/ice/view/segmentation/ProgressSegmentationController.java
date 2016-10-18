package ch.ice.view.segmentation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.Timer;

import ch.ice.SegmentationMain;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

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
		       		progressbar.setProgress(SegmentationMain.progressPercent);
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
	
	
