/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package police;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Elcot
 */
public class AuthorityReceiver extends Thread{
    
    AuthorityFrame rvs;
    int port;    
    
    AuthorityReceiver(AuthorityFrame r)
    {
        rvs=r;        
        port=2000;
    }
    
    public void run()
    {
        try
        {
            DatagramSocket ds=new DatagramSocket(2000);
            while(true)
            {                
                byte data[]=new byte[10000];
                DatagramPacket dp=new DatagramPacket(data,0,data.length);
                ds.receive(dp);
                String str=new String(dp.getData()).trim(); 
                System.out.println("Received: "+str);
                String req[]=str.split("#");
                if(req[0].equals("RIR"))       
                {
                    DefaultTableModel dm=(DefaultTableModel)rvs.jTable3.getModel();
                    Vector v=new Vector();
                    v.add(req[1].trim());
                    v.add(req[2].trim());
                    dm.addRow(v);
                }                
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

   
}
