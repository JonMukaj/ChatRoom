Chat Room
=========

This project is a simple chat room application developed using Java, JavaFX, Sockets, and Multithreading. The chat room allows multiple clients to connect to a server and communicate with each other.

Features
--------

-   Multiple clients can connect to the server.
-   Clients can send messages to all connected clients.
-   The server broadcasts messages to all connected clients.
-   The application has a simple graphical user interface developed using JavaFX.

Requirements
------------

-   Java 8 or higher
-   JavaFX
-   Intellij or another Java IDE
-   Maven

How to run
----------

1.  Clone the repository using the following command:

    `git clone https://github.com/JonMukaj/ChatRoom.git`

2.  Open the project in your Java IDE.

3.  Compile and run Server with maven plugin to start the server.

4.  Compile and run Client with maven plugin to start the client.

5.  Enter a username and click the "Connect" button to connect to the server.

6.  Type a message in the text field and press enter to send the message.

7.  In addition for ease of use, we have provided executables initially for Windows and Linux, which can be used even in systems where JDK is missing. You can find the  executables on the "platforms" directory.

How it works
------------

The server listens for incoming connections from clients. When a client connects, the server creates a new thread to handle communication with that client. The server listens for messages from all connected clients and broadcasts those messages to all other connected clients.

The client sends messages to the server by creating a new thread to handle communication with the server. The client listens for incoming messages from the server and displays them in the graphical user interface.

License
-------

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).

Authors
-------

-   [Jon Mukaj](https://github.com/jonmukaj)
-   [Drin Karkini](https://github.com/ComputerGeek5)
-   [Kevin Tenolli](https://github.com/ProfessorGustavi)
