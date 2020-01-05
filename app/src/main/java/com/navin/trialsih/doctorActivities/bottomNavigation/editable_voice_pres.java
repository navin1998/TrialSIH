package com.navin.trialsih.doctorActivities.bottomNavigation;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.navin.trialsih.R;
import com.navin.trialsih.doctorDBHelpers.DoctorCredentialsDBHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static androidx.core.content.FileProvider.getUriForFile;

public class editable_voice_pres extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 234;
    private static final int PICK_PDF_CODE =2342 ;
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
    ListviewAdapter adaptersymp,adapterdiag,adapterprescri,adapteradvice;
    private PdfPCell cell;
    private String textAnswer;
    Image bgImage1;
    private String path;
    private File dir;
    private File file;
    Button pdfgen;
    public String pathtoupload;
    public StorageReference storageReference;
    public DatabaseReference myReference;
    Uri filePath1;

    //use to set background color
    BaseColor myColor = WebColors.getRGBColor("#9E9E9E");
    BaseColor myColor1 = WebColors.getRGBColor("#757575");

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
        adaptersymp=new ListviewAdapter(getContext(),Symptoms);
        listSymptoms.setAdapter(adaptersymp);

        listDiagnose =voiceedit.findViewById(R.id.list_diagnose);
        listDiagnose.setItemsCanFocus(true);
        adapterdiag=new ListviewAdapter(getContext(),Diagnose);
        listDiagnose.setAdapter(adapterdiag);

        listPrescri =voiceedit.findViewById(R.id.list_prescriptions);
        listPrescri.setItemsCanFocus(true);
        adapterprescri=new ListviewAdapter(getContext(),Prescription);
        listPrescri.setAdapter(adapterprescri);

        listAdvice =voiceedit.findViewById(R.id.list_advice);
        listAdvice.setItemsCanFocus(true);
        adapteradvice=new ListviewAdapter(getContext(),Advice);
        listAdvice.setAdapter(adapteradvice);

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

                    createPDF();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });

        return voiceedit;
    }

    public void createPDF() throws FileNotFoundException, DocumentException {
        //create document file
        com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
        try {

            Log.e("PDFCreator", "PDF Path: " + path);
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
            pathtoupload=sdf.format(Calendar.getInstance().getTime()) + "" + Calendar.getInstance().getTimeInMillis() + ".pdf";
            file = new File(dir, pathtoupload);
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter writer = PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();
            //create table
            PdfPTable pt = new PdfPTable(3);
            pt.setWidthPercentage(100);
            float[] fl = new float[]{15,30, 45};
            pt.setWidths(fl);
            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);

            //set drawable in cell
            Drawable myImage = getActivity().getResources().getDrawable(R.drawable.trinity);
            Bitmap bitmap = ((BitmapDrawable) myImage).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();
            try {
                bgImage1 = Image.getInstance(bitmapdata);
                bgImage1.setAbsolutePosition(330f, 642f);
                cell.addElement(bgImage1);
                pt.addCell(cell);
                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                cell.addElement(new Paragraph(nameofPerson_edit.getText().toString()+"\n"+agesex_edit.getText().toString()));

                cell.addElement(new Paragraph(""));
                cell.addElement(new Paragraph(""));
                pt.addCell(cell);
                cell = new PdfPCell(new Paragraph(""));
                cell.setBorder(Rectangle.NO_BORDER);
                pt.addCell(cell);

                PdfPTable pTable = new PdfPTable(1);
                pTable.setWidthPercentage(100);
                cell = new PdfPCell();
                cell.setColspan(1);
                cell.addElement(pt);
                pTable.addCell(cell);
                PdfPTable table = new PdfPTable(3);

                float[] columnWidth = new float[]{6, 30, 30};
                table.setWidths(columnWidth);


                cell = new PdfPCell();


                cell.setBackgroundColor(myColor);
                cell.setColspan(3);
                cell.addElement(pTable);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" "));
                cell.setColspan(3);
                table.addCell(cell);
                cell = new PdfPCell();
                cell.setColspan(3);

                cell.setBackgroundColor(myColor1);

                cell = new PdfPCell(new Phrase("#"));
                cell.setBackgroundColor(myColor1);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Parameters"));
                cell.setBackgroundColor(myColor1);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Discription"));
                cell.setBackgroundColor(myColor1);
                table.addCell(cell);

                //table.setHeaderRows(3);
                cell = new PdfPCell();
                cell.setColspan(3);

                table.addCell(String.valueOf(1));
                table.addCell("Symptoms");
                String symtomcell="";
                for(String s12:Symptoms){
                    symtomcell+=s12+"\n";
                }
                table.addCell(symtomcell);

                table.addCell(String.valueOf(2));
                table.addCell("Diagnosis");
                String diagnosecell="";
                for(String s12:Diagnose){
                    diagnosecell+=s12+"\n";
                }
                table.addCell(diagnosecell);

                table.addCell(String.valueOf(3));
                table.addCell("Prescription");
                String Prescell="";
                for(String s12:Prescription){
                    Prescell+=s12+"\n";
                }
                table.addCell(Prescell);

                table.addCell(String.valueOf(2));
                table.addCell("Advice");
                String advicecell="";
                for(String s12:Advice){
                    advicecell+=s12+"\n";
                }
                table.addCell(advicecell);

                PdfPTable ftable = new PdfPTable(3);
                ftable.setWidthPercentage(100);
                float[] columnWidthaa = new float[]{30, 10, 30};
                ftable.setWidths(columnWidthaa);
                cell = new PdfPCell();
                cell.setColspan(3);
                cell.setBackgroundColor(myColor1);
                cell = new PdfPCell(new Phrase("Signature"));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setBackgroundColor(myColor1);
                ftable.addCell(cell);
                cell = new PdfPCell(new Phrase(""));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setBackgroundColor(myColor1);
                ftable.addCell(cell);
                cell = new PdfPCell(new Phrase(""));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setBackgroundColor(myColor1);
                ftable.addCell(cell);
                cell = new PdfPCell(new Paragraph("By Prank Limited"));
                cell.setColspan(3);
                ftable.addCell(cell);
                cell = new PdfPCell();
                cell.setColspan(3);
                cell.addElement(ftable);
                table.addCell(cell);
                doc.add(table);
                doc.close();


                //upload it to firebase
                //Toast.makeText(getContext(), pathtoupload, Toast.LENGTH_SHORT).show();

                File imagePath = new File(Environment.getExternalStorageDirectory(), "PdfFiles");
                File newFile = new File(imagePath, pathtoupload);
                Uri contentUri = getUriForFile(getContext(), "com.navin.trialsih", newFile);
                uploadFile(contentUri);

            } catch (Exception e) {
                Log.e("PDFCreator", "ioException:" + e);
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    //upload File
    private void uploadFile(final Uri filePath) {
        final DoctorCredentialsDBHelper doccre=new DoctorCredentialsDBHelper(getContext());
        //checking if file is available
        Toast.makeText(getContext(), filePath.toString(), Toast.LENGTH_SHORT).show();
        if (filePath != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Creating PDF");
            progressDialog.show();

            //getting the storage reference
            final StorageReference sRef = storageReference.child(doccre.getRegNumber()+ "/" + System.currentTimeMillis() + "." + nameofpat);

            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            //displaying success toast
//                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                            //creating the upload object to store uploaded image details
                            sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMyyyy");
                                    myReference.child("doctors").child(doccre.getRegNumber()).child("patientPrescriptions").child(nameofpat+sdf1.format(Calendar.getInstance().getTime())+Calendar.getInstance().getTimeInMillis()).setValue(uri.toString());
                                    AlertDialoger(uri);
                                }
                            });
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), /*exception.getMessage()*/"KKJ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Creating " + ((int) progress) + "%...");
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Please select a file", Toast.LENGTH_SHORT).show();
        }
    }

    void AlertDialoger(final Uri filePath2){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        // Setting Alert Dialog Title
        alertDialogBuilder.setTitle("Pdf Generated");
        // Icon Of Alert Dialog
        alertDialogBuilder.setIcon(R.drawable.ic_check_black_24dp);
        // Setting Alert Dialog Message
        alertDialogBuilder.setMessage("Pdf Generated");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                //Write the Send File code here

            }
        });

        alertDialogBuilder.setNegativeButton("Preview", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, filePath2);
                startActivity(browserIntent);
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
}
