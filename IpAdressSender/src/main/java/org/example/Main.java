package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Main {

    private static String emailAddress;
    private static String emailPassword;
    // For more information look - https://support.google.com/accounts/answer/185833?visit_id=638020694196511756-3824924280&p=InvalidSecondFactor&rd=1
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";

    private static String ipAddress = "0.0.0.0";
    private final static String ipAPIChecker = "https://api.ipify.org/?format=json";

    public static void main(String[] args) throws InterruptedException {
        Scanner in = new Scanner(System.in);
        System.out.println("Please enter your email address: ");
        emailAddress = in.nextLine();
        System.out.println("Please enter your email password: ");
        emailPassword = in.nextLine();
        System.out.println("Thank you.");
        System.out.println("Your email is - " + emailAddress);
        System.out.println("Your password is - " + emailPassword);

        while (true) {
            String ipFromAPI = getYourIpAddress();
            if (!ipAddress.equals(ipFromAPI)) {
                sendMail(ipFromAPI);
                ipAddress = ipFromAPI;
            }
            else {
                System.out.println("Your IP address is the same!");
            }

            TimeUnit.MINUTES.sleep(30);
        }

    }

    private static String getYourIpAddress() {

        String result = callApi();
        JSONObject rawResult = new JSONObject(result);
        return rawResult.getString("ip");
    }

    private static String callApi() {

        try {
            URL url = new URL(Main.ipAPIChecker);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder inputData = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                inputData.append(inputLine);
            }
            return inputData.toString();
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Error happened. API error." + ANSI_RESET);
            System.out.println(e.getMessage());
            System.exit(0);
            return "";
        }

    }

    private static void sendMail(String ipAddress) {

        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(emailAddress, emailPassword);

            }

        });

        // Used to debug SMTP issues
        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(emailAddress));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress));

            // Set Subject: header field
            message.setSubject("Your IP adress has been changed!");

            // Now set the actual message
            message.setText("Your new IP adress now is: " + ipAddress);

            System.out.println("sending...");
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }

    }
}
