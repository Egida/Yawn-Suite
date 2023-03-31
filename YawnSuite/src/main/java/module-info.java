module pers.harry_deng.yawnsuite {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires gson;
    requires jpcap;
    requires java.sql;

    opens pers.harry_deng.yawnsuite.beans.items to gson;
    opens pers.harry_deng.yawnsuite to javafx.fxml;
    opens pers.harry_deng.yawnsuite.controllers to javafx.fxml;
    exports pers.harry_deng.yawnsuite to javafx.graphics, javafx.fxml;
    exports pers.harry_deng.yawnsuite.controllers to javafx.graphics, javafx.fxml;
}