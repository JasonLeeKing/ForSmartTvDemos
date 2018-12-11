package com.ktc.serialport.serialtest;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.ktc.serialport.R;
import com.ktc.serialport.constants.CMD_TYPE_VALUE;
import com.ktc.serialport.constants.Constants;
import com.ktc.serialport.utils.KtcFactoryUtil;
import com.ktc.serialport.utils.KtcHexUtil;
import com.ktc.serialport.utils.KtcKeyUtil;
import com.ktc.serialport.utils.KtcNetworkUtil;
import com.ktc.serialport.utils.KtcOpenSdk;
import com.ktc.serialport.utils.KtcSystemUtil;
import com.ktc.serialport.utils.KtcTvUtil;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPictureManager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 *
 * TODO 串口通信自动测试
 *
 * @author Arvin
 * 2018-3-11
 */
public class SerialAutoConsoleActivity extends Activity implements OnClickListener{

	private static final String TAG = "SerialAutoConsoleActivity" ;
	
	private String mDevicePath = "dev/ttyS1";
	private int mBaudrate = 115200 ;
	
	private int flag_receive = Constants.FLAG_RECEIVE_HEX; 
	private boolean isAutoClear = false ;
	
	private TextView txt_receive , txt_status ,txt_tv_back;
	private RadioGroup mRadioGroup ;
	private CheckBox mAutoClear;
	
	private SerialControl mSerialControl ;
	
	private KtcSystemUtil mKtcSystemUtil ;
	private KtcFactoryUtil mKtcFactoryUtil ;
	private KtcTvUtil mKtcTvUtil ;
	private KtcKeyUtil mKtcKeyUtil ;
	private KtcNetworkUtil mKtcNetworkUtil ;
	private KtcOpenSdk mKtcOpenSdk ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.serial_activity_auto_console);
		
		mSerialControl = new SerialControl();
		mSerialControl.initSerialHelper();
		
		initViews();
		
		try {
			mSerialControl.initSerialPortConfig(new File(mDevicePath), mBaudrate, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initViews(){
    	txt_receive = (TextView) findViewById(R.id.txt_receive);
    	txt_status = (TextView) findViewById(R.id.txt_status);
    	txt_tv_back = (TextView) findViewById(R.id.txt_tv_back);
    	mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
    	mAutoClear = (CheckBox) findViewById(R.id.checkbox_autoclear);
    	mAutoClear.setChecked(false);
    	mRadioGroup.check(R.id.radio_hex);
    	mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int arg1) {
				switch (group.getCheckedRadioButtonId()) {
				case R.id.radio_txt://show txt
					flag_receive = Constants.FLAG_RECEIVE_TXT ;
					break;
				case R.id.radio_hex://show hex
					flag_receive = Constants.FLAG_RECEIVE_HEX ;
					break;	
				default:
					break;
			}
			}
		});
    	
    	mAutoClear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){   
            @Override   
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            	//auto clear receive data
            	isAutoClear = isChecked ;
            }   
        }); 
    	
    	//init utils
		mKtcOpenSdk = KtcOpenSdk.getInstance(this);
		mKtcSystemUtil = mKtcOpenSdk.getKtcSystemUtil();
		mKtcFactoryUtil = mKtcOpenSdk.getKtcFactoryUtil();
		mKtcTvUtil = mKtcOpenSdk.getKtcTvUtil();
		mKtcKeyUtil = mKtcOpenSdk.getKtcKeyUtil();
		mKtcNetworkUtil = mKtcOpenSdk.getKtcNetworkUtil();
		
    }
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_clear:
			txt_receive.setText("");
			break;

		default:
			break;
		}
	}
	
	/**
	 *
	 * TODO 串口控制工具类
	 * @author Arvin
	 * 2018-3-11
	 */
	StringBuilder sMsg = new StringBuilder();
	private class SerialControl extends SerialHelper{

		@Override
		protected void onDataReceived(byte[] buffer, int size) {
			updateReceive(buffer, size);
			parseCmds(buffer, size);
		}
    }
	
	/**
	 * TODO 解析PC->TV命令(Command Format:  0xC0(Vender) + ITEM (2bytes)+ DL(Data LEN)(2bytes) + Data + CS)
	 * @param byte[] buffer, int size
	 * @return void
	 */
	private boolean parseCmds(byte[] buffer, int size){
		String hexStr = KtcHexUtil.byte2HexStr(buffer, size);
		if(!isStrNotEmpty(hexStr)){
			return updateLoopBack(CMD_TYPE_VALUE.TV_TV_NACK) ;
		}
		Log.i(TAG , "parseCmd:  "+hexStr);
		String BACK_CMD_ITEM = "";//init cmd item
		String BACK_CMD_DL = "";//init cmd data len
		String BACK_CMD_DATA = "";//init cmd data
		
		if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_ENTER_FACTORY_MODE)){
			mKtcFactoryUtil.enterFactoryMode();
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		//for serial number
		else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_SET_SERIAL_NUMBER_7)){
			String data = getSubString(hexStr , 10, 7*2);
			if(isStrNotEmpty(data)){
				mKtcSystemUtil.setSerialNumber(data);
				updateStatus("PC_SET_SERIAL_NUMBER_7:\n	"+mKtcSystemUtil.getSerialNumber());
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK ;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_SET_SERIAL_NUMBER_10)){
			String data = getSubString(hexStr , 10, 10*2);
			if(isStrNotEmpty(data)){
				mKtcSystemUtil.setSerialNumber(KtcHexUtil.hexStrToString(data));
				updateStatus("PC_SET_SERIAL_NUMBER_10:\n	"+mKtcSystemUtil.getSerialNumber());
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK ;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_SET_SERIAL_NUMBER_15)){
			String data = getSubString(hexStr , 10, 15*2);
			if(isStrNotEmpty(data)){
				mKtcSystemUtil.setSerialNumber(KtcHexUtil.hexStrToString(data));
				updateStatus("PC_SET_SERIAL_NUMBER_15:\n	"+mKtcSystemUtil.getSerialNumber());
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_GET_SERIAL_NUMBER)){
			String mSerialNumber = mKtcSystemUtil.getSerialNumber() ;
			if(isStrNotEmpty(mSerialNumber)){
				updateStatus("PC_GET_SERIAL_NUMBER:\n"+mSerialNumber);
				BACK_CMD_ITEM = "000100";
				BACK_CMD_DL = Integer.toHexString(mSerialNumber.length()).format("%02x" , mSerialNumber.length());
				BACK_CMD_DATA = KtcHexUtil.str2HexStr(mSerialNumber) ;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		//for system
		else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_GET_FW_VERSION)){
			String mFwVersion = mKtcSystemUtil.getProductsVersion();
			if(isStrNotEmpty(mFwVersion)){
				updateStatus("PC_GET_FW_VERSION:\n"+mFwVersion);
				BACK_CMD_ITEM = "000100";
				BACK_CMD_DL = Integer.toHexString(mFwVersion.length()).format("%02x" , mFwVersion.length());
				BACK_CMD_DATA = KtcHexUtil.str2HexStr(mFwVersion) ;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_GET_MODEL_NAME)){
			String mModelName = mKtcSystemUtil.getProductsModel();
			if(isStrNotEmpty(mModelName)){
				updateStatus("PC_GET_FW_VERSION:\n"+mModelName); 
				BACK_CMD_ITEM = "000100";
				BACK_CMD_DL = Integer.toHexString(mModelName.length()).format("%02x" , mModelName.length());
				BACK_CMD_DATA = KtcHexUtil.str2HexStr(mModelName) ;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		
		//for tv
		else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_GET_HDMI1_EDID)){
			byte[] mHDMI1_Edid;
			try {
				mHDMI1_Edid = mKtcTvUtil.getHDMI1_Edid_byte();
				if(mHDMI1_Edid != null){
					String byte2HexStr = KtcHexUtil.byte2HexStr(mHDMI1_Edid) ;
					updateStatus("PC_GET_HDMI2_EDID:\n"+byte2HexStr); 
					BACK_CMD_ITEM = "00010100";
					BACK_CMD_DATA = KtcHexUtil.hexStrToString(byte2HexStr);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_GET_HDMI2_EDID)){
			byte[] mHDMI2_Edid;
			try {
				mHDMI2_Edid = mKtcTvUtil.getHDMI2_Edid_byte();
				if(mHDMI2_Edid != null){
					String byte2HexStr = KtcHexUtil.byte2HexStr(mHDMI2_Edid);
					updateStatus("PC_GET_HDMI2_EDID:\n"+byte2HexStr); 
					BACK_CMD_ITEM = "00010100";
					BACK_CMD_DATA = KtcHexUtil.hexStrToString(byte2HexStr);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_GET_HDMI3_EDID)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_GET_HDMI4_EDID)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_GET_HDMI5_EDID)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_GET_HDCP_KEY)){
			byte[] mHDCP_KEY_1_4;
			try {
				mHDCP_KEY_1_4 = mKtcTvUtil.getHDCPKey_1_4();
				if(mHDCP_KEY_1_4 != null){
					updateStatus("PC_GET_HDCP_KEY:\n"+KtcHexUtil.byte2HexStr(mHDCP_KEY_1_4)); 
					int len = Integer.parseInt("0130", 16);
					Log.i(TAG, "len:  "+len);
					BACK_CMD_ITEM = "00010130";
					BACK_CMD_DATA = KtcHexUtil.byte2HexStr(mHDCP_KEY_1_4) ;
					Log.i(TAG, "BACK_CMD_DATA:  "+BACK_CMD_DATA.length());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_CHECK_SD_FILE)){
			boolean hasSdFile = mKtcSystemUtil.hasSDFile();
			
			updateStatus("PC_CHECK_SD_FILE:\n"+hasSdFile); 
			BACK_CMD_ITEM = "00010001";
			BACK_CMD_DATA = (hasSdFile ? "01" : "00") ;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		
		else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_PIC_MODE_VIVID)){
			mKtcTvUtil.changePictureMode(TvPictureManager.PICTURE_MODE_DYNAMIC);
			
			updateStatus("PC_SET_PIC_MODE_VIVID:  "+mKtcTvUtil.getPictureMode()); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_PIC_MODE_STANDRAD)){
			mKtcTvUtil.changePictureMode(TvPictureManager.PICTURE_MODE_NORMAL);
			
			updateStatus("PC_SET_PIC_MODE_STANDRAD:  "+mKtcTvUtil.getPictureMode()); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_PIC_MODE_USER)){
			mKtcTvUtil.changePictureMode(TvPictureManager.PICTURE_MODE_USER);
			
			updateStatus("PC_SET_PIC_MODE_USER:  "+mKtcTvUtil.getPictureMode()); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_PIC_MODE_MOVIE)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_PIC_MODE_GAME)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_PIC_MODE_PC)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_PIC_MODE_HI_BRIGHT)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_PIC_MODE_FIXED)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_PIC_MODE_SPORT)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_PIC_MODE_GOLF)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_PIC_MODE_BASKETBALL)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_PIC_MODE_BASEBALL)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		
		else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_GET_EMMC_SIZE)){
			String data = mKtcSystemUtil.getEmmcSize()+"" ;
			updateStatus("PC_GET_EMMC_SIZE:  "+data); 
			if(isStrNotEmpty(data)){
				BACK_CMD_ITEM = "000100" ;
				BACK_CMD_DL = Integer.toHexString(data.length()).format("%02x" , data.length()) ;
				BACK_CMD_DATA = KtcHexUtil.str2HexStr(data) ;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_GET_DDRAM_SIZE)){
			String data = mKtcSystemUtil.getDDRAMSize()+"" ;
			updateStatus("PC_GET_DDRAM_SIZE:  "+data); 
			if(isStrNotEmpty(data)){
				BACK_CMD_ITEM = "000100" ;
				BACK_CMD_DL = Integer.toHexString(data.length()).format("%02x" , data.length());
				BACK_CMD_DATA = KtcHexUtil.str2HexStr(data);
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_GET_EMMC_ID)){
			updateStatus("PC_GET_EMMC_ID:  "+mKtcSystemUtil.getEMMC_Id() ); 
			BACK_CMD_ITEM = "00010010" ;
			BACK_CMD_DATA = mKtcSystemUtil.getEMMC_Id();
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_GET_ETHERNET_MAC_ADDR)){
			String data = mKtcNetworkUtil.getEthernetMacAddress();
			if(isStrNotEmpty(data)){
				String mEthMac = data.replaceAll(":", "");
				updateStatus("PC_GET_ETHERNET_MAC_ADDR:  "+mEthMac); 
				BACK_CMD_ITEM = "00010006" ;
				BACK_CMD_DATA = mEthMac;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_CHECK_USB_FILE)){
			boolean hasSdFile = mKtcSystemUtil.hasUsbFiles() ;
			updateStatus("PC_CHECK_USB_FILE:  "+hasSdFile );
			BACK_CMD_ITEM = "00010001" ;
			BACK_CMD_DATA = hasSdFile ? "01" : "00";
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_SET_VOLUME)){
			String data = getSubString(hexStr , 10, 2);
			if(isStrNotEmpty(data)){
				mKtcSystemUtil.setStreamVolume(Integer.parseInt(data , 16));
				
				updateStatus("PC_SET_VOLUME:\n	"+mKtcSystemUtil.getStreamVolume());
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK ;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		
		else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_FAC_PRESET_CHANNEL_FYT)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_FAC_PRESET_CHANNEL_FBC)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_FAC_PRESET_CHANNEL_FSK)){
			mKtcFactoryUtil.presetFactoryChannels();
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_FAC_PRESET_CHANNEL_KIT)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_FAC_PRESET_CHANNEL_LH)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_FAC_PRESET_CHANNEL_KS)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_FAC_PRESET_CHANNEL_TC)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_FAC_PRESET_CHANNEL_NJ)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		
		else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_MAIN_INPUT_SRC_DTV)){
			mKtcTvUtil.changeTvSource(TvCommonManager.INPUT_SOURCE_DTV);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_MAIN_INPUT_SRC_ATV)){
			mKtcTvUtil.changeTvSource(TvCommonManager.INPUT_SOURCE_ATV);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_MAIN_INPUT_SRC_AV)){
			mKtcTvUtil.changeTvSource(TvCommonManager.INPUT_SOURCE_CVBS);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_MAIN_INPUT_SRC_HDMI1)){
			mKtcTvUtil.changeTvSource(TvCommonManager.INPUT_SOURCE_HDMI);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_MAIN_INPUT_SRC_HDMI2)){
			mKtcTvUtil.changeTvSource(TvCommonManager.INPUT_SOURCE_HDMI2);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_MAIN_INPUT_SRC_HDMI3)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_MAIN_INPUT_SRC_HDMI4)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_MAIN_INPUT_SRC_HDMI5)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_MAIN_INPUT_SRC_VIDEO1)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_MAIN_INPUT_SRC_VIDEO2)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_MAIN_INPUT_SRC_YPBPR1)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_MAIN_INPUT_SRC_YPBPR2)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_MAIN_INPUT_SRC_VGA)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_MAIN_INPUT_SRC_VGA2)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_MAIN_INPUT_SRC_USB)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_MAIN_INPUT_SRC_DLNA)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		
		else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_UP)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_DPAD_UP);
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			showTips("--PC_IR_KEY_UP--");
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_DOWN)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN);
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			showTips("--PC_IR_KEY_DOWN--");
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_LEFT)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT);
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			showTips("--PC_IR_KEY_LEFT--");
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_RIGHT)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT);
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			showTips("--PC_IR_KEY_RIGHT--");
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_PAGE_UP)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_PAGE_UP);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_PAGE_DOWN)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_PAGE_DOWN);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_MTS)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_CC)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_WIDE)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_TOOLS)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_0)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_0);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_1)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_1);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_2)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_2);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_3)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_3);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_4)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_4);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_5)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_5);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_6)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_6);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_7)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_7);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_8)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_8);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_9)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_9);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_ENT)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_POWER)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_POWER);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_VOLUME_UP)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_VOLUME_UP);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_VOLUME_DOWN)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_VOLUME_DOWN);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_MENU)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_MENU);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_EXIT)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_BACK)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_BACK);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_INFO)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_INFO);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_INPUT)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_TV_INPUT);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_MUTE)){
			mKtcKeyUtil.sendKeyEvent(KeyEvent.KEYCODE_VOLUME_MUTE);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_IR_KEY_TELETEXT)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		
		else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_WHITE_PATTERN_EXIT)){
			mKtcFactoryUtil.setWbPattern((short)0);
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_WHITE_PATTERN_70)){
			mKtcFactoryUtil.setWbPattern((short)4);
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_WHITE_PATTERN_100)){
			mKtcFactoryUtil.setWbPattern((short)1);
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_WHITE_PATTERN_20)){
			mKtcFactoryUtil.setWbPattern((short)9);
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_WHITE_PATTERN_80)){
			mKtcFactoryUtil.setWbPattern((short)3);
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		
		else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_LED_STATUS_RED)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_LED_STATUS_AMBER)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_LED_STATUS_GREEN)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		
		else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_GET_LIGHT_SENSOR_LEVEL)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		
		else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_KEY_LOCK_IR_LOCK)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_KEY_LOCK_IR_UNLOCK)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_KEY_LOCK_KEYPAD_LOCK)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_KEY_LOCK_KEYPAD_UNLOCK)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		
		else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_GET_KEYPAD_ADC)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		
		else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_FACTORY_RESET)){
			mKtcFactoryUtil.factoryReset();
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_SET_BRGHTNSS)){
			String data = getSubString(hexStr , 10, 2);
			if(isStrNotEmpty(data)){
				mKtcTvUtil.setPicBrightness(Integer.parseInt(data , 16));
				updateStatus(null); 
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_SET_CONTRAST)){
			String data = getSubString(hexStr , 10, 2);
			if(isStrNotEmpty(data)){
				mKtcTvUtil.setPicContrast(Integer.parseInt(data , 16));
				updateStatus(null); 
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_SET_BACKLIGHT)){
			String data = getSubString(hexStr , 10, 2);
			if(isStrNotEmpty(data)){
				mKtcTvUtil.setBackLight(Integer.parseInt(data , 16));
				updateStatus(null); 
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		
		else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_AGING_MODE_OFF)){
			mKtcFactoryUtil.setFactoryAgeMode(false);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_AGING_MODE_ON)){
			mKtcFactoryUtil.setFactoryAgeMode(true);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_AGING_MODE_ON_WHITE)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		
		else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_COLORTEMP_COOL)){
			mKtcTvUtil.setPictureColorTemperature(TvPictureManager.COLOR_TEMP_COOL);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_COLORTEMP_NEUTRAL)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_COLORTEMP_WARM_1)){
			mKtcTvUtil.setPictureColorTemperature(TvPictureManager.COLOR_TEMP_WARM);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_COLORTEMP_WARM_2)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_COLORTEMP_STANDARD)){
			mKtcTvUtil.setPictureColorTemperature(TvPictureManager.COLOR_TEMP_NATURE);
			updateStatus(null); 
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_COLORTEMP_COMPUTER)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_COLORTEMP_NORMAL)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_COLORTEMP_USER)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		
		else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_CT_DATA_R_GAIN_DOUBLE)){
			updateStatus(null);
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_CT_DATA_G_GAIN_DOUBLE)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_CT_DATA_B_GAIN_DOUBLE)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_CT_DATA_R_OFFSET_DOUBLE)){
			String data = getSubString(hexStr , 12, 4);
			if(isStrNotEmpty(data)){
				int roffsetvalWB = Integer.parseInt(data , 16) ;
				mKtcFactoryUtil.setWB_R_Offset(roffsetvalWB);
				
				updateStatus("roffsetvalWB:  "+roffsetvalWB);
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_CT_DATA_G_OFFSET_DOUBLE)){
			String data = getSubString(hexStr , 12, 4);
			if(isStrNotEmpty(data)){
				int goffsetvalWB = Integer.parseInt(data , 16) ;
				mKtcFactoryUtil.setWB_G_Offset(goffsetvalWB);
				
				updateStatus("goffsetvalWB:  "+goffsetvalWB);
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_CT_DATA_B_OFFSET_DOUBLE)){
			String data = getSubString(hexStr , 12, 4);
			if(isStrNotEmpty(data)){
				int boffsetvalWB = Integer.parseInt(data , 16) ;
				mKtcFactoryUtil.setWB_B_Offset(boffsetvalWB);
				
				updateStatus("boffsetvalWB:  "+boffsetvalWB);
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		
		else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_CT_DATA_R_GAIN)){
			String data = getSubString(hexStr , 12, 2);
			if(isStrNotEmpty(data)){
				int roffsetvalWB = Integer.parseInt(data , 16) ;
				mKtcFactoryUtil.setWB_R_Gain(roffsetvalWB);
				
				updateStatus("roffsetvalWB:  "+roffsetvalWB);
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_CT_DATA_G_GAIN)){
			String data = getSubString(hexStr , 12, 2);
			if(isStrNotEmpty(data)){
				int goffsetvalWB = Integer.parseInt(data , 16) ;
				mKtcFactoryUtil.setWB_G_Gain(goffsetvalWB);
				
				updateStatus("goffsetvalWB:  "+goffsetvalWB);
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_CT_DATA_B_GAIN)){
			String data = getSubString(hexStr , 12, 2);
			if(isStrNotEmpty(data)){
				int boffsetvalWB = Integer.parseInt(data , 16) ;
				mKtcFactoryUtil.setWB_B_Gain(boffsetvalWB);
				
				updateStatus("boffsetvalWB:  "+boffsetvalWB);
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_CT_DATA_R_OFFSET)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_CT_DATA_G_OFFSET)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_CT_DATA_B_OFFSET)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		
		else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_GET_CT_DATA_R_GAIN)){
			int r_gain = mKtcFactoryUtil.getWB_R_Gain();
			
			updateStatus("PC_GET_CT_DATA_R_GAIN:  "+r_gain); 
			BACK_CMD_ITEM = "00010002" ;
			BACK_CMD_DATA = Integer.toHexString(r_gain).format("%04x" , r_gain) ;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_GET_CT_DATA_G_GAIN)){
			int g_gain = mKtcFactoryUtil.getWB_G_Gain();
			
			updateStatus("PC_GET_CT_DATA_G_GAIN:  "+g_gain);
			BACK_CMD_ITEM = "00010002" ;
			BACK_CMD_DATA = Integer.toHexString(g_gain).format("%04x" , g_gain) ;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_GET_CT_DATA_B_GAIN)){
			int b_gain = mKtcFactoryUtil.getWB_B_Gain();
			
			updateStatus("PC_GET_CT_DATA_B_GAIN:  "+b_gain ); 
			BACK_CMD_ITEM = "00010002" ;
			BACK_CMD_DATA = Integer.toHexString(b_gain).format("%04x" , b_gain) ;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_GET_CT_DATA_R_OFFSET)){
			int r_offset = mKtcFactoryUtil.getWB_R_Offset();
			
			updateStatus("PC_GET_CT_DATA_R_OFFSET:  "+r_offset); 
			BACK_CMD_ITEM = "00010002" ;
			BACK_CMD_DATA = Integer.toHexString(r_offset).format("%04x" , r_offset) ;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_GET_CT_DATA_G_OFFSET)){
			int g_offset = mKtcFactoryUtil.getWB_G_Offset();
			
			updateStatus("PC_GET_CT_DATA_G_OFFSET:  "+g_offset); 
			BACK_CMD_ITEM = "00010002" ;
			BACK_CMD_DATA = Integer.toHexString(g_offset).format("%04x" , g_offset) ;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_GET_CT_DATA_B_OFFSET)){
			int b_offset = mKtcFactoryUtil.getWB_B_Offset();
			
			updateStatus("PC_GET_CT_DATA_B_OFFSET:  "+b_offset); 
			BACK_CMD_ITEM = "00010002" ;
			BACK_CMD_DATA = Integer.toHexString(b_offset).format("%04x" , b_offset) ;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
		
		else if(getSubString(hexStr , 0 , 8).equals(CMD_TYPE_VALUE.PC_SET_CONNECT_WIFI_SSID)){
			String lenData = getSubString(hexStr , 8, 2);
			if(isStrNotEmpty(lenData)){
				int len = Integer.parseInt(lenData , 16);
				String data = getSubString(hexStr , 10 , len);
				
				if(isStrNotEmpty(data)){
					boolean isSuccess = mKtcNetworkUtil.connectOpenWifi(KtcHexUtil.hexStrToString(data), null);
					if(!isSuccess){
						BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
					}
				}else{
					BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
				}
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 8).equals(CMD_TYPE_VALUE.PC_SET_CONNECT_WIFI_IP_SSID)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NOT_SUPPORT;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_GET_WIFI_MAC_ADDR)){
			String data = mKtcNetworkUtil.getWlanMacAddress();
			if(isStrNotEmpty(data)){
				String mWlanMac = data.replaceAll(":", "");
				updateStatus("PC_GET_WIFI_MAC_ADDR:  "+mWlanMac); 
				BACK_CMD_ITEM = "00010006" ;
				BACK_CMD_DATA = mWlanMac ;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_GET_WIFI_PID)){
			int[] vid_pid = mKtcNetworkUtil.getWifiModule_VidAndPid();
			if(vid_pid.length > 1){
				String mHexPid = Integer.toHexString(vid_pid[1]).format("%04x" , vid_pid[1]);
				
				updateStatus("PC_GET_WIFI_PID:  "+vid_pid[1]); 
				BACK_CMD_ITEM = "0001" ;
				BACK_CMD_DL = "0002" ;
				BACK_CMD_DATA = mHexPid ;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_GET_WIFI_VID)){
			int[] vid_pid = mKtcNetworkUtil.getWifiModule_VidAndPid();
			if(vid_pid.length > 1){
				String mHexVid = Integer.toHexString(vid_pid[0]).format("%04x" , vid_pid[0]);
				
				updateStatus("PC_GET_WIFI_PID:  "+mHexVid); 
				BACK_CMD_ITEM = "0001" ;
				BACK_CMD_DL = "0002" ;
				BACK_CMD_DATA = mHexVid ;
			}else{
				BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK ;
			}
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_CHECK_WIFI_STATUS)){
			boolean isWifiConnected = mKtcNetworkUtil.isWifiConnected() ;

			updateStatus("PC_CHECK_WIFI_STATUS:   "+isWifiConnected); 
			BACK_CMD_ITEM = "00010001" ;
			BACK_CMD_DATA = isWifiConnected ? "01" : "00" ;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 10).equals(CMD_TYPE_VALUE.PC_GET_ETHERENT_STATUS)){
			boolean isEthEnable = mKtcNetworkUtil.isEthernetEnable() ;
			
			updateStatus("PC_GET_ETHERENT_STATUS:   "+isEthEnable); 
			BACK_CMD_ITEM = "00010001" ;
			BACK_CMD_DATA = isEthEnable ? "01" : "00" ;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_LIGHT_SENSOR_STATUS_OFF)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else if(getSubString(hexStr , 0 , 12).equals(CMD_TYPE_VALUE.PC_SET_LIGHT_SENSOR_STATUS_ON)){
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_ACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}else{
			BACK_CMD_ITEM = CMD_TYPE_VALUE.TV_TV_NACK;
			return updateLoopBack(BACK_CMD_ITEM + BACK_CMD_DL + BACK_CMD_DATA);
		}
	}
	
	/**
	 * TODO 截取指定字符串
	 * @param String hexStr , int startIndex , int offsetLen
	 * @return String
	 */
	private String getSubString(String hexStr , int startIndex , int offsetLen){
		if(hexStr == null || hexStr.equals(""))return "";
		if((startIndex + offsetLen) <= hexStr.length()){
			return hexStr.substring(startIndex, startIndex + offsetLen);
		}
		return "";
	}
	
	/**
	 * TODO 判断返回数据是否为空
	 * @param String
	 * @return boolean
	 */
	private boolean isStrNotEmpty(String str){
		if(str != null && !str.equals("")){
			return true ;
		}
		return false ;
	}
	
	/**
	 * TODO 更新数据接收区
	 * @param byte[] buffer, int size
	 * @return void
	 */
	private void updateReceive(byte[] buffer, int size){
		if(isAutoClear && sMsg !=null){
			sMsg.delete(0, sMsg.length());
		}
		String tmpStr = null ;
    	if(flag_receive == Constants.FLAG_RECEIVE_TXT){
    		tmpStr = new String(buffer,0,size).toString().toUpperCase()+"\n";
    	}else if(flag_receive == Constants.FLAG_RECEIVE_HEX){
    		tmpStr = KtcHexUtil.byte2HexStr(buffer, size)+"\n";
    	}
    	SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss");       
		String sRecTime = sDateFormat.format(new java.util.Date());
		sMsg.append("["+sRecTime+"]:  "+tmpStr);
    	
		runOnUiThread(new Runnable(){  
            @Override  
            public void run() { 
            	txt_receive.setText("\n"+sMsg);
            	Log.i(TAG , "DataReceive:" + sMsg);
            }  
        }); 
	}
	
	private void updateStatus(final String hexStatus){
		Log.i(TAG, "---updateStatus---");
		runOnUiThread(new Runnable(){  
            @Override  
            public void run() { 
            	txt_status.setText("");
        		txt_status.setText(hexStatus == null ? "" : hexStatus);
            }  
        }); 
	}
	
	private boolean updateLoopBack(final String hexStatus){
		runOnUiThread(new Runnable(){  
            @Override  
            public void run() { 
            	txt_tv_back.setText("");
        		txt_tv_back.setText(hexStatus == null ? "" : hexStatus);
            }  
        }); 
		if(hexStatus != null){
			return mSerialControl.sendStrCmd(hexStatus);
		}
		return false ;
	}
	
	/**
	 * TODO 显示提示信息
	 * @param 
	 * @return void
	 */
	private void showTips(final String hexStatus){
		runOnUiThread(new Runnable(){  
            @Override  
            public void run() { 
            	txt_tv_back.setText("");
            	Toast.makeText(SerialAutoConsoleActivity.this, hexStatus == null ? "" : hexStatus, Toast.LENGTH_SHORT).show();
            }  
        }); 
	}
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if(mSerialControl != null){
			mSerialControl.closeSerialPort();
		}
		super.onDestroy();
	}
	
}
