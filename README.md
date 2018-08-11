# WaveView
------
[![License](https://img.shields.io/badge/License%20-Apache%202-337ab7.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![API](https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=15)
[ ![Download](https://api.bintray.com/packages/zhangxinmin528/wavelibrary/wavelibrary/images/download.svg) ](https://bintray.com/zhangxinmin528/wavelibrary/wavelibrary/_latestVersion)

<p align="center">
  <img alt="logo" src="https://github.com/ZhangXinmin528/WaveView-Simple/blob/master/example/src/main/assets/logo.png"/>
</p>

Introduction
------
The library provide the effect of water ripple and is easy to use.

Usage
------

###Step1

Add dependencies in build.gradle.


```groovy

	dependencies {
    		implementation 'com.example.wavelibrary:wavelibrary:1.0.0'
    	}
```
###Step2

######布局中添加
```java

	<com.example.wavelibrary.WaveView
        android:id="@+id/waveview"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_centerHorizontal="true"
        app:behind_color="@color/colorWave_behind"
        app:front_color="@color/colorWave_front"
        app:shape_type="circle" />
```
###Step3

######页面中添加并控制动画
	1.初始化控件并添加动画辅助工具：
```java

	private WaveView mWaveView;
	mWaveView = (WaveView) findViewById(R.id.waveview);
    mWaveView.setSimpleAnimatorToView();
```
	2.控制动画的播放和暂停：
```java

	@Override
    protected void onResume() {
        super.onResume();
        mWaveView.getWaveAnimatorHelper().startAnimators();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWaveView.getWaveAnimatorHelper().endAnimators();
    }
```

######说明：
######在使用的时候可以灵活继承WaveAnimatorHelper接口进行动画的控制；


Communication
------
Email:zhangxinmin528@sina.com

Thanks
------
LOGO:仇盼宁

License
------

    Copyright 2017 ZhangXinmin528

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


The End
---
If you are interested in WaveView, don't forget to STAR [WaveView](https://github.com/ZhangXinmin528/WaveView-Simple/tree/master).  

Thank you for reading ~^o^~

