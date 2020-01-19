package com.pranks.trialsih.doctorActivities.bottomNavigation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import android.os.Environment;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.pranks.trialsih.PdfPreview;
import com.pranks.trialsih.PdfPreview;
import com.pranks.trialsih.R;
import com.pranks.trialsih.doctorActivities.bottomNavigation.JavaApiDemo;
import com.pranks.trialsih.doctorActivities.bottomNavigation.ListViewAdapter;
import com.pranks.trialsih.doctorDBHelpers.DoctorCredentialsDBHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static androidx.core.content.FileProvider.getUriForFile;

public class editable_voice_pres extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 234;
    private static final int PICK_PDF_CODE =2342;
    public View voiceedit;
    ArrayList<String> Symptoms=new ArrayList<>();
    ArrayList<String> Diagnose=new ArrayList<>();
    ArrayList<String> Prescription=new ArrayList<>();
    ArrayList<String> Advice=new ArrayList<>();
    String nameofpat,agesex;
    EditText nameofPerson_edit;
    EditText agesex_edit;
    EditText date;
    ListView listSymptoms;
    ListView listDiagnose;
    ListView listPrescri;
    ListView listAdvice;

    ListViewAdapter adaptersymp,adapterdiag,adapterprescri,adapteradvice;
    private PdfPCell cell;
    private String textAnswer;
    Image bgImage1;
    Image signImage;
    private String path;
    private File dir;
    private File file;
    Button pdfgen;
    public String pathtoupload;
    public StorageReference storageReference;
    public DatabaseReference myReference;
    String email2;
    static final int PICK_CONTACT_REQUEST = 1;
    private static String USER_PASS =""+(int)Math.floor(Math.random()*(1000000-100000)+100000);
    PopupWindow mPopupWindow;
    NestedScrollView scrollView;



    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String PROFILE = "profile";
    private static String REG_NUMBER;



    //use to set background color
    BaseColor myColor = WebColors.getRGBColor("#9E9E9E");
    BaseColor myColor1 = WebColors.getRGBColor("#757575");
    BaseColor backTop = WebColors.getRGBColor("#48CDDF");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        voiceedit = inflater.inflate(R.layout.fragment_editable_voice_pres, container, false);
        Bundle bundle=getArguments();
        nameofpat=bundle.getString("nameofpat");
        agesex=bundle.getString("ageandsex");
        Symptoms=bundle.getStringArrayList ("listSymptoms");
        Diagnose=bundle.getStringArrayList ("listdiagnose");
        Prescription=bundle.getStringArrayList ("listPrescription");
        Advice=bundle.getStringArrayList ("listAdvice");
        //firebase variables
        storageReference=FirebaseStorage.getInstance().getReference();
        myReference=FirebaseDatabase.getInstance().getReference();
        scrollView=voiceedit.findViewById(R.id.scrollLayout);
        pdfgen=voiceedit.findViewById(R.id.generatepdf);
        nameofPerson_edit=voiceedit.findViewById(R.id.patient_name_dis);
        nameofPerson_edit.setText(nameofpat);
        agesex_edit=voiceedit.findViewById(R.id.patient_age_dis);
        agesex_edit.setText(agesex);
        date=voiceedit.findViewById(R.id.patient_date_dis);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        date.setText(sdf.format(Calendar.getInstance().getTime()));

        listSymptoms =voiceedit.findViewById(R.id.list_symptoms);
        listSymptoms.setItemsCanFocus(true);
        adaptersymp=new ListViewAdapter(getContext(),Symptoms);
        listSymptoms.setAdapter(adaptersymp);

        listDiagnose =voiceedit.findViewById(R.id.list_diagnose);
        listDiagnose.setItemsCanFocus(true);
        adapterdiag=new ListViewAdapter(getContext(),Diagnose);
        listDiagnose.setAdapter(adapterdiag);

        listPrescri =voiceedit.findViewById(R.id.list_prescriptions);
        listPrescri.setItemsCanFocus(true);
        adapterprescri=new ListViewAdapter(getContext(),Prescription);
        listPrescri.setAdapter(adapterprescri);

        listAdvice =voiceedit.findViewById(R.id.list_advice);
        listAdvice.setItemsCanFocus(true);
        adapteradvice=new ListViewAdapter(getContext(),Advice);
        listAdvice.setAdapter(adapteradvice);


        //getting registration number of doctor...
        DoctorCredentialsDBHelper dbHelper = new DoctorCredentialsDBHelper(getContext());
        REG_NUMBER = dbHelper.getRegNumber();


        //creating new file path
        path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/PdfFiles";
        dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        pdfgen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE },8865);
                    }

                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View customView = inflater.inflate(R.layout.customsignature,null);

                    /*
                    mPopupWindow = new PopupWindow(
                            customView,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

                    // Set an elevation value for popup window
                    // Call requires API level 21
                    if(Build.VERSION.SDK_INT>=21){
                        mPopupWindow.setElevation(5.0f);
                    }
                    */

                    showSignPopUp();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return voiceedit;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        deleteFolder();
    }

    @Override
    public void onStop() {
        super.onStop();

        deleteFolder();
    }

    public void createPDF(Bitmap signBitmap, String doctorName, String doctorDegree, String doctorType, String doctorPhone, String clinicName, String clinicAddress, String clinicMail, String patientName, String patientAge, String patientGender) throws FileNotFoundException, DocumentException {

        /**
         *
         *
         *
         */


//        //create document file
//        com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
//            pathtoupload=sdf.format(Calendar.getInstance().getTime()) + "" + Calendar.getInstance().getTimeInMillis() + ".pdf";
//            file = new File(dir, pathtoupload);
//            FileOutputStream fOut = new FileOutputStream(file);
//            PdfWriter writer = PdfWriter.getInstance(doc, fOut);
//
//
//            writer.setEncryption(USER_PASS.getBytes(), USER_PASS.getBytes(),
//                    PdfWriter.ALLOW_PRINTING, PdfWriter.DO_NOT_ENCRYPT_METADATA);
//
//            //open the document
//            doc.open();
//            //create table
//            PdfPTable pt = new PdfPTable(3);
//            pt.setWidthPercentage(100);
//            float[] fl = new float[]{25,30, 45};
//            pt.setWidths(fl);
//            cell = new PdfPCell();
//            cell.setBorder(Rectangle.NO_BORDER);
//
//            //set drawable in cell
////            Drawable myImage = getActivity().getResources().getDrawable(R.drawable.trinity);
////            Bitmap bitmap = ((BitmapDrawable) myImage).getBitmap();
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] bitmapdata = stream.toByteArray();
//            try {
//                bgImage1 = Image.getInstance(bitmapdata);
//                bgImage1.setAbsolutePosition(330f, 642f);
//                cell.addElement(bgImage1);
//                pt.addCell(cell);
//                cell = new PdfPCell();
//                cell.setBorder(Rectangle.NO_BORDER);
//                cell.addElement(new Paragraph(nameofPerson_edit.getText().toString()+"\n"+agesex_edit.getText().toString()));
//
//                cell.addElement(new Paragraph(""));
//                cell.addElement(new Paragraph(""));
//                pt.addCell(cell);
//                cell = new PdfPCell(new Paragraph(""));
//                cell.setBorder(Rectangle.NO_BORDER);
//                pt.addCell(cell);
//
//                PdfPTable pTable = new PdfPTable(1);
//                pTable.setWidthPercentage(100);
//                cell = new PdfPCell();
//                cell.setColspan(1);
//                cell.addElement(pt);
//                pTable.addCell(cell);
//                PdfPTable table = new PdfPTable(3);
//
//                float[] columnWidth = new float[]{6, 30, 30};
//                table.setWidths(columnWidth);
//
//
//                cell = new PdfPCell();
//
//
//                cell.setBackgroundColor(myColor);
//                cell.setColspan(3);
//                cell.addElement(pTable);
//                table.addCell(cell);
//                cell = new PdfPCell(new Phrase(" "));
//                cell.setColspan(3);
//                table.addCell(cell);
//                cell = new PdfPCell();
//                cell.setColspan(3);
//
//                cell.setBackgroundColor(myColor1);
//
//                cell = new PdfPCell(new Phrase("#"));
//                cell.setBackgroundColor(myColor1);
//                table.addCell(cell);
//                cell = new PdfPCell(new Phrase("Parameters"));
//                cell.setBackgroundColor(myColor1);
//                table.addCell(cell);
//                cell = new PdfPCell(new Phrase("Discription"));
//                cell.setBackgroundColor(myColor1);
//                table.addCell(cell);
//
//                //table.setHeaderRows(3);
//                cell = new PdfPCell();
//                cell.setColspan(3);
//
//                table.addCell(String.valueOf(1));
//                table.addCell("Symptoms");
//                String symtomcell="";
//                for(String s12:Symptoms){
//                    symtomcell+=s12+"\n";
//                }
//                table.addCell(symtomcell);
//
//                table.addCell(String.valueOf(2));
//                table.addCell("Diagnosis");
//                String diagnosecell="";
//                for(String s12:Diagnose){
//                    diagnosecell+=s12+"\n";
//                }
//                table.addCell(diagnosecell);
//
//                table.addCell(String.valueOf(3));
//                table.addCell("Prescription");
//                String Prescell="";
//                for(String s12:Prescription){
//                    Prescell+=s12+"\n";
//                }
//                table.addCell(Prescell);
//
//                table.addCell(String.valueOf(2));
//                table.addCell("Advice");
//                String advicecell="";
//                for(String s12:Advice){
//                    advicecell+=s12+"\n";
//                }
//                table.addCell(advicecell);
//
//                PdfPTable ftable = new PdfPTable(3);
//                ftable.setWidthPercentage(100);
//                float[] columnWidthaa = new float[]{30, 10, 30};
//                ftable.setWidths(columnWidthaa);
//                cell = new PdfPCell();
//                cell.setColspan(3);
//                cell.setBackgroundColor(myColor1);
//                cell = new PdfPCell(new Phrase("Signature"));
//                cell.setBorder(Rectangle.NO_BORDER);
//                cell.setBackgroundColor(myColor1);
//                ftable.addCell(cell);
//                cell = new PdfPCell(new Phrase(""));
//                cell.setBorder(Rectangle.NO_BORDER);
//                cell.setBackgroundColor(myColor1);
//                ftable.addCell(cell);
//                cell = new PdfPCell(new Phrase(""));
//                cell.setBorder(Rectangle.NO_BORDER);
//                cell.setBackgroundColor(myColor1);
//                ftable.addCell(cell);
//                cell = new PdfPCell(new Paragraph("By Prank Limited"));
//                cell.setColspan(3);
//                ftable.addCell(cell);
//                cell = new PdfPCell();
//                cell.setColspan(3);
//                cell.addElement(ftable);
//                table.addCell(cell);
//                doc.add(table);
//                doc.close();


                /**
                 *
                 *
                 *
                 */


        com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
        try {

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.medical);
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
            pathtoupload=sdf.format(Calendar.getInstance().getTime()) + "" + Calendar.getInstance().getTimeInMillis() + ".pdf";
            file = new File(dir, pathtoupload);
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter writer = PdfWriter.getInstance(doc, fOut);

            writer.setEncryption(USER_PASS.getBytes(), USER_PASS.getBytes(),
                    PdfWriter.ALLOW_PRINTING, PdfWriter.DO_NOT_ENCRYPT_METADATA);


//            writer.setEncryption(USER_PASS.getBytes(), USER_PASS.getBytes(),
//                    PdfWriter.ALLOW_PRINTING, PdfWriter.DO_NOT_ENCRYPT_METADATA);

            //open the document
            doc.open();


            //create table
            PdfPTable pt = new PdfPTable(2);
            pt.setWidthPercentage(100);
            float[] fl = new float[]{25, 75};
            pt.setWidths(fl);
            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapData = stream.toByteArray();

            try {

                cell.setBackgroundColor(backTop);
                bgImage1 = Image.getInstance(bitmapData);
                bgImage1.setAbsolutePosition(0f, 0f);
                cell.addElement(bgImage1);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                pt.addCell(cell);


                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);


                /**
                 *
                 *
                 */

                Font whiteLarge = new Font(Font.FontFamily.TIMES_ROMAN, 30, Font.NORMAL, BaseColor.WHITE);
                whiteLarge.setStyle(Font.BOLD);

                Font whiteSmall = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.WHITE);

                Font greySmall = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.GRAY);

                Chunk c1 = new Chunk(clinicName, whiteLarge);
                Paragraph p1 = new Paragraph(c1);
                Chunk c2 = new Chunk(clinicAddress, whiteSmall);
                Paragraph p2 = new Paragraph(c2);
                Chunk c3 = new Chunk(clinicMail, whiteSmall);
                Paragraph p3 = new Paragraph(c3);


                /**
                 *
                 *
                 */

                cell.setBackgroundColor(backTop);
                cell.addElement(p1);
                cell.addElement(p2);
                cell.addElement(p3);
                pt.addCell(cell);

                /**
                 *
                 *
                 */


                PdfPTable pt2 = new PdfPTable(2);
                pt2.setWidthPercentage(70);
                fl = new float[]{50, 50};
                pt2.setWidths(fl);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPaddingTop(10);
                cell.setBackgroundColor(WebColors.getRGBColor("#FFFFFF"));

                /**
                 *
                 *
                 *
                 */


                cell.addElement(new Paragraph(doctorName + ", " + doctorDegree));
                cell.addElement(new Paragraph(doctorType));
                cell.addElement(new Paragraph(doctorPhone));
                cell.addElement(new Paragraph("   "));
                pt2.addCell(cell);


                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPaddingTop(10);
                cell.setBackgroundColor(WebColors.getRGBColor("#FFFFFF"));

                String presNumber = "" + ((int)(Math.random() * (1000000 - 10000)) + 10000);

                p1 = new Paragraph("Prescription no.: " + presNumber);
                p1.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(p1);

                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String strDate = dateFormat.format(date);

                p1 = new Paragraph("Date : " + strDate);
                p1.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(p1);

                cell.addElement(new Paragraph("   "));
                cell.addElement(new Paragraph("   "));
                pt2.addCell(cell);


                /**
                 *
                 *
                 *
                 */

                PdfPTable pt3 = new PdfPTable(1);
                pt3.setWidthPercentage(100);
                pt3.setPaddingTop(500f);
                fl = new float[]{100};
                pt3.setWidths(fl);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setBackgroundColor(backTop);
                cell.setFixedHeight(2f);
                pt3.addCell(cell);



                /**
                 *
                 *
                 *
                 */



                //pt4..

                PdfPTable pt4 = new PdfPTable(2);
                pt4.setWidthPercentage(70);
                fl = new float[]{50, 50};
                pt4.setWidths(fl);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setBackgroundColor(WebColors.getRGBColor("#FFFFFF"));

                /**
                 *
                 *
                 *
                 */

                Font greySmallBold = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.GRAY);

                c1 = new Chunk("Patient details: ", greySmallBold);
                p1 = new Paragraph(c1);

                cell.addElement(p1);
                cell.addElement(new Paragraph(patientName));
                cell.addElement(new Paragraph(patientAge + "/" + patientGender));
                cell.addElement(new Paragraph("   "));
                pt4.addCell(cell);


                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setBackgroundColor(WebColors.getRGBColor("#FFFFFF"));

                cell.addElement(new Paragraph("   "));
                cell.addElement(new Paragraph("   "));
                cell.addElement(new Paragraph("   "));
                cell.addElement(new Paragraph("   "));
                pt4.addCell(cell);


                /**
                 *
                 *
                 *
                 */


                PdfPTable pt5 = new PdfPTable(1);
                pt5.setWidthPercentage(100);
                fl = new float[]{100};
                pt5.setWidths(fl);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setBackgroundColor(backTop);
                cell.setFixedHeight(2f);
                pt5.addCell(cell);


                /**
                 *
                 *
                 */


                PdfPTable pt6 = new PdfPTable(3);
                pt6.setWidthPercentage(100);
                fl = new float[]{10, 45, 45};
                pt6.setWidths(fl);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                c1 = new Chunk("#", greySmallBold);
                p1 = new Paragraph(c1);
                p1.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(new Paragraph("   "));
                cell.addElement(p1);
                pt6.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                c1 = new Chunk("Parameters", greySmallBold);
                p1 = new Paragraph(c1);
                p1.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(new Paragraph("   "));
                cell.addElement(p1);
                pt6.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                c1 = new Chunk("Details", greySmallBold);
                p1 = new Paragraph(c1);
                p1.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(new Paragraph("   "));
                cell.addElement(p1);
                pt6.addCell(cell);


                /**
                 *
                 *
                 *
                 */



                PdfPTable pt7 = new PdfPTable(3);
                pt7.setWidthPercentage(100);
                fl = new float[]{10, 45, 45};
                pt7.setWidths(fl);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                p1 = new Paragraph("1.");
                p1.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(p1);
                pt7.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                p1 = new Paragraph("Symptoms");
                p1.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(p1);
                cell.addElement(new Paragraph("   "));
                pt7.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);

                for (String s : Symptoms)
                {
                    p1 = new Paragraph(s.toUpperCase());
                    p1.setAlignment(Element.ALIGN_CENTER);
                    cell.addElement(p1);
                }
                cell.addElement(new Paragraph("   "));
                pt7.addCell(cell);

                /**
                 *
                 *
                 *
                 */


                PdfPTable ptl = new PdfPTable(1);
                ptl.setWidthPercentage(100);
                fl = new float[]{100};
                ptl.setWidths(fl);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setBackgroundColor(WebColors.getRGBColor("#BCC0C4"));
                cell.setFixedHeight(2f);
                ptl.addCell(cell);


                /**
                 *
                 *
                 *
                 */


                PdfPTable pt8 = new PdfPTable(3);
                pt8.setWidthPercentage(100);
                fl = new float[]{10, 45, 45};
                pt8.setWidths(fl);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                p1 = new Paragraph("2.");
                p1.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(p1);
                pt8.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                p1 = new Paragraph("Diagnosis");
                p1.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(p1);
                cell.addElement(new Paragraph("   "));
                pt8.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);

                for (String s : Diagnose)
                {
                    p1 = new Paragraph(s.toUpperCase());
                    p1.setAlignment(Element.ALIGN_CENTER);
                    cell.addElement(p1);
                }
                cell.addElement(new Paragraph("   "));
                pt8.addCell(cell);

                /**
                 *
                 *
                 *
                 */

                PdfPTable pt9 = new PdfPTable(3);
                pt9.setWidthPercentage(100);
                fl = new float[]{10, 45, 45};
                pt9.setWidths(fl);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                p1 = new Paragraph("3.");
                p1.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(p1);
                pt9.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                p1 = new Paragraph("Prescription");
                p1.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(p1);
                cell.addElement(new Paragraph("   "));
                pt9.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);

                for (String s : Prescription)
                {
                    p1 = new Paragraph(s.toUpperCase());
                    p1.setAlignment(Element.ALIGN_CENTER);
                    cell.addElement(p1);
                }
                cell.addElement(new Paragraph("   "));
                pt9.addCell(cell);


                /**
                 *
                 *
                 *
                 */



                PdfPTable pt10 = new PdfPTable(3);
                pt10.setWidthPercentage(100);
                fl = new float[]{10, 45, 45};
                pt10.setWidths(fl);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                p1 = new Paragraph("4.");
                p1.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(p1);
                pt10.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                p1 = new Paragraph("Advice");
                p1.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(p1);
                cell.addElement(new Paragraph("   "));
                pt10.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);

                for (String s : Advice)
                {
                    p1 = new Paragraph(s.toUpperCase());
                    p1.setAlignment(Element.ALIGN_CENTER);
                    cell.addElement(p1);
                }
                cell.addElement(new Paragraph("   "));
                pt10.addCell(cell);



                /**
                 *
                 *
                 */

                // add sign here...


                /*

                PdfPTable ptSign = new PdfPTable(1);
                ptSign.setWidthPercentage(100);
                fl = new float[]{100};
                ptSign.setWidths(fl);


                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);


                stream = new ByteArrayOutputStream();
                signBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                bitmapData = stream.toByteArray();


                signImage = Image.getInstance(bitmapData);
                signImage.setAbsolutePosition(0f, 0f);
                cell.addElement(signImage);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                ptSign.addCell(cell);


                */



                PdfPTable ptSign = new PdfPTable(3);
                ptSign.setWidthPercentage(100);
                fl = new float[]{25,30, 45};
                ptSign.setWidths(fl);
                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);

                //set drawable in cell
                stream = new ByteArrayOutputStream();
                signBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();

                signImage = Image.getInstance(bitmapdata);
                signImage.setAbsolutePosition(330f, 642f);
                cell.addElement(signImage);
                cell.addElement(new Paragraph("Doctor's signature"));
                ptSign.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                cell.addElement(new Paragraph("   "));
                cell.addElement(new Paragraph("   "));
                cell.addElement(new Paragraph("   "));
                ptSign.addCell(cell);

                cell = new PdfPCell(new Paragraph("   "));
                cell.setBorder(Rectangle.NO_BORDER);
                ptSign.addCell(cell);


                /**
                 *
                 *
                 *
                 */

                doc.add(pt);
                doc.add(pt2);
                doc.add(pt3);
                doc.add(pt4);
                doc.add(pt5);
                doc.add(pt6);
                doc.add(ptl);
                doc.add(pt7);
                doc.add(ptl);
                doc.add(pt8);
                doc.add(ptl);
                doc.add(pt9);
                doc.add(ptl);
                doc.add(pt10);
                doc.add(ptl);
                doc.add(ptSign);
                doc.close();


                /***
                 *
                 *
                 *
                 */
                //AlertDialoger(pathtoupload);

                //showPDFPreviewAndSendDialog(pathtoupload);

                showAlertDialog(pathtoupload);

    } catch (Exception e) {
                Log.e("PDFCreator", "ioException:" + e);
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     *
     * method for show custom alert dialog to send and preview pdfs... (below)
     *
     */



    private void showPDFPreviewAndSendDialog(String filepath)
    {

        /**
         *  instantiating variables
         *
         */

        File phonePath = new File(Environment.getExternalStorageDirectory(), "PdfFiles");
        File newFile = new File(phonePath, path);
        Uri contentUri = getUriForFile(getContext(), "com.pranks.trialsih", newFile);


        /**
         *
         *
         */


        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_pdf_preview, null);
        final Button sendBtn = alertLayout.findViewById(R.id.sendpdf);
        final PDFView pdfView = alertLayout.findViewById(R.id.pdfView);


        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(alertLayout);
        builder.setCancelable(false);


        pdfView.fromUri(contentUri)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAnnotationRendering(false)
                .password(USER_PASS)
                .scrollHandle(null)
                .load();



        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "Send button clicked", Toast.LENGTH_SHORT).show();

                inputDialog(filepath);

                dialog.dismiss();
            }
        });

    }






    /**
     *
     *
     *
     */


    /**
     *
     * method for show custom alert dialog for cancel, send or preview (below)
     *
     */



    private void showAlertDialog(String filepath)
    {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_pdf_generated, null);
        final Button sendBtn = alertLayout.findViewById(R.id.btn_send);
        //final Button cancelBtn = alertLayout.findViewById(R.id.btn_cancel);
        final Button previewBtn = alertLayout.findViewById(R.id.btn_preview);


        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(alertLayout);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputDialogForMail(filepath);

                dialog.dismiss();
            }
        });


        previewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getContext(), PdfPreview.class);
                i.putExtra("path",filepath);
                i.putExtra("password",USER_PASS);
                try {
                    i.putExtra("patName", nameofPerson_edit.getText().toString());
                }
                catch (Exception e)
                {
                    i.putExtra("patName", "Anonymous");
                }
                startActivity(i);

                dialog.dismiss();
            }
        });

    }


    private void inputDialogForMail(String filepath)
    {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_ask_for_patient_mail, null);
        final Button sendBtn = alertLayout.findViewById(R.id.btn_send);
        final EditText input = alertLayout.findViewById(R.id.editText);


        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                        Toast.makeText(getContext(), "Enter valid mail ID", Toast.LENGTH_SHORT).show();
                    }
                }
                else {

                    Toast.makeText(getContext(), "Enter valid mail ID", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    /**
     *
     *
     *
     */






    void AlertDialoger(String filePath){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        // Setting Alert Dialog Title
        alertDialogBuilder.setTitle("Pdf generated");
        // Icon Of Alert Dialog
        alertDialogBuilder.setIcon(R.drawable.ic_check_black_24dp);
        // Setting Alert Dialog Message
        alertDialogBuilder.setMessage("Pdf generated successfully");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                //Write the Send File code her

                inputDialog(filePath);

            }
        });

        alertDialogBuilder.setNegativeButton("Preview", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i=new Intent(getContext(), PdfPreview.class);
                i.putExtra("path",filePath);
                i.putExtra("password",USER_PASS);
                startActivity(i);

            }
        });
        alertDialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(),"Canceled",Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void inputDialog(String filepath) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter Your Email");

        final EditText input = new EditText(getContext());

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
        builder.setView(input);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              String email=input.getText().toString();
                Toast.makeText(getContext(), email, Toast.LENGTH_SHORT).show();
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

        String PatientName=nameofPerson_edit.getText().toString();
        String mSubect="Patient's prescription";
        String mMessage="Hii "+PatientName+","+"\n\n"+"Password for this prescription: \n"+string;
        JavaApiDemo javaApiDemo=new JavaApiDemo(getContext(),email,mSubect,mMessage,filePath);
        javaApiDemo.execute();
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


    private void showSignPopUp()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.customsignature, null);

        SignaturePad mSignaturePad=alertLayout.findViewById(R.id.signature_pad);
        Button mClearButton = alertLayout.findViewById(R.id.clear_button);
        Button mSaveButton = alertLayout.findViewById(R.id.save_button);
        
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.show();

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                // signing started...
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });


        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();

                // calling of method...
                getInfos(signatureBitmap);
                dialog.dismiss();
            }
        });

    }


    private void getInfos(Bitmap signBitmap)
    {

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(REG_NUMBER).child(PROFILE);


        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String docName, docDegree, docType, docPhone, clinicName, clinicAddress, docMail, patName, patAge, patGender;


                docName = dataSnapshot.child("doctorName").getValue().toString();
                docDegree = dataSnapshot.child("doctorQualifications").getValue().toString();
                docType = dataSnapshot.child("doctorType").getValue().toString();
                docPhone = dataSnapshot.child("doctorBookingPhone").getValue().toString();
                clinicName = dataSnapshot.child("doctorClinicName").getValue().toString();
                clinicAddress = dataSnapshot.child("doctorClinicAddress").getValue().toString();
                docMail = dataSnapshot.child("doctorMail").getValue().toString();
                patName = nameofPerson_edit.getText().toString();

                try {
                    String tempArr[] = agesex_edit.getText().toString().split("/");
                    patAge = tempArr[0];
                    patGender = tempArr[1];
                    if (patGender.toLowerCase().contains("male")) {
                        patGender = "Male";
                    } else {
                        patGender = "Female";
                    }
                }
                catch (Exception e)
                {
                    patName = "Anonymous";
                    patAge = "xx Years";
                    patGender = "Unknown";
                }

                try {

                    createPDF(signBitmap, docName, docDegree, docType, docPhone, clinicName, clinicAddress, docMail, patName, patAge, patGender);
                }
                catch (Exception e)
                {
                    Snackbar.make(voiceedit, "Error: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



}
