package pers.harry_deng.yawnsuite.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import pers.harry_deng.yawnsuite.Starter;
import pers.harry_deng.yawnsuite.beans.properties.NICTableData;
import pers.harry_deng.yawnsuite.functions.NICChooser;
import pers.harry_deng.yawnsuite.utils.FileUtil;
import pers.harry_deng.yawnsuite.utils.SimpleUtil;

import java.io.IOException;

public class NICChooserFrameController {

    @FXML
    private Stage NICStage;

    public void setNICStage(Stage stage) {
        this.NICStage = stage;
    }

    @FXML
    private AnchorPane rootPane;

    @FXML
    private BorderPane nicPane;

    @FXML
    private TextField tf_chosen;

    @FXML
    private TableView<NICTableData> tv_NIC;

    @FXML
    private TableColumn<NICTableData, String> tc_num;

    @FXML
    private TableColumn<NICTableData, String> tc_nicName;

    @FXML
    private TableColumn<NICTableData, String> tc_nicIP;

    @FXML
    private TableColumn<NICTableData, String> tc_nicMAC;

    private final NICChooser nicService = new NICChooser();
    private final SimpleUtil simpleUtil = new SimpleUtil();
    public static NetworkInterface device;

    public void initialize() throws IOException {
        nicService.getNetworkInterface();
        //将数据添加到表格控件中
        nicService.setNICInfoTableViewData(tv_NIC, nicService.getNICInfoTableViewData(), tc_num, tc_nicName, tc_nicIP, tc_nicMAC);
        nicService.chooseNIC(tf_chosen, tv_NIC);
        FileUtil.clearFile("Packet.data");
    }


    @FXML
    void bt_chooseEvent() {
        device = JpcapCaptor.getDeviceList()[Integer.parseInt(tf_chosen.getText()) - 1];
    }

    @FXML
    void chooseNICEvent() {
        rootPane.getChildren().clear();
        rootPane.getChildren().add(nicPane);
    }

    @FXML
    void analysisEvent() {
        Pane pane = new Starter().initPacketFrame();
        rootPane.getChildren().clear();
        rootPane.getChildren().add(pane);
    }

    @FXML
    void scanHostEvent() {
        Pane pane = new Starter().initScanHostFrame();
        rootPane.getChildren().clear();
        rootPane.getChildren().add(pane);
    }

    @FXML
    void arpAttackEvent() {
        Pane pane = new Starter().initARPAttackFrame();
        rootPane.getChildren().clear();
        rootPane.getChildren().add(pane);
    }

    @FXML
    void closeEvent() {
        FileUtil.clearFile("Host.data");
        FileUtil.clearFile("Packet.data");
        NICStage.close();
    }

    @FXML
    void extendEvent() {
        Pane pane = new Starter().initMoreFunctionFrame();
        rootPane.getChildren().clear();
        rootPane.getChildren().add(pane);
    }

    @FXML
    void minusEvent() {
        NICStage.setIconified(true);
    }
}
