package pers.harry_deng.yawnsuite.functions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;
import pers.harry_deng.yawnsuite.beans.items.Host;
import pers.harry_deng.yawnsuite.beans.properties.HostTableData;
import pers.harry_deng.yawnsuite.utils.FileUtil;
import pers.harry_deng.yawnsuite.utils.JsonUtil;
import pers.harry_deng.yawnsuite.utils.SimpleUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static pers.harry_deng.yawnsuite.utils.NetUtil.macToStr;
import static pers.harry_deng.yawnsuite.utils.NetUtil.strToMac;

public class HostScanner implements Runnable {
    private NetworkInterface dev;
    private TextArea textArea;

    public void setNetworkInterface(NetworkInterface networkInterface) {
        this.dev = networkInterface;
    }

    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void run() {
        ArrayList<String> hostList = new ArrayList<>();
        JpcapCaptor captor;
        try {
            JpcapCaptor jpcapCaptor = JpcapCaptor.openDevice(dev, 65536, false, 20);
            captor = JpcapCaptor.openDevice(dev, 1024, false, 200);
            captor.setFilter("arp[7:1] == 2", true);

            Map<String, byte[]> ipAndMac = new HashMap<>();
            JpcapSender sender = captor.getJpcapSenderInstance();
            String netAddress = getGatewayAddress(dev);
            List<String> ips = getAllIP(netAddress);

            for (String ip : ips) {
                ARPPacket packet = createARP(ARPPacket.ARP_REQUEST,
                        dev.addresses[0].address.getHostAddress(),
                        macToStr(dev.mac_address),
                        ip, "00-00-00-00-00-00");
                sender.sendPacket(packet);

                ARPPacket p = (ARPPacket) captor.getPacket();
                if (p == null || !Arrays.equals(p.target_hardaddr, dev.mac_address)) {
                    System.out.println("- " + ip + " - is not alive on this LAN.");

                    String data1 = "- " + ip + " - is not alive on this LAN.";
                    hostList.add(0, data1);
                    textArea.setText(FileUtil.ListToStr(hostList));
                } else {
                    System.out.println("-----------------------------------------");
                    System.out.println("- " + ip + " + is alive \n- It's mac address is: " + macToStr(p.sender_hardaddr) + "\n-----------------------------------------");
                    System.out.println();
                    String data2 = "-----------------------------------------\n- " + ip + " - is alive \n- It's mac address is: " + macToStr(p.sender_hardaddr) + "\n-----------------------------------------";
                    hostList.add(0, data2);
                    textArea.setText(FileUtil.ListToStr(hostList));

                    Host host = new Host(ip, macToStr(p.sender_hardaddr));
                    saveHostInfo(host);
                    ipAndMac.put(ip, p.sender_hardaddr);
                }
            }
            System.out.println("\nScan Finished!");
            for (String ip : ipAndMac.keySet()) {
                System.out.println("-ip: " + ip + "-mac:" + macToStr(ipAndMac.get(ip)));
            }
        } catch (IOException e) {
            e.getCause();
        }

    }

    public static String getGatewayAddress(NetworkInterface device) {
        String hostIPAddress;
        if (device.addresses.length == 0) {
            hostIPAddress = "NULL";
        }else{
            hostIPAddress = device.addresses[0].address.getHostAddress();
        }
        String[] addressFlags = hostIPAddress.split("\\.");
        return addressFlags[0] + "." + addressFlags[1] +
                "." + addressFlags[2] + ".2";
    }

    public static List<String> getAllIP(String netAddress) {
        List<String> ips = new ArrayList<>();
        String ip = netAddress.substring(0, netAddress.length() - 1);
        for (int i = 1; i < 255; i++) {
            ips.add(ip + i);
        }
        return ips;
    }

    public static ARPPacket createARP(short operation, String srcIP, String srcMac, String desIp, String desMac)
            throws UnknownHostException {
        ARPPacket arpPacket = new ARPPacket();
        InetAddress srcip = InetAddress.getByName(srcIP);
        byte[] srcmac = strToMac(srcMac);
        InetAddress desip = InetAddress.getByName(desIp);
        byte[] desmac = strToMac(desMac);

        arpPacket.hardtype = ARPPacket.HARDTYPE_ETHER;   //硬件类型
        arpPacket.prototype = ARPPacket.PROTOTYPE_IP;    //协议类型
        arpPacket.hlen = 6;                              //物理地址长度
        arpPacket.plen = 4;                              //协议地址长度
        arpPacket.operation = operation;                 //包类型：请求或应答
        arpPacket.sender_hardaddr = srcmac;              //ARP包的发送端以太网地址
        arpPacket.sender_protoaddr = srcip.getAddress(); //发送端IP地址
        arpPacket.target_hardaddr = desmac;              //设置目的端的以太网地址
        arpPacket.target_protoaddr = desip.getAddress(); //目的端IP地址

        EthernetPacket ethernetPacket = new EthernetPacket();
        ethernetPacket.frametype = EthernetPacket.ETHERTYPE_ARP;    //帧类型
        ethernetPacket.src_mac = srcmac;                            //源MAC地址
        if (operation == ARPPacket.ARP_REQUEST) {
            ethernetPacket.dst_mac = strToMac("ff-ff-ff-ff-ff-ff"); //以太网目的地址，广播地址
        } else {
            ethernetPacket.dst_mac = desmac;
        }

        arpPacket.datalink = ethernetPacket;

        return arpPacket;
    }

    public static void saveHostInfo(Host host) throws IOException {
        String data = JsonUtil.toJson(host);
        data = data.replace("\n", "");
        FileUtil.writeData(data, "Host.data", true);
    }

    public List<Host> getHostInfoList() {

        List<Host> hostInfos = new ArrayList<>();
        List<Object> infos = FileUtil.getData("Host.data", Host.class);
        for (Object o : infos) {
            Host h = (Host) o;
            if (h != null) {
                hostInfos.add(h);
            }
        }
        return hostInfos;
    }

    public void setHostInfoTableViewData(TableView<HostTableData> tb_host, ObservableList<HostTableData> dataList,
                                         TableColumn<HostTableData, String> tc_hostIP,
                                         TableColumn<HostTableData, String> tc_hostMAC) {
        tc_hostIP.setCellValueFactory(cellData -> cellData.getValue().hostIPProperty());
        tc_hostMAC.setCellValueFactory(cellData -> cellData.getValue().hostMACProperty());
        tb_host.setItems(dataList);
    }

    public ObservableList<HostTableData> getHostInfoTableViewData() {
        ObservableList<HostTableData> hostList = FXCollections.observableArrayList();
        for (Host hi : getHostInfoList()) {
            HostTableData hiTD = new HostTableData(hi.getHostIP(), hi.getHostMAC());
            hostList.add(hiTD);
        }
        return hostList;
    }
}