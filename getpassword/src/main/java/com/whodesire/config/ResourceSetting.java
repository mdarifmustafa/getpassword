package com.whodesire.config;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.whodesire.util.OneMethod;
import com.whodesire.util.SpringUtilities;

public class ResourceSetting extends JDialog {
	private static final long serialVersionUID = -4143804536737482646L;

	private static ResourceSetting resourceSetting;

	// private JPanel parentPanel, childPanel;
	private static boolean visible;
	private JSlider slider;

	// private final static Font font = new Font("Dialog", Font.PLAIN, 15);

	private ResourceSetting() {
		init();
	}

	public static ResourceSetting getResourceSetting() {
		resourceSetting = new ResourceSetting();
		return resourceSetting;
	}

	public static boolean isIllustrating() {
		return visible;
	}

	private void init() {

		OneMethod env = OneMethod.getOneMethod();

		setTitle("Resource - Setting");
		setModal(true);
		setResizable(false);
		setType(Type.UTILITY);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds((env.getScreenWidth() / 2) - 325, (env.getScreenHeight() / 2) - 250, 650, 500);
		setLayout(new BorderLayout());

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent keyEvent) {
				if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE)
					closeResourceSetting();
			}
		});

		JPanel panel = setting_panel();
		;

		add(panel, BorderLayout.CENTER);
		panel.setLocation(0, 0);

		setAlwaysOnTop(true);
		validate();
		visible = true;
		setVisible(true);
	}

	private JPanel setting_panel() {

		JPanel panel = new JPanel(new SpringLayout());

		String[] labels = new String[] { "Adjust Session Expired (in seconds)", "Synchronize Backup with" };

		JLabel lb = new JLabel(labels[0], JLabel.TRAILING);

		int sessionAliveForSeconds = OneMethod.getSessionForSeconds();

		if (sessionAliveForSeconds < 60)
			sessionAliveForSeconds = 120;

		slider = new JSlider(JSlider.HORIZONTAL, 60, 300, sessionAliveForSeconds);
		slider.setMinorTickSpacing(15);
		slider.setMajorTickSpacing(30);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setPaintTrack(true);

		lb.setLabelFor(slider);
		panel.add(lb);
		panel.add(slider);

		slider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent event) {
				OneMethod.setPropertyValue("/config/primary.properties", "sessionAliveInSeconds",
						String.valueOf(slider.getValue()));

				OneMethod.setPropertyValue("/config/secondary.properties", "sessionAliveInSeconds",
						String.valueOf(slider.getValue()));
			}

		});

		Image img = null;

		try {
			img = ImageIO.read(getClass().getResource("/icons/dropbox32.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		lb = new JLabel(new ImageIcon(img), JLabel.LEFT);
		lb.setText("Synchronize Backup with");

		JButton backupBtn = new JButton("Backup Now");
		backupBtn.setFocusPainted(false);
		lb.setLabelFor(backupBtn);
		panel.add(lb);
		panel.add(backupBtn);

		backupBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {

				final String ACCESS_TOKEN = "wgKwRxouB_cAAAAAAAAQo_ppuhfejJD3wUE-088qA8vD6IzOK_DMyedyXSrShh-t";

				final String APP_KEY = "es9fwjmbwjad644";
				final String APP_SECRET = "kz35e6p1jw2wtem";

				DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

				// Create Dropbox client
				DbxRequestConfig config = new DbxRequestConfig("getpassword-authorize", Locale.getDefault().toString());
//				DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
//
//				// Get current account info
//				FullAccount account = null;
//				try {
//					account = client.users().getCurrentAccount();
//				} catch (DbxApiException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (DbxException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				System.out.println(account.getName().getDisplayName());
				
				DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
				
				String authorizeUrl = webAuth.start();
				System.out.println("1. Go to: " + authorizeUrl);
				System.out.println("2. Click \"Allow\" (you might have to log in first)");
				System.out.println("3. Copy the authorization code.");
				
				String code = null;
				
				try {
					code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println("code is : " + code);
				
				DbxAuthFinish authFinish = null;
				try {
					authFinish = webAuth.finish(code);
				} catch (DbxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String accessToken = authFinish.accessToken;
				
				DbxClient client = new DbxClient(config, accessToken);
				try {
					System.out.println("Linked account: " + client.getAccountInfo().displayName);
				} catch (DbxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		SpringUtilities.makeCompactGrid(panel, labels.length, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		return panel;
	}

	public static void closeResourceSettingStatically() {
		if (visible)
			resourceSetting.closeResourceSetting();
	}

	public void closeResourceSetting() {
		resourceSetting = null;
		setModal(false);
		setVisible(false);
		dispose();
		visible = false;
	}

	public static void main(String[] args) {
		ResourceSetting setting = new ResourceSetting();
	}

}
