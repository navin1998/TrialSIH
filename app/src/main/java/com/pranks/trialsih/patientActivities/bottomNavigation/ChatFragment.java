package com.pranks.trialsih.patientActivities.bottomNavigation;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.pranks.trialsih.BuildConfig;
import com.pranks.trialsih.MainActivity;
import com.pranks.trialsih.R;
import com.pranks.trialsih.patientActivities.PatientDashboardActivity;
import com.pranks.trialsih.patientActivities.ui.prevTransactions.PrevTransactionsViewModel;

import java.util.UUID;

import ai.api.AIServiceContext;
import ai.api.AIServiceContextBuilder;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

public class ChatFragment extends Fragment {

    public ChatFragment(){

    }

    private View v;

    Activity mListener;

    //private static final String TAG = .getSimpleName();
    private static final int USER = 10001;
    public static final int BOT = 10002;

    private String uuid = UUID.randomUUID().toString();
    private LinearLayout chatLayout;
    private EditText queryEditText;

    // Android client
    private AIRequest aiRequest;
    private AIDataService aiDataService;
    private AIServiceContext customAIServiceContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_patient_chat_bot, container, false);

        ((PatientDashboardActivity) getActivity()).getSupportActionBar().setTitle("Chat");


        final ScrollView scrollview = v.findViewById(R.id.chatScrollView);
        scrollview.post(() -> scrollview.fullScroll(ScrollView.FOCUS_DOWN));

        chatLayout = v.findViewById(R.id.chatLayout);

        ImageView sendBtn = v.findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this::sendMessage);

        queryEditText = v.findViewById(R.id.queryEditText);
        queryEditText.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        sendMessage(sendBtn);
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });

        // Android client
        initChatbot();


        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener =  activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnItemClickedListener");
        }
    }



    private void initChatbot() {
        final AIConfiguration config = new AIConfiguration(BuildConfig.ClientAccessToken1,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiDataService = new AIDataService(getContext(), config);
        customAIServiceContext = AIServiceContextBuilder.buildFromSessionId(uuid);// helps to create new session whenever app restarts
        aiRequest = new AIRequest();
    }

    private void sendMessage(View view) {
        String msg = queryEditText.getText().toString();
        if (msg.trim().isEmpty()) {
            Toast.makeText(getContext(), "Please enter your query!", Toast.LENGTH_LONG).show();
        } else {
            showTextView(msg, USER);
            queryEditText.setText("");
            // Android client
            aiRequest.setQuery(msg);
            RequestTask requestTask = new RequestTask(getActivity(), aiDataService, customAIServiceContext,new ChatFragment());
            requestTask.execute(aiRequest);
        }
    }



    public void callback(AIResponse aiResponse) {
        if (aiResponse != null) {
            // process aiResponse here
            String botReply = aiResponse.getResult().getFulfillment().getSpeech();
            showTextView(botReply, BOT);
        } else {

            showTextView("There was some communication issue. Please Try again!", BOT);
        }
    }


    public void showTextView(String message, int type) {
        FrameLayout layout;
        switch (type) {
            case USER:
                layout = getUserLayout();
                break;
            case BOT:
                layout = getBotLayout();
                break;
            default:
                layout = getBotLayout();
                break;
        }
        layout.setFocusableInTouchMode(true);
        chatLayout.addView(layout); // move focus to text view to automatically make it scroll up if softfocus
        TextView tv = layout.findViewById(R.id.chatMsg);
        tv.setText(message);
        layout.requestFocus();
        queryEditText.requestFocus(); // change focus back to edit text to continue typing
    }

    FrameLayout getUserLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        return (FrameLayout) inflater.inflate(R.layout.user_msg_layout, null);
    }

    FrameLayout getBotLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        return (FrameLayout) inflater.inflate(R.layout.bot_msg_layout, null);
    }

    //inner class

    public class RequestTask extends AsyncTask<AIRequest, Void, AIResponse> {

        Activity activity;
        ChatFragment ch;
        private AIDataService aiDataService;
        private AIServiceContext customAIServiceContext;

        RequestTask(Activity activity, AIDataService aiDataService, AIServiceContext customAIServiceContext,ChatFragment ch1){
            this.activity = activity;
            this.aiDataService = aiDataService;
            this.customAIServiceContext = customAIServiceContext;
            this.ch=ch1;
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
            callback(aiResponse);
        }
    }

}