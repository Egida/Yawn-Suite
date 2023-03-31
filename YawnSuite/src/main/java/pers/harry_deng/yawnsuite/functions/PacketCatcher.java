package pers.harry_deng.yawnsuite.functions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;
import pers.harry_deng.yawnsuite.beans.items.myPacket;
import pers.harry_deng.yawnsuite.beans.properties.PacketTableData;
import pers.harry_deng.yawnsuite.utils.FileUtil;
import pers.harry_deng.yawnsuite.utils.JsonUtil;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PacketCatcher implements Runnable {

    private final NetworkInterface device = NICChooser.getDevice();
    private JpcapCaptor jc;
    private boolean threadStop = false;
    private String filter = "";

    public void setThreadStop(boolean threadStop) {
        this.threadStop = threadStop;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public void run() {
        try {
            jc = JpcapCaptor.openDevice(device, 1512, true, 50);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!threadStop) {
            Packet packet = jc.getPacket();
            if (packet instanceof IPPacket && ((IPPacket) packet).version == 4) {
                IPPacket ip = (IPPacket) packet;//强转

                String protocol = "";
                switch ((int) ip.protocol) {
                    case 1:
                        protocol = "ICMP";
                        break;
                    case 2:
                        protocol = "IGMP";
                        break;
                    case 6:
                        protocol = "TCP";
                        break;
                    case 8:
                        protocol = "EGP";
                        break;
                    case 9:
                        protocol = "IGP";
                        break;
                    case 17:
                        protocol = "UDP";
                        break;
                    case 41:
                        protocol = "IPv6";
                        break;
                    case 89:
                        protocol = "OSPF";
                        break;
                    default:
                        break;
                }
                System.out.println("");
                System.out.println("----------------------------------");
                System.out.println("-IPv4"
                        + "-" + ip.priority
                        + "-" + ip.t_flag
                        + "-" + ip.r_flag
                        + "-" + ip.length
                        + "-" + ip.ident
                        + "-" + ip.dont_frag
                        + "-" + ip.more_frag
                        + "-" + ip.offset
                        + "-" + ip.hop_limit);

                Timestamp timestamp = new Timestamp((packet.sec * 1000) + (packet.usec / 1000));
                StringBuilder tempInfo = new StringBuilder();
                for (int j = 0; j < packet.data.length; j++) {
                    tempInfo.append(packet.data[j]);
                }
                try {
                    myPacket myPacket = new myPacket(timestamp.toString(), ip.src_ip.getHostAddress(), ip.dst_ip.getHostAddress(),
                            protocol, String.valueOf(packet.data.length), tempInfo.toString());
                    savePacketInfo(myPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //  }
            }
        }
    }

    public List<myPacket> getFiltered() {
        List<myPacket> fPackets = new ArrayList<>();
        for (int i = 0; i < getPacketInfoList().size(); i++) {
            myPacket p = getPacketInfoList().get(i);
            if (p.getProtocol().equals(filter) || p.getSource().equals(filter) || p.getDestination().equals(filter)) {
                fPackets.add(p);
            }
        }
        return fPackets;
    }

    public static void savePacketInfo(myPacket packetInfo) throws IOException {
        String data = JsonUtil.toJson(packetInfo);
        data = data.replace("\n", "");
        FileUtil.writeData(data, "Packet.data", true);
    }

    public List<myPacket> getPacketInfoList() {
        List<myPacket> packetInfos = new ArrayList<>();
        List<Object> infos = FileUtil.getData("Packet.data", myPacket.class);
        for (Object o : infos) {
            myPacket p = (myPacket) o;
            if (p != null) {
                packetInfos.add(p);
            }
        }
        return packetInfos;
    }

    public ObservableList<PacketTableData> getPacketInfoTableViewData() {
        ObservableList<PacketTableData> packetList = FXCollections.observableArrayList();

        if (!filter.equals("")) {
            for (myPacket pi : getFiltered()) {
                PacketTableData piTD = new PacketTableData(pi.getTime(), pi.getSource(), pi.getDestination(), pi.getProtocol(), pi.getLength(), pi.getInfo());
                packetList.add(piTD);
            }

        } else {
            for (myPacket pi : getPacketInfoList()) {
                PacketTableData piTD = new PacketTableData(pi.getTime(), pi.getSource(), pi.getDestination(), pi.getProtocol(), pi.getLength(), pi.getInfo());
                packetList.add(piTD);
            }
        }

        return packetList;
    }

    public void setPacketInfoTableViewData(TableView<PacketTableData> tv_packet, ObservableList<PacketTableData> dataList,
                                           TableColumn<PacketTableData, String> tc_time, TableColumn<PacketTableData, String> tc_source,
                                           TableColumn<PacketTableData, String> tc_des, TableColumn<PacketTableData, String> tc_pro,
                                           TableColumn<PacketTableData, String> tc_length, TableColumn<PacketTableData, String> tc_info) {
        tc_time.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        tc_source.setCellValueFactory(cellData -> cellData.getValue().sourceProperty());
        tc_des.setCellValueFactory(cellData -> cellData.getValue().destinationProperty());
        tc_pro.setCellValueFactory(cellData -> cellData.getValue().protocolProperty());
        tc_length.setCellValueFactory(cellData -> cellData.getValue().lengthProperty());
        tc_info.setCellValueFactory(cellData -> cellData.getValue().infoProperty());
        tv_packet.setItems(dataList);
    }

    public void getPacketInfo(TextArea textArea, TableView<PacketTableData> tableView) {

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> textArea.setText(newValue.getInfo())
        );
    }

}