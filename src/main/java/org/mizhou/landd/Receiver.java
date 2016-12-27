package org.mizhou.landd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Receiver implements Runnable {

    private static final int BUF_SIZE = 512;

    private final DatagramSocket socket;

    public Receiver(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        byte[] inData = new byte[BUF_SIZE];
        DatagramPacket inPacket = new DatagramPacket(inData, inData.length);

        while (true) {
            try {
                socket.receive(inPacket);
                if (!handlePacket(inPacket)) {
                    break;
                }
            } catch (IOException ex) {
                System.out.println("Receiver: Socket was closed.");
                break;
            }
        }
        System.out.println("Receiver: Thread is end");
    }

    /**
     * 处理接收到的数据报<br>
     * 子类需要重写这个方法，处理接收到的数据包，并返回是否继续接收
     *
     * @param packet 接收到的数据报
     * @return 是否需要继续接收
     */
    public boolean handlePacket(DatagramPacket packet) {
        return false;
    }
}
