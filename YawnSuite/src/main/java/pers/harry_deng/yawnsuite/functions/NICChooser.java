package pers.harry_deng.yawnsuite.functions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import pers.harry_deng.yawnsuite.Starter;
import pers.harry_deng.yawnsuite.beans.items.NIC;
import pers.harry_deng.yawnsuite.beans.properties.NICTableData;
import pers.harry_deng.yawnsuite.utils.FileUtil;
import pers.harry_deng.yawnsuite.utils.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static pers.harry_deng.yawnsuite.utils.NetUtil.macToStr;

public class NICChooser {

    public void getNetworkInterface() throws IOException {
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        FileUtil.clearFile("NIC.data");
        for (int i = 0; i < devices.length; i++) {
            String a = devices[i].description;
            String b;
            if (devices[i].addresses.length == 0) {
                b = "This NIC hasn't been allocated an IP address";
            }else{
                b = devices[i].addresses[0].address.getHostAddress();
            }

            String c = macToStr(devices[i].mac_address);
            NIC nic = new NIC(String.valueOf(i + 1),
                    a,
                    b,
                    c);
            saveNICInfo(nic);
        }
    }

    public static void saveNICInfo(NIC nic) throws IOException {
        String data = JsonUtil.toJson(nic);
        data = data.replace("\n", "");
        FileUtil.writeData(data, "NICInfo.data", true);
    }

    public List<NIC> getNICInfoList() {
        List<NIC> nicInfos = new ArrayList<>();
        List<Object> infos = FileUtil.getData("NICInfo.data", NIC.class);
        for (Object o : infos) {
            NIC n = (NIC) o;
            if (n != null) {
                nicInfos.add(n);
            }
        }
        return nicInfos;
    }

    public void setNICInfoTableViewData(TableView<NICTableData> tableView, ObservableList<NICTableData> dataList,
                                        TableColumn<NICTableData, String> tc_nicNum,
                                        TableColumn<NICTableData, String> tc_nicName,
                                        TableColumn<NICTableData, String> tc_nicIP,
                                        TableColumn<NICTableData, String> tc_nicMAC) {

        tc_nicNum.setCellValueFactory(cellData -> cellData.getValue().nicNumProperty());
        tc_nicName.setCellValueFactory(cellData -> cellData.getValue().nicNameProperty());
        tc_nicIP.setCellValueFactory(cellData -> cellData.getValue().nicIPProperty());
        tc_nicMAC.setCellValueFactory(cellData -> cellData.getValue().nicMACProperty());
        //将数据添加到表格控件中
        tableView.setItems(dataList);

    }

    public ObservableList<NICTableData> getNICInfoTableViewData() {
        ObservableList<NICTableData> nicList = FXCollections.observableArrayList();
        for (NIC ni : getNICInfoList()) {
            NICTableData niTD = new NICTableData(ni.getNicNum(), ni.getNicName(), ni.getNicIP(), ni.getNicMAC());
            nicList.add(niTD);
        }
        return nicList;
    }

    public static NetworkInterface device;

    public static NetworkInterface getDevice() {
        return device;
    }

    public void chooseNIC(TextField tf_chosen, TableView<NICTableData> tableView) {

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    tf_chosen.setText(newValue.getNicNum());
                    device = JpcapCaptor.getDeviceList()[Integer.parseInt(tf_chosen.getText()) - 1];
                }
        );

    }

}