import java.net.*;
import java.io.*;

public class ClientThread extends Thread {
   private Socket socket = null;
   private Client client = null;
   private DataInputStream in = null;

   public ClientThread(Client client, Socket socket) {
      this.client = client;
      this.socket = socket;
      open();
      start();
   }

   public void open() {
      try {
         in = new DataInputStream(socket.getInputStream());
      } catch (IOException e) {
         System.out.println("Error getting input stream: " + e);
         client.stop();
      }
   }

   public void close() {
      try {
         if (in != null)
            in.close();
      } catch (IOException e) {
         System.out.println("Error closing input stream: " + e);
      }
   }

   public void run() {
      while (true) {
         try {
            client.handle(in.readUTF());
         }
         catch (IOException e) {
            String msg = e.getMessage();
            if (msg != null && !msg.equals("")) {
               System.out.println("Listening error: " + msg);
               client.stop();
            } else {
               System.out.println("Closing client...");
               System.exit(0);
            }
         }
      }
   }
}