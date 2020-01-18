package com.pranks.trialsih.doctorActivities.bottomNavigation;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.pranks.trialsih.BuildConfig;
import com.pranks.trialsih.doctorActivities.bottomNavigation.editable_voice_pres;
import com.pranks.trialsih.R;
import com.pranks.trialsih.doctorActivities.DoctorDashboardActivity;
import com.pranks.trialsih.doctorDBHelpers.NeuralNetworkDBHelper;
import com.pranks.trialsih.patientActivities.bottomNavigation.ChatFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import ai.api.AIServiceContext;
import ai.api.AIServiceContextBuilder;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

public class VoicePresFragment extends Fragment {

    private View voice;
    ImageButton button;
    Button preview;
    SpeechRecognizer mspeechRecog;
    Intent mSpeechRecogInt;
    public TextView nameofperson;
    ArrayList<String> Symptoms=new ArrayList<>();
    ArrayList<String> Diagnose=new ArrayList<>();
    ArrayList<String> Prescription=new ArrayList<>();
    ArrayList<String> Advice=new ArrayList<>();
    //NueralNetworkTrainer objarr=new NueralNetworkTrainer();
    NeuralNetworkDBHelper neuraldb;
    String nameofpat="";
    public TextView date;
    public ListView symptomList;
    public ListView DiagnoseList;
    public ListView PresList;
    public ListView AdviceList;
    public String age="";
    public String sex="";
    TextView agesex;
    private ListView[] listViewArr;

    ArrayList<String> preNames;
    ArrayList<String> preSymptoms;
    ArrayList<String> preDiagnose=new ArrayList<>();
    ArrayList<String> prePrescription;
    ArrayList<String> preAdvice=new ArrayList<>();

    // Android client
    private AIRequest aiRequest;
    private AIDataService aiDataService;
    private AIServiceContext customAIServiceContext;
    private String uuid = UUID.randomUUID().toString();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.RECORD_AUDIO},8865);
        }
        voice = inflater.inflate(R.layout.doctor_fragment_voice_pres, container, false);

        ((DoctorDashboardActivity) getActivity()).getSupportActionBar().setTitle("Voice Pres");
        initChatbot();

        neuraldb = new NeuralNetworkDBHelper(getContext());
        preNames = neuraldb.getNames();
        preSymptoms=neuraldb.getSymptoms();
        prePrescription=neuraldb.getMedicines();
        preDiagnose.add("diabetes");

        nameofperson = voice.findViewById(R.id.patient_name_dis);
        date=voice.findViewById(R.id.patient_date_dis);
        symptomList=voice.findViewById(R.id.list_symptoms);
        DiagnoseList=voice.findViewById(R.id.list_diagnose);
        PresList=voice.findViewById(R.id.list_prescriptions);
        AdviceList=voice.findViewById(R.id.list_advice);
        agesex=voice.findViewById(R.id.patient_age_dis);
        listViewArr=new ListView[]{symptomList,DiagnoseList,PresList,AdviceList};
        preview=voice.findViewById(R.id.edit_button);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        date.setText(sdf.format(Calendar.getInstance().getTime()));
        checkPermission();
        button = voice.findViewById(R.id.speach_button);
        mspeechRecog = SpeechRecognizer.createSpeechRecognizer(getContext());
        mSpeechRecogInt = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecogInt.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecogInt.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());



        mspeechRecog.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {

                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    String recordrvoice=matches.get(0);

                    if(recordrvoice.toLowerCase().trim().contains("delete")||recordrvoice.toLowerCase().trim().contains("deletes")
                            ||recordrvoice.toLowerCase().trim().contains("remove")){
                        String record=recordrvoice.toLowerCase().trim();
                        int index=0;
                        boolean great=true;
                        for(int i=0;i<record.length();i++){
                            if(record.charAt(i)>=48&&record.charAt(i)<=57){
                                index+=Integer.parseInt(Character.toString(record.charAt(i)));
                                great=false;
                            }
                        }

                        if(great){
                            index=IntegerReturner(record);
                        }
                        if(index!=0) {
                            if ((record.contains("symptoms") || record.contains("symptom"))&&Symptoms.size()>=index) {
                                Symptoms.remove(index - 1);
                            }
                            if ((record.contains("diagnose") || record.contains("diagnosis"))&&Diagnose.size()>=index) {
                                Diagnose.remove(index - 1);
                            }
                            if ((record.contains("prescription") || record.contains("prescriptions"))&&Prescription.size()>=index) {
                                Prescription.remove(index - 1);
                            }
                            if ((record.contains("advice") || record.contains("advices")||record.contains("advise")||record.contains("advises"))&&Advice.size()>=index) {
                                Advice.remove(index - 1);
                            }
                        }
                    }
                    else if(recordrvoice.toLowerCase().trim().contains("rewrite")||recordrvoice.toLowerCase().trim().contains("replace")
                            ||recordrvoice.toLowerCase().trim().contains("replaces")){
                        String record=recordrvoice.toLowerCase().trim();
                        int index=0;
                        boolean great=true;
                        for(int i=0;i<record.length();i++){
                            if(record.charAt(i)>=48&&record.charAt(i)<=57){
                                index+=Integer.parseInt(Character.toString(record.charAt(i)));
                                great=false;
                            }
                        }
                        if(great){
                            index=IntegerReturner(record);
                        }
                        if(index!=0) {
                            //Toast.makeText(getContext(), Integer.toString(index), Toast.LENGTH_SHORT).show();
                            String names[] = recordrvoice.split("with");
                            if (names.length >= 2) {
                                if ((record.contains("symptoms") || record.contains("symptom"))&&Symptoms.size()>=index) {
                                    Symptoms.set(index - 1, names[1]);
                                }
                                if ((record.contains("diagnose") || record.contains("diagnosis"))&&Diagnose.size()>=index) {
                                    Diagnose.set(index - 1, names[1]);
                                }
                                if ((record.contains("prescription") || record.contains("prescriptions"))&&Prescription.size()>=index) {
                                    Prescription.set(index - 1, names[1]);
                                }
                                if ((record.contains("advice") || record.contains("advices")||record.contains("advise")||record.contains("advises"))&&Advice.size()>=index) {
                                    Advice.set(index - 1, names[1]);
                                }
                            }
                        }
                    }

                    else if(recordrvoice.toLowerCase().trim().contains("add")){

                    }
                    else {
                        boolean namebool = true, dignosebool = true, prescriptionbool = true, Advicebool = true;
                        String nameString[]=recordrvoice.trim().toLowerCase().split(" ");
                        for (String name1 : preNames) {
                            boolean namecheck=false;
                            for(String n1:nameString){
                                if (n1.equals(name1)) {
                                    boolean required=false,required1=true;
                                    String numberInvoice="";
                                    int indexofage=0;
                                    for(int j=0;j<recordrvoice.length();j++){
                                        if(recordrvoice.charAt(j)>=48&&recordrvoice.charAt(j)<=57){
                                            numberInvoice+=recordrvoice.charAt(j);
                                            required=true;
                                            if(required1)
                                                indexofage=j;
                                            required1=false;
                                        }
                                    }
                                    if(required){
                                        nameofpat="";
                                        for(int j=0;j<indexofage-1;j++){
                                            nameofpat+=recordrvoice.charAt(j);
                                        }
                                        age="";
                                        for(int j=indexofage;j<recordrvoice.length();j++){
                                            age+=recordrvoice.charAt(j);
                                        }
                                    }
                                    else{
                                        nameofpat = recordrvoice;
                                    }

                                    namebool = false;
                                    namecheck=true;
                                    break;
                                }
                            }
                            if (namecheck)
                                break;

                        }
                        if (namebool) {
                            for (String symptom1 : preSymptoms) {
                                if ((recordrvoice.trim().toLowerCase()).contains(symptom1)) {
                                    Symptoms.add(recordrvoice);
                                    dignosebool = false;
                                    break;
                                }
                            }
                        }
                        if (namebool && dignosebool) {
                            for (String diagnose1 : preDiagnose) {
                                if ((recordrvoice.trim().toLowerCase()).contains(diagnose1)) {
                                    Diagnose.add(recordrvoice);
                                    prescriptionbool = false;
                                    break;
                                }
                            }
                        }
                        if (namebool && dignosebool && prescriptionbool) {
                            for (String prescri1 : prePrescription) {
                                if ((recordrvoice.trim().toLowerCase()).contains(prescri1)) {
                                    Prescription.add(recordrvoice);
                                    Advicebool = false;
                                    break;
                                }
                            }
                        }
                        boolean agebool = true, sexbool = true;
                        if (namebool&&recordrvoice.toLowerCase().trim().contains("years") || recordrvoice.toLowerCase().trim().contains("year")) {
                            if(recordrvoice.toLowerCase().contains("male")||recordrvoice.toLowerCase().contains("female")) {
                                String gender1[]=recordrvoice.split(" ");
                                if(gender1.length>=3) {
                                    String age1 = gender1[0] + gender1[1];
                                    age = age1 + " / " + gender1[2];
                                }
                            }
                            else{
                                age=recordrvoice;
                            }
                            agebool = false;
                        }
                        if (namebool&&recordrvoice.toLowerCase().trim().equals("male") || recordrvoice.toLowerCase().trim().equals("mail")) {
                                sex = " / male";
                            sexbool = false;
                        }
                        if (namebool&&recordrvoice.toLowerCase().trim().equals("female")||recordrvoice.toLowerCase().trim().equals("femail")) {
                                sex = " / female";
                            sexbool = false;
                        }
                        if (namebool && dignosebool && prescriptionbool && Advicebool && agebool && sexbool ) {
                            sendMessage(recordrvoice);
                        }
//                        if (namebool && dignosebool && prescriptionbool && Advicebool && agebool && sexbool && recordrvoice.length() < 15)
//                            Toast.makeText(getContext(), "Not match with any field Please speak again your currently spoken text is:  " +
//                                    recordrvoice, Toast.LENGTH_SHORT).show();

                    }
                    Toast.makeText(getContext(), recordrvoice, Toast.LENGTH_SHORT).show();
                    UpdateData();
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mspeechRecog.stopListening();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        mspeechRecog.startListening(mSpeechRecogInt);
                        break;
                }
                return false;
            }
        });

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("nameofpat",nameofpat);
                bundle.putString("ageandsex",age+sex);
                bundle.putStringArrayList ("listSymptoms",Symptoms);
                bundle.putStringArrayList ("listdiagnose",Diagnose);
                bundle.putStringArrayList ("listPrescription",Prescription);
                bundle.putStringArrayList ("listAdvice",Advice);
                editable_voice_pres presCheckFrag = new editable_voice_pres();
                FragmentTransaction ft = (getActivity()).getSupportFragmentManager().beginTransaction();
                presCheckFrag.setArguments(bundle);
                ft.replace(R.id.doctor_nav_host_fragment, presCheckFrag);
                ft.addToBackStack(null);
                ft.commit();

            }
        });
        enableNestedScrolling();
        return voice;
    }

    private void enableNestedScrolling()
    {

        for (ListView l : listViewArr)
        {
            l.setOnTouchListener(new ListView.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                    v.onTouchEvent(event);
                    return true;
                }
            });
        }

    }

    private void checkPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + "com.example.demoapp"));
                startActivity(i);
            }
        }
    }

    public void UpdateData(){
        nameofperson.setText(nameofpat);
        symptomList.setAdapter(new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, Symptoms));
        DiagnoseList.setAdapter(new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1,Diagnose ));
        PresList.setAdapter(new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, Prescription));
        AdviceList.setAdapter(new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, Advice));
        if(age.contains("male")||age.contains("female"))
            agesex.setText(age);
        else
            agesex.setText(age+sex);
    }

    public int IntegerReturner(String record){
        int index=0;
        if(record.contains("first"))
            index=1;
        if(record.contains("second"))
            index=2;
        if(record.contains("third"))
            index=3;
        if(record.contains("fourth"))
            index=4;
        if(record.contains("fifth"))
            index=5;
        if(record.contains("sixth"))
            index=6;
        if(record.contains("seventh"))
            index=7;
        if(record.contains("eighth"))
            index=8;
        if(record.contains("nineth"))
            index=9;
        return index;
    }



    /*










     */



    //chatbot implementation

    private void sendMessage(String msg1) {
        String msg = msg1;
        if (msg.trim().isEmpty()) {
            Toast.makeText(getContext(), "Please enter your query!", Toast.LENGTH_LONG).show();
        } else {
            // Android client
            aiRequest.setQuery(msg);
            RequestTask requestTask = new RequestTask(getActivity(), aiDataService, customAIServiceContext,msg);
            requestTask.execute(aiRequest);
        }
    }


    public void callback(AIResponse aiResponse,String msg) {
        if (aiResponse != null) {
            // process aiResponse here
            String botReply = aiResponse.getResult().getFulfillment().getSpeech();
            if(botReply.equals("advice")){
                Advice.add(msg);
                UpdateData();
                //Toast.makeText(getContext(), botReply, Toast.LENGTH_SHORT).show();
            }
            if(botReply.equals("diagnosis")){
                Diagnose.add(msg);
                UpdateData();
                //Toast.makeText(getContext(), botReply, Toast.LENGTH_SHORT).show();
            }
            if(botReply.equals("symptom")){
                Symptoms.add(msg);
                UpdateData();
                //Toast.makeText(getContext(), botReply, Toast.LENGTH_SHORT).show();
            }
            if(botReply.equals("prescription")){
                Prescription.add(msg);
                UpdateData();
                //Toast.makeText(getContext(), botReply, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getContext(), "Network issue", Toast.LENGTH_SHORT).show();
        }
    }

    private void initChatbot() {
        final AIConfiguration config = new AIConfiguration(BuildConfig.ClientAccessToken,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiDataService = new AIDataService(getContext(), config);
        customAIServiceContext = AIServiceContextBuilder.buildFromSessionId(uuid);// helps to create new session whenever app restarts
        aiRequest = new AIRequest();
    }




    //inner class

    public class RequestTask extends AsyncTask<AIRequest, Void, AIResponse> {

        Activity activity;
        private AIDataService aiDataService;
        private AIServiceContext customAIServiceContext;
        String msg;

        RequestTask(Activity activity, AIDataService aiDataService, AIServiceContext customAIServiceContext,String msg){
            this.activity = activity;
            this.aiDataService = aiDataService;
            this.customAIServiceContext = customAIServiceContext;
            this.msg =msg;
        }

        @Override
        protected AIResponse doInBackground(AIRequest... aiRequests) {
            final AIRequest request = aiRequests[0];
            try {
                return aiDataService.request(request, customAIServiceContext);
            } catch (AIServiceException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(AIResponse aiResponse) {
            super.onPostExecute(aiResponse);
            callback(aiResponse,msg);
        }
    }
}