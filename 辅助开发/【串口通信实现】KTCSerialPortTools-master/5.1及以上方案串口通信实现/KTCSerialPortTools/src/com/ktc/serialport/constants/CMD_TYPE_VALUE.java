package com.ktc.serialport.constants;

public class CMD_TYPE_VALUE {	

	/*******PC--->TV*******/
	public static final String PC_ENTER_FACTORY_MODE =   "C001010000" ;
	public static final String PC_SET_SERIAL_NUMBER_7 =  "C0004E0007" ;
	public static final String PC_SET_SERIAL_NUMBER_10 = "C0004E000A" ;
	public static final String PC_SET_SERIAL_NUMBER_15 = "C0004E000F" ;
	public static final String PC_GET_SERIAL_NUMBER =    "C0004F0000" ;
	public static final String PC_GET_FW_VERSION =       "C0006A0000" ;
	public static final String PC_GET_MODEL_NAME =       "C0006E0000" ;
	
	public static final String PC_GET_HDMI1_EDID =  "C00098000000" ;
	public static final String PC_GET_HDMI2_EDID =  "C00098000001" ;
	public static final String PC_GET_HDMI3_EDID =  "C00098000002" ;
	public static final String PC_GET_HDMI4_EDID =  "C00098000003" ;
	public static final String PC_GET_HDMI5_EDID =  "C00098000004" ;
	
	public static final String PC_GET_HDCP_KEY = 	"C000610000" ;
	public static final String PC_CHECK_SD_FILE = 	"C000470000" ;
	
	public static final String PC_SET_PIC_MODE_VIVID = 	    "C00007000101" ;
	public static final String PC_SET_PIC_MODE_STANDRAD =   "C00007000102" ;
	public static final String PC_SET_PIC_MODE_USER = 	    "C00007000103" ;
	public static final String PC_SET_PIC_MODE_MOVIE = 	    "C00007000104" ;
	public static final String PC_SET_PIC_MODE_GAME = 	    "C00007000105" ;
	public static final String PC_SET_PIC_MODE_PC = 	    "C00007000106" ;
	public static final String PC_SET_PIC_MODE_HI_BRIGHT =  "C00007000107" ;
	public static final String PC_SET_PIC_MODE_FIXED = 	    "C00007000108" ;
	public static final String PC_SET_PIC_MODE_SPORT = 	    "C00007000109" ;
	public static final String PC_SET_PIC_MODE_GOLF = 	    "C0000700010A" ;
	public static final String PC_SET_PIC_MODE_BASKETBALL = "C0000700010B" ;
	public static final String PC_SET_PIC_MODE_BASEBALL = 	"C0000700010C" ;
	
	
	public static final String PC_GET_EMMC_SIZE =    "C00099000101" ;
	public static final String PC_GET_DDRAM_SIZE =   "C00099000102" ;
	public static final String PC_GET_EMMC_ID =      "C0009A0000" ;
	
	public static final String PC_GET_ETHERNET_MAC_ADDR  =  "C002880000" ;
	public static final String PC_CHECK_USB_FILE =          "C0004B0000" ;
	public static final String PC_SET_VOLUME =              "C000280001" ;
	
	public static final String PC_FAC_PRESET_CHANNEL_FYT =   "C00044000100" ;
	public static final String PC_FAC_PRESET_CHANNEL_FBC =   "C00044000101" ;
	public static final String PC_FAC_PRESET_CHANNEL_FSK =   "C00044000102" ;
	public static final String PC_FAC_PRESET_CHANNEL_KIT =   "C00044000103" ;
	public static final String PC_FAC_PRESET_CHANNEL_LH =    "C00044000104" ;
	public static final String PC_FAC_PRESET_CHANNEL_KS =    "C00044000105" ;
	public static final String PC_FAC_PRESET_CHANNEL_TC =    "C00044000106" ;
	public static final String PC_FAC_PRESET_CHANNEL_NJ =    "C00044000107" ;
	
	public static final String PC_SET_MAIN_INPUT_SRC_DTV =     "C00002000101" ;
	public static final String PC_SET_MAIN_INPUT_SRC_ATV =     "C00002000102" ;
	public static final String PC_SET_MAIN_INPUT_SRC_AV =      "C00002000103" ;
	public static final String PC_SET_MAIN_INPUT_SRC_HDMI1 =   "C00002000111" ;
	public static final String PC_SET_MAIN_INPUT_SRC_HDMI2 =   "C00002000112" ;
	public static final String PC_SET_MAIN_INPUT_SRC_HDMI3 =   "C00002000113" ;
	public static final String PC_SET_MAIN_INPUT_SRC_HDMI4 =   "C00002000114" ;
	public static final String PC_SET_MAIN_INPUT_SRC_HDMI5 =   "C00002000115" ;
	public static final String PC_SET_MAIN_INPUT_SRC_VIDEO1 =  "C00002000121" ;
	public static final String PC_SET_MAIN_INPUT_SRC_VIDEO2 =  "C00002000122" ;
	public static final String PC_SET_MAIN_INPUT_SRC_YPBPR1 =  "C00002000131" ;
	public static final String PC_SET_MAIN_INPUT_SRC_YPBPR2 =  "C00002000132" ;
	public static final String PC_SET_MAIN_INPUT_SRC_VGA=      "C00002000141" ;
	public static final String PC_SET_MAIN_INPUT_SRC_VGA2 =    "C00002000142" ;
	public static final String PC_SET_MAIN_INPUT_SRC_USB =     "C00002000151" ;
	public static final String PC_SET_MAIN_INPUT_SRC_DLNA =    "C00002000161" ;
	
	public static final String PC_IR_KEY_UP =         "C000A3000101" ;
	public static final String PC_IR_KEY_DOWN =       "C000A3000102" ;
	public static final String PC_IR_KEY_LEFT =       "C000A3000103" ;
	public static final String PC_IR_KEY_RIGHT =      "C000A3000104" ;
	public static final String PC_IR_KEY_PAGE_UP =    "C000A3000105" ;
	public static final String PC_IR_KEY_PAGE_DOWN =  "C000A3000106" ;
	public static final String PC_IR_KEY_MTS =        "C000A3000107" ;
	public static final String PC_IR_KEY_CC =         "C000A3000108" ;
	public static final String PC_IR_KEY_WIDE =       "C000A3000109" ;
	public static final String PC_IR_KEY_TOOLS =      "C000A300010A" ;
	
	public static final String PC_IR_KEY_0 = "C000A300010B" ;
	public static final String PC_IR_KEY_1 = "C000A300010C" ;
	public static final String PC_IR_KEY_2 = "C000A300010D" ;
	public static final String PC_IR_KEY_3 = "C000A300010E" ;
	public static final String PC_IR_KEY_4 = "C000A300010F" ;
	public static final String PC_IR_KEY_5 = "C000A3000110" ;
	public static final String PC_IR_KEY_6 = "C000A3000111" ;
	public static final String PC_IR_KEY_7 = "C000A3000112" ;
	public static final String PC_IR_KEY_8 = "C000A3000113" ;
	public static final String PC_IR_KEY_9 = "C000A3000114" ;
	
	public static final String PC_IR_KEY_ENT =          "C000A3000116" ;
	public static final String PC_IR_KEY_POWER =        "C000A3000117" ;
	public static final String PC_IR_KEY_VOLUME_UP =    "C000A3000118" ;
	public static final String PC_IR_KEY_VOLUME_DOWN =  "C000A3000119" ;
	public static final String PC_IR_KEY_MENU =         "C000A3000120" ;
	public static final String PC_IR_KEY_EXIT =         "C000A3000121" ;
	public static final String PC_IR_KEY_BACK =         "C000A3000122" ;
	public static final String PC_IR_KEY_INFO =         "C000A3000123" ;
	public static final String PC_IR_KEY_INPUT =        "C000A3000124" ;
	public static final String PC_IR_KEY_MUTE =         "C000A3000125" ;
	public static final String PC_IR_KEY_TELETEXT =     "C000A3000126" ;
	
	public static final String PC_SET_WHITE_PATTERN_EXIT =   "C00109000100" ;
	public static final String PC_SET_WHITE_PATTERN_70 =     "C00109000101" ;
	public static final String PC_SET_WHITE_PATTERN_100 =    "C00109000102" ;
	public static final String PC_SET_WHITE_PATTERN_20 =     "C00109000103" ;
	public static final String PC_SET_WHITE_PATTERN_80 =     "C00109000104" ;
	
	public static final String PC_SET_LED_STATUS_RED =         "C00050000201" ;
	public static final String PC_SET_LED_STATUS_AMBER =       "C00050000202" ;
	public static final String PC_SET_LED_STATUS_GREEN =       "C00050000203" ;
	
	public static final String PC_GET_LIGHT_SENSOR_LEVEL =     "C0007100000" ;
	
	public static final String PC_SET_KEY_LOCK_IR_LOCK =       "C00080000101" ;
	public static final String PC_SET_KEY_LOCK_IR_UNLOCK =     "C00080000102" ;
	public static final String PC_SET_KEY_LOCK_KEYPAD_LOCK =    "C00080000103" ;
	public static final String PC_SET_KEY_LOCK_KEYPAD_UNLOCK =  "C00080000104" ;
	
	public static final String PC_GET_KEYPAD_ADC = "C000830000" ;
	
	
	public static final String PC_FACTORY_RESET =   "C000540000" ;
	public static final String PC_SET_BRGHTNSS =    "C0000B0001" ;
	public static final String PC_SET_CONTRAST =    "C0000D0001" ;
	public static final String PC_SET_BACKLIGHT =   "C0000F0001" ;
	
	public static final String PC_SET_AGING_MODE_OFF =      "C00056000101" ;
	public static final String PC_SET_AGING_MODE_ON =       "C00056000102" ;
	public static final String PC_SET_AGING_MODE_ON_WHITE = "C00056000103" ;
	
	public static final String PC_SET_COLORTEMP_COOL =     "C00009000101" ;
	public static final String PC_SET_COLORTEMP_NEUTRAL =  "C00009000102" ;
	public static final String PC_SET_COLORTEMP_WARM_1 =   "C00009000103" ;
	public static final String PC_SET_COLORTEMP_WARM_2 =   "C00009000104" ;
	public static final String PC_SET_COLORTEMP_STANDARD = "C00009000105" ;
	public static final String PC_SET_COLORTEMP_COMPUTER = "C00009000106" ;
	public static final String PC_SET_COLORTEMP_NORMAL =   "C00009000107" ;
	public static final String PC_SET_COLORTEMP_USER =     "C00009000108" ;
	
	//双字节
	public static final String PC_SET_CT_DATA_R_GAIN_DOUBLE =   "C00072000301" ;
	public static final String PC_SET_CT_DATA_G_GAIN_DOUBLE =   "C00072000302" ;
	public static final String PC_SET_CT_DATA_B_GAIN_DOUBLE =   "C00072000303" ;
	public static final String PC_SET_CT_DATA_R_OFFSET_DOUBLE = "C00072000304" ;
	public static final String PC_SET_CT_DATA_G_OFFSET_DOUBLE = "C00072000305" ;
	public static final String PC_SET_CT_DATA_B_OFFSET_DOUBLE = "C00072000306" ;
	
	public static final String PC_SET_CT_DATA_R_GAIN =   "C00072000201" ;
	public static final String PC_SET_CT_DATA_G_GAIN =   "C00072000202" ;
	public static final String PC_SET_CT_DATA_B_GAIN =   "C00072000203" ;
	public static final String PC_SET_CT_DATA_R_OFFSET = "C00072000204" ;
	public static final String PC_SET_CT_DATA_G_OFFSET = "C00072000205" ;
	public static final String PC_SET_CT_DATA_B_OFFSET = "C00072000206" ;
	
	
	public static final String PC_GET_CT_DATA_R_GAIN =   "C00074000101" ;
	public static final String PC_GET_CT_DATA_G_GAIN =   "C00074000102" ;
	public static final String PC_GET_CT_DATA_B_GAIN =   "C00074000103" ;
	public static final String PC_GET_CT_DATA_R_OFFSET = "C00074000104" ;
	public static final String PC_GET_CT_DATA_G_OFFSET = "C00074000105" ;
	public static final String PC_GET_CT_DATA_B_OFFSET = "C00074000106" ;
	
	
	public static final String PC_SET_CONNECT_WIFI_SSID =    "C0029900" ;
	public static final String PC_SET_CONNECT_WIFI_IP_SSID = "C0029900" ;
	
	public static final String PC_GET_WIFI_MAC_ADDR  =             "C0020C0000" ;
	public static final String PC_GET_WIFI_PID =                   "C002010000" ;
	public static final String PC_GET_WIFI_VID =                   "C002020000" ;
	public static final String PC_CHECK_WIFI_STATUS =              "C0029A0000" ;
	public static final String PC_GET_ETHERENT_STATUS =            "C0028F0000" ;
	public static final String PC_SET_LIGHT_SENSOR_STATUS_OFF  =   "C0006F000100" ;
	public static final String PC_SET_LIGHT_SENSOR_STATUS_ON =     "C0006F000101" ;
	
	
	/*******TV--->PC*******/
	public static final String TV_TV_ACK =         "00000000" ;
	public static final String TV_TV_ACK_PARA =    "0001" ;
	public static final String TV_TV_NACK =        "01000000" ;
	public static final String TV_TV_NOT_SUPPORT = "01010000" ;
	
}
