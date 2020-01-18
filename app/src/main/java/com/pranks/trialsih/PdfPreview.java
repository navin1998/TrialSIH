package com.pranks.trialsih;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.snackbar.Snackbar;
import com.pranks.trialsih.doctorActivities.bottomNavigation.JavaApiDemo;

import java.io.File;
import java.util.regex.Pattern;

import static androidx.core.content.FileProvider.getUriForFile;

public class PdfPreview extends AppCompatActivity {
    PDFView pdfView;
    Button sendPdf;

    private boolean canPressBack = false;

    private String patientName;

    private static String USER_PASS;
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

        USER_PASS = i.getStringExtra("password");

        patientName = i.getStringExtra("patName");

        sendPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialogForMail(path);
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

        canPressBack = true;

        SpannableString string = new SpannableString(USER_PASS);
        string.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        String mSubect="Patient's prescription";
        String mMessage="Hii " + patientName + ",\n\nPassword for this prescription is: " + USER_PASS;
        JavaApiDemo javaApiDemo=new JavaApiDemo(PdfPreview.this,email,mSubect,mMessage,filePath);
        javaApiDemo.execute();
    }


    private void inputDialogForMail(String filepath)
    {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_ask_for_patient_mail, null);
        final Button sendBtn = alertLayout.findViewById(R.id.btn_send);
        final EditText input = alertLayout.findViewById(R.id.editText);


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(alertLayout);
        builder.setCancelable(true);

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dEst, int dStart, int dEnd) {
                for (int i = start; i < end; ++i)
                {
                    if (!Pattern.compile("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@1234567890_.]*").matcher(String.valueOf(source.charAt(i))).matches())
                    {
                        return "";
                    }
                }

                return null;
            }
        };

        input.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(50)});

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);


        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String testName = input.getText().toString().trim();
                testName = testName.replaceAll(" ", "");

                if ((testName.length() > 0))
                {
                    if (Patterns.EMAIL_ADDRESS.matcher(testName).matches())
                    {

                        sendEmail(input.getText().toString().toLowerCase(), filepath);

                        dialog.dismiss();
                    }
                    else
                    {
                        Toast.makeText(PdfPreview.this, "Enter valid mail ID", Toast.LENGTH_SHORT).show();
                    }
                }
                else {

                    Toast.makeText(PdfPreview.this, "Enter valid mail ID", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @Override
    public void onBackPressed() {
        if (canPressBack)
        {
            deleteFolder();
            super.onBackPressed();
        }
        else
        {
            Snackbar.make(getWindow().getDecorView().getRootView(), "This operation is only allowed after you send this mail", Snackbar.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        deleteFolder();
    }


    @Override
    protected void onStop() {
        super.onStop();

        deleteFolder();
    }

    void deleteFolder(){
        // Your directory with files to be deleted
        String sdcard = Environment.getExternalStorageDirectory() + "/PdfFiles";

// go to your directory
        File fileList = new File( sdcard );

//check if dir is not null
        if (fileList != null) {

            // so we can list all files
            File[] filenames = fileList.listFiles();

            // loop through each file and delete
            for (File tmpf : filenames) {
                tmpf.delete();
            }

        }
    }

}
