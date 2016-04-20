/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author huan
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.Socket;
import java.lang.String;

public class ConnectionHandler extends Thread
{
    private Socket clientSocket;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private String word;
    private int totalScore = 0;
    private String current;

    public ConnectionHandler(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    public void run()
    {
    	try
    	{
    		while(true)
    		{
    			word = null;
    			word = chooseWord();
    			if (word == null) {
    				System.out.println("Client terminated the game");
    				if (!clientSocket.isClosed()) {
    					System.out.println("Server closes the socket");
    					clientSocket.close();
    				}
    				return; //client says end
    			}
    			int attempt = 5;
    			current = getFormofWord(word);
    			while(attempt > 0)// in the five times attempt 
    			{
    				attempt--;
    				current = checkword(current);
    				if(current.equalsIgnoreCase(word)) //client input the right answer
    				{
    					String cong = "congratulations! The correct word is ";
    					byte[] fromserver1 = cong.getBytes();
    					out.write(fromserver1, 0, fromserver1.length);
    					byte[] fromserver2 = word.getBytes();
    					out.write(fromserver2, 0, fromserver2.length);
    					String score = ". The total score is ";
    					byte[] fromserver3 = score.getBytes();
    					out.write(fromserver3, 0, fromserver3.length);
    					totalScore++;
    					String total = Integer.toString(totalScore);
    					byte[] fromserver4 = total.getBytes();
    					out.write(fromserver4, 0, fromserver4.length);
    					out.flush();
    					break;
    				}else
    				{
    					if(attempt == 0) // no attempt left and current is not right answer
    					{
    						totalScore--;
    						if(totalScore < 0)
    						{
    							totalScore = 0;
    						}
    						gameover(totalScore);
    					}else
    					{
    						String continueGuess = "The current word is in form of ";
    						byte[] cont = continueGuess.getBytes();
    						out.write(cont, 0, cont.length);
    						
    						byte[] curr = current.getBytes();
    						out.write(curr, 0, curr.length);
    						
    						String noticeOfChance = "Chances to guess are: ";
    						byte[] noticeOfChancebyte = noticeOfChance.getBytes();
    						out.write(noticeOfChancebyte, 0, noticeOfChancebyte.length);
    						
    						String leftChance = Integer.toString(attempt);
    						byte[] leftchance = leftChance.getBytes();
    						out.write(leftchance, 0, leftchance.length);
    						out.flush();
    					}
    				}
    			}
    		}
    	}catch(IOException e)
		{
			System.out.println(e.toString());
			System.exit(1);
		}
    }
    
    public String chooseWord()
    {
    	final int minimum = 0;
        final int maximum = 25143;
        try
        {
            in = new BufferedInputStream(clientSocket.getInputStream());
            out = new BufferedOutputStream(clientSocket.getOutputStream());
            
            byte[] msg = new byte[5];
            in.read(msg, 0, 5);
            String msgFromClient = new String(msg);
            if(msgFromClient.equals("start"))
            {
    			FileInputStream fs= new FileInputStream("C://Users//huan//workspace//networkJavaLab1//src//words.txt");
    			BufferedReader br = new BufferedReader(new InputStreamReader(fs));
    			int index = (int) Math.round((Math.random() * maximum + minimum));
    			while (index > 1) {
    				index--;
    				br.readLine();
    			}
    			word = br.readLine();
            	word = word.toLowerCase();
    			try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			byte[] numberOfAttempt = intToByteArray(5);
    			out.write(numberOfAttempt, 0, numberOfAttempt.length);
            	byte[] toClient = intToByteArray(word.length());
                out.write(toClient, 0, toClient.length);
                out.flush();
                System.out.print(word);
            }
            else if (msgFromClient.contains("end")) {
            	
            }else 
            {
            	throw new IllegalArgumentException("Invalid input from client " + msgFromClient);
            }
        } catch (IOException e)
        {
            System.out.println(e.toString());
            System.exit(1);
        }
        return word;
    }

    public String checkword(String current)
    {
    		try
    		{
    			byte[] lengthfromclient = new byte[4];
    			in.read(lengthfromclient, 0, 4);
    			int length = byteArrayToInt(lengthfromclient);
    			
    			//System.out.println(length);
    			byte[] fromClient = new byte[length];
    			in.read(fromClient, 0, fromClient.length);
    			String newFromClient = new String(fromClient,"UTF-8");
    			//System.out.println(newFromClient);
    			if(newFromClient.length() == 1) //get one char
    			{
    				//uppdate current
    				char[] charArray = current.toCharArray();
    				for(int i = 0; i < word.length(); i++)
    				{
    					if(newFromClient.charAt(0) == word.charAt(i))
    					{
    						charArray[i] = newFromClient.charAt(0); 
    					}
    				}
    				current = new String(charArray);
    			}else //get one word 
    			{
    				if(newFromClient.equalsIgnoreCase(word)) //the input word is right
    				{
    					current = word;
    				}
    				//else the current is unchanged
    			}
    		}catch(IOException e)
    		{
    			System.out.println(e.toString());
    			System.exit(1);
    		}
			return current;
    	}
    
    public byte[] intToByteArray(int value) 
    {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }
    
    public static int byteArrayToInt(byte[] b) 
    {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }
    
    public String getFormofWord(String word)
    {
    	char[] chars = new char[word.length()];
    	for(int i = 0; i < word.length();i++)
    	{
    		chars[i] = '*';
    	}
    	return new String(chars);
    }
    
    public void gameover(int total)
    {
    	try
        {
    		String gameOver = "game over";
    		byte[] dead = gameOver.getBytes();
    		out.write(dead, 0, dead.length);
    		String score = ". The total score is ";
			byte[] fromserver3 = score.getBytes();
			out.write(fromserver3, 0, fromserver3.length);
			String totals = Integer.toString(total);
			byte[] fromserver4 = totals.getBytes();
			out.write(fromserver4, 0, fromserver4.length);
    		out.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void close()
    {
    	try
        {
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}