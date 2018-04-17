package com.whodesire.first;

import com.fasterxml.uuid.Generators;
import com.whodesire.util.Email;
import com.whodesire.util.OneMethod;

import java.util.List;

public class WelcomeEmail implements Runnable {

    private String userName;
    private String emailId;
    private char[] UUID;
    private String message;

    public WelcomeEmail(final String userName, final String emailId){
        this.userName = userName;
        this.emailId = emailId;
        this.UUID = generateUUID();
    }

    private char[] generateUUID(){
        //Generate time-based UUID
        java.util.UUID uuid = Generators.randomBasedGenerator().generate();
        return uuid.toString().toCharArray();
    }

    @Override
    public void run() {

        List<String> htmlLines = OneMethod.getFileLines("/mailx/WelcomeEmail.html");

        StringBuffer buffer = new StringBuffer("");

        for(int i = 0; i < htmlLines.size(); i++){

            String line = htmlLines.get(i);

            if(line.contains("{{user}}")){

                String replaced = line.replace("{{user}}", userName);
                htmlLines.set(i, replaced);
                buffer.append(replaced + "\n");

            } else if (line.contains("{{uuid}}")) {

                UUID = generateUUID();
                PasswordVerifier.setUUDI(UUID);

                String replaced = line.replace("{{uuid}}", String.valueOf(UUID));
                htmlLines.set(i, replaced);
                buffer.append(replaced + "\n");

            } else {
                buffer.append(line + "\n");
            }
        }

        message = buffer.toString();
        htmlLines.clear();

        Email email = new Email();
        email.prepareAndSendEmail(userName, message, emailId);

    }

    public void send(){
        Thread thread = new Thread(this);
        thread.start();
    }

}
