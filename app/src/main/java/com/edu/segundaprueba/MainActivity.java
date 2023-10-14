package com.edu.segundaprueba;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    Button btnIncrease;
    Button btnDecrease;
    Button btnStop;
    Button btnDir;
    Socket myAppSocketWrite = null;
    Socket myAppSocketRead = null;
    public static String moduleIp = "";
    public static int modulePort = 21567;
    public static String[] CMD = {"INC", "DECR", "STOP", "DIR"};

    public InetAddress inetAddress = null;

    public static String currentAction = "";
    public static boolean dirState = true;

    public DataOutputStream dataOutputStream = null;
    public static BufferedReader dataInputStream = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            inetAddress = InetAddress.getByName(MainActivity.moduleIp);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        btnIncrease = (Button) findViewById(R.id.increase);
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAction(CMD[0]);

            }
        });
        btnDecrease = (Button) findViewById(R.id.decrease);
        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAction(CMD[1]);

            }
        });
        btnStop = (Button) findViewById(R.id.stopBtn);

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAction(CMD[2]);

            }
        });
        btnDir = (Button) findViewById(R.id.changeDir);
        btnDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAction(CMD[3]);
            }
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                myAppSocketRead = new java.net.Socket(inetAddress, 21566);
                dataInputStream = new BufferedReader(new InputStreamReader(myAppSocketRead.getInputStream()));

                while (true) {
                    String strCurrentLine = "";
                    while (!(strCurrentLine = dataInputStream.readLine()).equals("\n")) {
                        // Print the line to the console.
                        System.out.println(strCurrentLine);
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        });

        executor.execute(() -> {
            try{
                myAppSocketWrite = getMyAppSocketWrite();
                dataOutputStream = new DataOutputStream(myAppSocketWrite.getOutputStream());
                String test = "Welcome to the server";
                dataOutputStream.write(test.getBytes(StandardCharsets.UTF_8));
                dataOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    protected void setAction(String action) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try{
                myAppSocketWrite = getMyAppSocketWrite();
                dataOutputStream = new DataOutputStream(myAppSocketWrite.getOutputStream());
                dataOutputStream.write(action.getBytes(StandardCharsets.UTF_8));
                dataOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    protected Socket getMyAppSocketWrite() throws IOException {
        if (myAppSocketWrite != null) {
            return myAppSocketWrite;
        } else {
           return myAppSocketWrite = new java.net.Socket(inetAddress, modulePort);
        }

    }
}

