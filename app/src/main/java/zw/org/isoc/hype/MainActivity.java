package zw.org.isoc.hype;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static zw.org.isoc.hype.IncentivizedInternetActivity.BASE_URL;

public class MainActivity extends AppCompatActivity {

    private Button btnEMKamboButton;
    private Button btnIncentivisedInternetButton;

    HypeServerApi hypeServerApi;

    private static MainActivity instance; // Way of accessing the application context from other classes

    public MainActivity() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Mbare Community");
        initButtonsFromResourceIDs();
        setButtonListeners();

        Retrofit retrofitGson = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        hypeServerApi = retrofitGson.create(HypeServerApi.class);

        registerForInternet();
    }

    public static String getHypeUserId() {

        return Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
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
                    finishAffinity();
                    System.exit(0);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                finishAffinity();
                System.exit(0);
                Log.e("Response fail: ", t.getMessage());
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show(); // ALL NETWORK ERROR HERE
            }
        });
    }

    private void setButtonListeners() {
        setListenerEMKamboButton();
        setListenerIncentivisedInternetButton();
    }

    private void initButtonsFromResourceIDs() {
        btnEMKamboButton = findViewById(R.id.btn_emkambo);
        btnIncentivisedInternetButton = findViewById(R.id.btn_offline_pages);
    }

    private void setListenerEMKamboButton() {
        btnEMKamboButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final Intent intent = new Intent(instance, SubscriptionsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setListenerIncentivisedInternetButton() {
        btnIncentivisedInternetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final Intent intent = new Intent(instance, IncentivizedInternetActivity.class);
                startActivity(intent);
            }
        });
    }

}
