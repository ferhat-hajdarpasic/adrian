package whitespider.com.adrian;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
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
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import whitespider.com.adrian.WiFiContent.WiFiItem;

public class ConfigureWiFiActivity extends AppCompatActivity {
    private ArrayAdapter<WiFiItem> arrayAdapter;
    private ListView listView;
    private TextView hiddenDomain;
    private TextView wiFiPassword;
    private RadioButton radioButtonSecurityTypeOpen;
    private RadioButton radioButtonSecurityTypeWep;
    private RadioButton radioButtonSecurityTypeWpa;
    private Button submitButton;
    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_wi_fi);
        BroadcastReceiver mWifiScanReceiver = new WiFiBroadcastReceiver(this);
        arrayAdapter =new ArrayAdapter<WiFiItem>(this.getApplicationContext(),
                        android.R.layout.simple_list_item_checked);
        listView = (ListView)this.findViewById(R.id.wiFiDomainsListView);
        hiddenDomain = (TextView) this.findViewById(R.id.editHiddenDomainText);
        wiFiPassword = (TextView) this.findViewById(R.id.editPasswordText);
        radioButtonSecurityTypeOpen = (RadioButton)this.findViewById(R.id.securityTypeOpen);
        radioButtonSecurityTypeWep = (RadioButton)this.findViewById(R.id.securityTypeWep);
        radioButtonSecurityTypeWpa = (RadioButton)this.findViewById(R.id.securityTypeWpa);
        submitButton = (Button)this.findViewById(R.id.submitButton);
        listView.setAdapter(arrayAdapter);

        hiddenDomain.clearFocus();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                hiddenDomain.clearFocus();
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
                hiddenDomain.setText("");
                CheckEnabledConfiguration();
            }
        });
        radioButtonSecurityTypeWpa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hiddenDomain.clearFocus();
                CheckEnabledConfiguration();
            }
        });
        radioButtonSecurityTypeWep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hiddenDomain.clearFocus();
                CheckEnabledConfiguration();
            }
        });
        radioButtonSecurityTypeOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hiddenDomain.clearFocus();
                CheckEnabledConfiguration();
            }
        });
        hiddenDomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckEnabledConfiguration();
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
        wiFiPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CheckEnabledConfiguration();
                return false;
            }
        });
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
    }

    private void CheckEnabledConfiguration() {
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
    }
}
