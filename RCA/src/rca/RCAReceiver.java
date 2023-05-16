/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rca;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Elcot
 */
public class RCAReceiver extends Thread{
    
    RCAFrame rvs;
    int port;    
    
    RCAReceiver(RCAFrame r)
    {
        rvs=r;        
        port=4000;
    }
    
    public void run()
    {
        try
        {
            DatagramSocket ds=new DatagramSocket(4000);
            while(true)
            {                
                byte data[]=new byte[10000];
                DatagramPacket dp=new DatagramPacket(data,0,data.length);
                ds.receive(dp);
                String str=new String(dp.getData()).trim(); 
                System.out.println("Received: "+str);
                String req[]=str.split("#");
                if(req[0].equals("Connect"))       
                {
                    DefaultTableModel dm=(DefaultTableModel)rvs.jTable1.getModel();
                    Vector v=new Vector();
                    v.add(req[1].trim());
                    dm.addRow(v);
                }                
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void packetTransmission(String msg, int pt) {
        try
        {
            byte data1[]=msg.getBytes();
            DatagramSocket ds1=new DatagramSocket();
            DatagramPacket dp1=new DatagramPacket(data1,0,data1.length,InetAddress.getByName("127.0.0.1"),pt);
            ds1.send(dp1);
            System.out.println("Port is "+pt+"\n");                        
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }    
    }
}
