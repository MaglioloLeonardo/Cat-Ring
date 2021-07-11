module CatERing {
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.swing;
    requires javafx.swt;
    requires javafx.web;
    requires java.sql;
    requires org.apache.commons.text;
    opens App;
    opens App.ui;
    opens App.ui.menu;
    exports App.ui.menu;
    opens App.Test;
}