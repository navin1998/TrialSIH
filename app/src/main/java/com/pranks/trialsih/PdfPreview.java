package com.pranks.trialsih;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.pranks.trialsih.R;

import java.io.File;

import static androidx.core.content.FileProvider.getUriForFile;

public class PdfPreview extends AppCompatActivity {
    PDFView pdfView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("PDF Preview");

        setContentView(R.layout.activity_pdf_preview);
        pdfView=findViewById(R.id.pdfView);

        Intent i=getIntent();
        String path=i.getStringExtra("path");
        String password=i.getStringExtra("password");

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
}
