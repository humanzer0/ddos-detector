package com.humanzero.sensor;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;

/**
 * Custom Receiver that receives the data from all network interfaces
 */

public class NetworkReceiver extends Receiver<Packet> {

    private static PcapNetworkInterface getInterfaceAny(){

        PcapNetworkInterface networkInterfaceAny = null;

        try {
            networkInterfaceAny = Pcaps.getDevByName("any");
        } catch (PcapNativeException e) {
            e.printStackTrace();
        }

        return networkInterfaceAny;
    }

    NetworkReceiver(){
        super(StorageLevel.MEMORY_AND_DISK_2());
    }

    @Override
    public void onStart() {
        new Thread(this::receive).start();
    }

    @Override
    public void onStop() {

    }

    private void receive(){

        PcapHandle packetHandler;
        int snapshotLength = 65536;
        int timeout = 10;

        try {

            packetHandler = getInterfaceAny()
                    .openLive(snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, timeout);

            while(packetHandler.getNextPacketEx()!=null){

                    store(packetHandler.getNextPacketEx());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
