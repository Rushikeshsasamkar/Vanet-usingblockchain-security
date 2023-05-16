/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package certificateauthority;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 *
 * @author Elcot
 */
public class Main {
    public static void main(String[] args) 
    {        
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        try
        {                    
			
            UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
            
            String s=JOptionPane.showInputDialog(new JFrame(), "Enter the CA ID: ");
            
            CAFrame bvs=new CAFrame(Integer.parseInt(s.trim()));
            bvs.setTitle("Certificate Authority - "+s);
            bvs.setVisible(true);
            bvs.setResizable(false);
        
            CAReceiver bvsr=new CAReceiver(bvs,Integer.parseInt(s.trim()));
            bvsr.start();
	}
	catch (Exception ex)
	{
            System.out.println("Failed loading L&F: ");
            System.out.println(ex);
	}        
    }
}
