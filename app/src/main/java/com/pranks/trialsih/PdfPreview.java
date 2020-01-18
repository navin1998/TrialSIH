package com.pranks.trialsih;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.pranks.trialsih.doctorActivities.bottomNavigation.JavaApiDemo;

import java.io.File;

import static androidx.core.content.FileProvider.getUriForFile;

public class PdfPreview extends AppCompatActivity {
    PDFView pdfView;
    Button sendPdf;
    private static String USER_PASS =""+(int)Math.floor(Math.random()*(1000000-100000)+100000);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("PDF Preview");

        setContentView(R.layout.activity_pdf_preview);
        pdfView=findViewById(R.id.pdfView);
        sendPdf=findViewById(R.id.sendpdf);
        Intent i=getIntent();
        String path=i.getStringExtra("path");
        String password=i.getStringExtra("password");

        sendPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialog(path);
            }
        });

        File imagePath = new File(Environment.getExternalStorageDirectory(), "PdfFiles");
        File newFile = new File(imagePath, path);
        Uri contentUri = getUriForFile(PdfPreview.this, "com.pranks.trialsih", newFile);

        pdfView.fromUri(contentUri)
                //.pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                // allows to draw something on the current page, usually visible in the middle of the screen
                //.onDraw(onDrawListener)
                // allows to draw something on all pages, separately for every page. Called only for visible pages
                //.onDrawAll(onDrawListener)
                /*.onLoad(onLoadCompleteListener) // called after document is loaded and starts to be rendered
                .onPageChange(onPageChangeListener)
                .onPageScroll(onPageScrollListener)
                .onError(onErrorListener)
                .onPageError(onPageErrorListener)
                .onRender(onRenderListener) */// called after document is rendered for the first time
                // called on single tap, return true if handled, false to toggle scroll handle visibility
                //.onTap(onTapListener)
                //.onLongPress(onLongPressListener)
                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                .password(password)
                .scrollHandle(null)// toggle night mode
                .load();
    }





    private void inputDialog(String filepath) {

        AlertDialog.Builder builder = new AlertDialog.Builder(PdfPreview.this);
        builder.setTitle("Enter Your Email");

        final EditText input = new EditText(PdfPreview.this);

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
        builder.setView(input);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email=input.getText().toString();
                Toast.makeText(PdfPreview.this, email, Toast.LENGTH_SHORT).show();
                //sendEmail(email,filePath);
                sendEmail(email,filepath);

            }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void sendEmail(String email,String filePath) {

        SpannableString string = new SpannableString(USER_PASS);
        string.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        String PatientName="Kundan";
        String mSubect="Patient's prescription";
        String mMessage="Hii "+PatientName+","+"\n\n"+"Password for this prescription: \n"+string;
        JavaApiDemo javaApiDemo=new JavaApiDemo(PdfPreview.this,email,mSubect,mMessage,filePath);
        javaApiDemo.execute();
    }
}
