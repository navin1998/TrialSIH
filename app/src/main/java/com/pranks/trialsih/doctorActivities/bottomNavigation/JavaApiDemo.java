package com.pranks.trialsih.doctorActivities.bottomNavigation;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

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
        mProgressDialog = ProgressDialog.show(mContext,"Sending", "Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismiss progress dialog when message successfully send
        mProgressDialog.dismiss();

        //Show success toast
        Toast.makeText(mContext,"Mail sent to: " + mEmail,Toast.LENGTH_LONG).show();
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

            //Adding Message
//            Log.i("message",mMessage);
            if(filepath.length()==0 || filepath.isEmpty() || filepath==null){
                mm.setText(mMessage);
                Transport.send(mm);
            }
            else {
                sendAttachment(mm,mMessage);
                Transport.send(mm);
            }

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendAttachment(MimeMessage mm,String mMessage) {
        try{
            BodyPart messageBodyPart=new MimeBodyPart();
            messageBodyPart.addHeader("Content-type", "text/HTML; charset=UTF-8");
            messageBodyPart.setText(mMessage);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(mMessage);
            String file = Environment.getExternalStorageDirectory().getAbsolutePath()+"/PdfFiles/"+filepath;
            String fileName = filepath;
            DataSource source = new FileDataSource(file);

            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(fileName);
            multipart.addBodyPart(messageBodyPart);
            mm.setContent(multipart);

        }
        catch (Exception e){
            Toast.makeText(mContext, "ERROR : "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}