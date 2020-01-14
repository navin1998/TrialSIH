package com.pranks.trialsih.doctorActivities.bottomNavigation;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaApiDemo extends AsyncTask<Void,Void,Void>  {

    //Add those line in dependencies
    //implementation files('libs/activation.jar')
    //implementation files('libs/additionnal.jar')
    //implementation files('libs/mail.jar')

    //Need INTERNET permission
    //Variables
    private Context mContext;
    private Session mSession;

    private String mEmail;
    private String mSubject;
    private String mMessage;
    private String filepath;

    private ProgressDialog mProgressDialog;

    //Constructor
    public JavaApiDemo(Context mContext, String mEmail, String mSubject, String mMessage,String filepath) {
        this.mContext = mContext;
        this.mEmail = mEmail;
        this.mSubject = mSubject;
        this.mMessage = mMessage;
        this.filepath=filepath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Show progress dialog while sending email
        mProgressDialog = ProgressDialog.show(mContext,"Sending message", "Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismiss progress dialog when message successfully send
        mProgressDialog.dismiss();

        //Show success toast
        Toast.makeText(mContext,"Message Sent",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        mSession = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Utils.EMAIL, Utils.PASSWORD);
                    }
                });

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(mSession);

            //Setting sender address
            mm.setFrom(new InternetAddress(Utils.EMAIL));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(mEmail));
            //Adding subject

            mm.setSubject(mSubject);


//
//            BodyPart messageBodyPart = new MimeBodyPart();
//
//            messageBodyPart.setText(mMessage);
//
//            Multipart multipart = new MimeMultipart();
//
//            multipart.addBodyPart(messageBodyPart);
//
//            MimeBodyPart attachpart = new MimeBodyPart();
//
//            String filePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/pdffiles/02012010.pdf";
////            DataSource source = new FileDataSource(filePath);
////
////            messageBodyPart.setDataHandler(new DataHandler(source));
////
////            messageBodyPart.setFileName(filePath);
//
//
//            attachpart.attachFile(filePath);
//            multipart.addBodyPart(attachpart);
//            mm.setContent(multipart);


            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText("hello");

            // Create a multipart message for attachment
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Second part is attachment
            messageBodyPart = new MimeBodyPart();
            String filename = filepath;
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            mm.setContent(multipart);

            // Send message
            Transport.send(mm);
            Toast.makeText(mContext, "EMail Sent Successfully with attachment!!", Toast.LENGTH_SHORT).show();



        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}