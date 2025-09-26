package com.example.serialportdemo;

import android.bluetooth.BluetoothDevice;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.serialportdemo.utils.ByteUtils;
import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.serialport.BaudRate;
import com.newland.nsdk.core.api.common.serialport.DataBits;
import com.newland.nsdk.core.api.common.serialport.ParityBit;
import com.newland.nsdk.core.api.common.serialport.SerialPortSettings;
import com.newland.nsdk.core.api.common.serialport.StopBits;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.external.communication.CommunicatorListener;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicatorState;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicatorType;
import com.newland.nsdk.core.api.external.communication.NSDKCommunicator;
import com.newland.nsdk.core.api.internal.NSDKModuleManager;
import com.newland.nsdk.core.api.internal.beeper.Beeper;
import com.newland.nsdk.core.api.internal.serialportmanager.SerialPortManager;
import com.newland.nsdk.core.api.internal.serialportmanager.SerialPortType;
import com.newland.nsdk.core.api.internal.serialportmanager.USBSerialPort;
import com.newland.nsdk.core.external.ExtNSDKModuleManagerImpl;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity {

    private static final String ACTION_USB_PERMISSION = "com.example.serialportdemo.USB_PERMISSION";

    private TextView tvInfo;

    private Button btnRead, btnWrite, btnBeep;

    private NSDKModuleManager nsdkModuleManager;

    private SerialPortManager serialPortManager;
    private SerialPortSettings portSettings;

    ///
    /// Communicator.
    ///
    private NSDKCommunicator communicator;

    ///
    /// Listener del communicator.
    ///
    private CommunicatorListener communicatorListener = new CommunicatorListener() {
        @Override
        public BluetoothDevice onBluetoothList(ArrayList<BluetoothDevice> arrayList) {
            return null;
        }

        @Override
        public void onConnectedStateChange(ExternalCommunicatorState externalCommunicatorState) {
            LogUtils.d(getClass().getName(), "Cambio de estado de conexión >>> " + externalCommunicatorState);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial);

        initSDK();
        initExtCommunicator();

        tvInfo = findViewById(R.id.tvInfo);

        btnRead = findViewById(R.id.btnRead);
        btnWrite = findViewById(R.id.btnWrite);
        btnBeep = findViewById(R.id.btnBeep);

        serialPortManager = (SerialPortManager) NSDKModuleManagerImpl
                .getInstance().getModule(ModuleType.SERIAL_PORT_MANAGER);

        portSettings = new SerialPortSettings(
                BaudRate.BPS115200,
                DataBits.DATA_BIT_8,
                ParityBit.NO_CHECK,
                StopBits.STOP_BIT_ONE,
                false
        );

        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DBG", "----------------- Lectura serial comienza -----------------");
                try {
                    byte[] bytes = communicator.receive(3000);
                    tvInfo.setText("Recibido: " + ByteUtils.bytesToHex(bytes));
                } catch (NSDKException e) {
                    Toast.makeText(getApplicationContext(), "¡Error leyendo el puerto!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                Log.d("DBG", "----------------- Lectura serial termina -----------------");
            }
        });

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DBG", "----------------- Escritura serial comienza -----------------");
                byte[] bytes = {0x31, 0x32, 0x33, 0x34};
                try {
                    communicator.send(bytes, 3000);
                } catch (NSDKException e) {
                    Log.d("DBG", "Error al enviar los datos");
                }
                Log.d("DBG", "----------------- Escritura serial termina -----------------");
            }
        });

        btnBeep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInfo.setText("¡Beep! La librería inicia correctamente.");
                Beeper beeper = (Beeper) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.BEEPER);
                try {
                    beeper.beep(1000, 500);
                } catch (NSDKException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initSDK() {
        nsdkModuleManager = NSDKModuleManagerImpl.getInstance();
        try {
            nsdkModuleManager.init(this);
            Toast.makeText(getApplicationContext(), "¡SDK listo!", Toast.LENGTH_SHORT).show();
        } catch (NSDKException e) {
            Toast.makeText(getApplicationContext(), "Error inicializando el SDK", Toast.LENGTH_SHORT).show();
            Log.d("DBG", "ERROR:");
            e.printStackTrace();
        }
    }

    private void initExtCommunicator() {
        /// Preparar el comunicador externo.
        try {
            communicator = ExtNSDKModuleManagerImpl.getInstance().getNSDKCommunicator(this, ExternalCommunicatorType.USB, communicatorListener);
            communicator.open(5000);
            Toast.makeText(getApplicationContext(), "¡Puerto abierto!", Toast.LENGTH_SHORT).show();
        } catch (NSDKException e) {
            Log.d("DBG", "Error al preparar el comunicador y abrir el canal");
            e.printStackTrace();
        }
    }
}
