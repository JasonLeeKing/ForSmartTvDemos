# WaveLoadingView

> 支持球型、矩形或者梯形水波纹进度条
>
> 支持水波纹颜色、进度条外边框及水波纹幅度自定义

## 效果图：

![screenshot](https://github.com/SmartArvin/ForSmartTvDemos/raw/master/UI%E5%BC%80%E5%8F%91/%E3%80%90%E6%B0%B4%E6%B3%A2%E7%BA%B9%E8%BF%9B%E5%BA%A6%E6%9D%A1%E3%80%91WaveLoadingView/screenshot/screenshot.PNG)

## 用法：

xml引用

```
<me.itangqi.waveloadingview.WaveLoadingView
            android:id="@+id/waveLoadingView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:wlv_borderColor="@color/colorAccent"
            app:wlv_borderWidth="3dp"
            app:wlv_progressValue="40"
            app:wlv_shapeType="circle"
            app:wlv_round_rectangle="true"
            app:wlv_triangle_direction="north"
            app:wlv_titleCenter="Center Title"
            app:wlv_titleCenterColor="@color/colorPrimaryText"
            app:wlv_titleCenterSize="24sp"
            app:wlv_waveAmplitude="70"
            app:wlv_waveColor="#BB6af6f3"
            android:background="@drawable/ic_launcher"
            />
```

接口封装还不够完善，有时间重构一下更方便使用