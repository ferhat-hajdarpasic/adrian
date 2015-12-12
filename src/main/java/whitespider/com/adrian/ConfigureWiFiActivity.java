package whitespider.com.adrian;

import android.content.BroadcastReceiver;
import android.net.wifi.ScanResult;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import whitespider.com.adrian.WiFiContent.WiFiItem;

public class ConfigureWiFiActivity extends AppCompatActivity {
    private ArrayAdapter<WiFiItem> arrayAdapter;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_wi_fi);
        BroadcastReceiver mWifiScanReceiver = new WiFiBroadcastReceiver(this);
        arrayAdapter =new ArrayAdapter<WiFiItem>(this.getApplicationContext(),
                        android.R.layout.simple_list_item_checked);
        listView = (ListView)this.findViewById(R.id.wiFiDomainsListView);
        listView.setAdapter(arrayAdapter);
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
}
