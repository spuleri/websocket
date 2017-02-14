public class server {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println(
                "Usage: java server <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        ServerLogic server = new ServerLogic(portNumber);

        // Start connection on the desired port
        server.listenOnPort();

        // Attempt to start and initiate a connection with a client
        server.acceptConnection();
    }
}
