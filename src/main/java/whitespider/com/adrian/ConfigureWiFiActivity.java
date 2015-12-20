package whitespider.com.adrian;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.BaseKeyListener;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import whitespider.com.adrian.WiFiContent.WiFiItem;

public class ConfigureWiFiActivity extends AppCompatActivity {
    public static final String CONFIGURE_WIFI_EVENT = "configure-wifi-event";
    public static final String CONFIGURE_WFI_RESULT_MESSAGE = "CONFIGURE_WFI_RESULT_MESSAGE";
    private ArrayAdapter<WiFiItem> arrayAdapter;
    private ListView wiFiDomainsListView;
    private TextView hiddenDomain;
    private TextView wiFiPassword;
    private RadioButton radioButtonSecurityTypeOpen;
    private RadioButton radioButtonSecurityTypeWep;
    private RadioButton radioButtonSecurityTypeWpa;
    private Button submitButton;
    private int selectedPosition = -1;
    private BroadcastReceiver broadcastReceiver;
    private ProgressBar wifiCollectProgressBar;
    private ProgressBar broadcastProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_wi_fi);
        BroadcastReceiver mWifiScanReceiver = new WiFiBroadcastReceiver(this);
        arrayAdapter =new ArrayAdapter<WiFiItem>(this.getApplicationContext(),
                        android.R.layout.simple_list_item_checked);
        wiFiDomainsListView = (ListView)this.findViewById(R.id.wiFiDomainsListView);
        hiddenDomain = (TextView) this.findViewById(R.id.editHiddenDomainText);
        wiFiPassword = (TextView) this.findViewById(R.id.editPasswordText);
        radioButtonSecurityTypeOpen = (RadioButton)this.findViewById(R.id.securityTypeOpen);
        radioButtonSecurityTypeWep = (RadioButton)this.findViewById(R.id.securityTypeWep);
        radioButtonSecurityTypeWpa = (RadioButton)this.findViewById(R.id.securityTypeWpa);
        submitButton = (Button)this.findViewById(R.id.submitButton);
        wifiCollectProgressBar = (ProgressBar)this.findViewById(R.id.wifiCollectProgressBar);
        broadcastProgressBar = (ProgressBar)this.findViewById(R.id.broadcastProgressBar);

        wiFiDomainsListView.setAdapter(arrayAdapter);

        hiddenDomain.clearFocus();

        wiFiDomainsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                //hiddenDomain.clearFocus();
                final WiFiItem item = arrayAdapter.getItem(position);
                if (item.securityType != null) {
                    switch (item.securityType) {
                        case "Open":
                            radioButtonSecurityTypeOpen.setChecked(true);
                            radioButtonSecurityTypeWep.setChecked(false);
                            radioButtonSecurityTypeWpa.setChecked(false);
                            break;
                        case "WPA":
                            radioButtonSecurityTypeOpen.setChecked(false);
                            radioButtonSecurityTypeWep.setChecked(false);
                            radioButtonSecurityTypeWpa.setChecked(true);
                            break;
                        case "WEP":
                            radioButtonSecurityTypeOpen.setChecked(false);
                            radioButtonSecurityTypeWep.setChecked(true);
                            radioButtonSecurityTypeWpa.setChecked(false);
                            break;
                        case "Other":
                            radioButtonSecurityTypeOpen.setChecked(false);
                            radioButtonSecurityTypeWep.setChecked(false);
                            radioButtonSecurityTypeWpa.setChecked(false);
                            break;
                    }
                }
//                hiddenDomain.setText("");
                checkEnabledConfiguration();

            }
        });
        radioButtonSecurityTypeWpa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hiddenDomain.clearFocus();
                checkEnabledConfiguration();
            }
        });
        radioButtonSecurityTypeWep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hiddenDomain.clearFocus();
                checkEnabledConfiguration();
            }
        });
        radioButtonSecurityTypeOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hiddenDomain.clearFocus();
                checkEnabledConfiguration();
            }
        });
        hiddenDomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEnabledConfiguration();
            }
        });
        hiddenDomain.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (hiddenDomain.getText().toString().isEmpty()) {
                        radioButtonSecurityTypeOpen.setChecked(false);
                        radioButtonSecurityTypeWep.setChecked(false);
                        radioButtonSecurityTypeWpa.setChecked(false);
                    }
                    //listView.setSelection(-1);
                }
            }
        });
        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkEnabledConfiguration();
            }
        };
        wiFiPassword.addTextChangedListener(textWatcher);
        hiddenDomain.addTextChangedListener(textWatcher);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                broadcastProgressBar.setVisibility(View.GONE);
                String message = intent.getStringExtra(CONFIGURE_WFI_RESULT_MESSAGE);
                Toast.makeText(ConfigureWiFiActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastReceiver,
                new IntentFilter(CONFIGURE_WIFI_EVENT));
    }

    public void refreshWiFiList(List<ScanResult> mScanResults) {
        List<WiFiItem> wiFiNetworks = new ArrayList<WiFiItem>();
        for(int i = 0; i < mScanResults.size(); i++){
            final ScanResult scanResult = mScanResults.get(i);
            Log.i("BSSID", scanResult.BSSID + ". ");
            Log.i("SSID", scanResult.SSID);
            Log.i("capabilities", scanResult.capabilities);
            WiFiItem item = new WiFiItem(scanResult.BSSID, scanResult.SSID, scanResult.capabilities);
            wiFiNetworks.add(item);
        }

        arrayAdapter.addAll(wiFiNetworks);
        wifiCollectProgressBar.setVisibility(View.INVISIBLE);
        wiFiDomainsListView.setVisibility(View.VISIBLE);
    }

    private void checkEnabledConfiguration() {
        final CharSequence text = hiddenDomain.getText();
        final CharSequence text1 = wiFiPassword.getText();
        final boolean checked = radioButtonSecurityTypeOpen.isChecked();
        final boolean checked1 = radioButtonSecurityTypeWep.isChecked();
        final boolean checked2 = radioButtonSecurityTypeWpa.isChecked();
        boolean enableButton =
                (selectedPosition != -1 || text.toString().trim().length() > 0) &&
                (text1.toString().trim().length() > 0) &&
                (checked || checked1 || checked2);
        this.submitButton.setEnabled(enableButton);
    }

    public void showPasswordOnClick(View view) {
        CheckBox checkBox = (CheckBox)view;
        if(checkBox.isChecked()) {
            wiFiPassword.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            wiFiPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    public void submit(View view) {
        final Intent intent = new Intent(this, TcpCommunicationIntentService.class);
        String domain = hiddenDomain.getText().toString().trim();
        if(domain.isEmpty()) {
            domain = arrayAdapter.getItem(selectedPosition).ssid;
        }
        intent.putExtra(TcpCommunicationIntentService.DOMAIN_KEY, domain);
        final String password = wiFiPassword.getText().toString();
        intent.putExtra(TcpCommunicationIntentService.PASSWORD_KEY, password);

        int securityType = 0;
        if(radioButtonSecurityTypeOpen.isChecked()) {
            securityType = 0;
        } else if (radioButtonSecurityTypeWpa.isChecked()) {
            securityType = 1;
        } else if (radioButtonSecurityTypeWep.isChecked()) {
            securityType = 2;
        }
        intent.putExtra(TcpCommunicationIntentService.SECURITY_TYPE_KEY, securityType);

        this.startService(intent);

        broadcastProgressBar.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
