package pers.harry_deng.yawnsuite.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import pers.harry_deng.yawnsuite.beans.properties.HostTableData;
import pers.harry_deng.yawnsuite.functions.HostScanner;
import pers.harry_deng.yawnsuite.functions.NICChooser;
import pers.harry_deng.yawnsuite.utils.FileUtil;
import pers.harry_deng.yawnsuite.utils.SetNumUtil;

public class HostScannerFrameController {

    @FXML
    private TextArea scanArea;

    @FXML
    private TableView<HostTableData> tb_host;

    @FXML
    private TableColumn<HostTableData, String> tc_num;

    @FXML
    private TableColumn<HostTableData, String> tc_hostIP;

    @FXML
    private TableColumn<HostTableData, String> tc_hostMAC;

    private final HostScanner hostService = new HostScanner();

    public void initialize() {
        hostService.setHostInfoTableViewData(tb_host, hostService.getHostInfoTableViewData(), tc_hostIP, tc_hostMAC);

        tc_num.setCellFactory(new SetNumUtil<>());
    }

    @FXML
    void refreshEvent() {
        tb_host.setItems(hostService.getHostInfoTableViewData());
    }

    @FXML
    void startScanEvent() {
        FileUtil.clearFile("Host.data");
        tb_host.setItems(hostService.getHostInfoTableViewData());
        hostService.setNetworkInterface(NICChooser.getDevice());
        hostService.setTextArea(scanArea);
        new Thread(hostService).start();
    }


}
