package org.mizhou.landd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class Sender implements Runnable {

    private static final byte[] EMPTY_DATA = new byte[0];

    private final DatagramSocket socket;
    private final SocketAddress bcAddr;
    private final long sendingInterval; // 发送间隔，单位为 毫秒

    public Sender(DatagramSocket socket,
            SocketAddress broadcastAddress, int sendingInterval) {
        this.socket = socket;
        this.bcAddr = broadcastAddress;
        this.sendingInterval = sendingInterval;
    }

    @Override
    public void run() {
        while (true) {
            byte[] data = getNextData();
            if (data == null || data.length == 0) {
                break;
            }

            DatagramPacket outPacket = new DatagramPacket(data, data.length, bcAddr);
            
            try {
                socket.send(outPacket);
                System.out.println("Sender: Data has been sent");

                Thread.sleep(sendingInterval);
            } catch (IOException | InterruptedException ex) {
                System.err.println("Sender: Error occurred while sending packet");
                break;
            }

        }
        System.out.println("Sender: Thread is end");
    }

    /**
     * 获得下一次发送的数据<br>
     * 子类需要重写这个方法，返回下一次要发送的数据
     *
     * @return 下一次发送的数据
     */
    public byte[] getNextData() {
        return EMPTY_DATA;
    }
}
