
package com.reactlibrary;
//package com.facebook.react.bridge;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import com.facebook.react.bridge.ReactMethod;
import com.reactlibrary.AudioWaveOut;
import com.facebook.react.bridge.Callback;

//import java.util.ArrayList;
import android.util.Log;


public class RNWaveformAudioLibModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNWaveformAudioLibModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNWaveformAudioLib";
  }

  @ReactMethod
  public void SendSquareWave(com.facebook.react.bridge.ReadableArray nFreq, com.facebook.react.bridge.ReadableArray nFch, com.facebook.react.bridge.ReadableArray nAmps, float fLen)
  {
    if (nFreq.size()>2 || nFreq.size() == 0 || nFch.size()>2 || nFch.size() == 0 || nAmps.size()>2 || nAmps.size()==0 || fLen>999f || fLen<0f)
      return;

    if(nFreq.getInt(0)<20 || nFch.getInt(0)<0 || nFch.getInt(0)>1 || nAmps.getInt(0)<0 || nAmps.getInt(0)>25)
      return;

    if (nFreq.size()>1) {
      if (nFreq.getInt(1) < 20)
        return;
    }

    if (nFch.size()>1)
    {
      if(nFch.getInt(1) < 0 || nFch.getInt(1) > 1)
        return;
    }

    if (nAmps.size()>1) {
      if (nAmps.getInt(1) < 0 || nAmps.getInt(1) > 25)
        return;
    }

    if(AudioWaveOut.getInstance() == null)
      return;

    AudioWaveOut.getInstance().resetting();// 重置参数
    boolean isbSuccess = AudioWaveOut.getInstance().setLorRChannl(0);
    if (!isbSuccess)
      return;

      AudioWaveOut.getInstance().setVolumeValue(reactContext);
      AudioWaveOut.getInstance().setFreqHz(nFreq.getInt(0), nFreq.getInt(1));// 频率
      AudioWaveOut.getInstance().setFch(nFch.getInt(0), nFch.getInt(1));
      AudioWaveOut.getInstance().setAmp(nAmps.getInt(0), nAmps.getInt(1));
      AudioWaveOut.getInstance().setRunloop(fLen);

      AudioWaveOut.getInstance().sendByteData();
  }

  @ReactMethod
  public void StopSquareWave(float nTime) {
    if (nTime < 0)
      return;

    if(AudioWaveOut.getInstance() == null)
      return;

      AudioWaveOut.getInstance().setCloseTime(nTime);
  }

  @ReactMethod
  public void SendPulseSequence(com.facebook.react.bridge.ReadableArray nFpsi, com.facebook.react.bridge.ReadableArray nAmp_pp, com.facebook.react.bridge.ReadableArray nPstd)
    {
    if (nFpsi.size()>2 || nFpsi.size() == 0 || nAmp_pp.size()>2 || nAmp_pp.size() == 0 || nPstd.size()>2 || nPstd.size()==0 )
      return;

    if(nFpsi.getInt(0)<0 || nFpsi.getInt(0)>1 || nAmp_pp.getInt(0)<0 || nAmp_pp.getInt(0)>25 || nPstd.getArray(0).size()<0 || nPstd.getArray(0).size()>999)//nPstd.getString(0).length()<=0)
      return;

    if (nFpsi.size()>1) {
      if (nFpsi.getInt(1) < 0 || nFpsi.getInt(1) > 1)
        return;
    }

    if (nAmp_pp.size()>1)
    {
      if(nAmp_pp.getInt(1) < 0 || nAmp_pp.getInt(1) > 25)
        return;
    }

    if (nPstd.size()>1) {
      if (nPstd.getArray(1).size() < 0 || nPstd.getArray(1).size() > 999)
        return;
    }


      if(AudioWaveOut.getInstance() == null)
        return;

      AudioWaveOut.getInstance().resettingPulse();// 重置参数
      boolean isbSuccess = AudioWaveOut.getInstance().setLorRChannl(0);
      if (!isbSuccess)
        return;

      AudioWaveOut.getInstance().setVolumeValue(reactContext);
      AudioWaveOut.getInstance().setFpsi(nFpsi.getInt(0), nFpsi.getInt(1));
      AudioWaveOut.getInstance().setAmp(nAmp_pp.getInt(0), nAmp_pp.getInt(1));
      AudioWaveOut.getInstance().setPstd(nPstd.getArray(0), nPstd.getArray(1));

      AudioWaveOut.getInstance().sendSpacePulseByteData();
}

}