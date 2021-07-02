module client {

    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;


    opens view.menus to javafx.fxml;
    exports view.menus;

}