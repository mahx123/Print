module com.rookie.printonline {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.rookie.printonline to javafx.fxml;
    exports com.rookie.printonline;
    requires jdk.httpserver;
    requires java.desktop;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.apache.commons.io;
    opens com.rookie.printonline.result to com.fasterxml.jackson.databind;

}