module com.example.lab_marksman {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;

    opens com.example.lab_marksman to javafx.fxml;
    exports com.example.lab_marksman;
}