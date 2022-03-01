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

  public boolean shutdown =  false;

  public GameClient(String hostAddress, int hostPort, Board board, Player player) {
    this.hostAddress = hostAddress;
    this.hostPort = hostPort;
    this.board = board;
    this.player = player;
  }

  public int initClient() {
    try {
      System.out.println("Creating client socket..");
      socket = new DatagramSocket();
      socket.setSoTimeout(SOCKET_TIMEOUT_MS);
      System.out.println("Client socket created");

      return 0;
    }
    catch (SocketException e) {
      System.err.println("Unable to create socket");
      return -1;
    }
  }

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

  public int closeSocket() {
    socket.close();
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

    InetAddress hostAddr;
    try {
      hostAddr = InetAddress.getByName(this.hostAddress);
      return new DatagramPacket (buffer, BUFFER_SIZE, hostAddr, this.hostPort);
    } catch (UnknownHostException e) {
      System.err.println ("invalid host address");
      return null;
    }
  }

  public void  run() {
    Scanner in = new Scanner(System.in);
    boolean handshake = false;

    if (initClient() < 0) {
      System.err.println("Exiting client thread...");
      return;
    }

    System.out.println("Attempting to contact server...");
    while(!handshake) {
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

    while(!shutdown) {
      System.out.print("Enter next move: ");
      String clientMsg = in.next();
      board.updateBoard(Integer.parseInt(clientMsg), 1);
      System.out.println("Sending to server: " + clientMsg);
      sendToServer(clientMsg);
      board.print();

      System.out.println("Waiting for server...");
      String serverMsg = receiveFromServer();
      board.updateBoard(Integer.parseInt(serverMsg), 0);
      System.out.println("Received from server: " + serverMsg);
      board.print();

      if (serverMsg.equals("-1")) {
        shutdown = true;
        break;
      }
    }

    closeSocket();
  }
}
