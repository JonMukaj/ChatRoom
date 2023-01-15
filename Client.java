import java.net.*;
import java.io.*;

public class Client implements Runnable {
   private Socket socket = null;
   private Thread thread = null;
   private DataInputStream in = null;
   private DataOutputStream out = null;
   private ClientThread client = null;
   private static final String colors []
               = new String[]{"\u001B[30m", "\u001B[31m", "\u001B[32m", "\u001B[33m", "\u001B[34m", "\u001B[35m", "\u001B[36m", "\u001B[37m"};
   public static final String ANSI_WHITE = "\u001B[0m";
   private String clientName = null;

   public Client(String serverName, int serverPort, String clientName) {
      this.clientName = clientName;
      System.out.println("Establishing connection for " + colors[clientName.hashCode() % 7]+this.clientName + ANSI_WHITE + ". Please wait ...");
      try {
         socket = new Socket(serverName, serverPort);
         System.out.println("Connected to server! (ServerAddress: " + socket.getInetAddress() + " ServerPort: " + socket.getPort() + ")");
         System.out.println("\u001B[31mType \"_exit\" to disconnect from server" + ANSI_WHITE);
         start();
      } catch (UnknownHostException u) {
         System.out.println("Host unknown: " + u.getMessage());
      } catch (IOException e) {
         System.out.println("Unexpected exception: " + e.getMessage());
      }
   }

   public void run() {
      while (thread != null) {
         try {
            out.writeUTF(in.readLine());
            out.flush();
         } catch (IOException e) {
            System.out.println("Sending error: " + e.getMessage());
            stop();
         }
      }
   }

   public void handle(String msg) {
      if (msg.equals("_exit")) {
         System.out.println("Good bye. Press RETURN to exit ...");
         stop();
      } else {
         System.out.println(msg);
      }
         
   }

   public void start() throws IOException {
      in = new DataInputStream(System.in);
      out = new DataOutputStream(socket.getOutputStream());
      out.writeUTF(clientName);
      if (thread == null) {
         client = new ClientThread(this, socket);
         thread = new Thread(this);
         thread.start();
      }
   }

   public void stop() {
      if (thread != null) {
         thread.stop();
         thread = null;
      }
      try {
         if (in != null)
            in.close();
         if (out != null)
            out.close();
         if (socket != null)
            socket.close();
      } catch (IOException e) {
         System.out.println("Error closing ...");
      }
      client.close();
      client.stop();
   }

   public static void main(String args[]) {
      Client client = null;
      if (args.length != 3)
         System.out.println("Usage: java Client <host> <port> <yourName>");
      else
         client = new Client(args[0], Integer.parseInt(args[1]), args[2]);
   }
}