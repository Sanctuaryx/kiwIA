module com.kiwi {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.kiwi to javafx.fxml;

    exports com.kiwi;
}
