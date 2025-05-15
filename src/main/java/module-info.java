module com.rookie.printonline {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.rookie.printonline to javafx.fxml;
    exports com.rookie.printonline;
    requires jdk.httpserver;

}