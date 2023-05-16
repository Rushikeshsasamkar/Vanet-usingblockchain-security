/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package rsu;

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
			
            UIManager.setLookAndFeel("com.jtattoo.plaf.texture.TextureLookAndFeel");
            
            String s=JOptionPane.showInputDialog(new JFrame(), "Enter the RSU ID: ");
            
            RSUFrame rvs=new RSUFrame(Integer.parseInt(s.trim()));
            rvs.setTitle("RSU - "+s);
            rvs.setVisible(true);
            rvs.setResizable(false);
        
            RSUReceiver rvsr=new RSUReceiver(rvs,Integer.parseInt(s.trim()));
            rvsr.start();
	}
	catch (Exception ex)
	{
            System.out.println("Failed loading L&F: ");
            System.out.println(ex);
	}        
    }
}
