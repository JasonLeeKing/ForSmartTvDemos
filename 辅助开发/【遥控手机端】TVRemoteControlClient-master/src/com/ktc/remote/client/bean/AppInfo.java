package com.ktc.remote.client.bean;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.widget.ImageView;

public class AppInfo  implements Parcelable{
	
	private int id;
	private int classid;
	private String classname;
	private int versioncode;
	private int size;
	private String icon;
	private String py;
	private String optype;
	private String downurl;
	private Drawable ivicon;
	private String appname;
	private String appversion;
	private String pkgname;
	private String description;
	private String uninstall;
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(id);
		dest.writeInt(classid);
		dest.writeString(classname);
		dest.writeString(appname);
		dest.writeString(appversion);
		dest.writeInt(versioncode);
		dest.writeString(pkgname);
		dest.writeInt(size);
		dest.writeString(icon);
		dest.writeString(downurl);
		dest.writeString(description);
		dest.writeString(py);
		dest.writeString(optype);
	//	dest.writeByteArray(drawableToByte(ivicon));
	}
	
	public static final Parcelable.Creator<AppInfo> CREATOR = new Creator<AppInfo>() {  
        public AppInfo createFromParcel(Parcel source) {  
        	AppInfo appInfo = new AppInfo();  
        	appInfo.id = source.readInt();
        	appInfo.appname = source.readString();
        	appInfo.py = source.readString();
        	appInfo.classid = source.readInt();
        	appInfo.classname = source.readString();
        	appInfo.appversion = source.readString();
        	appInfo.pkgname = source.readString();
        	appInfo.size = source.readInt();
        	appInfo.icon = source.readString();
        	appInfo.downurl = source.readString();
        	appInfo.description = source.readString();
        	appInfo.versioncode = source.readInt();
        	appInfo.optype = source.readString();
        	
            return appInfo;  
        }  
        public AppInfo[] newArray(int size) {  
            return new AppInfo[size];  
        }  
    };
    public String getUninstall() {
		return uninstall;
	}
	public void setUninstall(String uninstall) {
		this.uninstall = uninstall;
	}
	
    public String getPkgname() {
		return pkgname;
	}
	public void setPkgname(String pkgname) {
		this.pkgname = pkgname;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getClassid() {
		return classid;
	}
	public void setClassid(int classid) {
		this.classid = classid;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public int getVersioncode() {
		return versioncode;
	}
	public void setVersioncode(int versioncode) {
		this.versioncode = versioncode;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getPy() {
		return py;
	}
	public void setPy(String py) {
		this.py = py;
	}
	public String getOptype() {
		return optype;
	}
	public void setOptype(String optype) {
		this.optype = optype;
	}
	public String getDownurl() {
		return downurl;
	}
	public void setDownurl(String downurl) {
		this.downurl = downurl;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Drawable getIvIcon() {
		return ivicon;
	}
	
	public void setIvIcon(Drawable ivIcon) {
		this.ivicon = ivIcon;
	}
	
	public String getAppName() {
		return appname;
	}
	public void setAppName(String appName) {
		this.appname = appName;
	}
	public String getAppVersion() {
		return appversion;
	}
	public void setAppVersion(String appVersion) {
		this.appversion = appVersion;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public  synchronized  byte[] drawableToByte(Drawable drawable) {  
        
        if (drawable != null) {  
            Bitmap bitmap = Bitmap  
                    .createBitmap(  
                        drawable.getIntrinsicWidth(),  
                            drawable.getIntrinsicHeight(),  
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
                                    : Bitmap.Config.RGB_565);  
            Canvas canvas = new Canvas(bitmap);  
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),  
                    drawable.getIntrinsicHeight());  
            drawable.draw(canvas);  
            int size = bitmap.getWidth() * bitmap.getHeight() * 4;  
            // 创建一个字节数组输出流,流的大小为size  
            ByteArrayOutputStream baos = new ByteArrayOutputStream(size);  
            // 设置位图的压缩格式，质量为100%，并放入字节数组输出流中  
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);  
            // 将字节数组输出流转化为字节数组byte[]  
            byte[] imagedata = baos.toByteArray();  
            return imagedata;  
        }  
        return null;  
    }
	
	
}
