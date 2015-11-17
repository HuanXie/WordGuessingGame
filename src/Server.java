

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author huan
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{

    public static void main(String[] args) throws IOException
    {
        boolean listening = true;
        ServerSocket serverSocket = null;

        try
        {
            serverSocket = new ServerSocket(4444);
        } catch (IOException e)
        {
            System.err.println("Could not listen on port: 4444");
            System.exit(1);
        }

        while (listening)
        {
            Socket clientSocket = serverSocket.accept();
            ConnectionHandler newThread = new ConnectionHandler(clientSocket);
            newThread.start();
        }

        serverSocket.close();
    }
}