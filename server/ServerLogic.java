import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
/**
 * Created by sergiopuleri on 2/13/17.
 */
public class ServerLogic {

    private int port;
    private ServerSocket server;

    public ServerLogic(int port) {
        this.port = port;
    }

    public void listenOnPort() {

        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.out.printf("Could not listen on port %d\n", port);
            System.exit(-1);
        }

        System.out.printf("Listening on port %d\n", port);
    }

    public void acceptConnection() {
            try {
                //server.accept returns a client connection
                Socket clientSocket = server.accept();

                PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

                System.out.printf("get connection from ... (%s)\n", clientSocket.getRemoteSocketAddress().toString());

                // Now lets have a conversation
                talkToClient(clientSocket, out, in);
            } catch (IOException e) {
                System.out.printf("Accept failed on port %d\n", port);
                System.exit(-1);
            }
    }

    private void talkToClient(Socket client, PrintWriter outputBuffer, BufferedReader inputBuffer) {
        String inputLine, outputLine;

        // Initiate conversation with client and send the first message
        outputLine = "Hello!";
        outputBuffer.println(outputLine);

        // While he has more to say, process what he says
        try {
            Response response;
            while ((inputLine = inputBuffer.readLine()) != null) {
                // process the input line,
                response = processClientMessage(inputLine);
                outputLine = response.getMessage();
                System.out.printf("return: %s\n", outputLine);
                outputBuffer.println(outputLine);

                if (response.isTerminate()) {
                  System.exit(-1);
                }
                if (response.isBye()) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.printf("Communication failed on port %d\n%s\n", port, e.toString());
            System.exit(-1);
        }

        // Attempt to accept another connection
        // Will only get here if bye command is received
        acceptConnection();
    }

    private Response processClientMessage(String msg) {
        // Print client's message w/ no new line
        System.out.printf("get: %s, ", msg);

        List<String> input = Arrays.asList(msg.split("\\s+"));

        if (input.size() == 1) {
          // Check for bye / terminate command
          return checkForFinish(input.get(0));
        }

        Response response = new Response();

        String operator = input.get(0);

        List<String> operands = input.subList(1, input.size());

        int numOperands = operands.size();

        int operand;

        if (operator.equals("add") || operator.equals("subtract") || operator.equals("multiply") ) {
          // Start resut at first operand
          try {
            operand = Integer.parseInt(operands.get(0));

            // Set current value to the first operand
            response.setCurrentValue(operand);

          } catch (NumberFormatException e) {
            // System.out.printf("Parsing failed, invalid input: %s\n%s", operands.get(0) , e.toString());
            response.setMessage("-4");
            return response;
          }
        } else {
          // invalid operator supplied
          response.setMessage("-1");

          // Return highest priority error of -1
          return response;
        }

        // Process operands
        if ( numOperands < 2 ) {
          // Number of operands (inputs) less than 2, return -2
          response.setMessage("-2");
          response.setCurrentValue(-1);
        } else if (numOperands > 4) {
          // Number of operands (inputs) is greater than 4
          response.setMessage("-3");
          response.setCurrentValue(-1);
        } else {
          // Parse and compute result
          int i = 1;
          try {
            // Attempt to parse the operands
            for (; i < numOperands; i++) {
               // Attempt to parse next operand
               operand = Integer.parseInt(operands.get(i));

               // Perform operation
               // Modify the response objects values
               int val = performOperation(operator, response.getCurrentValue(), operand);
               response.setCurrentValue(val);
            }
          } catch (NumberFormatException e) {
            // System.out.printf("Parsing failed, invalid input: %s\n%s", operands.get(i) , e.toString());
            response.setCurrentValue(-1);
            response.setMessage("-4");
            return response;
          }
        }

        // Return the resposne object, whether its an error or result
        return response;
    }

    private int performOperation(String operator, int current, int operand) {
      if (operator.equals("add")) {
        return current + operand;
      } else if ( operator.equals("subtract") ) {
        return current - operand;
      } else if ( operator.equals("multiply") ) {
        return current * operand;
      } else {
        // shouldnt ever get here
        return current;
      }
    }

    private Response checkForFinish(String command) {
      if ( command.equals("bye") ) {
        // Close connection but continue listening for other connections
        return Response.byeResponse();
      } else if ( command.equals("terminate") ) {
        // Close connection and terminate
        return Response.terminateResponse();
      } else {
        // incorrect operation command
        return Response.invalidCommandResponse();
      }
    }
}
