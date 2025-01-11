module Application {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    exports edu.metrostate;
    opens edu.metrostate.Controller to javafx.fxml;
    exports edu.metrostate.Model;
    opens edu.metrostate.Model to javafx.base, javafx.fxml;
    opens edu.metrostate to javafx.base, javafx.fxml;
}