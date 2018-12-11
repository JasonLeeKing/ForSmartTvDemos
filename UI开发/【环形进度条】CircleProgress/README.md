# CircleProgress

> 环形进度条：支持进度条尺寸/颜色/进度值/动画时长、文字尺寸/颜色/位置等

### 效果图：

![screenshot](https://github.com/SmartArvin/CircleProgress/blob/master/arts/screenshot.gif)

### 用法：

xml引用

```
<com.example.circleprogress.CircleProgress
    	android:id="@+id/circle"
        android:layout_width="wrap_content"
    	android:layout_height="wrap_content"
    	android:layout_gravity="center"
    />
```

Api接口：

```
/**
* TODO 更新数据后同时执行绘制动画(自定义动画时长)
* /
updateProgress(int mProgress, int animDuration)

/**
* TODO 更新数据后同时执行绘制动画(500ms)
*/
updateProgress(int mProgress)

/**
* TODO 设置最大进度值
*/
setMaxProgress(int mMaxProgress)

/**
* TODO 设置进度条默认底色
*/
setDefaultWheelColor(int red, int green, int blue)

/**
* TODO 设置进度条默认底色
*/
setDefaultWheelColor(int colorId)

/**
* TODO 设置进度条颜色
*/
setProgressColor(int red, int green, int blue)

/**
* TODO 设置进度条颜色
*/
setProgressColor(int colorId)

/**
* TODO 设置百分比文本颜色
*/
setPercentColor(int red, int green, int blue)

/**
* TODO 设置百分比文本颜色
*/
setPercentColor(int colorId)

/**
* TODO 设置百分比文本字体大小
*/
setPercentTxtSize(float txtSize)

/**
* TODO 设置进度条轮廓尺寸
*/
setProgressWidth(float mProgressWidth)

/**
* TODO 设置进度条绘制动画的执行时间
*/
setAnimDuration(int durationTime)
```

