import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class GameServer extends Thread {

    private static final int BUFFER_SIZE = 256;

    private DatagramSocket socket;
    private byte[] buffer = new byte[BUFFER_SIZE];
    private boolean running;

    private String address;
    private int port;
    private Board board;
    private Player player;

    private InetAddress clientAddress = null;
    private int clientPort;

    public boolean shutdown = false;

    public GameServer(String address, int port, Board board, Player player) {
        this.address = address;
        this.port = port;
        this.board = board;
        this.player = player;
    }

    //Function to create socket
    public int initServer() {
        try {
            System.out.println("Creating server socket...");
            this.socket = new DatagramSocket(port);
            System.out.println("Server socket created - IP:" + address + ":" + port);
            return 0;
        } catch (SocketException e) {
            System.err.println("Unable to create server socket");
            return -1;
        }
    }

    //function to send message to client
    public int sendToClient(String msg) {
        DatagramPacket packet = createPacket(msg);

        if (packet != null) {
            try {
                this.socket.send(packet);
                return 0;
            } catch (IOException e) {
                System.err.println("Unable to send message to client");
                return -1;
            }
        }
        System.err.println("unable to create message");
        return -1;
    }

    //function to recieve message from client
    public String receiveFromClient() {

        DatagramPacket packet = new DatagramPacket(this.buffer, BUFFER_SIZE);

        try {
            this.socket.receive(packet);
            this.clientAddress = packet.getAddress();
            this.clientPort = packet.getPort();

            return new String(buffer).trim();
        } catch (IOException e) {
            System.err.println("Unable to receive message from server");
            return null;
        }
    }

    //function to close socket
    public int closeSocket() {
        this.socket.close();
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

        return new DatagramPacket(buffer, BUFFER_SIZE, this.clientAddress, this.clientPort);
    }

    public void run() {
        Scanner in = new Scanner(System.in);
        boolean handshake = false;

        if (initServer() < 0) {
            System.err.println("Exiting server thread...");
            return;
        }

        while (!handshake) {
            System.out.println("Waiting for client...");
            String clientMsg = receiveFromClient();

            if (clientMsg.equals("Hello!")) {
                System.out.println("Client connected...");
                sendToClient("Hello!");
                handshake = true;
            }
        }

        board.init();
        System.out.println("Initial Board:");
        board.print();

        //Game loop - the first move is done by the client
        while (!shutdown) {

            //receive the move from client
            System.out.println("Waiting for client...");
            String clientMsg = receiveFromClient();
            board.updateBoard(Integer.parseInt(clientMsg), 1);
            System.out.println("Received from client: " + clientMsg);
            board.print();
            if (clientMsg.equals("-1")) {
                shutdown = true;
                break;
            }

            //checking if game is over/its a draw after client move
            if (board.multiplayer_winner_check(2, 1)) {
                shutdown = true;
                break;
            }

            //Server side move
            System.out.print("Enter next move: ");
            if (!in.hasNextInt())
                System.exit(1);
            String serverMsg = in.next();
            int move = Integer.parseInt(serverMsg);
            while (move < 0 || move > board.getDim() - 1 || (board.getGridVal(0, move) != ' ')) {
                System.out.println("Please enter your move between 0-" + (board.getDim() - 1) + " or Q/q to quit");
                serverMsg = in.next();
                move = Integer.parseInt(serverMsg);
            }
            board.updateBoard(move, 2);
            System.out.println("Sending to client: " + serverMsg);

            //Sending server move to client
            sendToClient(serverMsg);
            board.print();

            //checking if game is over/its a draw after server move
            if (board.multiplayer_winner_check(2, 1)) {
                shutdown = true;
                break;
            }


        }

        closeSocket();
    }
}