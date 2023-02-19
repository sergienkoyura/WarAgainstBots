module com.project.waragainstbots {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;

    opens com.project.waragainstbots to javafx.fxml;
    exports com.project.waragainstbots;
}