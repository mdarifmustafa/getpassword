package com.whodesire.manager;

import com.whodesire.util.MessageUtil;
import com.whodesire.util.OneMethod;

import java.util.Timer;
import java.util.TimerTask;

public class GetPassword {

	private static boolean sessionActive;
	private static char[] secretPassword;
	private static int countTimer;
	private static int sessionForSeconds;

	private static GetPassword getPassword;

	private GetPassword (){}

	public final static synchronized GetPassword getInstance(){
		if(getPassword == null)
			getPassword = new GetPassword();
		return getPassword;
	}

	public final char[] getPassword() throws NullPointerException {
		return secretPassword;
	}

	public final void setPassword(char[] password) {

		secretPassword = password;
		
		setSessionActive(true);
		
		sessionForSeconds = OneMethod.getSessionForSeconds();
		countTimer = 0;
		
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				
				if(!(countTimer++ < sessionForSeconds)){

					secretPassword = "SessionExpired".toCharArray();
					setSessionActive(false);

					new MessageUtil("Secret Password Session Expired, You can extend through Settings.", MessageUtil.LEVEL.WARNING);

					cancel();

				}

				if(!PasswordManager.isIllustrating()){
					secretPassword = "SessionExpired".toCharArray();
					setSessionActive(false);
					countTimer = 0;
					cancel();
				}
				
			}
		};

		Timer timer = new Timer("timer");
		timer.schedule(task, 0, 1000);
		
	}

	public final boolean isSessionActive() {
		return GetPassword.sessionActive;
	}

	public void setSessionActive(boolean sessionActive) {

		GetPassword.sessionActive = sessionActive;

		if(!sessionActive && PasswordManager.isIllustrating()) {
			PasswordManager.setIllustrating(true);
		}
		
	}

}
