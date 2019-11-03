package zw.org.isoc.hype;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.api.services.customsearch.model.Result;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IncentivizedInternetActivity extends AppCompatActivity {
    HypeServerApi hypeServerApi;
    private Button btnRequestPage;
    private Button btnViewCached;

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.100.3:8100/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        hypeServerApi = retrofit.create(HypeServerApi.class);

        this.setTitle("Internet");
        initButtonsFromResourceIDs();
        setButtonListeners();
    }

    private void setButtonListeners() {
        setListenerRequestPageButton();
        setListenerViewCachedButton();
    }

    private void initButtonsFromResourceIDs() {
        btnRequestPage = findViewById(R.id.btn_request_webpage);
        btnViewCached = findViewById(R.id.btn_view_cached);
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
                displayCachedWebPageList(
                        getCachedWebpagesAdapter());
            }
        });
    }

    public ArrayAdapter<WebPage> getCachedWebpagesAdapter() {
        final ArrayList<WebPage> cachedWebPagesList = new ArrayList<>();
        WebPage webPage = new WebPage("asdasd", "ddfa");
        return new ArrayAdapter<WebPage>(this, R.layout.item_message, R.id.item_message_msg, cachedWebPagesList);
    }

    public ArrayAdapter<WebPage> getWebpagesByKeywordAdapter(String keyword) {
        ProgressDialog progressDialog = ProgressDialog.show(IncentivizedInternetActivity.this, "Searching", "Please wait...");
        List<Result> results = hypeServerApi.searchByKeyword(keyword);
        List<WebPage> webPageResults = new ArrayList<>();
        for (Result result : results) {
            WebPage webPage = new WebPage(result.getTitle(), result.getFormattedUrl());
            webPageResults.add(webPage);
        }
        progressDialog.dismiss();
        return new ArrayAdapter<>(this, R.layout.item_message, R.id.item_message_msg, webPageResults);
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
                ArrayAdapter<WebPage> cachedWebpagesAdapter = getWebpagesByKeywordAdapter(keyword);
                displayWebPageResultList(cachedWebpagesAdapter);
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
                String listItem = (String) listView.getItemAtPosition(position);
                //serviceAction.action(listItem);
                dialog.dismiss();
            }
        });
    }

    private void displayCachedWebPageList(ListAdapter adapter) {
        final ListView listView = new ListView(IncentivizedInternetActivity.this);
        listView.setAdapter(adapter);

        LinearLayout layout = new LinearLayout(IncentivizedInternetActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(listView);

        AlertDialog.Builder builder = new AlertDialog.Builder(IncentivizedInternetActivity.this);
        builder.setTitle("Cached web pages");
        builder.setCancelable(true);
        builder.setView(layout);
        builder.setMessage("Select a cached web page to view");
        builder.setNegativeButton("Cancel",
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
                String listItem = (String) listView.getItemAtPosition(position);
                //serviceAction.action(listItem);
                dialog.dismiss();
            }
        });
    }

}
