package com.mazaiting.wifidemo;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import java.util.List;

/**
 * Created by Administrator on 2017/8/8.
 */

public class WifiAdmin {

  private static final int WIFICIPHER_NOPASS = 0x01;
  private static final int WIFICIPHER_WEP = 0x02;
  private static final int WIFICIPHER_WPA = 0x03;
  /**
   * 定义一个WifiManager对象
   */
  private WifiManager mWifiManager;
  /**
   * 定义一个WifiInfo对象
   */
  private WifiInfo mWifiInfo;
  /**
   * 扫描出的网络连接列表
   */
  private List<ScanResult> mWifiList;
  /**
   * 网络连接列表
   */
  private List<WifiConfiguration> mWifiConfigurations;
  WifiManager.WifiLock mWifiLock;

  public WifiAdmin(Context context) {
    // 取得WifiManager对象
    mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    // 取得WifiInfo对象
    mWifiInfo = mWifiManager.getConnectionInfo();
  }

  /**
   * 打开Wifi
   */
  public void openWifi(){
    if(!mWifiManager.isWifiEnabled()){
      mWifiManager.setWifiEnabled(true);
    }
  }

  /**
   * 关闭Wifi
   */
  public void closeWifi(){
    if (mWifiManager.isWifiEnabled()){
      mWifiManager.setWifiEnabled(false);
    }
  }

  /**
   * 检查当前wifi状态
   * @return
   */
  public int checkState(){
    return mWifiManager.getWifiState();
  }

  /**
   * 锁定wifiLock
   */
  public void acquireWifiLock(){
    mWifiLock.acquire();
    Log.e("AKLJLK",mWifiLock.toString());
  }

  /**
   * 解锁wifiLock
   */
  public void releaseWifiLock(){
    // 判断是否锁定
    if (mWifiLock.isHeld()){
      mWifiLock.release();
    }
  }

  /**
   * 创建一个WifiLock
   */
  public void createWifiLock(){
    mWifiLock = mWifiManager.createWifiLock("test");
  }

  /**
   * 得到配置好的网络
   */
  public List<WifiConfiguration> getConfiguration(){
    return mWifiConfigurations;
  }

  /**
   * 指定配置好的网络进行连接
   * @param index
   */
  public void connectionConfiguration(int index){
    if (index>mWifiConfigurations.size()){
      return;
    }
    // 连接配置好指定ID的网络
    mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId,true);
  }

  /**
   * 扫描网络
   */
  public void startScan(){
    mWifiManager.startScan();
    // 得到扫描结果
    mWifiList = mWifiManager.getScanResults();
    // 得到配置好的网络连接
    mWifiConfigurations = mWifiManager.getConfiguredNetworks();
  }

  /**
   * 得到网络列表
   * @return
   */
  public List<ScanResult> getWifiList(){
    return mWifiList;
  }

  /**
   * 查看扫描结果
   * @return
   */
  public StringBuffer lookUpScan(){
    StringBuffer sb = new StringBuffer();
    for (int i=0;i<mWifiList.size();i++){
      sb.append("Index_" + new Integer(i + 1).toString() + ":");
      // 把ScanResult信息转换层一个字符串包
      // 其中把包括：BSSID、SSID、capabilities、frequency、level
      sb.append((mWifiList.get(i).toString())).append("\n");
    }
    return sb;
  }

  public String getMacAddress(){
    return (mWifiInfo==null)?"NULL":mWifiInfo.getMacAddress();
  }

  public String getBSSID(){
    return (mWifiInfo==null)?"NULL":mWifiInfo.getBSSID();
  }

  public int getIpAddress(){
    return (mWifiInfo==null)?0:mWifiInfo.getIpAddress();
  }
  //得到连接的ID
  public int getNetWordId(){
    return (mWifiInfo==null)?0:mWifiInfo.getNetworkId();
  }
  //得到wifiInfo的所有信息
  public String getWifiInfo(){
    return (mWifiInfo==null)?"NULL":mWifiInfo.toString();
  }
  //添加一个网络并连接
  public boolean addNetWork(WifiConfiguration configuration){
    int wcgId=mWifiManager.addNetwork(configuration);
    return mWifiManager.enableNetwork(wcgId, true);
  }
  //断开指定ID的网络
  public void disConnectionWifi(int netId){
    mWifiManager.disableNetwork(netId);
    mWifiManager.disconnect();
  }

  /**
   * 配置wifi
   *
   * @param SSID
   * @param Password
   * @param Type
   * @return
   */
  public WifiConfiguration createWifiInfo(String SSID,String Password, int Type)
  {
    WifiConfiguration config = new WifiConfiguration();
    config.allowedAuthAlgorithms.clear();
    config.allowedGroupCiphers.clear();
    config.allowedKeyManagement.clear();
    config.allowedPairwiseCiphers.clear();
    config.allowedProtocols.clear();
    config.SSID = "\"" + SSID + "\"";

    WifiConfiguration tempConfig = isExsits(SSID, mWifiManager);
    if (tempConfig != null) {
      mWifiManager.removeNetwork(tempConfig.networkId);
    }

    if (Type == WIFICIPHER_NOPASS) // WIFICIPHER_NOPASS
    {
      config.wepKeys[0] = "";
      config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
      config.wepTxKeyIndex = 0;
    }
    if (Type == WIFICIPHER_WEP) // WIFICIPHER_WEP
    {
      config.hiddenSSID = true;
      config.wepKeys[0] = "\"" + Password + "\"";
      config.allowedAuthAlgorithms
          .set(WifiConfiguration.AuthAlgorithm.SHARED);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
      config.allowedGroupCiphers
          .set(WifiConfiguration.GroupCipher.WEP104);
      config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
      config.wepTxKeyIndex = 0;
    }
    if (Type == WIFICIPHER_WPA) // WIFICIPHER_WPA
    {
      config.preSharedKey = "\"" + Password + "\"";
      config.hiddenSSID = true;
      config.allowedAuthAlgorithms
          .set(WifiConfiguration.AuthAlgorithm.OPEN);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
      config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
      config.allowedPairwiseCiphers
          .set(WifiConfiguration.PairwiseCipher.TKIP);
      // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
      config.allowedPairwiseCiphers
          .set(WifiConfiguration.PairwiseCipher.CCMP);
      config.status = WifiConfiguration.Status.ENABLED;
    }
    return config;
  }

  /**
   * 判断wifi是否存在
   *
   * @param SSID
   * @param wifiManager
   * @return
   */
  private static WifiConfiguration isExsits(String SSID,
      WifiManager wifiManager)
  {
    List<WifiConfiguration> existingConfigs = wifiManager
        .getConfiguredNetworks();
    for (WifiConfiguration existingConfig : existingConfigs) {
      if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
        return existingConfig;
      }
    }
    return null;
  }

  /**
   * 转换IP地址
   *
   * @param i
   * @return
   */
  public static String intToIp(int i)
  {
    return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
        + "." + ((i >> 24) & 0xFF);
  }
}


