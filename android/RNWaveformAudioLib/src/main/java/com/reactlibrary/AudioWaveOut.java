package com.reactlibrary;

import java.util.*;
import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.text.DecimalFormat;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.io.RandomAccessFile;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.*;

public class AudioWaveOut {
	private String modelName;
	private final float max_iAmp = 11f;
	private final float min_iAmp = -11f;
	private float curr_L_iAmp = 1;
	private float curr_R_iAmp = 1;
	private Boolean misbLoop = false;
	private int mLoopCount = -1;
	private int mLFreqHz = 20;// 20HZ
	private int mRFreqHz = 20;// 20HZ
	private String m_data;// 得到的数据
	private int m_lenght;// 得到数据的有效长度
	private int mLorR = 2;//0为左，1为右, 2以左同时, 3以右同时
	private int mLHorL = 1;//1正半周期，0负
	private int mRHorL = 1;//1正半周期，0负
	private int mLFpsi = 1;//1 Pulse, 0 Space
	private int mRFpsi = 1;//1 Pulse, 0 Space

	private int nLeftPos = 0;
	private int nRightPos = 0;

	private float l_pulseArray[] = null;
	private float l_spaceArray[] = null;

	private float r_pulseArray[] = null;
	private float r_spaceArray[] = null;

	private  ArrayList mLPulseList = new ArrayList();
	private  ArrayList mLSpaceList = new ArrayList();

	private  ArrayList mRPulseList = new ArrayList();
	private  ArrayList mRSpaceList = new ArrayList();

	private int mDataWriteCount = 0;

	private Integer mLPulseCount = 0;
	private Integer mLSpaceCount = 0;

	private Integer mRPulseCount = 0;
	private Integer mRSpaceCount = 0;

	private int m_i️SquareWave = 58;
	private int sampleRateInHz = 44100;//44100;//48000 // sampling frequency
	private int mChannel = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
	private int mSampBit = AudioFormat.ENCODING_PCM_FLOAT;
	private AudioTrack audioTrackF;
	private float[] m_bitDateF = null ;

	private float[] m_L_bitDateF = null ;
	private float[] m_R_bitDateF = null ;

	private int mLPstd = 499;//1~999
	private int mRPstd = 499;//1~999

	private float[] m_bitSpaceDateF = null;
	private float[] m_bitPulseDateF = null;

	private AudioTrackFThread audioTrackThread = null;
	private Boolean misbThreadLoop = true;

	private volatile BigDecimal mCloseTime = new BigDecimal(-1);

	private static class SingleTonHoulder{
		public static AudioWaveOut SingleTonHoulder;
		static{

				SingleTonHoulder = new AudioWaveOut();

		}

	}


	public static AudioWaveOut getInstance(){
			return SingleTonHoulder.SingleTonHoulder;
	}

	private AudioWaveOut()
	{

			int bufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, mChannel,
					mSampBit);
			//bufferSize = bufferSize*2;
			audioTrackF = new AudioTrack(AudioManager.STREAM_MUSIC,
					sampleRateInHz, mChannel, mSampBit, bufferSize,
					AudioTrack.MODE_STREAM);//MODE_STATIC
			m_bitDateF = new float[bufferSize];
			for (int i =0;i<bufferSize;i++)
				m_bitDateF[i] = (float)0;

			try {

			if (audioTrackThread == null) {
				audioTrackThread = new AudioTrackFThread();
				audioTrackThread.start();
				audioTrackF.play();
			}

			} catch (Exception e) {
				e.printStackTrace();
			}
	}

 	void initAudioTrack()
	{
		int bufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, mChannel,
				mSampBit);

		audioTrackF = new AudioTrack(AudioManager.STREAM_SYSTEM,
				sampleRateInHz, mChannel, mSampBit, bufferSize,
				AudioTrack.MODE_STREAM);//MODE_STATIC
		m_bitDateF = new float[bufferSize];
		for (int i =0;i<bufferSize;i++)
			m_bitDateF[i] = (float)0;

		try {

			if (audioTrackThread == null) {
				//AudioWaveOut.setEndLatch(2);
				audioTrackThread = new AudioTrackFThread();
				audioTrackThread.start();
				audioTrackF.play();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void resetting()
	{
		if (audioTrackF == null)
			initAudioTrack();

		sampleRateInHz = 44100;

		mDataWriteCount = 0;

		nLeftPos = 0;
		nRightPos = 0;

		mLPstd = 0;
		mRPstd = 0;

		misbLoop = false;
		mLoopCount = -1;

		mLHorL = 1;
		mRHorL = 1;
		m_i️SquareWave = 58;
		mLFreqHz = 20;
		mRFreqHz = 20;

		curr_L_iAmp = 1;
		curr_R_iAmp = 1;
	}

	public void resettingPulse()
	{
		if (audioTrackF == null)
			initAudioTrack();

		mLFpsi = 1;
		mRFpsi = 1;

		mDataWriteCount = 0;

		mLPulseCount = 0;
		mLSpaceCount = 0;

		mRPulseCount = 0;
		mRSpaceCount = 0;

		if (l_pulseArray != null)
			l_pulseArray = null;

		if (l_spaceArray != null)
			l_spaceArray = null;

		if (r_pulseArray != null)
			r_pulseArray = null;

		if (r_spaceArray != null)
			r_spaceArray = null;

		if (mLPulseList.size()>0)
			mLPulseList.clear();

		if (mLSpaceList.size()>0)
			mLSpaceList.clear();

		if (mRPulseList.size()>0)
			mRPulseList.clear();

		if (mRSpaceList.size()>0)
			mRSpaceList.clear();
	}

	public void setRunloop(float fLoop)
	{
		if (fLoop == 0.0f)
			misbLoop = true;
		else
			misbLoop = false;

		mLoopCount = (int)(fLoop*2f);
	}

	public boolean setLorRChannl(int r)
	{
		audioTrackF.setStereoVolume(7f, 7f);

		return true;
	}

	public int getLorRChannl()
	{
		if (mLorR == 2)
			return 0;
		if (mLorR == 3)
			return 1;

		return mLorR;
	}

	public void setVolumeValue(Context context)
	{
		AudioManager am =(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);

		am.adjustStreamVolume(AudioManager.STREAM_SYSTEM
				, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND) ;
	}

	public void setFreqHz(int nLHZ, int nRHZ)
	{
		mLFreqHz = nLHZ;
		mRFreqHz = nRHZ;
	}

	public void setFch(int nLHorL, int nRHorL)
	{
		mLHorL = nLHorL;
		mRHorL = nRHorL;
	}

	public void setSampleRateInHz(int nSampleRate)
	{
		sampleRateInHz = nSampleRate;
	}

	public void setFpsi(int nLFpsi, int nRFpsi)
	{
		mLFpsi = nLFpsi;
		mRFpsi = nRFpsi;
	}

	public void setAmp(int nLAmp, int nRAmp)
	{
		curr_L_iAmp = (float)nLAmp;
		curr_R_iAmp = (float)nRAmp;
	}

	void leftPstd(double bpms)
	{
		if(l_pulseArray != null) {
			for (int i = 0; i < l_pulseArray.length; i++) {
				float seed = l_pulseArray[i];

				if (seed < 0.02f)
					seed = 0.02f;
				else if(seed > 50f)
					seed = 50f;

				if (seed == 0.02f)
					l_pulseArray[i] = 1;
				else
					l_pulseArray[i] = (float) (seed * bpms) / 32;
			}
		}

		if(l_spaceArray != null) {
			for (int i = 0; i < l_spaceArray.length; i++) {
				float seed = l_spaceArray[i];

				if (seed < 0.02f)
					seed = 0.02f;
				else if(seed > 200f)
					seed = 200f;

				if (seed == 0.02f)
					l_spaceArray[i] = 1;
				else
					l_spaceArray[i] = (float) (seed * bpms) / 32;
			}
		}
	}

	void rightPstd(double bpms)
	{
		if(r_pulseArray != null) {
			for (int i = 0; i < r_pulseArray.length; i++) {
				float seed = r_pulseArray[i];

				if (seed < 0.02f)
					seed = 0.02f;
				else if(seed > 50f)
					seed = 50f;

				if (seed == 0.02f)
					r_pulseArray[i] = 1;
				else
					r_pulseArray[i] = (float) (seed * bpms) / 32;
			}
		}

		if(r_spaceArray != null) {
			for (int i = 0; i < r_spaceArray.length; i++) {
				float seed = r_spaceArray[i];

				if (seed < 0.02f)
					seed = 0.02f;
				else if(seed > 200f)
					seed = 200f;

				if (seed == 0.02f)
					r_spaceArray[i] = 1;
				else
					r_spaceArray[i] = (float) (seed * bpms) / 32;
			}
		}
	}

	public void setPstd(com.facebook.react.bridge.ReadableArray nLPstd, com.facebook.react.bridge.ReadableArray nRPstd)
	{
		int nLPstdCount = nLPstd.size();
		mLPstd = nLPstdCount;

		int nRPstdCount = nRPstd.size();
		mRPstd = nRPstdCount;

		double bpms = (double)sampleRateInHz*mSampBit*8*1/1000.0;
		float step = (float)1000/sampleRateInHz;

		DecimalFormat formater = new DecimalFormat("#0.###");
		formater.setRoundingMode(RoundingMode.FLOOR);
		step = Float.parseFloat(formater.format(step));

		float stepPulseCount = 50f;
		float stepSpaceCount = 200f;

		if (nLPstdCount%2 == 0) {
			l_pulseArray = new float[nLPstdCount/2];
			l_spaceArray = new float[nLPstdCount/2];

		}else
		{
			if (mLFpsi==0)//1 Pulse, 0 Space
			{
				if (nLPstdCount == 1)
				{
					l_spaceArray = new float[nLPstdCount];
				}
				else
				{
					l_pulseArray = new float[nLPstdCount / 2];
					l_spaceArray = new float[nLPstdCount / 2 + 1];
				}
			}else
			{
				if (nLPstdCount == 1)
				{
					l_pulseArray = new float[nLPstdCount];
				}
				else
				{
					l_pulseArray = new float[nLPstdCount / 2 + 1];
					l_spaceArray = new float[nLPstdCount / 2];
				}
			}
		}

		int s = 0;
		int p = 0;
		for(int i=0;i<nLPstdCount;i++)
		{
			if (mLFpsi==0)//1 Pulse, 0 Space
			{
				float number = 0;
				String strNumber = nLPstd.getString(i);
				number = Float.parseFloat(strNumber);

				if (number < 0.02f)
					number = 0.02f;

				if (i%2 == 0) {
					if (number > stepSpaceCount)
						number = stepSpaceCount;

					l_spaceArray[s] = number;
					s++;
				}
				else {
					if (number > stepPulseCount)
						number = stepPulseCount;

					l_pulseArray[p] = number;
					p++;
				}

			}else
			{
				float number = 0;
				String strNumber = nLPstd.getString(i);
				number = Float.parseFloat(strNumber);

				if (number < 0.02f)
					number = 0.02f;

				if (i%2 == 0) {
					if (number > stepPulseCount)
						number = stepPulseCount;

					l_pulseArray[p] = number;
					p++;
				}else {
					if (number > stepSpaceCount)
						number = stepSpaceCount;

					l_spaceArray[s] = number;
					s++;
				}
			}
		}

		if (nRPstdCount%2 == 0) {
			r_pulseArray = new float[nRPstdCount/2];
			r_spaceArray = new float[nRPstdCount/2];

		}else
		{
			if (mRFpsi==0)//1 Pulse, 0 Space
			{
				if (nRPstdCount == 1)
				{
					r_spaceArray = new float[nRPstdCount];
				}
				else
				{
					r_pulseArray = new float[nRPstdCount / 2];
					r_spaceArray = new float[nRPstdCount / 2 + 1];
				}
			}else
			{
				if (nRPstdCount == 1)
				{
					r_pulseArray = new float[nRPstdCount];
				}
				else
				{
					r_pulseArray = new float[nRPstdCount / 2 + 1];
					r_spaceArray = new float[nRPstdCount / 2];
				}
			}
		}

		s = 0;
		p = 0;
		for(int i=0;i<nRPstdCount;i++)
		{
			if (mRFpsi==0)//1 Pulse, 0 Space
			{
				float number = 0;
				String strNumber = nRPstd.getString(i);
				number = Float.parseFloat(strNumber);

				if (number < 0.02f)
					number = 0.02f;

				if (i%2 == 0) {
					if (number > stepSpaceCount)
						number = stepSpaceCount;

					r_spaceArray[s] = number;
					s++;
				}
				else {
					if (number > stepPulseCount)
						number = stepPulseCount;

					r_pulseArray[p] = number;
					p++;
				}

			}else
			{
				float number = 0;
				String strNumber = nRPstd.getString(i);
				number = Float.parseFloat(strNumber);

				if (number < 0.02f)
					number = 0.02f;

				if (i%2 == 0) {
					if (number > stepPulseCount)
						number = stepPulseCount;

					r_pulseArray[p] = number;
					p++;
				}else {
					if (number > stepSpaceCount)
						number = stepSpaceCount;

					r_spaceArray[s] = number;
					s++;
				}
			}
		}

		leftPstd(bpms);
		rightPstd(bpms);
	}

	public void leftByteData()
	{
		m_i️SquareWave = sampleRateInHz/mLFreqHz/2;

		String bitdata = "";
		if (mLoopCount > 0) {
			for (int i = 0; i < mLoopCount; i++) {
				if (mLHorL == 1) {
					if (i%2 == 0)
						bitdata += "1";
					else
						bitdata += "0";
				} else {
					if (i%2 == 0)
						bitdata += "0";
					else
						bitdata += "1";
				}
			}
		}else {
			if (mLHorL == 1) {
				bitdata += "10";
			} else {
				bitdata += "01";
			}
		}

		this.m_data = bitdata;
		this.m_lenght = bitdata.length();

		m_L_bitDateF = null;

		m_L_bitDateF = getShortData(curr_L_iAmp);

	}

	public void rightByteData()
	{
		m_i️SquareWave = sampleRateInHz/mRFreqHz/2;

		String bitdata = "";
		if (mLoopCount > 0) {
			for (int i = 0; i < mLoopCount; i++) {
				if (mRHorL == 1) {
					if (i%2 == 0)
						bitdata += "1";
					else
						bitdata += "0";
				} else {
					if (i%2 == 0)
						bitdata += "0";
					else
						bitdata += "1";
				}
			}
		}else {
			if (mRHorL == 1) {
				bitdata += "10";
			} else {
				bitdata += "01";
			}
		}

		this.m_data = bitdata;
		this.m_lenght = bitdata.length();

		m_R_bitDateF = null;

		m_R_bitDateF = getShortData(curr_R_iAmp);

	}

	public void sendByteData()
	{
		if (audioTrackF == null)
			initAudioTrack();

		audioTrackF.flush();

		leftByteData();
		rightByteData();

		m_bitDateF = null;

		int bufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, mChannel,
				mSampBit);

		int count = 0;
		float m_TDateF[] = null;
			if (m_L_bitDateF != null && m_R_bitDateF != null)
			{
				if (m_L_bitDateF.length < m_R_bitDateF.length)
					count = m_R_bitDateF.length;
				else
					count = m_L_bitDateF.length;

				m_TDateF = new float[count*2];
				for (int i=0,j=0;i<count;i++)
				{
					if (i < m_L_bitDateF.length)
					{
						m_TDateF[j] = m_L_bitDateF[i];
					}else
					{
						m_TDateF[j] = m_L_bitDateF[i%m_L_bitDateF.length];
						nLeftPos = i%m_L_bitDateF.length;
					}

					if (i < m_R_bitDateF.length)
					{
						m_TDateF[j+1] = m_R_bitDateF[i];
					}else
					{
						m_TDateF[j+1] = m_R_bitDateF[i%m_R_bitDateF.length];
						nRightPos = i%m_R_bitDateF.length;
					}
					j+=2;
				}
			}else
				return;

		if (!misbLoop) {
			if (count*2 < bufferSize) {
				m_bitDateF = new float[bufferSize];
			}else
				m_bitDateF = new float[count*2];

			for (int i = 0; i < m_bitDateF.length; i++)
				m_bitDateF[i] = (float) 0.0f;
			System.arraycopy(m_TDateF, 0, m_bitDateF, 0, m_TDateF.length);
		}else
		{
			m_bitDateF = new float[m_TDateF.length];
			System.arraycopy(m_TDateF, 0, m_bitDateF, 0, m_TDateF.length);
		}

		if (audioTrackThread == null) {
			//AudioWaveOut.setEndLatch(2);
			audioTrackThread = new AudioTrackFThread();
			audioTrackThread.start();
			audioTrackF.play();
		}else
		{

			synchronized (audioTrackThread) {
				if (audioTrackF != null)
					audioTrackF.play();
				audioTrackThread.notify();
			}
		}

	}

	float[] leftSpacePulseData()
	{
		mLFreqHz = 1000;

		m_i️SquareWave = sampleRateInHz/mLFreqHz/2;

		mLSpaceCount = getSpaceData(curr_L_iAmp, l_spaceArray, mLSpaceList);
		mLPulseCount = getPulseData(curr_L_iAmp, l_pulseArray, mLPulseList);

		int flat_count = 0;
		float left_data[] = null;
		if (mLSpaceCount + mLPulseCount <=0)
			return null;
		else
			left_data = new float[mLSpaceCount + mLPulseCount];

		// 1 pulse  0 space
		if (mLFpsi == 0)
		{
			if (mLPstd == 1)
			{
				float tempSpace[] = (float[]) mLSpaceList.get(0);

				System.arraycopy(tempSpace, 0, left_data, 0, tempSpace.length);


			}else {
				int i = 0;
				int count = flat_count;
				for (; i < mLPstd / 2; i++) {
					float tempSpace[] = (float[]) mLSpaceList.get(i);
					float tempPulse[] = (float[]) mLPulseList.get(i);

					System.arraycopy(tempSpace, 0, left_data, count, tempSpace.length);
					System.arraycopy(tempPulse, 0, left_data, count + tempSpace.length, tempPulse.length);

					count = count + tempPulse.length + tempSpace.length;
				}

				if (mLSpaceList.size() > mLPulseList.size()) {
					float tempSpace[] = (float[]) mLSpaceList.get(i);

					System.arraycopy(tempSpace, 0, left_data, count, tempSpace.length);
				} else if (mLSpaceList.size() < mLPulseList.size()) {
					float tempPulse[] = (float[]) mLPulseList.get(i);
					System.arraycopy(tempPulse, 0, left_data, count, tempPulse.length);
				}
			}
		}else
		{
			if (mLPstd == 1)
			{
				float tempPulse[] = (float[]) mLPulseList.get(0);

				System.arraycopy(tempPulse, 0, left_data, 0, tempPulse.length);

			}else {
				int i = 0;
				int count = flat_count;
				for (; i < mLPstd / 2; i++) {
					float tempSpace[] = (float[]) mLSpaceList.get(i);
					float tempPulse[] = (float[]) mLPulseList.get(i);

					System.out.println("tempSpace length  : "+tempSpace.length);
					System.out.println("tempPulse length  : "+tempPulse.length);
					System.arraycopy(tempPulse, 0, left_data, count, tempPulse.length);

					System.arraycopy(tempSpace, 0, left_data, count + tempPulse.length, tempSpace.length);

					count = count + tempPulse.length + tempSpace.length;;
				}

				if (mLSpaceList.size() > mLPulseList.size()) {
					float tempSpace[] = (float[]) mLSpaceList.get(i);
					System.arraycopy(tempSpace, 0, left_data, count, tempSpace.length);

				} else if (mLSpaceList.size() < mLPulseList.size()) {
					float tempPulse[] = (float[]) mLPulseList.get(i);
					System.arraycopy(tempPulse, 0, left_data, count, tempPulse.length);

				}
			}
		}

		return  left_data;
	}

	float[] rightSpacePulseData()
	{
		mLFreqHz = 1000;

		m_i️SquareWave = sampleRateInHz/mRFreqHz/2;

		mRSpaceCount = getSpaceData(curr_R_iAmp, r_spaceArray, mRSpaceList);
		mRPulseCount = getPulseData(curr_R_iAmp, r_pulseArray, mRPulseList);

		float right_data[] = null;
		if (mRSpaceCount + mRPulseCount <=0)
			return null;
		else
			right_data = new float[mRSpaceCount + mRPulseCount];

		int flat_count = 0;
		// 1 pulse  0 space
		if (mRFpsi == 0)
		{
			if (mRPstd == 1)
			{
				float tempSpace[] = (float[]) mRSpaceList.get(0);

				System.arraycopy(tempSpace, 0, right_data, 0, tempSpace.length);

			}else {
				int i = 0;
				int count = flat_count;
				for (; i < mRPstd / 2; i++) {
					float tempSpace[] = (float[]) mRSpaceList.get(i);
					float tempPulse[] = (float[]) mRPulseList.get(i);

					System.arraycopy(tempSpace, 0, right_data, count, tempSpace.length);
					System.arraycopy(tempPulse, 0, right_data, count + tempSpace.length, tempPulse.length);

					count = count + tempPulse.length + tempSpace.length;
				}

				if (mRSpaceList.size() > mRPulseList.size()) {
					float tempSpace[] = (float[]) mRSpaceList.get(i);

					System.arraycopy(tempSpace, 0, right_data, count, tempSpace.length);
				} else if (mRSpaceList.size() < mRPulseList.size()) {
					float tempPulse[] = (float[]) mRPulseList.get(i);
					System.arraycopy(tempPulse, 0, right_data, count, tempPulse.length);
				}
			}
		}else
		{
			if (mRPstd == 1)
			{
				float tempPulse[] = (float[]) mRPulseList.get(0);

				System.arraycopy(tempPulse, 0, right_data, 0, tempPulse.length);

			}else {
				int i = 0;
				int count = flat_count;
				for (; i < mRPstd / 2; i++) {
					float tempSpace[] = (float[]) mRSpaceList.get(i);
					float tempPulse[] = (float[]) mRPulseList.get(i);

					System.arraycopy(tempPulse, 0, right_data, count, tempPulse.length);

					System.arraycopy(tempSpace, 0, right_data, count + tempPulse.length, tempSpace.length);

					count = count + tempPulse.length + tempSpace.length;
				}

				if (mRSpaceList.size() > mRPulseList.size()) {
					float tempSpace[] = (float[]) mRSpaceList.get(i);
					System.arraycopy(tempSpace, 0, right_data, count, tempSpace.length);

				} else if (mRSpaceList.size() < mRPulseList.size()) {
					float tempPulse[] = (float[]) mRPulseList.get(i);
					System.arraycopy(tempPulse, 0, right_data, count, tempPulse.length);

				}
			}
		}

		return right_data;
	}

	public void sendSpacePulseByteData()
	{
		if (audioTrackF == null)
			initAudioTrack();

		float leftData[] = leftSpacePulseData();
		float rightData[] = rightSpacePulseData();

		if (leftData != null && rightData != null)
		{
			int bufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, mChannel,
				mSampBit);

			m_bitDateF = null;

			if ((leftData.length + rightData.length) > bufferSize) {
				m_bitDateF = new float[leftData.length + rightData.length];
			}else
				m_bitDateF = new float[bufferSize];

			for (int i =0;i<m_bitDateF.length;i++)
				m_bitDateF[i] = (float)0.0f;

			int count = 0;
			if (leftData.length < rightData.length)
				count = rightData.length;
			else
				count = leftData.length;

			for (int i=0,j=0;i<count;i++)
			{
				if (i < leftData.length)
				{
					m_bitDateF[j] = leftData[i];
				}else
				{
					m_bitDateF[j] = 0;
				}

				if (i < rightData.length)
				{
					m_bitDateF[j+1] = rightData[i];
				}else
				{
					m_bitDateF[j+1] = 0;
				}
				j+=2;
			}
		}else
			return;

		setRunloop(1);

		if (audioTrackThread == null) {

			audioTrackThread = new AudioTrackFThread();
			audioTrackThread.start();
			audioTrackF.play();
		}else
		{
			synchronized (audioTrackThread) {

				if (audioTrackF != null)
					audioTrackF.play();
				audioTrackThread.notify();
			}
		}
	}


	private int getPulseData(float curr_iAmp, float[] pulseArray, ArrayList mPulseList)
	{
		if (pulseArray == null)
			return 0;

		final float order = 25;
		final float min_order = min_iAmp/order;
		final float max_order = max_iAmp/order;

		int mPulseCount = 0;

		for (int k=0;k<pulseArray.length;k++)
		{
			int j = 0;

			int m_iSW = (int)pulseArray[k];

			int m_bitDateSize = m_iSW;
			float m_pPulseDate[] = new float[m_bitDateSize];

			int ct = m_iSW;

			while (ct-- > 0) {
				if (curr_iAmp == 25) {
					m_pPulseDate[j++] = max_iAmp;

				}else
					m_pPulseDate[j++] = (float)(max_order * curr_iAmp);
			}

			mPulseList.add(m_pPulseDate);
			mPulseCount+=m_bitDateSize;
		}

		return mPulseCount;
	}

	private int getSpaceData(float curr_iAmp, float spaceArray[], ArrayList mSpaceList)
	{
		if (spaceArray == null)
			return 0;

		final float order = 25;
		final float min_order = min_iAmp/order;
		final float max_order = max_iAmp/order;

		int mSpaceCount = 0;
		for (int k=0;k<spaceArray.length;k++)
		{
			int j = 0;
			int m_iSW = (int)spaceArray[k];

			int m_bitDateSize = m_iSW;
			float[] m_pSpaceDate = new float[m_bitDateSize];

			int ct = m_iSW;

				while (ct-- > 0) {
					if (curr_iAmp == 25)
						m_pSpaceDate[j++] = min_iAmp;
					else
						m_pSpaceDate[j++] = (float) (min_order * curr_iAmp);
				}

			mSpaceList.add(m_pSpaceDate);
			mSpaceCount+=m_bitDateSize;
		}

		return mSpaceCount;
	}

	// 通过byteDate转为short型的声音数据
	private float[] getShortData(float curr_iAmp)
	{
		int j = 0;
		int nWaveCount = m_i️SquareWave;
		String strBinary = this.m_data;
		int len = strBinary.length();
		int m_bitDateSize = len * m_i️SquareWave;
		float[] m_pDate = new float[m_bitDateSize];
		System.out.println("m_bitDateSize : "+m_bitDateSize);
		final float order = 25;
		final float min_order = (float)(min_iAmp/order);
		final float max_order = (float)(max_iAmp/order);

		for (int i = 0; i < len; i++)
		{
			int ct = nWaveCount;

				if (strBinary.charAt(i) == '1')
				{

					while (ct-- > 0) {

						if (curr_iAmp == 25)
							m_pDate[j++] = max_iAmp;
						else
							m_pDate[j++] = (float)(max_order*curr_iAmp);
					}
				} else {

					while (ct-- > 0) {
						if (curr_iAmp == 25)
							m_pDate[j++] = min_iAmp;
						else
							m_pDate[j++] = (float)(min_order*curr_iAmp);
					}
				}
		}

		return m_pDate;
	}

	// 将一个字节编码转为2进制字符串
	private String getstrBinary(byte[] date, int lenght) {
		StringBuffer strDate = new StringBuffer(lenght * 8);
		for (int i = 0; i < lenght; i++) {
			String str = Integer.toBinaryString(date[i]);

			while (str.length() < 8) {
				str = '0' + str;
			}
			strDate.append(str);
		}

		return strDate.toString();
	}

	public synchronized void setCloseTime(double mTime)
	{
		mCloseTime = new BigDecimal(mTime/1000);// 毫秒
	}

	public void setThreadLoop(boolean isbThreadLoop)
	{
		misbThreadLoop = isbThreadLoop;
	}

	private float[] reData()
	{
		int count = 0;
		float m_TDateF[] = null;
		if (m_L_bitDateF != null && m_R_bitDateF != null)
		{
			if (m_L_bitDateF.length < m_R_bitDateF.length)
				count = m_R_bitDateF.length;
			else
				count = m_L_bitDateF.length;

			int lPos = 0;
			int rPos = 0;
			m_TDateF = new float[count*2];
			for (int i=0,j=0;i<count;i++)
			{
				if (i < m_L_bitDateF.length)
				{
					if (nLeftPos == 0)
						m_TDateF[j] = m_L_bitDateF[i];
					else
						m_TDateF[j] = m_L_bitDateF[(i+nLeftPos+1)%m_L_bitDateF.length];
				}else
				{
					m_TDateF[j] = m_L_bitDateF[(i+nLeftPos+1)%m_L_bitDateF.length];
					lPos = (i+nLeftPos+1)%m_L_bitDateF.length;
				}

				if (i < m_R_bitDateF.length)
				{
					if (nRightPos == 0)
						m_TDateF[j+1] = m_R_bitDateF[i];
					else
						m_TDateF[j+1] = m_R_bitDateF[(i+nRightPos+1)%m_R_bitDateF.length];
				}else
				{
					m_TDateF[j+1] = m_R_bitDateF[(i+nRightPos+1)%m_R_bitDateF.length];
					rPos = (i+nRightPos+1)%m_R_bitDateF.length;
				}
				j+=2;
			}

			nLeftPos = lPos;
			nRightPos = rPos;
		}else
			return null;


		return m_TDateF;
	}

	private void playWaveF(float[] mbitDateF) {

		if (audioTrackF == null) {
			System.out.println(modelName+" audioTrackF is null, return");
			return;
		}

		BigDecimal loopTime = new BigDecimal(0);
		long beginCloseTimeStart = 0;
		long endCloseTimeStart = 0;

		mDataWriteCount = 0;
		int bufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, mChannel,
				mSampBit);

		while(misbLoop || mLoopCount>0) {
			long start = 0;
			long end = 0;
			endCloseTimeStart = 0;

			if (loopTime.compareTo(mCloseTime) == -1 && beginCloseTimeStart == 0) {
				beginCloseTimeStart = System.nanoTime();//纳秒
			}else if(loopTime.compareTo(mCloseTime) == 0)
			{
				break;
			}

			if (beginCloseTimeStart > 0)
				start = System.nanoTime();//纳秒

				if (mbitDateF != null && mbitDateF.length > 0) {
					if (mbitDateF.length > bufferSize) {
						if (mDataWriteCount < mbitDateF.length) {
							int dataCount = bufferSize;
							if ((mbitDateF.length - mDataWriteCount) < dataCount)
								dataCount = mbitDateF.length - mDataWriteCount;
							int afw = audioTrackF.write(mbitDateF, mDataWriteCount, dataCount, AudioTrack.WRITE_BLOCKING);// 该方法是阻塞的

							mDataWriteCount += afw;
						} else {
							mDataWriteCount = 0;
							if (mLoopCount>0)
							{
								break;
							}else
							{
								mbitDateF = null;
								mbitDateF = reData();
							}
						}
					} else {
						int afw = audioTrackF.write(mbitDateF, 0, mbitDateF.length, AudioTrack.WRITE_BLOCKING);

						if (mLoopCount>0)
						{
							break;
						}else
						{
							mbitDateF = null;
							mbitDateF = reData();
						}

					}
				} else {

				}


			if (beginCloseTimeStart>0) {
				end = System.nanoTime();//纳秒

				BigDecimal diff = BigDecimal.valueOf(end - start, 10);//秒级差值

				BigDecimal result = diff.setScale(9, RoundingMode.HALF_UP);//调节精度

				endCloseTimeStart = System.nanoTime();//纳秒
				BigDecimal _diff = BigDecimal.valueOf(endCloseTimeStart - beginCloseTimeStart, 10);//秒级差值
				BigDecimal _result = _diff.setScale(9, RoundingMode.HALF_UP);//调节精度
				BigDecimal time_consuming = _result.add(result);

				if (mCloseTime.compareTo(time_consuming) == -1) {
					misbLoop = false;
					setCloseTime(-1);
					audioTrackF.flush();
					break;
				}
			}

		}

		misbLoop = false;
		setCloseTime(-1);
		audioTrackF.flush();

		closeAudioTrack();
	}

	public void closeAudioTrack(){
		if(audioTrackF != null){
			audioTrackF.stop();
			audioTrackF.release();
			audioTrackF = null;
		}
	}

	class AudioTrackFThread extends Thread{
		@Override
		public void run() {
			while(misbThreadLoop) {

				playWaveF(m_bitDateF);

				synchronized (audioTrackThread) {
					try {
						audioTrackThread.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						audioTrackThread.notify();
					}
				}
			}

			super.run();
			audioTrackThread = null;
		}
	}

	protected void finalize()
	{
		if (audioTrackF != null) {
			audioTrackF.stop();
			audioTrackF.release();
			audioTrackF = null;
		}
		if (m_bitDateF != null) {
			m_bitDateF = null;
			}
	}
}
