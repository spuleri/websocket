import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class client {

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Usage: java client <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
            Socket socket = new Socket(hostName, portNumber);
            PrintWriter outToServer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader inFromServer = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        ) {
            // Receive input from user using the client program
            BufferedReader stdInFromUser =
                new BufferedReader(new InputStreamReader(System.in));
            String serverMsg;
            String userMsg;

            while ((serverMsg = inFromServer.readLine()) != null) {
              // Interpret the server's response
              if (serverMsg.equals("-1")) {
                  System.out.printf("receive: %s", "incorrect operation command\n");
              }
              else if (serverMsg.equals("-2")) {
                  System.out.printf("receive: %s", "number of inputs is less than two\n");
              }
              else if (serverMsg.equals("-3")) {
                  System.out.printf("receive: %s", "number of inputs is more than four\n");
              }
              else if (serverMsg.equals("-4")) {
                  System.out.printf("receive: %s", "one or more of the inputs contain(s) non-number(s)\n");
              }
              else if (serverMsg.equals("-5")) {
                  System.out.printf("receive: %s", "exit\n");
                  System.exit(1);
              } else {
                // Print out the message from the server
                System.out.printf("receive: %s\n", serverMsg);
              }

              // Allow the user to input a message
              userMsg = stdInFromUser.readLine();
              if (userMsg != null) {
                  // System.out.println("Client: " + userMsg);
                  outToServer.println(userMsg);
              }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
    }

}
