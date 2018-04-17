package com.whodesire.first;

import com.whodesire.config.ResourceHelper;
import com.whodesire.config.TermsViewer;
import com.whodesire.manager.GetPassword;
import com.whodesire.manager.PasswordManager;
import com.whodesire.util.*;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;

@Component
@Qualifier("verifier")
@Scope("prototype")
public class PasswordVerifier extends JDialog {
	private static final long serialVersionUID = -3255444874110528017L;

	final private String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

	private static int hashCode = 0;

	private WelcomeEmail welcomeEmail;

	private Integer WIDTH = 450, HEIGHT = 300;
	private JPanel panel, grpBox1, grpBox2;
	private JLabel lb_close, lb_icon, lb_question;
	private JTextField tf1, tf2, tf3;
	private JPasswordField pw1, pw2;
	private JButton btn1, btn2;
	private JRadioButton rb1, rb2, rb3;

	private static boolean isPressed = false;
	private static short X, Y;
	private static boolean visible = false;

	private static boolean firstQuestionDropped = false;

	private static String UUID;
	private static Map<String, String> securityQuestions;
	private static Map<String, String> questionsAnswers = new HashMap<>();
	private static int questionIndex = 0;

	private PasswordVerifier getPasswordVerifier() {
		return this;
	}

	public static Map<String, String> getSecurityQuestions() {
		return securityQuestions;
	}

	public static void setSecurityQuestions(Map<String, String> securityQuestions) {
		PasswordVerifier.securityQuestions = securityQuestions;
	}

	public static void setUUDI(char[] UUID){
		PasswordVerifier.UUID = String.valueOf(UUID);
	}

	public static boolean isIllustrating(){
		return visible;
	}

	@PostConstruct
	public void init() {

		panel = decoratePanel();

		setUndecorated(true);
		setType(Type.UTILITY);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBackground(new Color(0, 0, 0, 0));
		setSize(WIDTH, HEIGHT);
		setLayout(new GridLayout(1, 1));

		add(panel);

		Dimension screenSize;
		screenSize = OneMethod.getOneMethod().getScreenSize();

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				if (!isPressed) {
					isPressed = true;
					X = (short) evt.getX();
					Y = (short) evt.getY();
				}
			}

			@Override
			public void mouseReleased(MouseEvent evt) {
				if (isPressed)
					isPressed = false;
			}
		});

		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent evt) {
				if (isPressed)
					setLocation(evt.getXOnScreen() - X, evt.getYOnScreen() - Y);
			}

			@Override
			public void mouseMoved(MouseEvent evt) {
			}
		});

		setLocation((screenSize.width / 2) - (WIDTH / 2), (screenSize.height / 2) - (HEIGHT / 2));
		setAlwaysOnTop(true);
		validate();
		visible = true;

//		System.out.println("PasswordVerifier is going to visible");
		setVisible(true);
	}

	private void closeVerifier() {
		setModal(false);
		setVisible(false);
		dispose();
		visible = false;
	}

	private JPanel decoratePanel() {

		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

		Font font = new Font("Dialog", Font.BOLD, 14);

		URL iconURL = getClass().getResource("/icons/add-user-32.png");

		lb_icon = new JLabel();
		lb_icon.setOpaque(true);
		lb_icon.setIcon(new ImageIcon(iconURL));
		lb_icon.setBounds(3, 1, 32, 32);
		panel.add(lb_icon);

		lb_close = new JLabel("X", SwingConstants.CENTER);
		lb_close.setFont(font);
		lb_close.setOpaque(true);
		lb_close.setBackground(new Color(255, 50, 50));
		lb_close.setForeground(Color.BLACK);
		lb_close.setBounds(WIDTH - 26, 2, 24, 24);
		lb_close.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				closeVerifier();
			}
		});
		panel.add(lb_close);

		PropertiesUtil util = null;

		try {
			util = new PropertiesUtil("/config/primary.properties");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		if (!Boolean.valueOf(util.getProperty("passwordInserted"))) {
			buildGroupBox1();
			panel.add(grpBox1);
		} else {
			buildGroupBox2();
			panel.add(grpBox2);
		}

		return panel;
	}

	private void constructResourceVerifier(String name, String emailId,
										   char[] password, char[] pin){
		welcomeEmail = new WelcomeEmail(name, emailId);
		welcomeEmail.send();

		tf1.setEnabled(false);
		tf2.setEnabled(false);
		pw1.setEnabled(false);
		pw2.setEnabled(false);

		tf3.setEnabled(true);
	}

	private void buildGroupBox1() {

		Font font = new Font("Dialog", Font.BOLD, 14);

		String htmlTitle = "<html><font face=" + font.getFamily() + " size=5 color=#002175> getPassword </font>"
				+ "<font face=" + font.getFamily() + " size=4 color=black>" + " Enter Registration Details and Verify "
				+ "</font></html>";

		grpBox1 = new JPanel(new SpringLayout());
		grpBox1.setBounds(5, 30, WIDTH - 10, HEIGHT - (30 + 5));
		grpBox1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), htmlTitle,
				TitledBorder.LEFT, TitledBorder.TOP));

		font = new Font("Dialog", Font.PLAIN, 15);

		String[] labels = { "User Full Name", "User Email Id", "Password", "PIN Number", "Verify Email Key", "Secret Answers", "Terms & Conds." };

		// Name
		JLabel lb = new JLabel(labels[0], JLabel.TRAILING);
		tf1 = new JTextField();
		tf1.setDocument(new JTextFieldLimit(30));
		tf1.setFont(font);
		lb.setLabelFor(tf1);
		grpBox1.add(lb);
		grpBox1.add(tf1);

		// Email Id
		lb = new JLabel(labels[1], JLabel.TRAILING);
		tf2 = new JTextField();
		tf2.setDocument(new JTextFieldLimit(35));
		tf2.setFont(font);
		lb.setLabelFor(tf2);
		grpBox1.add(lb);
		grpBox1.add(tf2);
		tf2.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (hashCode == 0)
					hashCode = tf2.hashCode();
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (!tf2.getText().matches(EMAIL_REGEX)) {
					tf2.setBorder(BorderFactory.createLineBorder(Color.RED));
					tf2.requestFocus();
					tf2.setCaretPosition(tf2.getText().length());
				} else {
					hashCode = 0;
					tf2.setBorder(tf1.getBorder());
				}
			}
		});

		// Password
		lb = new JLabel(labels[2], JLabel.TRAILING);
		pw1 = new JPasswordField();
		pw1.setDocument(new JTextFieldLimit(25));
		pw1.setFont(font);
		lb.setLabelFor(pw1);
		grpBox1.add(lb);
		grpBox1.add(pw1);

		// PIN
		lb = new JLabel(labels[3], JLabel.TRAILING);
		pw2 = new JPasswordField();
		pw2.setDocument(new JTextFieldLimit(6));
		pw2.setFont(font);
		lb.setLabelFor(pw2);
		grpBox1.add(lb);
		grpBox1.add(pw2);

		// Verify Email Key
		lb = new JLabel(labels[4], JLabel.TRAILING);
		tf3 = new JTextField();
		tf3.setDocument(new JTextFieldLimit(45));
		tf3.setFont(font);
		lb.setLabelFor(tf3);
		grpBox1.add(lb);
		grpBox1.add(tf3);
		tf3.setEnabled(false);

		KeyListener btnListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent keyEvent) {

				if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER){
					if(keyEvent.getComponent() == btn1){
						SecretQuestions questions = SecretQuestions.getInstance();
						questions.setVisible(true);
					}else {
						submitRegister();
					}
				}
			}
		};

		// Secret Answers
		lb = new JLabel(labels[5], JLabel.TRAILING);
		btn1 = new JButton("Set Secret Questions and Answers");
		btn1.setFocusPainted(false);
		lb.setLabelFor(btn1);
		grpBox1.add(lb);
		grpBox1.add(btn1);
		btn1.addKeyListener(btnListener);
		btn1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				SecretQuestions questions = SecretQuestions.getInstance();
				questions.setVisible(true);
			}
		});

		// Submit
		lb = new JLabel("", JLabel.TRAILING){
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics gr) {
				super.paintComponent(gr);
				Graphics2D g = (Graphics2D) gr;
				g.setColor(new Color(66, 133, 244));
				g.setFont(new Font("Dialog", Font.BOLD, 11));
				g.drawString("Terms&Cond.", 0, 15);
				g.drawLine(0, 18, 100, 18);
			}
		};
		lb.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn2 = new JButton("Submit Details");
		btn2.setFocusPainted(false);
		grpBox1.add(lb);
		grpBox1.add(btn2);
		btn2.addKeyListener(btnListener);
		btn2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				submitRegister();
			}
		});
		lb.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent evt){
				TermsViewer.getTermsViewer();
			}
		});

		KeyAdapter keyda = new KeyAdapter() {
			public void keyTyped(KeyEvent evt) {
				if (Character.isWhitespace(evt.getKeyChar())) {
					evt.consume();
				}

				if (evt.getComponent().hashCode() == pw2.hashCode()) {
					if (!Character.isDigit(evt.getKeyChar()) && !Character.isISOControl(evt.getKeyChar())) {
						evt.consume();
					}
				}
			}

			public void keyReleased(KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_TAB) {
					if (evt.getComponent().hashCode() == hashCode)
						passwordFieldFocusListener((JPasswordField) evt.getComponent());
				}
			}
		};

		FocusListener focus = new FocusListener() {
			@Override
			public void focusGained(FocusEvent evt) {
				if (hashCode == 0)
					hashCode = evt.getComponent().hashCode();
				else {
					return;
				}
			}

			@Override
			public void focusLost(FocusEvent evt) {
				if (evt.getComponent().hashCode() == hashCode)
					passwordFieldFocusListener((JPasswordField) evt.getComponent());
			}
		};

		ActionListener action = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (evt.getSource().hashCode() == hashCode)
					passwordFieldFocusListener((JPasswordField) evt.getSource());
			}
		};

		pw1.addKeyListener(keyda);
		pw2.addKeyListener(keyda);

		pw1.addActionListener(action);
		pw2.addActionListener(action);

		pw1.addFocusListener(focus);
		pw2.addFocusListener(focus);

		// Layout the panel.
		SpringUtilities.makeCompactGrid(grpBox1, labels.length, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad


	}

	private void submitRegister(){

		new Thread(new Runnable() {
			public void run() {

				if (tf1.getText().length() > 3 && tf2.getText().length() > 7 && pw1.getPassword().length >= 8
						&& pw2.getPassword().length == 6) {


					if(!tf3.isEnabled()){

						if(securityQuestions == null){
							new MessageUtil("At least 5 Security Questions must be answered, no answered founded.", MessageUtil.LEVEL.CRITICAL);
						}else{

							if(UUID == null){
								constructResourceVerifier(tf1.getText(), tf2.getText(),
										pw1.getPassword(), pw2.getPassword());
							}

						}

					}else{

						if(tf3.getText().equals(String.valueOf(UUID))) {

							ResourceHelper helper = new ResourceHelper();
							helper.setPasswordInserted(true);

							java.util.List<char[]> list = new ArrayList<>();
							list.add(tf1.getText().toCharArray());
							list.add(tf2.getText().toCharArray());
							list.add(pw1.getPassword());
							list.add(pw2.getPassword());

							Encoder encoder = new Encoder();
							PropertiesUtil utils;

							//Setting Security Questions...
							{
								try {

									Set<String> keySet = securityQuestions.keySet();
									Iterator<String> itr = keySet.iterator();

									while(itr.hasNext()){

										String key = itr.next();
										char[] value = encoder.encryptSentence(UUID.toCharArray(), securityQuestions.get(key).toCharArray());
										securityQuestions.put(key, String.valueOf(value));
									}

									utils = new PropertiesUtil("/security/security.questions");
									utils.addProperties(securityQuestions);

									//Setting password and pin encryption through UUID
									utils = new PropertiesUtil("/security/validator.steps");
									utils.setProperty("prioritySecret",
											String.valueOf(encoder.encryptSentence(UUID.toCharArray(), pw1.getPassword())));
									utils.setProperty("secondarySecret",
											String.valueOf(encoder.encryptSentence(UUID.toCharArray(), pw2.getPassword())));

								} catch (URISyntaxException e) {
									e.printStackTrace();
								}
							}

							List<char[]> password_list = encoder.encryptList(pw1.getPassword(), list);
							List<char[]> pin_list = encoder.encryptList(pw2.getPassword(), list);

//							System.out.println(password_list + ", " + password_list.size());
//							System.out.println(pin_list + ", " + pin_list.size());

							Map<String, String> password_map = new HashMap<>();
							password_map.put("name", String.valueOf(password_list.get(0)));
							password_map.put("email", String.valueOf(password_list.get(1)));
							password_map.put("password", String.valueOf(password_list.get(2)));
							password_map.put("pin", String.valueOf(password_list.get(3)));
							password_map.put("passwordInserted", "true");
							password_map.put("resourceVerified", "true");

							Map<String, String> pin_map = new HashMap<>();
							pin_map.put("name", String.valueOf(pin_list.get(0)));
							pin_map.put("email", String.valueOf(pin_list.get(1)));
							pin_map.put("password", String.valueOf(pin_list.get(2)));
							pin_map.put("pin", String.valueOf(pin_list.get(3)));
							pin_map.put("passwordInserted", "true");
							pin_map.put("resourceVerified", "true");

							try {

								//setting configuration as password
								utils = new PropertiesUtil("/config/primary.properties");
								utils.setUpdateValueOnExistenceEnabled(true);
								utils.addProperties(password_map);

								//setting configuration as pin number
								utils = new PropertiesUtil("/config/secondary.properties");
								utils.setUpdateValueOnExistenceEnabled(true);
								utils.addProperties(pin_map);

								Thread.sleep(800);

								GetPassword getPassword = GetPassword.getInstance();
								getPassword.setPassword(pw1.getPassword());

							} catch (URISyntaxException use) {
								use.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							} finally {
								password_map.clear();
								pin_map.clear();
								password_list.clear();
								pin_list.clear();
							}

							java.awt.Component[] comps = grpBox1.getComponents();
							for (java.awt.Component comp : comps) {
								grpBox1.remove(comp);
							}
							grpBox1.repaint();
							panel.remove(grpBox1);
							panel.repaint();

							PasswordManager passwordManager = PasswordManager.getPasswordManager();
							PasswordManager.setIllustrating(true);
							passwordManager.setAlwaysOnTop(true);
							passwordManager.requestFocusOnTable();

							PasswordVerifier verifier = getPasswordVerifier();
							verifier.closeVerifier();

						}else{
							new MessageUtil("Secret UUID key not matched, Please try again...", MessageUtil.LEVEL.CRITICAL);
						}

					}

				}

			}
		}).start();
	}

//	private void testList(String listName, List<char[]> list){
////		System.out.println(listName);
//		for(char[] object : list){
////			System.out.println(String.valueOf(object));
//		}
//	}

	private void buildGroupBox2() {

		firstQuestionDropped = false;

		Font font = new Font("Dialog", Font.BOLD, 14);

		String htmlTitle = "<html><font face=" + font.getFamily() + " size=5 color=#002175> getPassword </font>"
				+ "<font face=" + font.getFamily() + " size=4 color=black>" + " Verify Login " + "</font></html>";

		grpBox2 = new JPanel(new SpringLayout());
		grpBox2.setBounds(5, 30, WIDTH - 10, HEIGHT - (30 + 5));
		grpBox2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), htmlTitle,
				TitledBorder.LEFT, TitledBorder.TOP));

		font = new Font("Dialog", Font.PLAIN, 15);

		String[] labels = { "Password", "PIN Number", "I Forgot", "Show Next", "Secret UUID Key", "Submit" };

		// Password
		rb1 = new JRadioButton(labels[0]);
		pw1 = new JPasswordField();
		pw1.setDocument(new JTextFieldLimit(25));
		pw1.setFont(font);
		grpBox2.add(rb1);
		grpBox2.add(pw1);

		// PIN
		rb2 = new JRadioButton(labels[1]);
		pw2 = new JPasswordField();
		pw2.setDocument(new JTextFieldLimit(6));
		pw2.setFont(font);
		grpBox2.add(rb2);
		grpBox2.add(pw2);

		// Forget Answers
		rb3 = new JRadioButton(labels[2]);
		lb_question = new JLabel("Secret Questions");
		lb_question.setBorder(pw1.getBorder());
		grpBox2.add(rb3);
		grpBox2.add(lb_question);

		ActionListener action = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				String text = ((JRadioButton) evt.getSource()).getText();

				btn1.setEnabled(false);
				tf1.setEnabled(false);
				tf2.setEnabled(false);

				if (text.equals("Password")) {

					pw1.requestFocus();
					pw1.setCaretPosition(pw1.getPassword().length);

				} else if (text.equals("PIN Number")) {

					pw2.requestFocus();
					pw2.setCaretPosition(pw2.getPassword().length);

				} else {
					btn1.setEnabled(true);
					tf1.setEnabled(true);
					tf2.setEnabled(true);

					showNextQuestion();
				}
			}
		};

		FocusListener focus = new FocusListener() {
			@Override
			public void focusGained(FocusEvent evt) {

				btn1.setEnabled(false);
				tf1.setEnabled(false);

				if (evt.getComponent() == pw1) {
					rb1.setSelected(true);
				} else if (evt.getComponent() == pw2) {
					rb2.setSelected(true);
				}
			}

			@Override
			public void focusLost(FocusEvent evt) {
			}
		};

		ActionListener textFieldAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				decideSubmit();
			}
		};

		ButtonGroup bg = new ButtonGroup();
		bg.add(rb1);
		bg.add(rb2);
		bg.add(rb3);

		rb1.addActionListener(action);
		rb2.addActionListener(action);
		rb3.addActionListener(action);
		rb2.setSelected(true);

		pw1.addFocusListener(focus);
		pw2.addFocusListener(focus);

		pw1.addActionListener(textFieldAction);
		pw2.addActionListener(textFieldAction);

		// Try Another Question
		btn1 = new JButton(labels[3]);
		btn1.setFocusPainted(false);
		btn1.setEnabled(false);
		tf1 = new JTextField();
		tf1.setDocument(new JTextFieldLimit(30));
		tf1.setFont(font);
		tf1.setEnabled(false);
		grpBox2.add(btn1);
		grpBox2.add(tf1);

		Action nextAction = new AbstractAction("Show Next") {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				showNextQuestion();
			}
		};
		nextAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
		btn1.setAction(nextAction);

		// Verify Email Key
		JLabel lb = new JLabel(labels[4], JLabel.TRAILING);
		tf2 = new JTextField();
		tf2.setDocument(new JTextFieldLimit(45));
		tf2.setFont(font);
		lb.setLabelFor(tf2);
		grpBox2.add(lb);
		grpBox2.add(tf2);

		// Submit
		lb = new JLabel(labels[5], JLabel.TRAILING);
		btn2 = new JButton("Submit");
		btn2.setFocusPainted(false);
		grpBox2.add(lb);
		grpBox2.add(btn2);
		btn2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				decideSubmit();
			}
		});

		KeyAdapter keyda = new KeyAdapter() {
			public void keyTyped(KeyEvent evt) {
				if (Character.isWhitespace(evt.getKeyChar())) {
					evt.consume();
				}

				if (evt.getComponent().hashCode() == pw2.hashCode()) {
					if (!Character.isDigit(evt.getKeyChar()) && !Character.isISOControl(evt.getKeyChar())) {
						evt.consume();
					}
				}
			}
		};

		pw1.addKeyListener(keyda);
		pw2.addKeyListener(keyda);

		// Layout the panel.
		SpringUtilities.makeCompactGrid(grpBox2, labels.length, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		setPINSelectionOnVerifier();
	}

	private void showNextQuestion(){

		try {

			PropertiesUtil utils = new PropertiesUtil("/security/security.questions");
			securityQuestions = utils.getPropertiesMap();

			loop:
			for(String question : securityQuestions.keySet()) {

				if(firstQuestionDropped){

//					System.out.println("Question : " + lb_question.getText() + ", " + tf1.getText());
					questionsAnswers.put(lb_question.getText(), tf1.getText());

					if(++questionIndex >= securityQuestions.size())
						questionIndex = 0;

					List<String> keyList = new ArrayList<>(securityQuestions.keySet());

					lb_question.setText(keyList.get(questionIndex));

					clearTextField(tf1);

					if(questionsAnswers.get(lb_question.getText()) != "")
						tf1.setText(questionsAnswers.get(lb_question.getText()));

					break loop;

				}else{

					questionIndex = 0;
					firstQuestionDropped = true;
					lb_question.setText(question);
					break loop;
				}

			}

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void clearTextField(JTextField tf){

		try{
			Robot robot = new Robot();
			tf.requestFocus();

			Thread.sleep(2);

			robot.keyRelease(KeyEvent.VK_ALT);
			Thread.sleep(2);

			tf.setCaretPosition(tf.getText().length());

			int length = tf.getText().length();

			for(int i = 0; i < length; i++){

				robot.keyPress(KeyEvent.VK_BACK_SPACE);
				robot.keyRelease(KeyEvent.VK_BACK_SPACE);
				Thread.sleep(2);
				robot.keyPress(KeyEvent.VK_END);
				robot.keyRelease(KeyEvent.VK_END);

			}

		} catch (AWTException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void setPINSelectionOnVerifier(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					Thread.sleep(500);
				}catch(InterruptedException ie){
					ie.printStackTrace();
				}
				rb2.setSelected(true);
				pw2.requestFocus();
				pw2.setCaretPosition(pw1.getPassword().length);
			}
		}).start();
	}

	private final void decideSubmit() {

		boolean flag;

		pw1.setBorder(tf1.getBorder());
		pw2.setBorder(tf1.getBorder());

		if (rb1.isSelected()) {

			flag = validateSubmit(pw1.getPassword());
			if (!flag)
				pw1.setBorder(BorderFactory.createLineBorder(Color.RED));

		} else if (rb2.isSelected()) {

			flag = validateSubmit(pw2.getPassword());
			if (!flag)
				pw2.setBorder(BorderFactory.createLineBorder(Color.RED));

		} else if (rb3.isSelected()){

			if(questionsAnswers != null && questionsAnswers.size() >= 3 && tf2.getText().length() >= 10){
				//Write the logic evaluate equality of questionAnswers and Secret Questions.

				Iterator<String> iterator = questionsAnswers.keySet().iterator();
				Encoder encoder = new Encoder();
				int givenAnswers;

				for(givenAnswers = 0; iterator.hasNext();){

					String question = iterator.next();

					if(questionsAnswers.get(question).length() > 3){
						String answer = questionsAnswers.get(question);

//						Set<String> questionSet = securityQuestions.keySet();

						try{
							String secretAnswer = securityQuestions.get(question);
							char[] decryptedAnswer = encoder.decryptSentence(tf2.getText().toCharArray(), secretAnswer.toCharArray());

							if(answer.equals(String.valueOf(decryptedAnswer)))
								++givenAnswers;

						}catch(EncryptionOperationNotPossibleException eonpe){

						}catch(Exception expception){}

					}
				}

				if(givenAnswers<3){
					new MessageUtil("To recover password and pin, you must give at least 3 answers, Please check your answers", MessageUtil.LEVEL.WARNING);
				}else{

					try {
						PropertiesUtil utils = new PropertiesUtil("/security/validator.steps");

						char[] password = encoder.decryptSentence(tf2.getText().toCharArray(),
												utils.getProperty("prioritySecret").toCharArray());

						char[] pinNumber = encoder.decryptSentence(tf2.getText().toCharArray(),
												utils.getProperty("secondarySecret").toCharArray());

						new MessageUtil("Your Password is [" + String.valueOf(password) + "] " +
										"and PIN is [" + String.valueOf(pinNumber) + "], " +
											"Please remember it well, thanks.");

					} catch (URISyntaxException e) {
						e.printStackTrace();
					}

				}

			}else{
				new MessageUtil("Give at least 3 answers and Secret UUID Key must be entered", MessageUtil.LEVEL.CRITICAL);
			}
		}

	}

	private final boolean validateSubmit(char[] password) {

		boolean flag = false;

		try {

			PropertiesUtil utils;
			Encoder encoder = new Encoder();
			char[] decryptedPassword;
			char[] sessionPassword;
			
			// first getting the property, second decrypting, matching with entered password

			// In Case User is Passing PIN
			if (password.length == 6) {
				utils = new PropertiesUtil("/config/secondary.properties");
				decryptedPassword = encoder.decryptSentence(password, utils.getProperty("pin").toCharArray());
				sessionPassword = encoder.decryptSentence(password, utils.getProperty("password").toCharArray());
			}else{
				utils = new PropertiesUtil("/config/primary.properties");
				decryptedPassword = encoder.decryptSentence(password, utils.getProperty("password").toCharArray());
				sessionPassword = decryptedPassword;
			}

			if (String.valueOf(decryptedPassword).equals(String.valueOf(password))) {
				
				GetPassword getPassword = GetPassword.getInstance();
				getPassword.setPassword(sessionPassword);

				PasswordManager.setIllustrating(true);

				java.awt.Component[] comps = grpBox2.getComponents();
				for (java.awt.Component comp : comps) {
					grpBox2.remove(comp);
				}
				grpBox2.repaint();
				panel.remove(grpBox2);
				panel.repaint();

				closeVerifier();

				flag = true;

			} else {
				flag = false;
			}

		} catch (URISyntaxException use) {
			flag = false;
		} catch (EncryptionOperationNotPossibleException eonpe){
			flag = false;
		}

		return flag;
	}

	private void passwordFieldFocusListener(java.awt.Component comp) {
		JPasswordField pf = (JPasswordField) comp;

		if (comp.hashCode() == pw1.hashCode() && !isFactosValid(pf.getPassword())) {
			pf.setBorder(BorderFactory.createLineBorder(Color.RED));
			pf.requestFocus();
			pf.setCaretPosition(pf.getPassword().length);
			return;
		}

		if (comp.hashCode() == pw2.hashCode() && pf.getPassword().length != 6) {
			pf.setBorder(BorderFactory.createLineBorder(Color.RED));
			pf.requestFocus();
			pf.setCaretPosition(pf.getPassword().length);
			return;
		}

		pf.setBorder(tf1.getBorder());

		String value = JOptionPane.showInputDialog(this, "Confirm earlier entered value?");
		if (value != null && value.length() >= 6) {
			if (String.valueOf(pf.getPassword()).equals(value)) {
				hashCode = 0;
			} else {
				pf.requestFocus();
				pf.setCaretPosition(pf.getPassword().length);
			}
		} else {
			pf.requestFocus();
			pf.setCaretPosition(pf.getPassword().length);
		}

	}

	private boolean isFactosValid(char[] password) {
		int alphaUpper = 0, alphaLower = 0, num = 0, spcl = 0, len = 0;

		len = password.length;
		for (int i = 0; i < len; i++) {
			if (Character.isLetter(password[i])) {
				if (Character.isUpperCase(password[i]))
					++alphaUpper;
				else
					++alphaLower;
			} else if (Character.isDigit(password[i]))
				++num;
			else if ((password[i]) != ' ')
				++spcl;
		}

		if (alphaUpper != 0 && alphaLower != 0 && num != 0 && spcl != 0 && len != 7)
			return true;
		else
			return false;

	}

}
