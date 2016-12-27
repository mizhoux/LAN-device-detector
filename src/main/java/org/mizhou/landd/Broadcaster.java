package org.mizhou.landd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

public class Broadcaster {

    private static final int DEFAULT_BROADCAST_PORT = 10000;

    private final InetAddress bcAddr;
    private final int bcPort;

    private DatagramSocket socket;

    public Broadcaster(InetAddress broadcastAddress, int broadcastPort) {
        this.bcAddr = broadcastAddress;
        this.bcPort = broadcastPort;
    }

    /**
     * 启动 Broadcaster
     *
     * @throws SocketException
     * @throws InterruptedException
     */
    public void start() throws SocketException, InterruptedException {
        System.out.println("Broadcaster has been started...");

        final Room room = new Room("Test");
        System.out.printf("Broadcaster: Created room '%s'\n\n", room.getName());

        socket = new DatagramSocket();
        SocketAddress bcSocketAddr = new InetSocketAddress(bcAddr, bcPort);

        Sender sender = new Sender(socket, bcSocketAddr, 1000) {// 每隔 1000ms 广播一次
            final byte[] DATA = "ROOM".getBytes();

            @Override
            public byte[] getNextData() {
                return DATA;
            }
        };

        Receiver recver = new Receiver(socket) {

            @Override
            public boolean handlePacket(DatagramPacket packet) {
                String recvMsg = new String(packet.getData(), 0, packet.getLength());
                if ("JOIN".equals(recvMsg)) {
                    Device device = new Device(packet.getAddress(), packet.getPort());
                    room.addDevice(device);
                    room.listDevices();
                }
                return true; // 一直接收
            }
        };

        Thread senderThread = new Thread(sender);
        Thread recverThread = new Thread(recver);
        senderThread.start();
        recverThread.start();

        // 在命令行中使用 Ctrl+C 终止 JVM 进程时，会调用 ShutdownHook
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                close();
                System.out.println("Broadcaster has been closed.");
            }
        }));

    }

    public void close() {
        if (socket != null) {
            socket.close();
        }
    }

    public static void main(String[] args) throws Exception {
        InetAddress bcAddr = LANAddressTool.getLANBroadcastAddressOnWindows();

        if (bcAddr != null) {
            System.out.println("Broadcast Address: " + bcAddr.getHostAddress());
            Broadcaster broadcaster = new Broadcaster(bcAddr, DEFAULT_BROADCAST_PORT);
            broadcaster.start();
        } else {
            System.out.println("Please check your LAN or Operating System"
                    + "(LANAddressTool can only get LAN address on Windows so far).");
        }
    }
}
