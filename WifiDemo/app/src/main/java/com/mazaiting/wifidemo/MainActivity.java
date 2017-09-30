package com.mazaiting.wifidemo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = "MainActivity";
  @BindView(R.id.scan) public Button btnScan;
  @BindView(R.id.start) public Button btnStart;
  @BindView(R.id.stop) public Button btnStop;
  @BindView(R.id.check) public Button btnCheck;
  @BindView(R.id.allNetWork) public ListView lvAllNetWork;

  private WifiAdmin mWifiAdmin;
  private List<ScanResult> list;
  private StringBuffer sb = new StringBuffer();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    mWifiAdmin = new WifiAdmin(this);
  }

  @OnClick(R.id.start) public void start() {
    mWifiAdmin.openWifi();
    Toast.makeText(this, mWifiAdmin.checkState() + "", Toast.LENGTH_SHORT).show();
  }

  @OnClick(R.id.stop) public void stop() {
    mWifiAdmin.closeWifi();
    Toast.makeText(this, mWifiAdmin.checkState() + "", Toast.LENGTH_SHORT).show();
  }

  @OnClick(R.id.scan) public void scan() {
    getAllNetWorkList();
  }

  @OnClick(R.id.check) public void check() {
    Log.e(TAG, "check: " + mWifiAdmin.checkState());
    Toast.makeText(this, mWifiAdmin.checkState() + "", Toast.LENGTH_SHORT).show();
  }

  @OnClick(R.id.allWifiInfo) public void allWifiInfo() {
    Log.e(TAG, "check: " + mWifiAdmin.getWifiInfo());
  }

  @OnClick(R.id.createWifiLock) public void createWifiLock() {
    mWifiAdmin.createWifiLock();
  }

  @OnClick(R.id.acquireWifiLock) public void acquireWifiLock() {
    mWifiAdmin.acquireWifiLock();
  }

  @OnClick(R.id.releaseWifiLock) public void releaseWifiLock() {
    mWifiAdmin.releaseWifiLock();
  }

  @OnClick(R.id.getConfiguration) public void getConfiguration() {
    List<WifiConfiguration> configuration = mWifiAdmin.getConfiguration();
    for (int i = 0; i < configuration.size(); i++) {
      //Log.e(TAG, "getConfiguration: "+configuration.get(i).enterpriseConfig.getPassword());
      Log.e(TAG, "getConfiguration: ID: " + configuration.get(i).networkId);
    }
  }

  @OnClick(R.id.disConnectionWifi) public void disConnectionWifi() {
    mWifiAdmin.disConnectionWifi(0);
  }

  @OnClick(R.id.addNetWork) public void addNetWork() {
    //config.SSID = scanResult.SSID;
    //config.BSSID = scanResult.BSSID;
    WifiConfiguration wifiConfiguration = mWifiAdmin.getConfiguration().get(0);

    boolean b = mWifiAdmin.addNetWork(wifiConfiguration);
    Log.e("TAG<", b + "  -   " + wifiConfiguration.preSharedKey);
  }

  public void getAllNetWorkList() {
    // 每次点击扫描之前清空上一次的扫描结果
    if (sb != null) {
      sb = new StringBuffer();
    }
    //开始扫描网络
    mWifiAdmin.startScan();
    list = mWifiAdmin.getWifiList();
    Log.e(TAG, "getAllNetWorkList: " + list.size());
    MyAdapter adapter = new MyAdapter(list);
    lvAllNetWork.setAdapter(adapter);
    //if (list != null) {
    //  for (int i = 0; i < list.size(); i++) {
    //    //得到扫描结果
    //    ScanResult mScanResult = list.get(i);
    //    sb = sb.append(mScanResult.BSSID + "  ")
    //        .append(mScanResult.SSID + "   ")
    //        .append(mScanResult.capabilities + "   ")
    //        .append(mScanResult.frequency + "   ")
    //        .append(mScanResult.level + "\n\n");
    //    Log.e(TAG, "getAllNetWorkList: BSSID:" + mScanResult.BSSID);
    //    Log.e(TAG, "getAllNetWorkList: capabilities:" + mScanResult.capabilities);
    //    Log.e(TAG, "getAllNetWorkList: SSID:" + mScanResult.SSID);
    //    Log.e(TAG, "getAllNetWorkList: frequency:" + mScanResult.frequency);
    //    Log.e(TAG, "getAllNetWorkList: level:" + mScanResult.level);
    //    Log.e(TAG, "getAllNetWorkList: timestamp:" + mScanResult.timestamp);
    //  }
    //}
  }

  private class MyAdapter extends BaseAdapter {
    List<ScanResult> mList;

    public MyAdapter(List<ScanResult> list) {
      this.mList = list;
    }

    @Override public int getCount() {
      return mList.size();
    }

    @Override public Object getItem(int position) {
      return mList.get(position);
    }

    @Override public long getItemId(int position) {
      return position;
    }

    @Override public View getView(final int position, View convertView, ViewGroup parent) {
      ViewHolder holder = null;
      if (convertView == null) {
        holder = new ViewHolder();
        convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_layout, null);
        holder.textView = (TextView) convertView.findViewById(R.id.textView);
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }
      holder.textView.setText(mList.get(position).SSID);
      convertView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          View layout = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_dialog_layout, null);
          AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
          builder.setTitle("请输入密码").setView(layout);
          final EditText passowrdText = (EditText) layout.findViewById(R.id.password_edittext);
          builder.setPositiveButton("连接", new DialogInterface.OnClickListener() {

            @Override public void onClick(DialogInterface dialog, int which) {
              connetionConfiguration(position, passowrdText.getText().toString());
            }
          }).show();
        }
      });
      return convertView;
    }
  }

  /**
   * 连接wifi
   *
   * @author passing
   */
  class ConnectWifiThread extends AsyncTask<String, Integer, String> {

    @Override protected String doInBackground(String... params) {
      int index = Integer.parseInt(params[0]);
      // 连接配置好指定ID的网络
      WifiConfiguration config = mWifiAdmin.createWifiInfo(
          list.get(index).SSID, params[1], 3);

      boolean b = mWifiAdmin.addNetWork(config);
      Log.e(TAG, "doInBackground: "+b);
      return null;
    }
  }

  /**
   * 连接网络
   */
  public void connetionConfiguration(int index, String password) {
    new ConnectWifiThread().execute(index + "", password);
  }

  private class ViewHolder {
    TextView textView;
  }
}
