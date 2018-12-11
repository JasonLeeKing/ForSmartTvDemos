# TvFlyFrameView

> 适用于电视TV端的飞框效果，一般用于使用基础控件搭建的UI实现

## 使用方法

- xml配置

  父布局需要声明以下属性，确保子控件可以超出父布局显示

```
android:clipChildren="false"
android:focusable="false"
```

- 代码配置

  ```
  FlyFrameView mFlyFrameView = new FlyFrameView(this);
  mFlyFrameView.setBackgroundResource(R.drawable.border_highlight);

  main = (RelativeLayout) findViewById(R.id.main);
  mFlyFrameView.attachTo(main);

  for (int i = 0; i < main.getChildCount(); i++) {
  main.getChildAt(i).setOnFocusChangeListener(new OnFocusChangeListener() {
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
      if(hasFocus){
      view.bringToFront();
      }
    }
  });
  }
  ```

  如上，代码中只需要将各子项的父布局绑定FlyFrameView即可。同时设置子项获取焦点时执行view.bringToFront();以将子控件浮出至顶层，实现浮动效果



## 效果图

![ScreenShot](https://github.com/SmartArvin/ForSmartTvDemos/raw/master/UI%E5%BC%80%E5%8F%91/%E3%80%90%E9%A3%9E%E6%A1%86%E6%95%88%E6%9E%9C%E3%80%91TvFlyFrameView/screenshot/screenshot.gif)

