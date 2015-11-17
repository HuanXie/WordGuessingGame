/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Clientgui1 extends javax.swing.JPanel 
{
  
    private javax.swing.JButton start;
    private javax.swing.JButton enter;
    private javax.swing.JLabel address;
    private javax.swing.JLabel port;
    private javax.swing.JTextField inputOfAddress;
    private javax.swing.JTextField inputOfPort;
    private javax.swing.JTextField input;
    private javax.swing.JLabel show;
    private clientThread connection;
    private final String msgOfStart = "start";
    
    public Clientgui1() {
        creatGUI();
       // add(showPanel());
    }

    private void creatGUI(){
        inputOfAddress = new javax.swing.JTextField();
        inputOfPort = new javax.swing.JTextField();
        address = new javax.swing.JLabel();
        port = new javax.swing.JLabel();
        start = new javax.swing.JButton();
        input = new javax.swing.JTextField();
        enter = new javax.swing.JButton();
        show = new javax.swing.JLabel();

        address.setText("IPaddress");

        port.setText("Port");

        start.setText("Start");

        enter.setText("enter");
        enter.setEnabled(false);
        
        inputOfAddress.setText("127.0.0.1");
        
        inputOfPort.setText("4444");

        show.setText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(address)
                    .addComponent(port))
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inputOfAddress)
                    .addComponent(inputOfPort))
                .addGap(18, 18, 18)
                .addComponent(start)
                .addGap(31, 31, 31))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(input, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(enter)
                .addGap(70, 70, 70))
            .addGroup(layout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addComponent(show, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(84, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(start, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(address)
                            .addComponent(inputOfAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(inputOfPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(port))))
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(input, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enter))
                .addGap(18, 18, 18)
                .addComponent(show, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        
        start.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                String host = inputOfAddress.getText();
                //System.out.println(host);
                int portnumber = Integer.parseInt(inputOfPort.getText());
                //System.out.println(portnumber);
                start.setEnabled(false);
                connection =
                       new clientThread(Clientgui1.this, host, portnumber,msgOfStart);
                new Thread(connection).start(); //multithreaded
            }
        });
        
        enter.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
            	String checkChar = input.getText().toLowerCase();
            	connection.getCheck(checkChar);
            }
        });
    }
    
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("client");
        frame.setContentPane(new Clientgui1());
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    public void showStart(final String result)
    {
    	SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                show.setText(result);
                enter.setEnabled(true);
            }
        });
    }
    
    public void showResult(final String result)
    {
    	SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                show.setText(result);
            }
        });
    }
}


