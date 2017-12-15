package com.mfp.nytsearchmfp.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.mfp.nytsearchmfp.R;
import com.mfp.nytsearchmfp.databinding.ActivityWebViewBinding;

public class WebViewActivity extends AppCompatActivity {

    Toolbar toolbar;
    private ActivityWebViewBinding binding;
    WebView web;
    public static final String WEB_URL = "web_url";
    ShareActionProvider miShareAction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view);
        web = binding.webview;
        toolbar = binding.appBar.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title  = (TextView) binding.appBar.toolbarTitle;
        String url = getIntent().getStringExtra(WEB_URL);
        title.setText(url);
        title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        title.setSelected(true);
        title.setSingleLine(true);
        title.setMarqueeRepeatLimit(-1);
        web.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });

        web.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater in = getMenuInflater();
        in.inflate(R.menu.webview_menu,menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        shareItem.setIcon(R.drawable.share);
        // Fetch reference to the share action provider
        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,web.getUrl());
        miShareAction.setShareIntent(shareIntent);
        return super.onCreateOptionsMenu(menu);
    }
}
