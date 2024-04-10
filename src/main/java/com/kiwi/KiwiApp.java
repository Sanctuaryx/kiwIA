package com.kiwi;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class KiwiApp extends Application {

    private List<String> list = new ArrayList<>();
    private int currentIndex = 0;
    private double orgCliskSceneX, orgReleaseSceneX;
    private ImageView imageView;
    boolean isClicked = false; // Flag to track if the image has been clicked
    private List<TextField> allInputTextFields = new ArrayList<>();

    boolean modelUploaded = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("kiwIA - Selector de modelos");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/kiwi.png")));

        // Main layout using BorderPane
        BorderPane borderPane = new BorderPane();

        // Navigation items
        ObservableList<String> navItems = FXCollections.observableArrayList(
                "Carga del modelo", "Variables de entrada", "Selector de objetivos");

        // Left side navigation panel using ListView
        ListView<String> navigation = new ListView<>(navItems);
        navigation.setPrefWidth(150);
        navigation.setPadding(new Insets(10));
        VBox.setVgrow(navigation, Priority.ALWAYS);

        Button computeButton = new Button("Computar");
        computeButton.setMaxWidth(Double.MAX_VALUE); // Make the button fill the width
        computeButton.setOnAction(event -> {
            if ((imageView != null && imageView.getEffect() != null) && modelUploaded && checkAllVariablesEntered()) {
                // Action for the compute button
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error en la selección");
                alert.setContentText(
                        "Por favor, asegúrese de haber subido el modelo, seleccionado una objetivo y completado todas las variables de entrada.");

                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/kiwi.png")));

                alert.showAndWait();
            }
        });

        // Handling navigation selection
        navigation.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "Carga del modelo":
                    borderPane.setCenter(createModelUploadSection(primaryStage));
                    break;
                case "Variables de entrada":
                    borderPane.setCenter(createInputVariablesSection());
                    break;
                case "Selector de objetivos":
                    borderPane.setCenter(createImageSelectionSection(primaryStage));
                    break;
                default:
                    borderPane.setCenter(createModelUploadSection(primaryStage));
            }
        });

        StackPane navigationLayer = new StackPane(navigation, computeButton);
        StackPane.setAlignment(computeButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(computeButton, new Insets(0, 20, 20, 20)); // Adjust the bottom margin to position the
                                                                       // button

        borderPane.setLeft(navigationLayer); // Add navigation to the main layout

        // Default center view
        borderPane.setCenter(createModelUploadSection(primaryStage));

        // Setting the scene and stage
        Scene scene = new Scene(borderPane, 600, 400);
        scene.getStylesheets().add(getClass().getResource("style/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.show();

    }

    private VBox createModelUploadSection(Stage primaryStage) {
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Label modelLabel = new Label("Modelo (.pickle):");
        TextField modelPathField = new TextField();
        modelPathField.setEditable(false);
        Button uploadModelButton = new Button("Subir");

        uploadModelButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pickle Files", "*.pickle"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null && selectedFile.getName().endsWith(".pickle")) {
                modelPathField.setText(selectedFile.getAbsolutePath());
                modelUploaded = modelPathField != null && !modelPathField.getText().isEmpty();
            } else {
                modelPathField.setText("");
                modelUploaded = false;
            }
        });

        HBox hbox = new HBox(10, modelLabel, modelPathField, uploadModelButton);
        hbox.setAlignment(Pos.CENTER);

        vbox.getChildren().add(hbox);

        return vbox;
    }

    private VBox createInputVariablesSection() {
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        // Define labels and fields for the input variables
        String[] labels = { "Variable 1:", "Variable 2:", "Variable 3:", "Variable 4:", "Variable 5:" };
        for (int i = 0; i < labels.length; i++) {
            Label label = new Label(labels[i]);
            TextField textField = new TextField();
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                checkAllVariablesEntered();
            });

            // Adjustments to make text field look better
            textField.setPromptText("Ingrese un valor");
            textField.setPrefHeight(30);
            textField.setPrefWidth(200);
            gridPane.add(label, 0, i); // Column 0, Row i
            gridPane.add(textField, 1, i); // Column 1, Row i

            allInputTextFields.add(textField);
        }

        // Column constraints for even distribution and centering
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHalignment(HPos.RIGHT); // Right align labels

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.ALWAYS); // Allow text field to grow

        gridPane.getColumnConstraints().addAll(column1, column2); // Apply column constraints

        vbox.getChildren().add(gridPane);

        return vbox;
    }

    private VBox createImageSelectionSection(Stage primaryStage) {
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        try {
            list.add(getClass().getResource("/images/kiwi.png").toExternalForm());
            list.add(getClass().getResource("/images/papa.png").toExternalForm());
            list.add(getClass().getResource("/images/pina.png").toExternalForm());
            list.add(getClass().getResource("/images/platano.png").toExternalForm());

            GridPane root = new GridPane();
            root.setAlignment(Pos.CENTER);

            Button lbutton = new Button("<");
            Button rButton = new Button(">");

            Image images[] = new Image[list.size()];
            for (int i = 0; i < list.size(); i++) {
                images[i] = new Image(list.get(i));
            }

            imageView = new ImageView(images[currentIndex]);
            imageView.setPreserveRatio(true); // Ensure aspect ratio is preserved
            imageView.setSmooth(true); // Use higher quality filtering
            imageView.setCache(true); // Cache to improve performance

            StackPane imagePane = new StackPane(); // Encapsulate imageView in StackPane
            imagePane.getChildren().add(imageView);

            imageView.setOnMousePressed(t -> orgCliskSceneX = t.getSceneX());

            rButton.setOnAction(e -> {
                currentIndex = (currentIndex + 1) % list.size();
                imageView.setImage(images[currentIndex]);
                isClicked = false; // Reset selection when navigation button is clicked
                imageView.setEffect(null); // Remove highlighting effect
            });

            lbutton.setOnAction(e -> {
                currentIndex = (currentIndex - 1 + list.size()) % list.size();
                imageView.setImage(images[currentIndex]);
                isClicked = false; // Reset selection when navigation button is clicked
                imageView.setEffect(null); // Remove highlighting effect
            });

            imagePane.setOnMouseClicked(event -> {
                isClicked = !isClicked; // Toggle the selection state
                if (isClicked) {
                    // Apply permanent highlighting effect when selected
                    imageView.setEffect(new Glow(0.5)); // Adjust glow intensity as needed
                } else {
                    // Remove highlighting effect when deselected
                    imageView.setEffect(null);
                }
            });

            imagePane.setOnMouseEntered(event -> {
                // Apply highlighting effect only when not selected
                if (!isClicked) {
                    imageView.setEffect(new Glow(0.5)); // Adjust glow intensity as needed
                }
            });

            imagePane.setOnMouseExited(event -> {
                // Remove highlighting effect when mouse exits the image
                if (!isClicked) {
                    imageView.setEffect(null);
                }
            });

            imageView.setFitHeight(200);
            imageView.setFitWidth(600);

            HBox hBox = new HBox();
            hBox.setSpacing(15);
            hBox.setAlignment(Pos.CENTER);
            hBox.getChildren().addAll(lbutton, imagePane, rButton); // Add imagePane instead of imageView

            root.add(hBox, 1, 1);
            vbox.getChildren().add(root);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vbox;
    }

    private boolean checkAllVariablesEntered() {
        for (TextField textField : allInputTextFields) {
            if (textField.getText().isEmpty()) {
                return false;
            }
        }
        return true;

    }

    public static void main(String[] args) {
        launch(args);
    }
}
