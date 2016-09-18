package ch.ice.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.ice.controller.MainController;
import ch.ice.controller.web.SearchEngineFactory;
import ch.ice.utils.Config;

public class MetaController implements Initializable {

	@FXML
	Button cancelMetaButton;
	@FXML
	Button okMetaButton;
	@FXML
	Label metaTagsLabel;
	@FXML
	FlowPane flowPane;
	@FXML
	VBox vBox;
	@FXML
	ComboBox<Image> selectEngine;
	@FXML
	private TextField keyTextField;
	@FXML
	private TextField othersTextField;
	@FXML
	private Label keyLabel;
	@FXML
	private Label othersLabel;
	@FXML
	private Hyperlink unselectAll;
	@FXML
	private Hyperlink selectAll;
	@FXML
	private ListView<String> blackListView;
	@FXML
	private Label searchEngineLabel;
	@FXML
	private Label metaOptionsLabel;
	@FXML
	private Label blackListLabel;
	@FXML
	private Label checkAccountLabel;
	@FXML
	private Button resetKeysButton;
	@FXML
	private CheckBox checkCategorization;
	@FXML
	private Label checkCatLabel;

	/**
	 * Check boolean for Categorization
	 */
	public static boolean checkCatBool = false;

	/**
	 * Menu for editing Blacklist
	 */
	private ContextMenu editMenu = new ContextMenu();
	private MenuItem deleteOption = new MenuItem("Delete");
	private MenuItem addNew = new MenuItem("Add new");

	/**
	 * Constants for moving the Window
	 */
	private double xOffset = 0;
	private double yOffset = 0;

	private Image googleImage = new Image(
			MetaController.class.getResourceAsStream("/Google.png"));
	private Image bingImage = new Image(
			MetaController.class.getResourceAsStream("/Bing.png"));
	/**
	 * ArrayList for CheckBox Objects
	 */
	private ArrayList<CheckBox> checkList;

	public static PropertiesConfiguration config = Config.PROPERTIES;

	public static final Logger logger = LogManager
			.getLogger(MetaController.class.getName());

	/**
	 * set global Search constant in config
	 * 
	 * @param searchEngine
	 */
	public void saveSearchGlobal(String searchEngine) {

		config.setProperty("searchEngine.global", searchEngine);

		try {
			config.save();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e);
		}

	}

	/**
	 * Get Bing Parameters for GUI
	 */
	public void setBingParams() {
		keyLabel.setText("Bing Primary Account Key:");
		othersLabel.setText("Bing Azure URL:");
		keyTextField.setText(config.getString("searchEngine.bing.accountKey"));
		othersTextField.setText(config.getString("searchEngine.bing.pattern"));

	}

	/**
	 * Get Google Parameters for GUI
	 */
	public void setGoogleParams() {
		keyLabel.setText("API Key:");
		othersLabel.setText("Google Search Engine ID:");
		keyTextField
				.setText(config.getString("searchEngine.google.accountKey"));
		othersTextField.setText(config.getString("searchEngine.google.cx"));
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		/**
		 * make settings
		 */
		checkAccountLabel
				.setText("Please check Account of selected search Engine for remaining search requests.");
		checkAccountLabel.setTextFill(Color.ORANGE);
		metaTagsLabel.setWrapText(true);
		metaTagsLabel.setMaxWidth(500);
		metaTagsLabel.setMaxHeight(150);
		keyTextField.setPrefWidth(400);
		keyTextField.setMaxWidth(500);
		othersTextField.setPrefWidth(400);
		othersTextField.setMaxWidth(500);

		searchEngineLabel.setTooltip(new Tooltip(
				"Select the Search Engine you would like to use"));
		metaOptionsLabel
				.setTooltip(new Tooltip(
						"Select the Meta Tags you would like to include in the Lookup"));
		blackListLabel
				.setTooltip(new Tooltip(
						"Select the URL you would like to avoid being considered as results"));

		/**
		 * Add Items for BlackList Menu
		 */
		editMenu.getItems().add(deleteOption);
		editMenu.getItems().add(addNew);

		GUIController.getProperties(metaTagsLabel);

		/**
		 * check Cat CheckBox
		 */
		checkCatLabel.setTooltip(new Tooltip(
				"Check if you want to activate the Categorization for Industries"
						+ "\n"
						+ "(Info: Only Proof of Concept version implemented)"));
		if (checkCatBool == false) {
			checkCategorization.setSelected(false);
			checkCategorization.setText("Categorization Inactive");
			checkCategorization.setTextFill(Color.RED);
		} else {
			checkCategorization.setSelected(true);
			checkCategorization.setText("Categorization Active");
			checkCategorization.setTextFill(Color.GREEN);
		}

		/**
		 * Search Engine Drop Down
		 */
		ObservableList<Image> options = FXCollections.observableArrayList(
				googleImage, bingImage

		);

		Callback<ListView<Image>, ListCell<Image>> cellfactory = new Callback<ListView<Image>, ListCell<Image>>() {
			@Override
			public ListCell<Image> call(ListView<Image> param) {
				ListCell cell = new ListCell<Image>() {
					final ImageView view;
					// final ListCell<ImageView> cell = new
					// ListCell<ImageView>() {
					{
						// setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
						view = new ImageView();

					}

					@Override
					public void updateItem(Image item, boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty) {
							setGraphic(null);
							//
						}
						view.setImage(item);
						setGraphic(view);

					}

				};
				return cell;

			}

		};

		/**
		 * get Selected Value
		 */
		selectEngine.setItems(options);
		if (MainController.searchEngineIdentifier == SearchEngineFactory.BING) {
			selectEngine.getSelectionModel().select(bingImage);
			setBingParams();

		}
		if (MainController.searchEngineIdentifier == SearchEngineFactory.GOOGLE) {
			selectEngine.getSelectionModel().select(googleImage);
			setGoogleParams();
		}
		selectEngine.setButtonCell(cellfactory.call(null));
		selectEngine.setCellFactory(cellfactory);

		selectEngine.valueProperty().addListener(new ChangeListener<Image>() {
			@Override
			public void changed(ObservableValue ov, Image t, Image t1) {
				if (t1 == bingImage) {
					System.out.println("Bing selected");
					MainController.searchEngineIdentifier = SearchEngineFactory.BING;
					saveSearchGlobal("BING");
					setBingParams();

				}
				if (t1 == googleImage) {
					System.out.println("Google selected");
					MainController.searchEngineIdentifier = SearchEngineFactory.GOOGLE;
					saveSearchGlobal("GOOGLE");
					setGoogleParams();
				}
			}
		});

		cancelMetaButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Node source = (Node) event.getSource();
				Stage stage = (Stage) source.getScene().getWindow();
				stage.close();

			}
		});

		/**
		 * Save everything and check if at least one meta tag is selected
		 */
		okMetaButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				System.out.println(GUIController.metaTagElements.size());
				// System.out.println(GUIController.metaTagElements.get(0));

				if (GUIController.metaTagElements.size() < 1) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.initStyle(StageStyle.UNDECORATED);
					DialogPane pane = alert.getDialogPane();
					pane.getStylesheets().add("ch/ice/view/WebCrawler.css");
					alert.setTitle("Information Dialog");
					alert.setHeaderText("No Meta Tags selected");
					alert.setContentText("Please select at least one Meta Tag");

					alert.showAndWait();
				} else {
					config.setProperty(
							"crawler.searchForMetaTags",
							GUIController.metaTagElements.toString()
									.replace("[", "").replace("]", ""));

					if (MainController.searchEngineIdentifier == SearchEngineFactory.GOOGLE) {
						config.setProperty("searchEngine.google.accountKey",
								keyTextField.getText());
						config.setProperty("searchEngine.google.cx",
								othersTextField.getText());
					}

					if (MainController.searchEngineIdentifier == SearchEngineFactory.BING) {
						config.setProperty("searchEngine.bing.accountKey",
								keyTextField.getText());
						config.setProperty("searchEngine.bing.pattern",
								othersTextField.getText());
					}

					try {
						config.save();
					} catch (ConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						logger.error(e);
					}
					Node source = (Node) event.getSource();
					Stage stage = (Stage) source.getScene().getWindow();
					stage.close();
				}
			}
		});

		/**
		 * Load CheckBoxes
		 */
		ArrayList<MetaTag> endList = MetaTag.getMetaList();
		checkList = new ArrayList<CheckBox>();

		for (int i = 0; i < endList.size(); i++) {

			MetaTag mt = endList.get(i);
			String name = mt.getMapXML().get("name");
			String description = mt.getMapXML().get("description");
			String example = mt.getMapXML().get("example");
			CheckBox cb = new CheckBox(name);
			cb.setTooltip(new Tooltip("Description: " + description + "\n"
					+ "\n" + "Example: " + example));
			checkList.add(cb);
			cb.setMinWidth(150);
			cb.setMinHeight(30);
			for (String checker : GUIController.metaTagElements) {
				if (cb.getText().equals(checker)) {
					cb.selectedProperty().set(true);
				}
				System.out.println(cb.getText() + " " + checker);
			}
			cb.selectedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(
						ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
				}
			});
			cb.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					// TODO Auto-generated method stub
					if (cb.isSelected()) {
						System.out.println(cb.getText() + " is checked");
						GUIController.metaTagElements.add(cb.getText());
						String innerLabel = GUIController.metaTagElements
								.toString().replace("[", "").replace("]", "");
						metaTagsLabel.setText(innerLabel);

					}
					if (cb.isSelected() == false) {
						System.out.println("We are in!");
						for (String checker : GUIController.metaTagElements) {
							if (checker.equals(cb.getText())) {

								GUIController.metaTagElements.remove(checker);
								String innerLabel = GUIController.metaTagElements
										.toString().replace("[", "")
										.replace("]", "");
								metaTagsLabel.setText(innerLabel);
							}
						}

					}

				}

			});

			flowPane.setPrefWrapLength(620);
			flowPane.getChildren().add(cb);
		}

		/**
		 * Unselect all Meta tags
		 */
		unselectAll.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				System.out.println("Unselected pressed");
				System.out.println(checkList.size());

				for (CheckBox checky : checkList) {
					if (checky.isSelected() == true) {
						checky.fire();
					}

				}

			}
		});

		/**
		 * Select all Meta tags
		 */
		selectAll.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.out.println("Selected pressed");
				for (CheckBox checky : checkList) {
					if (checky.isSelected() == false) {
						checky.fire();
					}

				}

			}
		});

		List<String> blackArray = new CopyOnWriteArrayList<String>(
				Arrays.asList(GUIController.config
						.getStringArray("searchEngine.bing.blacklist")));
		ObservableList<String> blackArray2 = FXCollections
				.observableArrayList(blackArray);
		blackListView.setItems(blackArray2);
		blackListView.setEditable(true);

		blackListView.setCellFactory(TextFieldListCell.forListView());

		blackListView
				.setOnEditStart(new EventHandler<ListView.EditEvent<String>>() {
					@Override
					public void handle(ListView.EditEvent<String> t) {
						editMenu.hide();

					}

				});

		blackListView
				.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>() {
					@Override
					public void handle(ListView.EditEvent<String> t) {
						blackListView.getItems().set(t.getIndex(),
								t.getNewValue());
						System.out.println("setOnEditCommit");
						editMenu.hide();

					}

				});

		blackListView
				.setOnEditCancel(new EventHandler<ListView.EditEvent<String>>() {
					@Override
					public void handle(ListView.EditEvent<String> t) {
						System.out.println("setOnEditCancel");
						editMenu.hide();
					}
				});

		blackListView.addEventHandler(MouseEvent.MOUSE_CLICKED,
				new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						if (event.getButton().equals(MouseButton.SECONDARY)) {
							editMenu.show(blackListView, event.getScreenX(),
									event.getScreenY());
						} else if (event.getButton()
								.equals(MouseButton.PRIMARY)) {
							editMenu.hide();
						}

					}

				});

		deleteOption.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				blackArray2.remove(blackListView.getSelectionModel()
						.getSelectedItem());
				config.setProperty("searchEngine.bing.blacklist", blackArray2
						.toString().replace("[", "").replace("]", ""));
				try {
					config.save();
				} catch (ConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.error(e);
				}

			}
		});

		addNew.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				FlowPane pane = new FlowPane(10, 0);
				pane.setId("flowPaneBlackList");
				pane.setPrefWidth(300);
				pane.setPadding(new Insets(5, 5, 5, 5));
				Stage newWindow = new Stage();
				newWindow.initStyle(StageStyle.UNDECORATED);
				newWindow.initModality(Modality.APPLICATION_MODAL);
				TextField editNew = new TextField();
				editNew.setEditable(true);
				Button cancel = new Button("Cancel");
				cancel.setTextFill(Color.WHITE);
				Button save = new Button("Save");
				save.setTextFill(Color.WHITE);
				pane.getChildren().addAll(editNew, cancel, save);
				Scene scene = new Scene(pane);
				scene.getStylesheets().add("ch/ice/view/WebCrawler.css");
				newWindow.setScene(scene);
				save.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {

						if (editNew.getText().isEmpty() == false) {
							blackArray2.add(editNew.getText());
							config.setProperty("searchEngine.bing.blacklist",
									blackArray2.toString().replace("[", "")
											.replace("]", ""));
							try {
								GUIController.config.save();
							} catch (ConfigurationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								logger.error(e);
							}

							newWindow.close();
						}
						if (editNew.getText().equals("")) {
							Alert alert = new Alert(AlertType.ERROR);
							alert.initStyle(StageStyle.UNDECORATED);
							DialogPane pane = alert.getDialogPane();
							pane.getStylesheets().add(
									"ch/ice/view/WebCrawler.css");
							alert.setTitle("Empty Field");
							alert.setHeaderText("Empty Field");
							alert.setContentText("Please enter Blacklist name");
							alert.show();
						}
					}
				});
				cancel.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						newWindow.close();

					}
				});

				newWindow.show();

			}
		});

		resetKeysButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String bingKeyReset = config
						.getString("searchEngine.bing.accountKeyReset");
				String bingPatternReset = config
						.getString("searchEngine.bing.patternReset");
				String googleKeyReset = config
						.getString("searchEngine.google.accountKeyReset");
				String googlePatternReset = config
						.getString("searchEngine.google.cxReset");

				config.setProperty("searchEngine.bing.accountKey", bingKeyReset);
				config.setProperty("searchEngine.bing.pattern",
						bingPatternReset);
				config.setProperty("searchEngine.google.accountKey",
						googleKeyReset);
				config.setProperty("searchEngine.google.cx", googlePatternReset);

				if (MainController.searchEngineIdentifier == SearchEngineFactory.GOOGLE) {
					System.out.println("Hase");
					setGoogleParams();
					try {
						config.save();
					} catch (ConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						logger.error(e);
					}
				}
				if (MainController.searchEngineIdentifier == SearchEngineFactory.BING) {
					System.out.println("Bär");
					setBingParams();
					try {
						config.save();
					} catch (ConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						logger.error(e);
					}
				}
			}
		});

		checkCategorization.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if (checkCategorization.isSelected() == false) {
					checkCatBool = false;
					checkCategorization.setText("Categorization Inactive");
					checkCategorization.setTextFill(Color.RED);
				} else {
					checkCatBool = true;
					checkCategorization.setText("Categorization Active");
					checkCategorization.setTextFill(Color.GREEN);
				}

			}
		});

		vBox.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
		});
		vBox.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Node source = (Node) event.getSource();
				Stage stage = (Stage) source.getScene().getWindow();
				stage.setX(event.getScreenX() - xOffset);
				stage.setY(event.getScreenY() - yOffset);
			}
		});
	}
}
