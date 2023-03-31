package pers.harry_deng.yawnsuite;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pers.harry_deng.yawnsuite.controllers.NICChooserFrameController;

import java.io.IOException;


public class Starter extends Application {
    double x, y = 0;
    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) throws Exception {
        Starter.stage = stage;
        initChooseNICFrame();
    }

    public void initChooseNICFrame() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Starter.class.getResource("views/NICChooserFrame.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.initStyle(StageStyle.UNDECORATED);

        root.setOnMousePressed(evt -> {
            x = evt.getSceneX();
            y = evt.getSceneY();
        });
        root.setOnMouseDragged(evt -> {
            stage.setX(evt.getScreenX() - x);
            stage.setY(evt.getScreenY() - y);
        });
        stage.setScene(scene);
        NICChooserFrameController controller = loader.getController();
        controller.setNICStage(stage);
        String urlDir = "file:D:\\FileStorage\\DevelopFile\\CodeField\\CodeField_JAVAFx\\YawnSuite\\YawnSuite\\src\\main\\resources\\icons\\mainLogo.png";
        Image image = new Image(urlDir);
        stage.getIcons().add(image);
        stage.show();
    }

    public Pane initScanHostFrame() {
        return loadFrame("views/HostScannerFrame.fxml");
    }

    public Pane initARPAttackFrame() {
        return loadFrame("views/ARPAttackFrame.fxml");
    }

    public Pane initPacketFrame(){
        return loadFrame("views/PacketCatcherFrame.fxml");
    }

    public Pane initMoreFunctionFrame(){
        return loadFrame("views/MoreFunctionFrame.fxml");
    }

    public Pane loadFrame(String name) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(name));
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

