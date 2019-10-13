package com.humanzero.sensor;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

/**
 * Ca
 *
 */

public class App {

	private static Stream<Packet> packetStream = Stream.empty();

//	private static void capturePackets(){
//		Will be implemented later
//	}

	public static void main(String[] args) {

		final Logger logger = LoggerFactory.getLogger(App.class);

		PcapNetworkInterface networkInterface = null;

		try {
			networkInterface = new NifSelector().selectNetworkInterface();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int snapshotLength = 65536;
		int timeout = 10;

		List<Packet> packetList = new ArrayList<>();

		try {
		        PcapHandle packageHandler = networkInterface.openLive(snapshotLength, PromiscuousMode.PROMISCUOUS, timeout);

		        while (true) {
		            Packet packet = packageHandler.getNextPacketEx();
		            logger.info(packageHandler.getTimestamp().toString() + " Header is: " +
                            packet.getHeader() + " Payload is: " +
                            packet.getPayload());
		            packetList.add(packet);

                    logger.info("Captured: " + packetList.size() + " packets." );
                }

		} catch (PcapNativeException | EOFException | TimeoutException | NotOpenException e) {
				e.printStackTrace();
		}

	}
}
