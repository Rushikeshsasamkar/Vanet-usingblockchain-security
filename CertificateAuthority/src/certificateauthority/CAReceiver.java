/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package certificateauthority;

import certificateauthority.CAFrame.Signature;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Elcot
 */
public class CAReceiver extends Thread{
    
    CAFrame bvs;
    ArrayList bvsnorepeat=new ArrayList();
    public static ArrayList rvsprivloc=new ArrayList();
    int count=0,rowid=0;
    public static ArrayList vidOnly=new ArrayList();
    public static ArrayList PrivAttribKeyOnly=new ArrayList();
    int caid,port;
    DBConnection dbn=new DBConnection();
    Statement st=dbn.stt;
    
    CAReceiver(CAFrame f, int id)
    {
        bvs=f;
        caid=id;
        port=caid+5000;
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
                if(req[0].equals("Connect"))       
                {
                    if(!(bvsnorepeat.contains(req[2].trim())))
                    {                        
                        DefaultTableModel dm=(DefaultTableModel)bvs.jTable1.getModel();
                        Vector v=new Vector();
                        v.add(req[1].trim());
                        v.add(req[2].trim());
                        int upper=300,lower=100;
                        int r = (int) (Math.random() * (upper - lower)) + lower;
                        v.add(r);
                        dm.addRow(v);  

                        bvsnorepeat.add(req[2].trim());

                        rvsprivloc.add(req[1].trim()+"#"+req[2].trim());

                        String msg="ACK#"+"Connected Successfully!";
                        int pt=Integer.parseInt(req[1].trim())+6000;
                        packetTransmission(msg,pt);                            
                    }
                    else
                    {
                        String msg="ACK#"+"In this location, one RVS was already fixed!"; 
                        int pt=Integer.parseInt(req[1].trim())+6000;
                        packetTransmission(msg,pt);
                    }
                }
                if(req[0].equals("CertificateSigningRequest"))       
                {
                    String vid=req[1].trim();
                    String authoritycertificate=req[2].trim();
                    String rsuid=req[3].trim();
                    String privateKey=vid;
                    
                    String LTC=authoritycertificate;        // old pseudonym
                    
                    Signature sg=new Signature();
                    String iSTC=sg.calculateRFC2104HMAC(LTC,privateKey);    // signature
                    
                    String msg="CertificateSigningResponse#"+vid+"#"+LTC+"#"+iSTC;
                    int pt=Integer.parseInt(rsuid.trim())+6000;
                    JOptionPane.showMessageDialog(bvs,"Certificate Signed and uploaded to Pseudonym Blockchain and also sent to RSU - "+rsuid+" successfully!");
                    packetTransmission(msg,pt);
                    
                    try
                    {
                        int blockid=0;
                        String previousBlockHash="-";
                        ResultSet rs=st.executeQuery("select * from pseudonymblockchain");
                        while(rs.next())
                        {
                            previousBlockHash=rs.getString(4);
                            blockid++;
                        }
                        blockid++;
                        
                        String transaction="Vehicle Id: "+vid+"\nOld Pseudonym: "+LTC+"\n"+"New Pseudonym: -"+"\n"+"Signature: "+iSTC;
                        
                        String block=blockid+","+transaction+","+previousBlockHash;
                        String currentBlockHash=sg.calculateRFC2104HMAC(block,privateKey);
                        
                        st.executeUpdate("insert into pseudonymblockchain values('"+blockid+"','"+transaction+"','"+previousBlockHash+"','"+currentBlockHash+"')");                        
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                if(req[0].equals("Forward"))       
                {
                    DefaultTableModel dm=(DefaultTableModel)bvs.jTable2.getModel();
                    Vector v=new Vector();
                    v.add(req[1].trim());
                    v.add(req[2].trim());                            
                    v.add(req[3].trim());
                    v.add(req[4].trim());
                    v.add(req[5].trim());
                    dm.addRow(v); 
                }
                if(req[0].equals("RIR"))       
                {
                    ArrayList maliciouVehiclesPseudonyms=new ArrayList();
                    try
                    {
                        ResultSet rs=st.executeQuery("select * from pseudonymrevocationblockchain");
                        while(rs.next())
                        {
                            String blockId=rs.getString(1);
                            String transaction=rs.getString(2);
                            String sp[]=transaction.trim().split("\n");
                            String ps=sp[0].trim().replaceAll("Misbehave Vehicle Pseudonym: ", "");
                            maliciouVehiclesPseudonyms.add(ps.trim());
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(bvs,"Real Identity Response has been sent to Authority (Police) successfully!");
                    for(int i=0;i<maliciouVehiclesPseudonyms.size();i++)
                    {
                        String ps=maliciouVehiclesPseudonyms.get(i).toString().trim();
                        
                        String realId="";
                        try
                        {
                            ResultSet rs=st.executeQuery("select * from pseudonymblockchain");
                            while(rs.next())
                            {
                                String transaction=rs.getString(2);                                
                                if(transaction.contains(ps.trim()))
                                {
                                    String sp[]=transaction.trim().split("\n");
                                    realId=sp[0].trim().replaceAll("Vehicle Id: ", "");
                                }
                            }
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                        
                        String msg="RIR#"+ps+"#"+realId;                        
                        int pt=2000;                        
                        packetTransmission(msg,pt); 
                    }
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
