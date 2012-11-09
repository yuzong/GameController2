package controller.net;

import data.GameControlData;

import java.io.IOException;
import java.net.*;

/**
 * @author: Marcel Steinbeck
 *
 * This class is used to send the current {@link GameControlData} (game-state) to all robots every 500 ms.
 * The package will be send via UDP on port {@link GameControlData#GAMECONTROLLER_PORT} over broadcast.
 *
 * To prevent race-conditions (the sender is executed in its thread-context), the sender will hold a deep copy
 * of {@link GameControlData} (have a closer look to the copy-constructor
 * {@link GameControlData#GameControlData(data.GameControlData)}).
 *
 * This class is a singleton!
 */
public class Sender extends Thread {
    /** The instance of the singleton. */
    private static Sender instance;

    /** The socket, which is used to send the current game-state */
    private final DatagramSocket datagramSocket;

    /** The used inet-address (the broadcast address). */
    private final InetAddress group;

    /** The used port. */
    private final int port = GameControlData.GAMECONTROLLER_PORT;

    /** The current deep copy of the game-state. */
    private GameControlData data;

    /**
     * Creates a new Sender.
     *
     * @throws SocketException      if an error occurs while creating the socket
     * @throws UnknownHostException if the used inet-address is not valid
     */
    private Sender() throws SocketException, UnknownHostException {
        instance = this;

        this.datagramSocket = new DatagramSocket();
        this.group = InetAddress.getByName("255.255.255.255");
    }

    /**
     * Returns the instance of the singleton. If the Sender wasn't initialized once before, a new instance will
     * be created and returned (lazy instantiation)
     *
     * @return  The instance of the Sender
     * @throws IllegalStateException if the first creation of the singleton throws an exception
     */
    public synchronized static Sender getInstance() {
        if (instance == null) {
            try {
                instance = new Sender();
            } catch (Exception e) {
                throw new IllegalStateException("fatal: Error while setting up Sender.", e);
            }
        }

        return instance;
    }

    /**
     * Sets the current game-state to send. Creates a deep copy of data to prevent race-conditions.
     * Have a closer look to {@link GameControlData#GameControlData(data.GameControlData)}
     *
     * @param data the current game-state to send to all robots
     */
    public void send(GameControlData data) {
        this.data  = new GameControlData(data);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            if (data != null) {
                byte[] arr = data.toByteArray().array();
                DatagramPacket packet = new DatagramPacket(arr, arr.length, group, port);

                try {
                    datagramSocket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                interrupt();
            }
        }
    }
}
