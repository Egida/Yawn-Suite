package pers.harry_deng.yawnsuite.utils;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import pers.harry_deng.yawnsuite.beans.properties.HostTableData;

import java.util.Optional;

public class SimpleUtil {

    public boolean isTextEmpty(String str) {
        return str != null && !"".equals(str.trim());
    }

    public void informationDialog(Alert.AlertType alterType, String title, String header, String message) {
        Alert alert = new Alert(alterType, message, new ButtonType
                ("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE),
                new ButtonType("Confirm", ButtonBar.ButtonData.YES));

        alert.setTitle(title);
        alert.setHeaderText(header);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("file:D:\\FileStorage\\DevelopFile\\CodeField\\CodeField_JAVAFx\\YawnSuite\\YawnSuite\\src\\main\\resources\\icons\\666.png"));
        Optional<ButtonType> buttonType = alert.showAndWait();
        buttonType.get();
    }


}
