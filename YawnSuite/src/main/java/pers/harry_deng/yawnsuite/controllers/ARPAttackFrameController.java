package pers.harry_deng.yawnsuite.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import pers.harry_deng.yawnsuite.beans.properties.HostTableData;
import pers.harry_deng.yawnsuite.functions.ARPAttack;
import pers.harry_deng.yawnsuite.functions.HostScanner;
import pers.harry_deng.yawnsuite.functions.NICChooser;
import pers.harry_deng.yawnsuite.utils.NetUtil;
import pers.harry_deng.yawnsuite.utils.SetNumUtil;
import pers.harry_deng.yawnsuite.utils.SimpleUtil;

public class ARPAttackFrameController {

    @FXML
    private TableView<HostTableData> tv_host;

    @FXML
    private TableColumn<HostTableData, String> tc_num;

    @FXML
    private TableColumn<HostTableData, String> tc_ip;

    @FXML
    private TableColumn<HostTableData, String> tc_mac;

    @FXML
    private TextField tf_desIP;

    @FXML
    private TextField tf_srcIP;

    @FXML
    private TextField tf_srcMAC;

    @FXML
    private TextField tf_attIP;


    @FXML
    private ImageView sending;

    private final ARPAttack arpService = new ARPAttack();
    private static final HostScanner hostService = new HostScanner();
    private final SimpleUtil simpleUtil = new SimpleUtil();
    private final NetUtil netUtil = new NetUtil();

    public void initialize() {
        hostService.setHostInfoTableViewData(tv_host, hostService.getHostInfoTableViewData(), tc_ip, tc_mac);
        tc_num.setCellFactory(new SetNumUtil<>());
        sending.setVisible(false);
        arpService.getHostIP(tf_desIP,tv_host);
        arpService.setNetworkInterface(NICChooser.getDevice());
    }

    @FXML
    void startAttackEvent() {
        arpService.setThreadStop(false);

        new Thread(arpService).start();

        simpleUtil.informationDialog(Alert.AlertType.INFORMATION, "Info", "Attack Info", "Attack Succeed!");
        sending.setVisible(true);
    }

    @FXML
    void startAttackEvent2() {
        arpService.setThreadStop(false);
        arpService.setIfTwoWay(true);

        new Thread(arpService).start();

        simpleUtil.informationDialog(Alert.AlertType.INFORMATION, "Info", "Attack Info", "Attack Succeed!");
        sending.setVisible(true);
    }

    @FXML
    void stopAttackEvent() {
        arpService.setDesIP("0.0.0.0");
        arpService.setThreadStop(true);
        System.out.println("Attack finished!");
        sending.setVisible(false);
    }

}
