import java.net.*;
import java.nio.ByteBuffer;

public class Server {
    public static void main (String[] args) {
        ServerThread serverThread = new ServerThread();
        serverThread.start();
    }
}

class ServerThread extends Thread {
    private final static int CLIENT_REQUEST_PACKET_SIZE = 40;
    private final static int ANSWER_PACKET_SIZE = 9;

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(8099, InetAddress.getLocalHost());
            System.out.println("Server socket address: " + socket.getLocalSocketAddress().toString().substring(1) + "\n");

            while (true) {
                DatagramPacket receivedPacket = new DatagramPacket(new byte[CLIENT_REQUEST_PACKET_SIZE], CLIENT_REQUEST_PACKET_SIZE) ;
                socket.receive(receivedPacket);
                Double xCoordinate = ByteBuffer.wrap(receivedPacket.getData(), 0, 8).getDouble();
                Double yCoordinate = ByteBuffer.wrap(receivedPacket.getData(), 8, 8).getDouble();
                Double radius = ByteBuffer.wrap(receivedPacket.getData(), 16, 8).getDouble();
                int position = ByteBuffer.wrap(receivedPacket.getData() , 24, 8).getInt();
                int port = ByteBuffer.wrap(receivedPacket.getData(), 32, 8).getInt();
                Kontur.setRadius(radius);

                Boolean answer = Kontur.checkIsPointInsideTheArea(xCoordinate, yCoordinate);
                byte[] answerBytes = new byte[ANSWER_PACKET_SIZE];
                answerBytes[0] = (byte) (answer ? 1 : 0);
                ByteBuffer.wrap(answerBytes, 1, 8).putInt(position);
                DatagramPacket answerPacket = new DatagramPacket(answerBytes, answerBytes.length, receivedPacket.getAddress(), port);
                socket.send(answerPacket);

                System.out.println("=============================");
                System.out.println("From: " + receivedPacket.getSocketAddress().toString().substring(1));
                System.out.println("Packet received successfully.");
                System.out.println("Point #" + position);
                System.out.println("(" + xCoordinate + "; " + yCoordinate + "). R = " + radius);
                System.out.println(answer ? "Inside" : "Outside" + " the area.");
                System.out.println("Answer sent successfully.\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Kontur {
    private static double mRadius;

    static void setRadius(double pRadius) {
        mRadius = pRadius;
    }

    Kontur(float pRadius) {
        mRadius = pRadius;
    }

    static boolean checkIsPointInsideTheArea(double x, double y) {
        return  x > 0
                ?
                y >= -1 * Math.sqrt(mRadius * mRadius - x * x) && y <= 0
                :
                y > 0
                        ?
                        x >= (-1 * mRadius) && (y - x / 2) <= (mRadius / 2)
                        :
                        x >= (-1 * mRadius) && y >= (-1 * mRadius);
    }
}
