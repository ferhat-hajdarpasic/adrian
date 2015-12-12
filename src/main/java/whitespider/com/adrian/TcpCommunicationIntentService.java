package whitespider.com.adrian;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import whitespider.com.adrian.command.ConfigureWiFiCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class TcpCommunicationIntentService  extends IntentService {
    private static final String TAG = TcpCommunicationIntentService.class.getSimpleName();

    public TcpCommunicationIntentService() {
        super("TCP COMMS");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String dataString = intent.getDataString();
        Log.i(TAG, "Got data " + dataString);

        ConfigureWiFiCommand command = new ConfigureWiFiCommand();
        command.Domain = intent.getStringExtra("SSID");
        command.Password = "Password123";
        command.SecurityType =1;
        SocketAddress sockaddr = new InetSocketAddress("192.168.0.7", 9080);

        Socket socket = new Socket();
        try {
            socket.connect(sockaddr, 15000); //10 second connection timeout
            if (socket.isConnected()) {
                InputStream nis = socket.getInputStream();
                OutputStream nos = socket.getOutputStream();
                byte[] dataToSend = command.getBytes();
                nos.write(dataToSend); //This is blocking
                Log.i(TAG, "Message sent");
                byte[] buffer = new byte[4096];
                Log.i(TAG, "Waiting for response");
                int read = nis.read(buffer, 0, buffer.length); //This is blocking
                Log.i(TAG, "Got response");
                Log.i(TAG, "Finished collecting response");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error concatenating two arrays.", e);
            throw new RuntimeException("Error concatenating two arrays.", e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
