import java.net.*;
import java.io.*;

public class ServerThread extends Thread {
   private Server server = null;
   private Socket socket = null;
   private int id = -1;
   private DataInputStream in = null;
   private DataOutputStream out = null;

   public ServerThread(Server _server, Socket _socket) {
      super();
      server = _server;
      socket = _socket;
      id = socket.getPort();
   }

   public void send(String msg) {
      try {
         out.writeUTF(msg);
         out.flush();
      } catch (IOException e) {
         System.out.println(id + " ERROR sending: " + e.getMessage());
         server.remove(id);
         stop();
      }
   }

   public int getID() {
      return id;
   }

   public void run() {
      System.out.println("Server Thread " + id + " running.");
      while (true) {
         try {
            server.handle(id, in.readUTF());
         } catch (IOException e) {
            System.out.println(id + " ERROR reading: " + e.getMessage());
            server.remove(id);
            stop();
         }
      }
   }

   public void open() throws IOException {
      in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
      out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
   }

   public void close() throws IOException {
      if (socket != null)
         socket.close();
      if (in != null)
         in.close();
      if (out != null)
         out.close();
   }
}