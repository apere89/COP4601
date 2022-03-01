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

  public int initServer() {
    try {
      System.out.println("Creating server socket...");
      this.socket = new DatagramSocket(port);
      System.out.println("Server socket created - IP:" + address + ":" + port);
      return 0;
    }
    catch (SocketException e) {
      System.err.println("Unable to create server socket");
      return -1;
    }
  }

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

  public int closeSocket() {
    this.socket.close();
    return  0;
  }

  private DatagramPacket createPacket(String msg)
  {
    byte buffer[] = new byte[BUFFER_SIZE];

    for (int i = 0; i < BUFFER_SIZE; i++) {
      buffer[i] = '\0';
    }
    // copy message into buffer
    byte data[] = msg.getBytes();
    System.arraycopy(data, 0, buffer, 0, Math.min(data.length, buffer.length));

    return new DatagramPacket (buffer, BUFFER_SIZE, this.clientAddress, this.clientPort);
  }

  public void  run() {
    Scanner in = new Scanner(System.in);
    boolean handshake = false;

    if (initServer() < 0) {
      System.err.println("Exiting server thread...");
      return;
    }

    while(!handshake) {
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

    while(!shutdown) {
      System.out.println("Waiting for client...");
      String clientMsg = receiveFromClient();
      board.updateBoard(Integer.parseInt(clientMsg), 1);
      System.out.println("Received from client: " + clientMsg);
      board.print();

      if (clientMsg.equals("-1")) {
        shutdown = true;
        break;
      }

      System.out.print("Enter next move: ");
      String serverMsg = in.next();
      board.updateBoard(Integer.parseInt(serverMsg), 0);
      System.out.println("Sending to client: " + serverMsg);
      sendToClient(serverMsg);
      board.print();
    }

    closeSocket();
  }
}
