package view;


import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Prompt {

    public static void showMessage(String message, PromptType type) {
        Stage stage = new Stage();
        Label label = new Label();
        Button button = new Button("OK");
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.alignmentProperty().set(Pos.CENTER);
        vBox.getChildren().add(label);
        vBox.getChildren().add(button);
        button.setOnMouseClicked(mouseEvent -> stage.close());
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vBox);
        Scene scene = new Scene(borderPane);
        if (type.equals(PromptType.Error))
            scene.getStylesheets().add(Prompt.class.getResource("/CSS/Error.css").toExternalForm());
        if (type.equals(PromptType.Success))
            scene.getStylesheets().add(Prompt.class.getResource("/CSS/Success.css").toExternalForm());
        if (type.equals(PromptType.Message))
            scene.getStylesheets().add(Prompt.class.getResource("/CSS/Message.css").toExternalForm());
        label.setText(type + " : " + message);
        stage.setScene(scene);
        stage.setX(Stage.getWindows().get(0).getX());
        stage.setY(Stage.getWindows().get(0).getY() + 180);
        stage.setWidth(480);
        stage.setHeight(200);
        stage.show();
    }
}