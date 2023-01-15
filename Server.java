import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class Server implements Runnable {
   private ServerThread clients[] = new ServerThread[50];
   private ServerSocket server = null;
   private Thread thread = null;
   private int clientCount = 0;
   private static Map<Integer,String> clientNames = new HashMap<>();

   private static final String colors []
   = new String[]{"\u001B[30m", "\u001B[31m", "\u001B[32m", "\u001B[33m", "\u001B[34m", "\u001B[35m", "\u001B[36m", "\u001B[37m"};
   public static final String ANSI_WHITE = "\u001B[0m";

   public Server(int port) {
      try {
         System.out.println("Binding to port " + port + ", please wait  ...");
         server = new ServerSocket(port);
         System.out.println("Server started: " + server);
         start();
      } catch (IOException e) {
         System.out.println("Can not bind to port " + port + ": " + e.getMessage());
      }
   }

   public void run() {
      while (thread != null) {
         try {
            System.out.println("Waiting for a client ...");
            addThread(server.accept());
         } catch (IOException e) {
            System.out.println("Server accept error: " + e);
            stop();
         }
      }
   }

   private int findClient(int id) {
      for (int i = 0; i < clientCount; i++)
         if (clients[i].getID() == id)
            return i;
      return -1;
   }

   public synchronized void handle(int id, String input) {
      if (input.equals("_exit")) {
         clients[findClient(id)].send("_exit");
         remove(id);
      }
      else if (clientNames.size() < clientCount) {
         clientNames.put(id, input);
      } 
      else {
         for (int i = 0; i < clientCount; i++) {
            int nr = clientNames.get(id).hashCode() % 7;
            clients[i].send(colors[nr] + clientNames.get(id) + ANSI_WHITE + ": " + input);
         }
      }
   }

   public synchronized void remove(int id) {
      int pos = findClient(id);
      if (pos >= 0) {
         ServerThread removeClient = clients[pos];
         System.out.println("Removing client thread " + id + " at " + pos);
         if (pos < clientCount - 1)
            for (int i = pos + 1; i < clientCount; i++)
               clients[i - 1] = clients[i];
         clientCount--;
         clientNames.remove(id);
         try {
            removeClient.close();
         } catch (IOException e) {
            System.out.println("Error closing thread: " + e);
         }
         removeClient.stop();
      }
   }

   private void addThread(Socket socket) {
      if (clientCount < clients.length) {
         System.out.println("Client accepted! (ClientAddress: " + socket.getInetAddress() + " ClientPort: " + socket.getPort() + ")");
         clients[clientCount] = new ServerThread(this, socket);
         try {
            clients[clientCount].open();
            clients[clientCount].start();
            clientCount++;
         } catch (IOException e) {
            System.out.println("Error opening thread: " + e);
         }
      } else
         System.out.println("Client refused: maximum " + clients.length + " reached.");
   }

   public void start() {
      if (thread == null) {
         thread = new Thread(this);
         thread.start();
      }
   }

   public void stop() {
      if (thread != null) {
         thread.stop();
         thread = null;
      }
   }

   public static void main(String args[]) {
      Server server = null;
      if (args.length != 1)
         System.out.println("Usage: java Server port");
      else
         server = new Server(Integer.parseInt(args[0]));

      System.out.println("\u001B[31mType \"exit\" to close server" + ANSI_WHITE);
      Console c = System.console();
      if (c.readLine().equals("exit")) {
         System.out.println("Closing server...");
         System.exit(0);
      }
   }
}
