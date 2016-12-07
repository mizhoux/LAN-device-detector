package org.mizhou.landd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Device {

    private static final int DEFAULT_LISTENING_PORT = 10000;

    private final InetAddress address;
    private final int port;

    private DatagramSocket socket;

    public Device(int port) throws IOException {
        this.port = port;
        this.address = InetAddress.getLocalHost();
    }

    public Device(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public void start() throws SocketException, InterruptedException {
        System.out.println("Device has been started...");
        InetAddress lanAddr = LANAddressTool.getLANAddressOnWindows();
        if (lanAddr != null) {
            System.out.println("Device: LAN Address: " + lanAddr.getHostAddress());
        }

        socket = new DatagramSocket(port);
        Receiver receiver = new Receiver(socket) {
            int recvCount = 0;

            @Override
            public boolean handlePacket(DatagramPacket packet) {
                String recvMsg = new String(packet.getData(), 0, packet.getLength());
                if ("ROOM".equals(recvMsg)) {
                    System.out.printf("Device: Received msg '%s'\n", recvMsg);
                    recvCount++;
                    if (recvCount == 3) {
                        byte[] data = "JOIN".getBytes();
                        DatagramPacket respMsg = new DatagramPacket(
                                data, data.length, packet.getSocketAddress()); // 此时 packet 包含了发送者地址和监听端口
                        try {
                            socket.send(respMsg);
                            System.out.println("Device: Sent response 'JOIN'");
                        } catch (IOException ex) {
                            ex.printStackTrace(System.err);
                        }
                        return false; // 停止接收
                    }
                }
                return true;
            }
        };

        Thread deviceThread = new Thread(receiver);
        deviceThread.start();
        deviceThread.join();

        close();

        System.out.println("Device has been closed.");
    }

    public void close() {
        if (socket != null) {
            socket.close();
        }
    }

    @Override
    public String toString() {
        return "Device {" + "address=" + address + ", port=" + port + '}';
    }

    public static void main(String[] args) throws Exception {
        Device device = new Device(DEFAULT_LISTENING_PORT);
        device.start();
    }
}
