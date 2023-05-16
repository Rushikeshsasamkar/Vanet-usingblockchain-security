/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rca;

import java.sql.Statement;
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
            
            RCAFrame rvs=new RCAFrame();
            rvs.setTitle("Root Certificate Authority");
            rvs.setVisible(true);
            rvs.setResizable(false);
        
            RCAReceiver rvsr=new RCAReceiver(rvs);
            rvsr.start();
            
            DBConnection dbn=new DBConnection();
            Statement st=dbn.stt;
            
            st.execute("delete from pseudonymblockchain");
            st.execute("delete from pseudonymrevocationblockchain");
	}
	catch (Exception ex)
	{
            System.out.println("Failed loading L&F: ");
            System.out.println(ex);
	}        
    }
}
