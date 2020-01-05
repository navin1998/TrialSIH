package com.navin.trialsih;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class PreviewPres extends Fragment {

    View Prev;
    String Url;
    private WebView mywebview;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Prev = inflater.inflate(R.layout.fragment_editable_voice_pres, container, false);

        Bundle bundle=new Bundle();
        Url=bundle.getString("PrevUri");

        mywebview=Prev.findViewById(R.id.webview);
        WebSettings webSettings=mywebview.getSettings();
        webSettings.getJavaScriptEnabled();
        mywebview.loadUrl(Url);
        mywebview.setWebViewClient(new WebViewClient());

        return Prev;
    }

}
