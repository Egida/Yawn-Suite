package pers.harry_deng.yawnsuite.beans.items;

public class NIC {
    private final String nicNum;
    private final String nicName;
    private final String nicIP;
    private final String nicMAC;

    public NIC(String nicNum,String nicName, String nicIP, String nicMAC) {
        this.nicNum = nicNum;
        this.nicName = nicName;
        this.nicIP = nicIP;
        this.nicMAC = nicMAC;
    }

    public String getNicNum() {
        return nicNum;
    }

    public String getNicName() {
        return nicName;
    }

    public String getNicIP() {
        return nicIP;
    }

    public String getNicMAC() {
        return nicMAC;
    }

}
