package pers.harry_deng.yawnsuite.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import pers.harry_deng.yawnsuite.beans.properties.PacketTableData;
import pers.harry_deng.yawnsuite.functions.PacketCatcher;
import pers.harry_deng.yawnsuite.utils.FileUtil;
import pers.harry_deng.yawnsuite.utils.SetNumUtil;
import pers.harry_deng.yawnsuite.utils.SimpleUtil;

public class PacketCatcherFrameController {

    @FXML
    private AnchorPane packetPane;

    @FXML
    private TableView<PacketTableData> tv_packet;

    @FXML
    private TextField tf_filter;

    @FXML
    private TableColumn<PacketTableData, String> tc_num;

    @FXML
    private TableColumn<PacketTableData, String> tc_time;

    @FXML
    private TableColumn<PacketTableData, String> tc_source;

    @FXML
    private TableColumn<PacketTableData, String> tc_des;

    @FXML
    private TableColumn<PacketTableData, String> tc_pro;

    @FXML
    private TableColumn<PacketTableData, String> tc_length;

    @FXML
    private TableColumn<PacketTableData, String> tc_info;

    @FXML
    private TextArea infoArea;

    private final PacketCatcher packetService = new PacketCatcher();
    private final SimpleUtil simpleUtil = new SimpleUtil();

    public void initialize() {
        tc_num.setCellFactory(new SetNumUtil<>());
        packetService.setPacketInfoTableViewData(tv_packet, packetService.getPacketInfoTableViewData(),
                tc_time, tc_source, tc_des, tc_pro, tc_length, tc_info);
        packetService.getPacketInfo(infoArea,tv_packet);
    }


    @FXML
    void clearEvent(ActionEvent event) {
        tv_packet.getItems().clear();
        infoArea.clear();
    }

    @FXML
    void okEvent() {
        packetService.setFilter(tf_filter.getText());
        tv_packet.setItems(packetService.getPacketInfoTableViewData());
    }

    @FXML
    void stopEvent() {
        packetService.setThreadStop(true);
    }

    @FXML
    void refreshEvent() {
        tv_packet.setItems(packetService.getPacketInfoTableViewData());
    }

    @FXML
    void startEvent() {
        new Thread(packetService).start();
    }

}
