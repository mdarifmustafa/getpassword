package com.whodesire.boot;

import com.whodesire.util.OneMethod;

import javax.swing.*;

public class StartPasswordManager {

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				
				try {
					
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException ex) {
					ex.printStackTrace();
				}

				OneMethod.getOneMethod().loadTrayIcon();
				
			}
		});

	}

}
