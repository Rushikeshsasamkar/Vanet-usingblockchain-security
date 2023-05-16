/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vehicle;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Elcot
 */
public class VehicleReceiver extends Thread{
    
    VehicleFrame vf;
    int vid,port;
    public static String ms;
    public static int Accepted=0,Rejected=0;
    
    VehicleReceiver(VehicleFrame f, int id)
    {
        vf=f;
        vid=id;
        port=vid+7000;
    }
    
    public void run()
    {
        try
        {
            DatagramSocket ds=new DatagramSocket(port);
            while(true)
            {                
                byte data[]=new byte[10000];
                DatagramPacket dp=new DatagramPacket(data,0,data.length);
                ds.receive(dp);
                String str=new String(dp.getData()).trim(); 
                System.out.println("Received: "+str);
                String req[]=str.split("#");
                if(req[0].equals("CertificateSigningResponse"))       
                {
                    String vid1=req[1].trim();
                    String LTC=req[2].trim();
                    String iSTC=req[3].trim();
                    
                    JOptionPane.showMessageDialog(vf,"Certificate Signing Response has been Received Successfully!");
                    
                    vf.jTextField8.setText(LTC.trim());
                    vf.jTextField15.setText(iSTC.trim());
                }
                if(req[0].equals("NEM"))       
                {
                    String vid1=req[1].trim();
                    String misbehNodePseudo=req[2].trim();                    
                    
                    String response="Rejected";
                    int reply = JOptionPane.showConfirmDialog(vf, "Do you accept Vehicle Pseudonym - "+misbehNodePseudo+" is misbehave!", "Accept or Reject", JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) 
                    {
                        response="Accepted";
                    }        
                    
                    String msg="NEMReply#"+response;
                    int pt=Integer.parseInt(vid1)+7000;
                    packetTransmission(msg,pt);
                    
                }
                if(req[0].equals("NEMReply"))       
                {
                    String response=req[1].trim();
                    if(response.trim().equals("Accepted"))
                    {
                        Accepted++;                        
                    }
                    else
                    {
                        Rejected++;                        
                    }
                    vf.jTextField12.setText(""+Accepted);
                    vf.jTextField13.setText(""+Rejected);
                    if(Accepted>=Rejected)
                    {
                        vf.jTextField16.setText("Accepted");
                    }
                    else
                    {
                        vf.jTextField16.setText("Rejected");
                    }
                    vf.jTextField12.setEditable(false);
                    vf.jTextField13.setEditable(false);
                    vf.jTextField16.setEditable(false);
                    vf.jButton14.setEnabled(true);
                }
                if(req[0].equals("ACK"))       
                {
                    JOptionPane.showMessageDialog(vf, req[1].trim());
                }                
                if(req[0].equals("AuthorityCertificateResponse"))       
                {
                    JOptionPane.showMessageDialog(vf,"Authority Certificate Response has been Received Successfully!");
                    vf.jTextField7.setText(req[1].trim());
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
