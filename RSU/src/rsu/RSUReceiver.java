/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package rsu;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Elcot
 */
public class RSUReceiver extends Thread{
    
    RSUFrame rsu;
    int rsuid,port;    
    
    RSUReceiver(RSUFrame r,int id)
    {
        rsu=r;
        rsuid=id;
        port=rsuid+6000;
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
                if(req[0].equals("AuthorityCertificateRequest"))       
                {
                    JOptionPane.showMessageDialog(rsu,"Authority Certificate Request Received Successfully!");
                    DefaultTableModel dm=(DefaultTableModel)rsu.jTable2.getModel();
                    Vector v=new Vector();
                    v.add(req[1].trim());
                    v.add(req[2].trim());
                    int upper=300,lower=100;
                    int dist = (int) (Math.random() * (upper - lower)) + lower;
                    v.add(dist);
                    String AuthorityCertificate=RandomStringGenerator.generateRandomString(5,RandomStringGenerator.Mode.ALPHA);
                    JOptionPane.showMessageDialog(rsu,"Authority Certificate Generated Successfully!");
                    v.add(AuthorityCertificate);
                    dm.addRow(v);
                    
                    JOptionPane.showMessageDialog(rsu,"Authority Certificate Response has been sent to Vehicle - "+req[1].trim()+" Successfully!");
                    String msg="AuthorityCertificateResponse#"+AuthorityCertificate;
                    int pt=Integer.parseInt(req[1].trim())+7000;
                    packetTransmission(msg,pt);
                }
                if(req[0].equals("CertificateSigningRequest"))       
                {
                    String vid=req[1].trim();
                    String authoritycertificate=req[2].trim();
                    
                    String msg="CertificateSigningRequest#"+vid+"#"+authoritycertificate+"#"+rsuid;
                    int pt=rsu.caid+5000;
                    JOptionPane.showMessageDialog(rsu,"Certificate Signing Request has been forward to CA - "+rsu.caid+" successfully!");
                    packetTransmission(msg,pt);
                }
                if(req[0].equals("CertificateSigningResponse"))       
                {
                    String vid=req[1].trim();
                    String LTC=req[2].trim();
                    String iSTC=req[3].trim();
                    
                    String msg="CertificateSigningResponse#"+vid+"#"+LTC+"#"+iSTC;
                    int pt=Integer.parseInt(vid)+7000;
                    JOptionPane.showMessageDialog(rsu,"Certificate Signing Response has been forward to Vehicle - "+vid+" successfully!");
                    packetTransmission(msg,pt);
                }
                if(req[0].equals("NEM"))       
                {
                    String vid=req[1].trim();
                    String misbehNodePseudo=req[2].trim();
                    
                    JOptionPane.showMessageDialog(rsu,"NEM message has been received and forward to all connected vehicles successfully!");
                    
                    String msg="NEM#"+vid+"#"+misbehNodePseudo+"#"+rsuid;
                    for(int i=0;i<rsu.jTable2.getRowCount();i++)
                    {
                        String vehicleId=rsu.jTable2.getValueAt(i,0).toString().trim();
                        if(!(vehicleId.trim().equals(vid.trim())))
                        {
                            int pt=Integer.parseInt(vehicleId.trim())+7000;                    
                            packetTransmission(msg,pt);
                        }
                    }
                }
                if(req[0].equals("ACK"))       
                {
                    JOptionPane.showMessageDialog(new JFrame(), req[1].trim());
                }
                if(req[0].equals("Connect"))       
                {                    
                    DefaultTableModel dm=(DefaultTableModel)rsu.jTable2.getModel();
                    Vector v=new Vector();
                    v.add(req[1].trim());
                    v.add(req[2].trim());
                    int upper=300,lower=100;
                    int dist = (int) (Math.random() * (upper - lower)) + lower;
                    v.add(dist);
                    dm.addRow(v);                        

                    String msg1="Forward#"+req[1]+"#"+req[3]+"#"+req[2]+"#"+dist+"#"+req[4];        
                    //req[1]-Vehicle id, req[2]-vehicle current location req[3]-rvsid req[4]-public user key
                    int pt1=5000;
                    packetTransmission(msg1,pt1);

                    String msg="ACK#"+"Joined Successfully!";
                    int pt=Integer.parseInt(req[1].trim())+7000;
                    packetTransmission(msg,pt);
                    
                    String location=rsu.jTextField1.getText().trim();
                    String msg2="RVSPublicKey#"+location;
                    int pt2=Integer.parseInt(req[1].trim())+7000;
                    packetTransmission(msg2,pt2);                    
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

    public static class RandomStringGenerator 
    {	
	public static enum Mode 
        {
	    ALPHA, ALPHANUMERIC, NUMERIC 
	}
	
	public static String generateRandomString(int length, Mode mode) throws Exception 
        {
            StringBuffer buffer = new StringBuffer();
            String characters = "";

            switch(mode){

            case ALPHA:
                    characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                    break;

            case ALPHANUMERIC:
                    characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
                    break;

            case NUMERIC:
                    characters = "1234567890";
                break;
            }

            int charactersLength = characters.length();

            for (int i = 0; i < length; i++) {
                    double index = Math.random() * charactersLength;
                    buffer.append(characters.charAt((int) index));
            }
            return buffer.toString();
	}	
    }
}
