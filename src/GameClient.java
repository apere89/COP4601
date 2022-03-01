import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class GameClient extends Thread {

    private static final int BUFFER_SIZE = 256;
    private static final int SOCKET_TIMEOUT_MS = 5000;
    private DatagramSocket socket;
    private boolean running;

    private String hostAddress;
    private int hostPort;
    private Board board;
    private Player player;

    public boolean shutdown = false;

    public GameClient(String hostAddress, int hostPort, Board board, Player player) {
        this.hostAddress = hostAddress;
        this.hostPort = hostPort;
        this.board = board;
        this.player = player;
    }

    //function to create socket
    public int initClient() {
        try {
            System.out.println("Creating client socket..");
            socket = new DatagramSocket();
            socket.setSoTimeout(SOCKET_TIMEOUT_MS);
            System.out.println("Client socket created");

            return 0;
        } catch (SocketException e) {
            System.err.println("Unable to create socket");
            return -1;
        }
    }

    //function to send message to server
    public int sendToServer(String msg) {
        DatagramPacket packet = createPacket(msg);

        if (packet != null) {
            try {
                this.socket.send(packet);
                return 0;
            } catch (IOException e) {
                System.err.println("Unable to send message to server");
                return -1;
            }
        }
        System.err.println("unable to create message");
        return -1;
    }

    //function to recieve message from server
    public String receiveFromServer() {
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
        try {
            socket.receive(packet);
            return new String(buffer).trim();
        } catch (IOException e) {
            System.err.println("Unable to receive message from server");
            return null;
        }
    }

    //function to close socket
    public int closeSocket() {
        socket.close();
        return 0;
    }

    //function to create datagram packet
    private DatagramPacket createPacket(String msg) {
        byte buffer[] = new byte[BUFFER_SIZE];

        for (int i = 0; i < BUFFER_SIZE; i++) {
            buffer[i] = '\0';
        }
        // copy message into buffer
        byte data[] = msg.getBytes();
        System.arraycopy(data, 0, buffer, 0, Math.min(data.length, buffer.length));
        InetAddress hostAddr;
        try {
            hostAddr = InetAddress.getByName(this.hostAddress);
            return new DatagramPacket(buffer, BUFFER_SIZE, hostAddr, this.hostPort);
        } catch (UnknownHostException e) {
            System.err.println("invalid host address");
            return null;
        }
    }

    public void run() {
        Scanner in = new Scanner(System.in);
        boolean handshake = false;

        if (initClient() < 0) {
            System.err.println("Exiting client thread...");
            return;
        }

        System.out.println("Attempting to contact server...");
        while (!handshake) {
            String pingServer = "Hello!";
            sendToServer(pingServer);

            try {
                String serverResponse = receiveFromServer();
                if (serverResponse == null)
                    System.out.println("No response from server. Retrying...");
                else {
                    System.out.println("Connected to server...");
                    handshake = true;
                    socket.setSoTimeout(0);
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        board.init();
        System.out.println("Initial Board:");
        board.print();

        //Game loop - the first move is done by the client
        while (!shutdown) {

            //client makes the first move
            System.out.print("Enter next move: ");
            if (!in.hasNextInt())
                System.exit(1);
            String clientMsg = in.next();
            int move = Integer.parseInt(clientMsg);
            while (move < 0 || move > board.getDim() - 1 || (board.getGridVal(0, move) != ' ')) {
                System.out.println("Please enter your move between 0-" + (board.getDim() - 1) + " or Q/q to quit");
                clientMsg = in.next();
                move = Integer.parseInt(clientMsg);
            }

            board.updateBoard(move, 1);

            //sending the client move to the server
            System.out.println("Sending to server: " + clientMsg);
            sendToServer(clientMsg);
            board.print();

            //checking if game is over/its a draw after client move
            if (board.multiplayer_winner_check(1, 2)) {
                shutdown = true;
                break;
            }

            //receiving move from server
            System.out.println("Waiting for server...");
            String serverMsg = receiveFromServer();
            board.updateBoard(Integer.parseInt(serverMsg), 2);
            System.out.println("Received from server: " + serverMsg);
            board.print();

            //checking if game is over/its a draw after server move
            if (board.multiplayer_winner_check(1, 2)) {
                shutdown = true;
                break;
            }

            if (serverMsg.equals("-1")) {
                shutdown = true;
                break;
            }
        }

        closeSocket();
    }
}