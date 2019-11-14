package zw.org.isoc.hype;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.api.services.customsearch.model.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static zw.org.isoc.hype.MainActivity.getHypeUserId;


public class IncentivizedInternetActivity extends AppCompatActivity {
    private static final String directoryPrefix = "webpages";
    public static final String URL_KEY = "url";
    public static final String TITLE_KEY = "title";
    private static final String htmlFileSuffix = ".html";
    public static final String MEDIA_TYPE_TEXT_HTML = "text/html";
    public static final String BASE_URL = "http://192.168.100.17:8100/";

    HypeServerApi hypeServerApi;
    private Button btnRequestPage;
    private Button btnViewCached;
    private Button btnViewBalance;

    private static IncentivizedInternetActivity instance; // Way of accessing the application context from other classes

    public IncentivizedInternetActivity() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incentivized_internet);

        Retrofit retrofitGson = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        hypeServerApi = retrofitGson.create(HypeServerApi.class);

        this.setTitle("Internet");
        initButtonsFromResourceIDs();
        setButtonListeners();

        registerForInternet();

    }

    private void registerForInternet() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Registering", "Please wait...");
        HypeUser hypeUser = new HypeUser();
        hypeUser.setId(getHypeUserId());
        Call<ResponseBody> call = hypeServerApi.register(hypeUser);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Successfully registered!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Internet gateway not available", Toast.LENGTH_LONG).show();
                    final Intent intent = new Intent(instance, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Internet gateway not available", Toast.LENGTH_LONG).show();
                final Intent intent = new Intent(instance, MainActivity.class);
                startActivity(intent);
                Log.e("Response fail: ", t.getMessage());
            }
        });
    }

    private void setButtonListeners() {
        setListenerRequestPageButton();
        setListenerViewCachedButton();
        setListenerViewBalanceButton();
    }

    private void initButtonsFromResourceIDs() {
        btnRequestPage = findViewById(R.id.btn_request_webpage);
        btnViewCached = findViewById(R.id.btn_view_cached);
        btnViewBalance = findViewById(R.id.btn_view_balance);
    }

    private void setListenerRequestPageButton() {
        btnRequestPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                displayKeywordPrompt();
            }
        });
    }

    private void setListenerViewCachedButton() {
        btnViewCached.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getCachedWebpagesAdapter();
            }
        });
    }

    private void setListenerViewBalanceButton() {
        btnViewBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showBalance();
            }
        });
    }

    private void showBalance() {
        final ProgressDialog progressDialog = ProgressDialog.show(IncentivizedInternetActivity.this, "Getting cached content", "Please wait...");
        Call<HypeBalance> call = hypeServerApi.getBalance(getHypeUserId());

        call.enqueue(new Callback<HypeBalance>() {
            @Override
            public void onResponse(Call<HypeBalance> call, Response<HypeBalance> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    HypeBalance balance = response.body();// have your all data
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle("HypeBalance");
                    if (balance == null) {
                        throw new RuntimeException("Balance returned is null!");
                    }
                    alertDialog.setMessage("Your current token balance is " + balance.getBalance() +
                            "\nStay connected to the network and gather more tokens.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    Log.d("Response fail: ", String.valueOf(response.errorBody()));
                    Toast.makeText(getApplicationContext(), "An error occurred while trying to process your request", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<HypeBalance> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("Response fail: ", t.getMessage());
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show(); // ALL NETWORK ERROR HERE
            }
        });
    }

    public void getCachedWebpagesAdapter() {
        final ProgressDialog progressDialog = ProgressDialog.show(IncentivizedInternetActivity.this, "Getting cached content", "Please wait...");
        Call<Map<String, WebPage>> call = hypeServerApi.getCachedContent();

        call.enqueue(new Callback<Map<String, WebPage>>() {
            @Override
            public void onResponse(Call<Map<String, WebPage>> call, Response<Map<String, WebPage>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Map<String, WebPage> results = response.body();// have your all data
                    assert results != null;
                    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
                    for (Map.Entry<String, WebPage> entry : results.entrySet()) {
                        Map<String, String> datum = new HashMap<String, String>();
                        datum.put("title", entry.getValue().getTitle());
                        datum.put("url", entry.getValue().getUrl());
                        data.add(datum);
                    }
                    SimpleAdapter adapter = new SimpleAdapter(getContext(), data,
                            android.R.layout.simple_list_item_2,
                            new String[]{"title", URL_KEY},
                            new int[]{android.R.id.text1,
                                    android.R.id.text2});

                    displayCachedWebPageResultList(adapter);

                } else {
                    Log.d("Response fail: ", String.valueOf(response.errorBody()));
                    Toast.makeText(getApplicationContext(), "An error occurred while trying to process your request", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, WebPage>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("Response fail: ", t.getMessage());
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show(); // ALL NETWORK ERROR HERE
            }
        });
    }

    public void getWebpagesByKeywordAdapter(String keyword) {
        final ProgressDialog progressDialog = ProgressDialog.show(IncentivizedInternetActivity.this, "Searching", "Please wait...");
        Call<List<Result>> call = hypeServerApi.searchByKeyword(keyword);

        call.enqueue(new Callback<List<Result>>() {
            @Override
            public void onResponse(Call<List<Result>> call, Response<List<Result>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    List<Result> results = response.body();// have your all data
                    assert results != null;
                    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
                    for (Result result : results) {
                        Map<String, String> datum = new HashMap<String, String>();
                        datum.put("title", result.getTitle());
                        datum.put("url", result.getFormattedUrl());
                        data.add(datum);
                    }
                    SimpleAdapter adapter = new SimpleAdapter(getContext(), data,
                            android.R.layout.simple_list_item_2,
                            new String[]{"title", URL_KEY},
                            new int[]{android.R.id.text1,
                                    android.R.id.text2});

                    displayWebPageResultList(adapter);

                } else {
                    Log.d("Response fail: ", String.valueOf(response.errorBody()));
                    Toast.makeText(getApplicationContext(), "An error occurred while trying to process your request", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Result>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("Response fail: ", t.getMessage());
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show(); // ALL NETWORK ERROR HERE
            }
        });
    }

    private void displayKeywordPrompt() {

        AlertDialog.Builder builder = new AlertDialog.Builder(IncentivizedInternetActivity.this);

        final EditText editText = new EditText(IncentivizedInternetActivity.this);
        builder.setTitle("Request a web pages");
        builder.setTitle("Search for a keyword");
        editText.setLines(1);
        editText.setMaxLines(1);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(40, 0, 40, 0);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.addView(editText, layoutParams);

        builder.setView(container);
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String keyword = editText.getText().toString();
                getWebpagesByKeywordAdapter(keyword);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.show();
    }

    private void displayWebPageResultList(ListAdapter adapter) {
        final ListView listView = new ListView(IncentivizedInternetActivity.this);
        listView.setAdapter(adapter);

        LinearLayout layout = new LinearLayout(IncentivizedInternetActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(listView);

        AlertDialog.Builder builder = new AlertDialog.Builder(IncentivizedInternetActivity.this);
        builder.setTitle("Search results");
        builder.setCancelable(true);
        builder.setView(layout);
        builder.setMessage("Select a web page to view");
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.setPositiveButton("Search again", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                displayKeywordPrompt();
            }
        });
        final Dialog dialog = builder.create();
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> listItem = (Map<String, String>) listView.getItemAtPosition(position);
                viewWebPage(listItem.get(TITLE_KEY), listItem.get(URL_KEY));
                //serviceAction.action(listItem);
                dialog.dismiss();
            }
        });
    }


    private void displayCachedWebPageResultList(ListAdapter adapter) {
        final ListView listView = new ListView(IncentivizedInternetActivity.this);
        listView.setAdapter(adapter);

        LinearLayout layout = new LinearLayout(IncentivizedInternetActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(listView);

        AlertDialog.Builder builder = new AlertDialog.Builder(IncentivizedInternetActivity.this);
        builder.setTitle("Cached content");
        builder.setCancelable(true);
        builder.setView(layout);
        builder.setMessage("Select a cached web page to view");
        builder.setNegativeButton("Close",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        final Dialog dialog = builder.create();
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> listItem = (Map<String, String>) listView.getItemAtPosition(position);
                viewCachedWebPage(listItem.get(URL_KEY));
                //serviceAction.action(listItem);
                dialog.dismiss();
            }
        });
    }

    private String constructFilename(String htmlFileName) {
        return htmlFileName + htmlFileSuffix;
    }

    private String constructAbsolutePath(String htmlFileName) {
        return directoryPrefix + htmlFileName + htmlFileSuffix;
    }

    private void viewCachedWebPage(String key) {
        String offlineUrl = BASE_URL + "hype/webpage/cached" + "?key=" + key + "&userId=" + getHypeUserId();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(offlineUrl));
        startActivity(browserIntent);
    }

    private void viewWebPage(String title, String url) {
        String offlineUrl = BASE_URL + "hype/webpage" + "?title=" + title + "&url=" + url + "&userId=" + getHypeUserId();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(offlineUrl));
        startActivity(browserIntent);
    }

}
