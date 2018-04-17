package com.whodesire.util;

import com.whodesire.config.SpringAppConfig;
import com.whodesire.first.PasswordVerifier;
import com.whodesire.manager.GetPassword;
import com.whodesire.manager.PasswordManager;
import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lc.kra.system.mouse.GlobalMouseHook;
import lc.kra.system.mouse.event.GlobalMouseAdapter;
import lc.kra.system.mouse.event.GlobalMouseEvent;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.FileSystemUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Provider;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class OneMethod {

	private static OneMethod method;

	private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static PasswordManager passwordManager;

	private static boolean isPressed = false, isTimerRunning = false;
	private static short countForReleased = 0;

	private static TrayIcon trayIcon;
	private static GlobalKeyboardHook kbHook = null;
	private static GlobalMouseHook mouseHook = null;
	
	private static String getPasswordPath = null;

	private static String[] resourceDirs =
			new String[]{"backup", "config", "data", "icons",
					"images", "loggings", "mailx", "replicate", "security"};

	private static boolean resourceDirCreated = false;

	private static final BouncyCastleProvider bouncyCastleProvider
			= new org.bouncycastle.jce.provider.BouncyCastleProvider();

	private final static Logger logger = Logger.getLogger(OneMethod.class);

	private OneMethod() {

		Security.addProvider(bouncyCastleProvider);
//		Security.insertProviderAt(bouncyCastleProvider, 1);

		Field field = null;

		try {
			field = Class.forName("javax.crypto.JceSecurity").
					getDeclaredField("isRestricted");
			field.setAccessible(true);
			field.setBoolean(null, false);
//			field.set(null, false);
//			field.setAccessible(false);

		} catch (Exception ex) {

//			try {
//				if ( Boolean.TRUE.equals(field.get(null)) ) {
//                    if ( Modifier.isFinal(field.getModifiers()) ) {
//                        Field modifiers = Field.class.getDeclaredField("modifiers");
//                        modifiers.setAccessible(true);
//                        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
//                    }
//                    field.setAccessible(true);
//                    field.setBoolean(null, false); // isRestricted = false;
//                    field.setAccessible(false);
//                }
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			} catch (NoSuchFieldException e) {
//				e.printStackTrace();
//			}

		}

		getGetPasswordPath();

	}

	public static OneMethod getOneMethod() {
		if(method == null)
			method = new OneMethod();
		return method;
	}
	
	public static String getGetPasswordPath() throws NullPointerException{
		try{
			if(getPasswordPath == null){
				getPasswordPath = new File(OneMethod.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getAbsolutePath().replaceAll("\\\\", "/");
				getPasswordPath = getPasswordPath.substring(0, getPasswordPath.lastIndexOf("/")+1);
				getPasswordPath = getPasswordPath.replaceAll("\\\\", "/");
			}
		}catch(Exception io){
//			System.err.println("GetPassword Path does'nt recognize the Opening Path...." + io);
			getPasswordPath = System.getProperty("user.dir").replaceAll("\\\\", "/");
		}finally {
			copyResourceDirectories();
//			System.setProperty("log4j.properties", getPasswordPath+"log4j.properties");
			System.setProperty("-Dlog4j.configuration", "file:" + getPasswordPath + "log4j.properties");
		}
		return getPasswordPath;
	}

	public static String getJarName(){
		String jarName = null;
		try {
			jarName = new File(OneMethod.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getName();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return jarName;
	}

	public static boolean isBouncyCastleAdded() {
		for(Provider provider : Security.getProviders()){
			if(provider.getInfo().contains("BouncyCastle Security Provider"))
				return true;
		}
		return false;
	}

	public void exitPasswordManager() {

		if(kbHook != null)
			kbHook.shutdownHook();
		kbHook = null;

		if(mouseHook != null)
			mouseHook.shutdownHook();
		mouseHook = null;

		System.exit(0);
	}

	public static void hidePasswordManager() {
		PasswordManager.setIllustrating(false);
	}

	private void requestVisibility() {

		logger.info("PasswordManager visibility Requested.");

		if(GetPassword.getInstance().isSessionActive()){
			
			PasswordManager.setIllustrating(true);
			passwordManager.setAlwaysOnTop(true);
			passwordManager.requestFocusOnTable();

		}else{

			if(!PasswordVerifier.isIllustrating()){
				try (AbstractApplicationContext context =
							 new AnnotationConfigApplicationContext(SpringAppConfig.class)) {
					context.getBean(PasswordVerifier.class);
				}
			}
		}

	}

	private static PasswordManager getPasswordManager() {
		if(passwordManager == null)
			passwordManager = PasswordManager.getPasswordManager();
		return passwordManager;
	}

	static boolean isPasswordManagerAvailable() {
		passwordManager = getPasswordManager();
		return passwordManager.isVisible();
	}

	public final synchronized static List<String> getFileLines(String path) {

		List<String> lines = Collections.emptyList();

		try {

			lines = Files.readAllLines(Paths.get(getFilePath(path)), StandardCharsets.UTF_8);

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return lines;

	}

	public final List<char[]> getValidatorResource() {

		List<char[]> resourceList = new ArrayList<>();

		PropertiesUtil utils = null;
		try {
			utils = new PropertiesUtil("/security/validator.steps");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		String validatorResource = utils.getProperty("validatorResource");
		String validatorSecretKey = utils.getProperty("validatorSecretKey");
		String validatorBackup = utils.getProperty("validatorBackup");
		String validatorResourceName = utils.getProperty("ResourceName");
		String validatorBackupName = utils.getProperty("BackupName");

		resourceList.add(new String(Base64.decode(validatorResource)).toCharArray());
		resourceList.add(new String(Base64.decode(validatorSecretKey)).toCharArray());
		resourceList.add(new String(Base64.decode(validatorBackup)).toCharArray());
		resourceList.add(new String(Base64.decode(validatorResourceName)).toCharArray());
		resourceList.add(new String(Base64.decode(validatorBackupName)).toCharArray());

		return resourceList;
	}

	public final Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}
	
	public final Integer getScreenWidth() {
		return getScreenSize().width;
	}
	
	public final Integer getScreenHeight() {
		return getScreenSize().height;
	}

	public final static boolean isNetConnAvailable() {
		boolean flag = false;
		try {
			final URL url = new URL("https://www.google.com");
			final URLConnection conn = url.openConnection();
			conn.connect();
			flag = true;
		} catch (MalformedURLException e) {
			flag = false;
		} catch (IOException e) {
			flag = false;
		}
		return flag;
	}

	private static Image createImage(String path, String description) {
		return (new ImageIcon(getFilePath(path), description)).getImage();
	}

	public static void loadTrayIcon(){
		try{

			Runnable runnable = new Runnable(){
				public void run(){

					if(passwordManager == null)
						passwordManager = PasswordManager.getPasswordManager();

					kbHook = new GlobalKeyboardHook();
					kbHook.addKeyListener(new GlobalKeyAdapter(){
						@Override
						public void keyPressed(GlobalKeyEvent evt){
							if(!passwordManager.isVisible()) {
								if(evt.isControlPressed() && evt.isShiftPressed() && evt.isMenuPressed()
										&& evt.getVirtualKeyCode() == GlobalKeyEvent.VK_P){
									method.requestVisibility();
								}
							}
						}
					});

					mouseHook = new GlobalMouseHook();
					mouseHook.addMouseListener(new GlobalMouseAdapter() {
						@Override
						public void mousePressed(GlobalMouseEvent evt)  {
							if(!isPasswordManagerAvailable()) {
								if(evt.getButton() == GlobalMouseEvent.BUTTON_LEFT && (evt.getX() > screenSize.width - 10)) {
									isPressed = true;
									trackMouseMovementTimer();
								}
							}
						}

						@Override
						public void mouseReleased(GlobalMouseEvent evt)  {
							if(!isPasswordManagerAvailable() && isPressed) {
								if(evt.getButton() == GlobalMouseEvent.BUTTON_LEFT &&
										(evt.getX() > (int)(screenSize.width / 2) && evt.getX() < (screenSize.width - 150))) {
									isTimerRunning = false;
									method.requestVisibility();
									isPressed = false;
								}
							}
						}
					});

					//Check the SystemTray support
					if (!SystemTray.isSupported()) {
//						System.err.println("SystemTray is not supported");
						return;
					}
					final SystemTray tray = SystemTray.getSystemTray();

					trayIcon = new TrayIcon(createImage("/icons/add-user-32.png", "tray icon"), "Password Manager");
					trayIcon.setImageAutoSize(true);

					trayIcon.addMouseListener(new MouseAdapter(){
						@Override
						public void mouseClicked(MouseEvent evt){
							if(evt.getClickCount() == 2){
								method.requestVisibility();
							}
						}
					});

					try {
						tray.add(trayIcon);
					} catch (AWTException e) {
//						System.out.println("Password Manager tray icon could not be added.");
						return;
					}
				}
			};

			SwingUtilities.invokeLater(runnable);

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private static void copyResourceDirectories(){

		if(isWindowOperatingSystem()) {
//			System.out.println("GetPassword path is : " + getPasswordPath);

			if (!checkFileExplicitly(getPasswordPath + "/config", false)) {

				ClassLoader classLoader = OneMethod.class.getClassLoader();
				InputStream stream = classLoader.getResourceAsStream("build/ResourceList");
				List<String> lines = new ArrayList<>();

				try (BufferedReader bir = new BufferedReader(new InputStreamReader(stream, "UTF-8"))){

					for (String line; (line = bir.readLine()) != null;)
						lines.add(line);

					stream.close();

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					copyResourceDirectories(lines);
				}

				new Thread(new Runnable() {
					@Override
					public void run() {
						configureFilePath();
					}
				}).start();

			}

			deleteUnusedExternalResources();
			resourceDirCreated = true;

		}

	}

	private static void copyResourceDirectories(List<String> lines){

		if (Files.isWritable(Paths.get(getPasswordPath))) {
			for(String resourceName : lines){

				if(!resourceName.contains("build")){//don't want to copy the build and its resources
					if(Arrays.asList(resourceDirs).contains(resourceName)){//this is a directory
						createDirectory(getPasswordPath + resourceName);
					}else{//resource is a file
						copyResource("/" + resourceName, getPasswordPath + resourceName);
					}
				}

			}

		}
	}

	private static void createDirectory(String destination){
		File file = new File(destination);
		if(!file.exists())
			file.mkdir();
	}

	private static void copyResource(String source, String destination){

		try {

			URL url = OneMethod.class.getResource(source.replaceAll("\\\\", "/"));
			InputStream inputStream = url.openStream();
			Path resource = Paths.get(destination.replaceAll("\\\\", "/"));

			Files.write(resource, IOUtils.toByteArray(inputStream));

			if(source.contains("bcprov-jdk15on-1.58.jar")){
				copyBCTOJRE();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void copyBCTOJRE(){

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					if(isWindowOperatingSystem()) {

						String jceProviderPath = (System.getProperty("java.home") + "/lib/ext/").replaceAll("\\\\", "/");

						if(Files.isWritable(Paths.get(jceProviderPath))){

							File srcFile = new File(getFilePath("security/bcprov-jdk15on-1.58.jar"));
							File destFile = new File(jceProviderPath + "/bcprov-jdk15on-1.58.jar");

							if(!destFile.exists())
								FileSystemUtils.copyRecursively(srcFile, destFile);

							Thread.sleep(300);

							if(!destFile.exists())
								FileSystemUtils.copyRecursively(srcFile, destFile);

							Thread.sleep(300);

							if(!destFile.exists())
								new MessageUtil("Please Check path, " + jceProviderPath + ", if failed, Copy it Manually and Restart.", MessageUtil.LEVEL.CRITICAL);

						}else{
							new MessageUtil("Please Copy \"security/bcprov-jdk15on-1.58.jar\" manually and Try Again!", MessageUtil.LEVEL.CRITICAL);
						}
					}

				} catch(InterruptedException iex){
					iex.printStackTrace();
				} catch(Exception exc){
					exc.printStackTrace();
				}
			}
		}).start();

	}

	private static void buildConfigureBat(String jarName){

		Path path = Paths.get(getPasswordPath + "configurer.bat");
		Charset charset = StandardCharsets.UTF_8;

		String content = null;
		try {
			content = new String(Files.readAllBytes(path), charset);
			content = content.replaceAll("getJarName", jarName)
						.replaceAll("getPasswordPath", getPasswordPath);

//			System.out.println(content);

			Files.write(path, content.getBytes(charset));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void deleteUnusedExternalResources(){

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					Thread.sleep(500);

					File file = new File(getFilePath("log4j.properties"));
					if(file.exists())
						file.delete();

					Thread.sleep(500);

					file = new File(getFilePath("configurer.bat"));
					if(file.exists())
						file.delete();

				}catch(InterruptedException iex){
					iex.printStackTrace();
				}
			}
		}).start();

	}

	private static void configureFilePath() {

		try {

			PropertiesUtil util = new PropertiesUtil("log4j.properties");
			util.setProperty("log4j.appender.getpassword.File",
					(getPasswordPath + "loggings/trace.info").replaceAll("\\\\", "/"));

			String jarName = getJarName();

			//Copy Extracted File to inside JAR or Project File
			if(!jarName.equals("classes")) {

				buildConfigureBat(jarName);

//				String cmd = "javaw -jar app-support.jar " + getPasswordPath + "configurer.bat";
//				Runtime.getRuntime().exec("cmd /c start " + cmd);

				System.exit(0);

			}

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}

	public static String getFilePath(String filePath) {

		String path = null;

		try {

			while(!resourceDirCreated){
				try{
					Thread.sleep(100);
				}catch(InterruptedException iex){
					iex.printStackTrace();
				}
			}

			path = getPasswordPath + filePath;

			logger.info("getFilePath : " + path);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return path;

	}

	private static boolean isWindowOperatingSystem(){
		return System.getProperty("os.name").startsWith("Win");
	}

	private static boolean checkFileExplicitly(final String fileFullPath, final boolean isFile){
		boolean flag = false;
		File file = new File(fileFullPath);
		if(isFile && file.isFile())//is a file
			flag = true;
		if(!isFile && file.isDirectory())//is a directory
			flag = true;
		return flag;
	}

	private static void trackMouseMovementTimer() {
		if(!isTimerRunning) {

			countForReleased = 0;

//			System.out.println("PasswordManager Mouse Pressed Listening 4");
			TimerTask task = new TimerTask(){
				@Override
				public void run(){
					if(isPressed) {
						if(++countForReleased > 60) {
							isPressed = false;
//							System.out.println("MouseReleased Movement Exceeded 2 second time span...hence closing listening...");
						}
					}else {
						isTimerRunning = false;
						this.cancel();
					}
				}
			};

			Timer timer = new java.util.Timer();
			isTimerRunning = true;
			timer.schedule(task, 0, 25);
		}
	}

	public final static int getSessionForSeconds(){

		PropertiesUtil utils = null;
		int sessionForSeconds = 0;

		try {
			utils = new PropertiesUtil("/config/primary.properties");
			sessionForSeconds = Integer.parseInt(utils.getProperty("sessionAliveInSeconds"));
//			System.out.println("OneMethod sessionForSeconds : " + sessionForSeconds);
		} catch (URISyntaxException urise) {
			urise.printStackTrace();
		}

		return sessionForSeconds;

	}

	public final static String getPropertyValue(String filePath, String propertyKey){

		PropertiesUtil utils = null;
		String result = null;

		try {
			utils = new PropertiesUtil(filePath);
			result = utils.getProperty(propertyKey);
		} catch (URISyntaxException urise) {
			urise.printStackTrace();
		}

		return result;

	}

	public final static void setPropertyValue(String filePath, String propertyKey, String property){

		PropertiesUtil utils = null;

		try {
			utils = new PropertiesUtil(filePath);
			utils.setProperty(propertyKey, property);
		} catch (URISyntaxException urise) {
			urise.printStackTrace();
		}

	}

	public static String getIPV6Address(){

		String ip = null;
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				// filters out 127.0.0.1 and inactive interfaces
				if (iface.isLoopback() || !iface.isUp())
					continue;

				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while(addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();

					// *EDIT*
					if (addr instanceof Inet6Address) continue;

					ip = addr.getHostAddress();
//					System.out.println(iface.getDisplayName() + " " + ip);
				}
			}
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}

		return ip;
	}

	public final static String getCurrentDateTime(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
	}

	public final static String getOSName(){
		return System.getProperty("os.name");
	}

	public final static String getCountryAndLanguage(){
		String value = System.getProperty("user.country");
		value += (", " + System.getProperty("user.language"));
		return value;
	}

}
