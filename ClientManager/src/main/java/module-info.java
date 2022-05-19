module com.example.clientmanager {
    requires javafx.controls;
    requires javafx.fxml;

    requires javafx.base;
    requires javafx.graphics;

    requires io.netty.transport;
    requires io.netty.codec;
    requires io.netty.buffer;
    requires lombok;

    opens com.example to javafx.fxml;
    exports com.example;

}