package zw.org.isoc.hype;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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
    }

    public static String getHypeUserId() {

        return Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
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
