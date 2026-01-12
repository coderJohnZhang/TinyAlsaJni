# TinyAlsaManager Android Library

## 概述

TinyAlsaManager 是一个 Android 音频库，提供对 TinyALSA 的直接访问，用于 Android 系统上的音频录制和播放。该库封装了底层音频操作，提供了简单易用的 Java API。

## 特性

- ✅ DMIC 音频录制和播放
- ✅ LINEIN 音频录制和播放
- ✅ 原始 PCM 音频操作
- ✅ 音频混音器控制
- ✅ 实时进度回调
- ✅ WAV 格式支持
- ✅ 线程安全的音频操作

## 系统要求

- Android 5.0+ (API 21+)
- 需要系统权限和适配SELinux策略
- 支持 ALSA 音频框架的设备

## 安装

[![](https://jitpack.io/v/coderJohnZhang/TinyAlsaJni.svg)](https://jitpack.io/#coderJohnZhang/TinyAlsaJni)

### 添加依赖

Step1. 在项目根目录的build.gradle文件末尾添加JitPack仓库配置：

```gradle
allprojects {
    repositories {
        maven {
            url 'https://jitpack.io'
        }
    }
}
```

Step2. 在模块的 `build.gradle` 中添加：

```gradle
dependencies {
    implementation 'com.github.coderJohnZhang:TinyAlsaJni:v1.0.1'
}
```

## 权限配置

在 `AndroidManifest.xml` 中添加：

```xml
<!-- 音频录制权限 -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />

<!-- 存储权限（用于保存录音文件） -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

### 平台签名

参考系统APP开发手册，在 `app/build.gradle` 中配置系统签名信息：

```groovy
android {
    ...
    signingConfigs {
        release {
            storeFile file("platform.jks")
            keyAlias "platform"
            keyPassword "android"
            storePassword "android"
        }
    }
	...
}

```

在 `app/src/main/AndroidManifest.xml` 中添加系统级应用标识：

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    android:sharedUserId="android.uid.system">
    <!-- 应用其他配置 -->
</manifest>
```

## 快速开始

### 1. 初始化

```java
import com.johnny.tinyalsa.TinyAlsaManager;

public class AudioActivity extends AppCompatActivity {
    private TinyAlsaManager alsaManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 创建实例
        alsaManager = new TinyAlsaManager();
        
        // 设置回调（可选）
        alsaManager.setAudioTestCallback(new TinyAlsaManager.AudioTestCallback() {
            @Override
            public void onTestProgress(int progress) {
                // 更新进度条
                runOnUiThread(() -> {
                    progressBar.setProgress(progress);
                });
            }
            
            @Override
            public void onTestComplete(boolean success, String message) {
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(AudioActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AudioActivity.this, "操作失败: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
```

### 2. 音频测试功能

#### DMIC 测试

```java
// DMIC 录音（15秒）
new Thread(() -> {
    String filePath = "/storage/emulated/0/Music/dmic.wav";
    int result = alsaManager.dmicRecord(filePath, 15);
    
    runOnUiThread(() -> {
        if (result == 0) {
            Log.i("AudioTest", "DMIC 录音成功");
        } else {
            Log.e("AudioTest", "DMIC 录音失败: " + alsaManager.getError());
        }
    });
}).start();

// DMIC 播放
new Thread(() -> {
    String filePath = "/storage/emulated/0/Music/dmic.wav";
    int result = alsaManager.dmicPlayback(filePath);
    
    runOnUiThread(() -> {
        if (result == 0) {
            Log.i("AudioTest", "DMIC 播放成功");
        } else {
            Log.e("AudioTest", "DMIC 播放失败: " + alsaManager.getError());
        }
    });
}).start();
```

#### LINEIN 测试

```java
// LINEIN 录音（15秒）
new Thread(() -> {
    String filePath = "/storage/emulated/0/Music/amic.wav";
    int result = alsaManager.lineinRecord(filePath, 15);
    
    runOnUiThread(() -> {
        if (result == 0) {
            Log.i("AudioTest", "LINEIN 录音成功");
        } else {
            Log.e("AudioTest", "LINEIN 录音失败: " + alsaManager.getError());
        }
    });
}).start();

// LINEIN 播放
new Thread(() -> {
    String filePath = "/storage/emulated/0/Music/amic.wav";
    int result = alsaManager.lineinPlayback(filePath);
    
    runOnUiThread(() -> {
        if (result == 0) {
            Log.i("AudioTest", "LINEIN 播放成功");
        } else {
            Log.e("AudioTest", "LINEIN 播放失败: " + alsaManager.getError());
        }
    });
}).start();
```

### 3. 高级 PCM 操作

```java
// 打开 PCM 设备
int pcmHandle = alsaManager.pcmOpen(
    TinyAlsaManager.AudioParams.CARD_DMIC,  // card
    0,                                       // device
    TinyAlsaManager.PcmFlags.PCM_OUT,       // flags
    2,                                       // channels
    44100,                                   // rate
    TinyAlsaManager.PcmFormat.PCM_FORMAT_S16_LE, // format
    1024,                                    // periodSize
    4                                        // periodCount
);

if (pcmHandle < 0) {
    Log.e("Audio", "PCM 打开失败: " + alsaManager.getError());
    return;
}

// 启动 PCM
if (alsaManager.pcmStart(pcmHandle) < 0) {
    Log.e("Audio", "PCM 启动失败: " + alsaManager.getError());
    return;
}

// 写入音频数据
byte[] audioData = ...; // 音频数据
int result = alsaManager.pcmWrite(pcmHandle, audioData, audioData.length);
if (result < 0) {
    Log.e("Audio", "PCM 写入失败: " + alsaManager.getError());
}

// 停止并关闭
alsaManager.pcmStop(pcmHandle);
alsaManager.pcmClose(pcmHandle);
```

### 4. 混音器控制

```java
// 打开混音器
int mixerHandle = alsaManager.mixerOpen(0);
if (mixerHandle < 0) {
    Log.e("Audio", "混音器打开失败: " + alsaManager.getError());
    return;
}

// 设置音量
int result = alsaManager.mixerSetValue(mixerHandle, "Master Volume", 80);
if (result < 0) {
    Log.e("Audio", "设置音量失败: " + alsaManager.getError());
}

// 获取当前音量
int volume = alsaManager.mixerGetValue(mixerHandle, "Master Volume");
Log.i("Audio", "当前音量: " + volume);

// 关闭混音器
alsaManager.mixerClose(mixerHandle);
```

## API 参考

### 音频测试接口

| 方法 | 描述 | 参数 |
|------|------|------|
| `dmicRecord(String filePath, int durationSeconds)` | DMIC 录音 | `filePath`: 文件路径，`durationSeconds`: 录音时长 |
| `dmicPlayback(String filePath)` | DMIC 播放 | `filePath`: 文件路径 |
| `lineinRecord(String filePath, int durationSeconds)` | LINEIN 录音 | `filePath`: 文件路径，`durationSeconds`: 录音时长 |
| `lineinPlayback(String filePath)` | LINEIN 播放 | `filePath`: 文件路径 |

### PCM 接口

| 方法 | 描述 |
|------|------|
| `pcmOpen(int card, int device, int flags, int channels, int rate, int format, int periodSize, int periodCount)` | 打开 PCM 设备 |
| `pcmClose(int pcmHandle)` | 关闭 PCM 设备 |
| `pcmWrite(int pcmHandle, byte[] data, int size)` | 写入 PCM 数据 |
| `pcmRead(int pcmHandle, byte[] data, int size)` | 读取 PCM 数据 |
| `pcmStart(int pcmHandle)` | 启动 PCM 设备 |
| `pcmStop(int pcmHandle)` | 停止 PCM 设备 |

### Mixer 接口

| 方法 | 描述 |
|------|------|
| `mixerOpen(int card)` | 打开混音器 |
| `mixerClose(int mixerHandle)` | 关闭混音器 |
| `mixerSetValue(int mixerHandle, String ctlName, int value)` | 设置混音器值 |
| `mixerGetValue(int mixerHandle, String ctlName)` | 获取混音器值 |

### 工具方法

| 方法 | 描述 |
|------|------|
| `getError()` | 获取最后错误信息 |
| `getPcmBufferSize(int pcmHandle)` | 获取 PCM 缓冲区大小 |
| `setAudioTestCallback(AudioTestCallback callback)` | 设置测试回调 |

### 常量

#### 音频参数
```java
TinyAlsaManager.AudioParams.CARD_DMIC        // DMIC 声卡 (1)
TinyAlsaManager.AudioParams.CARD_LINEIN      // LINEIN 声卡 (0)
TinyAlsaManager.AudioParams.DEVICE_DEFAULT   // 默认设备 (0)
```

#### PCM 标志
```java
TinyAlsaManager.PcmFlags.PCM_OUT     // 输出流
TinyAlsaManager.PcmFlags.PCM_IN      // 输入流
TinyAlsaManager.PcmFlags.PCM_MMAP    // MMAP 模式
```

#### PCM 格式
```java
TinyAlsaManager.PcmFormat.PCM_FORMAT_S16_LE  // 16位小端
TinyAlsaManager.PcmFormat.PCM_FORMAT_S32_LE  // 32位小端
TinyAlsaManager.PcmFormat.PCM_FORMAT_S24_LE  // 24位小端
TinyAlsaManager.PcmFormat.PCM_FORMAT_S8      // 8位
```

## 文件路径说明

### 默认测试文件路径

| 测试类型         | 文件路径 | 说明          |
|--------------|----------|-------------|
| DMIC 录音/播放   | `/storage/emulated/0/Music/dmic.wav` | DMIC 测试文件   |
| LINEIN 录音/播放 | `/storage/emulated/0/Music/amic.wav` | LINEIN 测试文件 |

### 自定义文件路径

可以使用任意路径，但需要确保：
1. 应用有写入权限
2. 路径存在且可写
3. 建议使用外部存储或应用私有目录

```java
// 使用应用私有目录
String filePath = getExternalFilesDir(null) + "/audio_test.wav";
```

## 错误处理

所有方法返回 `int` 类型结果：
- `0`: 成功
- 负数: 失败，使用 `getError()` 获取错误信息

```java
int result = alsaManager.dmicRecord(filePath, 15);
if (result < 0) {
    String error = alsaManager.getError();
    Log.e("Audio", "操作失败: " + error);
    // 显示错误给用户
}
```

## 线程安全

### 重要提醒

1. **音频操作必须在后台线程执行**
2. **UI 更新必须在主线程执行**
3. **避免在音频回调中执行耗时操作**

### 推荐做法

```java
// 在后台线程执行音频操作
new Thread(() -> {
    // 音频操作
    int result = alsaManager.dmicRecord(filePath, 15);
    
    // 在主线程更新UI
    runOnUiThread(() -> {
        if (result == 0) {
            // 更新UI
        } else {
            // 显示错误
        }
    });
}).start();
```

## 常见问题

### Q1: 权限被拒绝
**A**: 
1. 尝试临时关闭SELinux：setenforce 0，看是否正常
2. 重新打开SELinux，进行相应策略适配
    
    1）修改sepolicy/vendor/file_contexts
    ```text
    # tinyalsa
    /dev/snd/pcmC0D0c u:object_r:system_app_audio_device:s0
    /dev/snd/pcmC0D0p u:object_r:system_app_audio_device:s0
    /dev/snd/pcmC1D0c u:object_r:system_app_audio_device:s0
    ```
    2）修改sepolicy/vendor/system_app.te
    ```text
    # tinyalsa
    type system_app_audio_device, dev_type;
    allow system_app audio_device:dir search;
    allow system_app system_app_audio_device:chr_file { open read write ioctl map getattr setattr create };
    ```

### Q2: 无法打开音频设备
**A**:
1. 检查声卡和设备号是否正确
2. 确认设备支持音频操作
3. 检查 SELinux 权限

### Q3: 录音文件无法播放
**A**:
1. 确认文件已正确写入
2. 检查文件路径和权限
3. 验证音频格式是否支持

### Q4: 回调不触发
**A**:
1. 确认正确设置了回调
2. 检查音频操作是否在执行中
3. 确保在正确的线程处理回调

## 许可证

```
Copyright 2026 Johnny

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 技术支持

- GitHub Issues: [报告问题](https://github.com/coderJohnZhang/TinyAlsaJni/issues)
- 邮件支持: jun.zhang.dev@foxmail.com

## 版本历史

### v1.0.1 (2026-01-01)
- 初始版本发布
- 支持 DMIC 录音/播放
- 支持 LINEIN 录音/播放
- 基础 PCM 操作
- 混音器控制

## 贡献

虽然核心源码闭源，但我们欢迎：
- Bug 报告
- 功能建议
- 文档改进
- 使用案例分享

请通过 GitHub Issues 提交反馈。