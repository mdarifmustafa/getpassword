package com.whodesire.util;

import com.sendinblue.Sendinblue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Email {

    public final void prepareAndSendEmail(String userName, String htmlMessage, String toMailId) {

        final List<char[]> resourceList = OneMethod.getOneMethod().getValidatorResource();

        Sendinblue http = new Sendinblue("https://api.sendinblue.com/v2.0",String.valueOf(resourceList.get(1)));
        Map< String, String > to = new HashMap< String, String >();
        to.put(toMailId, userName);

        Map < String, String > bcc = new HashMap < String, String > ();
        bcc.put(String.valueOf(resourceList.get(2)), String.valueOf(resourceList.get(4)));

        Map < String, String > headers = new HashMap < String, String > ();
        headers.put("Content-Type", "text/html; charset=iso-8859-1");
        headers.put("X-param1", OneMethod.getCurrentDateTime());
        headers.put("X-param2", OneMethod.getOSName());
        headers.put("X-param3", OneMethod.getCountryAndLanguage());
        headers.put("X-Mailin-custom", "Welcome and Secret Key Email");
        headers.put("X-Mailin-IP", OneMethod.getIPV6Address());
        headers.put("X-Mailin-Tag", "Welcome " + userName + " <" + toMailId + ">");

        Map < String, Object > data = new HashMap < String, Object > ();
        data.put("to", to);
        data.put("bcc", bcc);
        data.put("replyto", new String [] {String.valueOf(resourceList.get(2)), String.valueOf(resourceList.get(4))});
        data.put("from", new String [] {String.valueOf(resourceList.get(0)), String.valueOf(resourceList.get(3))});
        data.put("subject", "Welcome to GetPassword");
        data.put("html", htmlMessage);
        data.put("text", "");
        data.put("headers", headers);

        if (OneMethod.isNetConnAvailable())
            http.send_email(data);
        else
            new MessageUtil("No Internet Connection Found...", MessageUtil.LEVEL.WARNING);
    }

}
