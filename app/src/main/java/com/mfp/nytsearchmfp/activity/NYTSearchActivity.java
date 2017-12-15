package com.mfp.nytsearchmfp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
//import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentManager;
import android.app.ProgressDialog;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mfp.nytsearchmfp.R;
import com.mfp.nytsearchmfp.adapter.ArticleAdapter;
import com.mfp.nytsearchmfp.broadcastReceiver.NetworkStateReceiver;
import com.mfp.nytsearchmfp.databinding.ActivityNytsearchBinding;
import com.mfp.nytsearchmfp.fragment.SearchFilterFragment;
import com.mfp.nytsearchmfp.model.Article;
import com.mfp.nytsearchmfp.model.ArticleModel;
import com.mfp.nytsearchmfp.utils.ApiService;
import com.mfp.nytsearchmfp.utils.EndlessRecyclerViewScrollListener;
import com.mfp.nytsearchmfp.utils.ItemClickSupport;
import com.mfp.nytsearchmfp.utils.RetroClient;
import com.mfp.nytsearchmfp.utils.VerticalSpaceItemDecoration;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NYTSearchActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener,SearchFilterFragment.SaveFilterListener{

    private static final String API_KEY = "d31fe793adf546658bd67e2b6a7fd11a";
    public static final String NYTSEARCH_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    public static final String TAG = "NYTSearchActivity";
    public static final String PREFERENCES = "nytsearch_pref";
    public static final String ARTS = "arts";
    public static final String SPORTS = "sports";
    public static final String FASHION = "fashion";
    public static final String BUSINESS = "business";
    public static final String WEEKEND = "weekend";
    public static final String BEGIN_DATE = "begin_dt";
    public static final String SORT_ORDER = "sort_order";
    public static final String WEB_URL = "web_url";
    EndlessRecyclerViewScrollListener scrollListener;
    Map<String,Object> paramsMap;
    public static boolean isList;
    ArrayList<Article> articleList;
    ProgressDialog progress;
    TextView empty;
    Bundle data;
    RecyclerView nyGrid;
    SwipeRefreshLayout refresh;
    ArticleAdapter adp ;
    AsyncHttpClient client;
    SearchView searchView;
    SearchFilterFragment filterFrag;
    FragmentManager fm;
    StaggeredGridLayoutManager gridLayoutManager;
    LinearLayoutManager listLayoutManager;
    RequestParams params;
    Toolbar toolbar;
    Context mCtx;
    private NetworkStateReceiver networkStateReceiver;
    private Menu menu;
    private ActivityNytsearchBinding binding;
    SharedPreferences pref;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nytsearch);
        client = new AsyncHttpClient();

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        setUpViews();
        setUpToolbar();
        setUpLayouts();
        setUpAdapters();
        setUpListenersAndAnimations();
        mCtx = NYTSearchActivity.this;
        data = new Bundle();
        fm = getSupportFragmentManager();
        isList = false;


       /* if(isList)
            nyGrid.setLayoutManager(listLayoutManager);
        else*/

        if(articleList.size()==0)
        {
            nyGrid.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.GONE);
        }

        fillBundleFromPreferences();
    }

    private void setUpListenersAndAnimations(){
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(searchView.getQuery().toString().isEmpty())
                    refresh.setRefreshing(false);
                else {
                    refresh.setVisibility(View.VISIBLE);
                    searchArticle(searchView.getQuery().toString());
                }
            }
        });
        ItemClickSupport.addTo(nyGrid).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    Article article = articleList.get(position);
                    Toast.makeText(mCtx,"Loading the webpage. Please wait..",Toast.LENGTH_SHORT).show();
                        /*
                            WebView Implementation begins
                         */
                    Intent intent = new Intent();
                    intent.setClass(mCtx,WebViewActivity.class);
                    intent.putExtra(WEB_URL,article.getWeb_url());
                    startActivity(intent);
                        /*
                            WebView Implementation end
                         */
                       /*
                        *Chrome custom tab implementation begins
                        */
                       /* Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.action_share);
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, article.getWeb_url());
                        int requestCode = 100;

                        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx,
                                requestCode,
                                shareIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                        builder.setToolbarColor(ContextCompat.getColor(mCtx, R.color.colorAccent));
                        builder.setActionButton(bitmap, "Share this Link", pendingIntent, true);
                        // set toolbar color and/or setting custom actions before invoking build()
                        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
                        CustomTabsIntent customTabsIntent = builder.build();
                        // and launch the desired Url with CustomTabsIntent.launchUrl()
                        customTabsIntent.launchUrl(mCtx, Uri.parse(article.getWeb_url()));*/


                }
        );

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG,"Total Items ="+totalItemsCount);
                Log.i(TAG,"page ="+page);

                loadNextDataFromApi(page);
            }
        };
        nyGrid.addOnScrollListener(scrollListener);

        VerticalSpaceItemDecoration divider = new VerticalSpaceItemDecoration(5);
        nyGrid.addItemDecoration(divider);
        nyGrid.setItemAnimator(new SlideInUpAnimator());
    }


    private void setUpViews(){
        toolbar = binding.toolbar;
        progress = new ProgressDialog(this);
        nyGrid = binding.nyView;
        empty = binding.emptyView;
        refresh = binding.refresh;

    }

    private void setUpLayouts(){
        gridLayoutManager = new StaggeredGridLayoutManager(2,1);
        listLayoutManager = new LinearLayoutManager(this);
        nyGrid.setLayoutManager(gridLayoutManager);
    }

    private void setUpAdapters(){
        articleList = new ArrayList<>();
        adp = new ArticleAdapter(this,articleList);
        nyGrid.setAdapter(adp);

    }

    private void setUpToolbar(){

        toolbar.setTitle("NewYorkTimes Search");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private void fillBundleFromPreferences(){
        pref = getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        data.putString(BEGIN_DATE,pref.getString(BEGIN_DATE,""));
        data.putInt(SORT_ORDER,pref.getInt(SORT_ORDER,1));
        data.putBoolean(ARTS,pref.getBoolean(ARTS,false));
        data.putBoolean(SPORTS,pref.getBoolean(SPORTS,false));
        data.putBoolean(FASHION,pref.getBoolean(FASHION,false));
        data.putBoolean(BUSINESS,pref.getBoolean(BUSINESS,false));
        data.putBoolean(WEEKEND,pref.getBoolean(WEEKEND,false));
    }

    private void fillPreferencesFromBundle(){
        editor = pref.edit();
        editor.putString(BEGIN_DATE,data.getString(BEGIN_DATE,""));
        editor.putInt(SORT_ORDER,data.getInt(SORT_ORDER,1));
        editor.putBoolean(ARTS,data.getBoolean(ARTS,false));
        editor.putBoolean(SPORTS,data.getBoolean(SPORTS,false));
        editor.putBoolean(FASHION,data.getBoolean(FASHION,false));
        editor.putBoolean(BUSINESS,data.getBoolean(BUSINESS,false));
        editor.putBoolean(WEEKEND,data.getBoolean(WEEKEND,false));
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void onDestroy() {
        super.onDestroy();
        fillPreferencesFromBundle();
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }

    public void loadNextDataFromApi(int offset) {
        paramsMap.remove("page");
        paramsMap.put("page",offset);
        ApiService service = RetroClient.getApiService();
        Call<ArticleModel> call = service.getMyJSON(paramsMap);
        Log.i(TAG,"loadNextDataFromApi params "+paramsMap.toString());

        call.enqueue(new Callback<ArticleModel>() {
            @Override
            public void onResponse(Call<ArticleModel> call, Response<ArticleModel> response) {
                int statusCode = response.code();
                Log.i(TAG,"Retrofit response code  : "+statusCode);
                if(response.isSuccessful()){
                    ArticleModel myResponse = response.body();
                    Log.i(TAG,"Retrofit success : "+myResponse.toString());

                    int cursize = articleList.size();
                    ArrayList<Article> moreArticles = Article.getArticleList(myResponse.getResponse().getDocs());
                    articleList.addAll(moreArticles);
                    adp.notifyItemRangeInserted(cursize, articleList.size()-1);
                    Log.i(TAG, articleList.toString());
                }
            }

            @Override
            public void onFailure(Call<ArticleModel> call, Throwable t) {
                Log.i(TAG,t.toString());

                Log.i(TAG, "Search Failed");
                Log.i(TAG,""+t.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mCtx,"No more pages to load",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater in = getMenuInflater();
        in.inflate(R.menu.article_menu,menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        if(!isOnline()) {
            searchItem.setEnabled(false);
        }
        else{
            searchItem.setEnabled(true);
        }
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(getResources().getColor(R.color.colorPrimary));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchArticle(query);
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItem chooseView = menu.findItem(R.id.chooseView);
        if(isList)
            chooseView.setIcon(R.drawable.grid);
        else
            chooseView.setIcon(R.drawable.list);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.filter) {
            filterFrag = new SearchFilterFragment();
            filterFrag.setArguments(data);
            filterFrag.show(fm, "filter");
        }
        else if(item.getItemId()==R.id.chooseView) {
            item.setIcon(isList ? R.drawable.list : R.drawable.grid);
            //nyGrid.setLayoutManager(isList ? gridLayoutManager : listLayoutManager);
            //nyGrid.setAdapter(adp);
            isList = !isList;

        }
        return true;
    }

    public void searchArticle(String query) {
        paramsMap = new HashMap<>();
        params = new RequestParams();
        if (!query.isEmpty()) {
            progress.setMessage("Searching in progress.Please wait...");
            progress.show();
            int curSize = articleList.size();
            articleList.clear();
            adp.notifyItemRangeRemoved(0, curSize);
            scrollListener.resetState();
            String date = data.getString(BEGIN_DATE, "");
            Boolean arts = data.getBoolean(ARTS, false);
            Boolean sports = data.getBoolean(SPORTS, false);
            Boolean fashion = data.getBoolean(FASHION, false);
            Boolean business = data.getBoolean(BUSINESS, false);
            Boolean weekend = data.getBoolean(WEEKEND, false);


            ApiService service = RetroClient.getApiService();
            params.put("q",query);
            params.put("api_key",API_KEY);
            if (!(date.isEmpty())) {
                StringBuilder datestr = new StringBuilder();
                datestr.append(date.split("/")[2]).append(date.split("/")[0]).append(date.split("/")[1]);
                paramsMap.put("begin_date", datestr.toString());
            }

            int sort_order = data.getInt(SORT_ORDER, 1);
            if (sort_order == 1)
                paramsMap.put("sort", "newest");
            else
                paramsMap.put("sort", "oldest");

            if (arts || sports || fashion || weekend || business) {
                paramsMap.put("fq", getnews_desk(arts, sports, fashion, weekend,business));
            }

            Call<ArticleModel> call = service.getMyJSON(paramsMap);
            call.enqueue(new Callback<ArticleModel>() {
                @Override
                public void onResponse(Call<ArticleModel> call, Response<ArticleModel> response) {
                    int statusCode = response.code();
                    Log.i(TAG,"Retrofit response code  : "+statusCode);
                    if(response.isSuccessful()){
                        ArticleModel myResponse = response.body();
                        Log.i(TAG,"Retrofit success : "+myResponse.toString());
                        articleList.addAll(Article.getArticleList(myResponse.getResponse().getDocs()));
                        adp.notifyItemRangeInserted(0, articleList.size());
                        Log.i(TAG, articleList.toString());


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                                if(refresh.isRefreshing())
                                    refresh.setRefreshing(false);
                                if(articleList.size()==0){
                                    nyGrid.setVisibility(View.GONE);
                                    refresh.setVisibility(View.GONE);
                                    empty.setVisibility(View.VISIBLE);
                                    empty.setText("No results found !! \uD83D\uDE1E  \uD83D\uDE1E");
                                }
                                else{
                                    nyGrid.setVisibility(View.VISIBLE);
                                    refresh.setVisibility(View.VISIBLE);
                                    empty.setVisibility(View.GONE);
                                    Toast.makeText(mCtx, "Search Success", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<ArticleModel> call, Throwable t) {
                    Log.i(TAG,t.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            if(refresh.isRefreshing())
                                refresh.setRefreshing(false);
                            Toast.makeText(mCtx, "Failed to Search", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if(!isOnline()) {
            searchItem.setEnabled(false);
        }
        else{
            searchItem.setEnabled(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    //fq=(news_desk:%20("None""Sports"))
    String getnews_desk(Boolean arts, Boolean sports, Boolean fashion, Boolean weekend, Boolean business){
        StringBuilder new_desk = new StringBuilder();
        new_desk.append("news_desk:(");
        if(arts)
            new_desk.append("\"Arts\"");
        if(sports) {
                new_desk.append("\"Sports\"");
        }
        if(fashion){
                new_desk.append("\"Fashion Style\"");
        }
        if(weekend){
            new_desk.append("\"Weekend\"");
        }
        if(business){
            new_desk.append("\"Business\"");
        }

        new_desk.append(")");

        Log.i(TAG,"new_desk = "+new_desk.toString());
        return new_desk.toString();


    }


    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }


    @Override
    public void networkAvailable() {
        Log.i(TAG,"Network is on");
        invalidateOptionsMenu();
    }

    @Override
    public void networkUnavailable() {
        Log.i(TAG,"Network is off");
        invalidateOptionsMenu();
    }

    @Override
    public void onFilterSaved(Bundle bundle) {
        data = bundle;
    }
}
