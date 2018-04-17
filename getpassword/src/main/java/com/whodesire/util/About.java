package com.whodesire.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class About extends JInternalFrame implements KeyListener, MouseMotionListener, MouseListener {
	private static final long serialVersionUID = 1100288834992031287L;

	private boolean isOnSurface = false;
	private JLabel lbBottom,  lbChng, lb11;
	private JPanel pnl1, pnl2;
	private JPanel pnlBottom;
	private Font lbFont = new Font("Segoe UI Semibold", Font.BOLD, 14);
	private Image img;
	public int getHashCode = 0;

	public About(){
		super("About GetPassword", /*resizable*/false, /*closable*/true,
									/*maximizable*/false, /*iconifiable*/false);
		this.init();
	}
	
	public boolean getVisible(){
		return super.isVisible();
	}
	
	private void init(){
		//super.setType(JFrame.Type.UTILITY);	//Set the type JFrame appear but not in Taskbar.
		super.setBorder(BorderFactory.createLineBorder(new Color(141, 136, 122)));
		super.setBounds(500, 100, 256, 355);
		super.setLayout(null);
		super.setVisible(true);
		
		//panel1 container begins from here-----------------------
		pnl1 = new JPanel();
		pnl1.setBounds(0, 0, 254, 300);
		pnl1.setLayout(null);
		pnl1.setBackground(Color.WHITE);
		super.add(pnl1);
		pnl1.addMouseListener(this);
		pnl1.addMouseMotionListener(this);
		
		lb11 = new JLabel();
		lb11.setBounds(3, 0, pnl1.getWidth(), 125);
		pnl1.add(lb11);
		lb11.setBackground(Color.WHITE);
		lb11.setIcon(new ImageIcon(scale("/ignouLogo.jpg" , lb11.getWidth(), lb11.getHeight())));
		lb11.addMouseListener(this);
		lb11.addMouseMotionListener(this);

		JLabel lbAuthor = new JLabel();
		lbAuthor.setBounds(30, lb11.getY() + lb11.getHeight() + 5, pnl1.getWidth(), 150);
		pnl1.add(lbAuthor);
		lbAuthor.setIcon(new ImageIcon(scale("/arif_mustafa.png" ,
				lbAuthor.getWidth() - 120, lbAuthor.getHeight())));

		lbChng = new JLabel();
		lbChng.setSize(52, 48);
		lbChng.setVisible(false);
		lbChng.addMouseListener(this);
		pnl1.add(lbChng);
		lbChng.setLocation(254 - (lbChng.getWidth() + 2), 
				(int)((pnl1.getHeight() / 2) - (lbChng.getHeight() / 2)));
		this.setImage(lbChng, "/right_48.png");
				
		//panel2 container begins from here-----------------------
		pnl2 = new JPanel();
		pnl2.setBounds(pnl1.getX() + pnl1.getWidth() + 2, 0, 254, 300);
		pnl2.setLayout(null);
		pnl2.setBackground(Color.WHITE);
		super.add(pnl2);
		pnl2.setVisible(false);
		pnl2.addMouseMotionListener(this);
		
		JLabel lb20 = new JLabel();
		pnl2.add(lb20);
		lb20.setBounds(4, 6, 80, 78);
		this.setImage(lb20, "/authorPhoto.jpg");
		
		JLabel lb21 = new JLabel("Mohammad Arif Mustafa");
		lb21.setBounds(88, 2, 160, 17);
		pnl2.add(lb21);
		lb21.setFont(lbFont);
		lb21.setForeground(new Color(7, 134, 211));
		
		JLabel lb22 = new JLabel("Software Developer");
		lb22.setBounds(88, (lb21.getY() + lb21.getHeight())-1, 160, 17);
		pnl2.add(lb22);
		lbFont = new Font(lbFont.getFamily(), Font.PLAIN, lbFont.getSize()-3);
		lb22.setFont(lbFont);
		
		JLabel lb31 = new JLabel("<html>Study & work on ERP Technology and using " + 
									"Microsoft and Oracle technologies like: " + 
									".NET(VB, C#, ASP), Java, SQL Server, SQL+, etc." + 
									" I also like to participate in online " +
									"forums, communities, group discussions, " +
									"blogs and to learn about latest technology," + 
									" thoughts, experiments and development.</html>", SwingConstants.LEFT);
		lb31.setVerticalAlignment(SwingConstants.TOP);
		lb31.setBounds(lb20.getX(), lb20.getY() + lb20.getHeight() + 2, pnl1.getWidth() - (lb20.getX()+2), 116);
		pnl2.add(lb31);
		lbFont = new Font(lbFont.getFamily(), Font.PLAIN, lbFont.getSize()+1);
		lb31.setFont(lbFont);
		
		JLabel lb23 = new JLabel();
		lb23.setBounds(4, lb31.getY() + lb31.getHeight() + 3, 24, 24);
		pnl2.add(lb23);
		lb23.setFont(lb22.getFont());
		this.setImage(lb23, "/indianFlag.png");
		
		JLabel lb24 = new JLabel("India");
		lb24.setBounds(lb23.getX() + lb23.getWidth() + 2, lb23.getY(), 34, 24);
		pnl2.add(lb24);
		lb24.setFont(lbFont);
		
		JLabel lb25 = new JLabel();
		lb25.setBounds(lb24.getX() + lb24.getWidth() + 73, lb23.getY(), 24, 24);
		pnl2.add(lb25);
		lb25.setFont(lbFont);
		this.setImage(lb25, "/msdn24.png");
		
		JLabel lb26 = new JLabel("Forum");
		lb26.setBounds(lb25.getX() + lb25.getWidth() + 2, lb25.getY(), 40, 24);
		pnl2.add(lb26);
		lb26.setFont(lb24.getFont());
		
		JLabel lb27 = new JLabel();
		lb27.setBounds(4, lb23.getY() + lb23.getHeight() + 5, 24, 24);
		pnl2.add(lb27);
		lb27.setFont(lbFont);
		this.setImage(lb27, "/oracle24.png");
		
		JLabel lb28 = new JLabel("Oracle");
		lb28.setBounds(lb27.getX() + lb27.getWidth() + 2, lb27.getY(), 34, 24);
		pnl2.add(lb28);
		lb28.setFont(lb24.getFont());
		
		JLabel lb29 = new JLabel();
		lb29.setBounds(lb28.getX() + lb28.getWidth() + 73, lb28.getY(), 24, 24);
		pnl2.add(lb29);
		lb29.setFont(lbFont);
		this.setImage(lb29, "/java24.png");
		
		JLabel lb30 = new JLabel("Java Language");
		lb30.setBounds(lb29.getX() + lb29.getWidth() + 2, lb29.getY(), 90, 24);
		pnl2.add(lb30);
		lb30.setFont(lb24.getFont());
		
		JLabel lb32 = new JLabel();
		lb32.setBounds(4, lb27.getY() + lb27.getHeight() + 6, 65, 24);
		pnl2.add(lb32);
		lb32.setFont(lbFont);
		this.setImage(lb32, "/apache24.png");
		
		JLabel lb33 = new JLabel("Apache");
		lb33.setBounds(lb32.getX() + lb32.getWidth() + 2, lb32.getY(), 44, 24);
		pnl2.add(lb33);
		lb33.setFont(lb24.getFont());
		
		JLabel lb34 = new JLabel();
		lb34.setBounds(lb29.getX(), lb33.getY(), 24, 24);
		pnl2.add(lb34);
		lb34.setFont(lbFont);
		this.setImage(lb34, "/stackoverflow24.png");
		
		JLabel lb35 = new JLabel("stackOverflow");
		lb35.setBounds(lb34.getX() + lb34.getWidth() + 2, lb34.getY(), 80, 24);
		pnl2.add(lb35);
		lb35.setFont(lb24.getFont());
		
		//pnlBottom container begins from here-----------------------
		pnlBottom = new JPanel();
		pnlBottom.setBounds(0, pnl1.getY() + pnl1.getHeight(), pnl1.getWidth(), 32);
		pnlBottom.setLayout(null);
		pnlBottom.setBackground(new Color(7, 134, 211));
		super.add(pnlBottom);
		
		JLabel lb51 = new JLabel();
		lb51.setBounds(2, 1, 187, 29);
		lb51.setVerticalAlignment(SwingConstants.TOP);
		pnlBottom.add(lb51);
		this.setImage(lb51, "/copyRight.png");
		
		lbBottom = new JLabel();
		lbBottom.setBounds(pnlBottom.getWidth() - 34, -1, 32, 32);
		pnlBottom.add(lbBottom);
		this.setImage(lbBottom, "/get32rgh.png");
		lbBottom.addMouseListener(this);
		
		//---------------------------------------------------------------------
		
		super.addKeyListener(this);
		super.addMouseListener(this);
		super.addMouseMotionListener(this);
		
		super.requestFocus();
	}
	
	private void closeFrame(){
		super.setVisible(false);
	}
	
	private void setImage(Component comp, String imgPath){

		URL url = getClass().getResource("/images" + imgPath);

		img = new ImageIcon(url).getImage();

		if(comp instanceof JLabel)
			((JLabel)comp).setIcon(new ImageIcon(img));
		else if(comp instanceof JButton)
			((JButton)comp).setIcon(new ImageIcon(img));

	}
	
	private void changePanel(){
		this.lbChng.setVisible(false);
		if (pnl2.isVisible()==false){
			this.pnl1.setVisible(false);
			this.pnl1.remove(lbChng);
			this.pnl2.add(lbChng);
			this.pnl2.setLocation(0, 0);
			this.setImage(lbChng, "/left_48.png");
			this.setImage(lbBottom, "/get32left.png");
			lbChng.setLocation(2, (int)((pnl1.getHeight() / 2) - (lbChng.getHeight() / 2)));
			this.pnl2.setVisible(true);
		}else{
			this.pnl2.setVisible(false);
			this.pnl2.remove(lbChng);
			this.pnl1.add(lbChng);
			this.pnl1.setLocation(0, 0);
			this.setImage(lbChng, "/right_48.png");
			this.setImage(lbBottom, "/get32rgh.png");
			lbChng.setLocation(254 - (lbChng.getWidth() + 2), (int)((pnl1.getHeight() / 2) - (lbChng.getHeight() / 2)));
			this.pnl1.setVisible(true);
		}
		
		super.repaint();
		super.requestFocus();
	}

	@Override
	public void keyPressed(KeyEvent evt) {
		if (evt.getSource() == (JInternalFrame)this){
			if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
				closeFrame();
			else if(evt.getKeyCode() == KeyEvent.VK_RIGHT){
				if (pnl2.isVisible()==false)
					changePanel();
			}else if(evt.getKeyCode() == KeyEvent.VK_LEFT){
				if (pnl1.isVisible()==false)
					changePanel();
			}			
		}
	}

	@Override
	public void keyReleased(KeyEvent evt) {}
	@Override
	public void keyTyped(KeyEvent evt) {}

	@Override
	public void mouseDragged(MouseEvent evt) {}
	@Override
	public void mouseMoved(MouseEvent evt) {
		if(evt.getSource() == pnl1 || evt.getSource() == lb11){
			if (evt.getX() > this.pnl1.getWidth() - 40 || evt.getX() > this.lb11.getWidth() - 40)
				this.isOnSurface = true;
			else
				this.isOnSurface = false;
			this.decideLB_ED();
		}else if(evt.getSource() == pnl2){
			if (evt.getX() < 40)
				this.lbChng.setVisible(true);
			else
				this.lbChng.setVisible(false);
		}
	}

	@Override
	public void mouseClicked(MouseEvent evt) {}
	@Override
	public void mouseEntered(MouseEvent evt) {}
	@Override
	public void mouseExited(MouseEvent evt) {
		if(evt.getSource() == lb11){
			this.isOnSurface = false;
			this.decideLB_ED();
		}
	}
	@Override
	public void mousePressed(MouseEvent evt) {
		if(evt.getSource() == this.lbBottom){
			this.changePanel();
		}else if(evt.getSource() == this.lbChng){
			changePanel();
		}
	}

	@Override
	public void mouseReleased(MouseEvent evt) {}
	
	private void decideLB_ED(){
		if(this.isOnSurface == false){
			this.lbChng.setVisible(false);
		}else{
			this.lbChng.setVisible(true);
		}
	}
	
	public final BufferedImage scale(String imgPath, int w, int h) {

		BufferedImage src, dst = null;

		try {

			File file = new File(OneMethod.getFilePath("/images" + imgPath));

			src = ImageIO.read(file);
			dst = new BufferedImage(w, h, BufferedImage.SCALE_DEFAULT);
		    Graphics2D g2d = (Graphics2D) dst.createGraphics();
		    g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
		    g2d.drawImage(src, 0, 0, w, h, null);
		    g2d.dispose();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return dst;
	}
}
