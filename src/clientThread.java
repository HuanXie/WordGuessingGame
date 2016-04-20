/*
 * To change this license he
ader, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author huan
 */
public class clientThread extends Thread{
        private final String host;
        private final int port;
        private final Clientgui1 gui;
        private Socket clientSocket; 
        private BufferedInputStream in = null;
    	private BufferedOutputStream out = null;
    	private final LinkedBlockingQueue<String> check =
                new LinkedBlockingQueue<>();
    	private final LinkedBlockingQueue<String> startQueue =
                new LinkedBlockingQueue<>();
    	private int counter;
        
        public clientThread(Clientgui1 gui, String host, int port)
        {
        	this.host = host;
        	this.port = port;
        	this.gui = gui;
        }
        
        public void run()
        {
        	connect();
        	while (true) {
        		startgame();
        		while(!check());
        	}
        }
        
        public void connect()
        {
        	if(!host.equals("127.0.0.1")|| port != 4444)
        	{
        		String result = "the input ip address or port is wrong.";
        		gui.showResult(result);
        	}
        	try
        	{
        		clientSocket = new Socket(host, port);
        		in = new BufferedInputStream(clientSocket.getInputStream());
        		out = new BufferedOutputStream(clientSocket.getOutputStream());
        		startQueue.add("start");
        	}
        	catch (UnknownHostException e)
            {
                System.err.println("Don't know about host: " + host + ".");
                System.exit(1);
            }catch (IOException e)
            {
                System.err.println("Couldn't get I/O for the connection to: "
                        + host + ".");
                System.exit(1);
            }
        }
        
        public void startgame()
        {
        	String result = null;
        	int length;
        	StringBuilder newResult = new StringBuilder();
        	try
        	{
        		byte[] toServer = startQueue.take().getBytes();
        		out.write(toServer, 0, toServer.length);
                out.flush();
                
                byte[] attempt = new byte[4];
                in.read(attempt, 0, attempt.length);
                counter = byteArrayToInt(attempt);
                byte[] fromServer = new byte[4];
                int n = in.read(fromServer, 0, fromServer.length);
                if (n != fromServer.length)
                {
                    result = "something wrong with byte of int";
                } else
                {
                	length = byteArrayToInt(fromServer);
                    newResult.append("Game Start, the word is ");
                    for(int j = 0; j < length; j++)
                    {
                    	newResult.append("*");
                    }
                    newResult.append(" chances to guess a word is ");
                    newResult.append(counter);
                    //System.out.println(length);
                    result = newResult.toString();
                }
        	}catch(IOException e)
        	{
        		result = "unable to send start message";
        	} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	gui.showStart(result);
        }
        
        public void getCheck(String checkChar)
        {
        	check.add(checkChar);
        }
        
        public void continueGame(String cont)
        {
        	startQueue.add(cont);
        }
        
        public boolean check()
        {
        	boolean finished = false;
        	String result = null;
        	try
        	{ 
        		String checkChar = check.take();
        		byte[] LengthOfCheckChar = intToByteArray(checkChar.length());
        		System.out.println(checkChar.length());
        		out.write(LengthOfCheckChar, 0, LengthOfCheckChar.length);
        		byte[] toServer = checkChar.getBytes();
        		out.write(toServer, 0, toServer.length);
        		out.flush();
        		byte[] fromServer = new byte[256];
                in.read(fromServer, 0, fromServer.length);
                result = new String(fromServer);
                if(result.contains("congratulations") || result.contains("game over"))
                {
                	finished = true; 
                }
        	}catch(IOException e)
        	{
        		System.out.println(e);
        	} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	gui.showResult(result);
        	return finished;
        }
        
        public static int byteArrayToInt(byte[] b) 
        {
            return   b[3] & 0xFF |
                    (b[2] & 0xFF) << 8 |
                    (b[1] & 0xFF) << 16 |
                    (b[0] & 0xFF) << 24;
        }
        
        public byte[] intToByteArray(int value) 
        {
            return new byte[] {
                    (byte)(value >>> 24),
                    (byte)(value >>> 16),
                    (byte)(value >>> 8),
                    (byte)value};
        }
        
        public void close()
        {
        	if (!clientSocket.isClosed()) {
        		try {
        			byte[] toServer = "end".getBytes();
        			out.write(toServer, 0, toServer.length);
        			out.flush();
        			out.close();
        			in.close();
        			clientSocket.close();
        			System.out.println("Client closes the Socket");
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        	}
        }
    }


