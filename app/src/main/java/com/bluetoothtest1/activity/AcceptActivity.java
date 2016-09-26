package com.bluetoothtest1.activity;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.bluetoothtest1.bluetoothTools.BluetoothService;
import com.bluetoothtest1.excel.ExcelUtils;
import java.io.File;


/**
 * Created by Administrator on 2016/9/15.
 */
public class AcceptActivity extends Activity implements View.OnClickListener {

    //Debug检查
    private static final boolean D = true;
    public static final String TOAST = "toast";
    public static final String TAG = "BluetoothTest";
    public static final String DEVICE_NAME = "device_name";

    //按钮
    private Button scan, clear, accept, save;

    //
    private ExcelUtils excelUtils = new ExcelUtils();
    private File file;
    private int num = 1;
    String readMessage = null;


    //数据显示布局
    StringBuffer readData = new StringBuffer();
    //显示数据
    EditText listView;

    //连接状态显示
    private TextView connectDevices;
    private TextView issave;


    //连接蓝牙名称
    private String mConnectedDeviceName = null;

    private BluetoothService mCommService;  //蓝牙连接服务

    private BluetoothDevice device = null;//蓝牙设备

    private BluetoothAdapter bluetooth;//本地蓝牙适配器

    //bluetoothService 传来的消息状态
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    private int isSave = 1;
    //请求连接的requestCode
    static final int REQUEST_CONNECT_DEVICE = 2;
    // The Handler that gets information back from the BluetoothChatService
    //这个Handler用于获取BluetoothChatService返回的数据

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            mConnectedDeviceName = device.getName();
                            connectDevices.setText(R.string.title_connected_to);
                            connectDevices.append(mConnectedDeviceName);
                            //    mConversationArrayAdapter.clear();

                            break;
                        case BluetoothService.STATE_CONNECTING:
                            connectDevices.setText(R.string.title_connecting);
                            break;

                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            connectDevices.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    msg.obj = null;
                    // construct a string from the buffer
                    //  String writeMessage = new String(writeBuf);
                    //   mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;


                case MESSAGE_READ:

                    byte[] readBuf = (byte[]) msg.obj;

                    String read = new String(readBuf, 0, msg.arg1);

                    String read2[] = read.split("c");

                    if (read2.length > 1) {
                        readMessage = read2[read2.length - 1];
                    }

                    if (readMessage != null && !readMessage.equals(" ")) {
                        readData.append(readMessage + "\n");
                        listView.setText(readData);
                    }
                    if (isSave < 0) {
                        if (readMessage != null && !readMessage.equals(" ")) {
                            num++;
                            excelUtils.writeToExcel(file, String.valueOf(num), readMessage);
                        }
                    }
                    readMessage = null;

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.accept);

        //获取本地蓝牙
        bluetooth = BluetoothAdapter.getDefaultAdapter();

        //创建表格
        file = excelUtils.setPath("mydata");
        excelUtils.createExcel(file);

        connectDevices = (TextView) findViewById(R.id.connected_device);
        listView = (EditText) findViewById(R.id.listview);
        issave = (TextView) findViewById(R.id.save1);

        //按键监听事件 scan ,clear ,save
        scan = (Button) findViewById(R.id.contentbnt);
        clear = (Button) findViewById(R.id.clearRx);
        save = (Button) findViewById(R.id.saveData);

        scan.setOnClickListener(this);
        clear.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //扫描
            case R.id.contentbnt:
                //搜索蓝牙
                Intent serverIntent = new Intent(AcceptActivity.this, SearchDerviceActivity.class);
                //启动带返回数据的活动
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                break;
            //清除
            case R.id.clearRx:
                listView.setText("");
                readData = new StringBuffer();
                break;

            //保存
            case R.id.saveData:
                isSave = ~isSave;
                if (isSave < 0) {
                    issave.setText("可以保存");
                    Intent intent = new Intent(AcceptActivity.this, SavePathActivity.class);
                    startActivityForResult(intent, 3);

                } else {
                    issave.setText("不可保存");
                }

                break;
        }
    }


    //处理接受数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(SearchDerviceActivity.EXTRA_DEVICE_ADDRESS);
                    device = bluetooth.getRemoteDevice(address);
                    //Log.i(TAG, "获取到地址，准备连接" + address);
                    //尝试连接设备
                    mCommService.connect(device);
                }
                break;
            case 3:
                if (resultCode == Activity.RESULT_OK) {
                    String pathStr = data.getStringExtra("data_return");
                    Log.i(TAG, "路径：" + pathStr);
                    //创建表格
                    file = excelUtils.setPath(pathStr);
                    excelUtils.createExcel(file);
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //打开蓝牙请求
        if (!bluetooth.isEnabled()) {
            //请求打开蓝牙设备
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intent);
        } else {
            // Toast.makeText(AcceptActivity.this, R.string.bluetooth_open_ok, Toast.LENGTH_SHORT).show();
            //  Log.i(TAG, "蓝牙打开");
            if (mCommService == null) {
                mCommService = new BluetoothService(this, mHandler);
            }

        }
    }

    @Override
    protected synchronized void onResume() {
        super.onResume();
        if (mCommService != null) {
            if (mCommService.getState() == BluetoothService.STATE_NONE) {
                // ，开启监听线程
                mCommService.start();
                Log.i(TAG, "开启监听线程");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mCommService != null) mCommService.stop();
        if (bluetooth != null) bluetooth.disable();

    }


}
