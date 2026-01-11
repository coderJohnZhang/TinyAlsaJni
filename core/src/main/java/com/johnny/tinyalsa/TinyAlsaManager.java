package com.johnny.tinyalsa;

public class TinyAlsaManager {
    static {
        System.loadLibrary("tinyalsa_jni");
    }

    // 音频测试接口
    // dmicRecord() → tinycap /storage/emulated/0/Music/dmic.wav -D 1 -T 15
    //
    // dmicPlayback() → tinyplay /storage/emulated/0/Music/dmic.wav -D 0
    //
    // lineinRecord() → tinycap /storage/emulated/0/Music/amic.wav -D 0 -T 15
    //
    // lineinPlayback() → tinyplay /storage/emulated/0/Music/amic.wav -D 0
    public native int dmicRecord(String filePath, int durationSeconds);
    public native int dmicPlayback(String filePath);
    public native int lineinRecord(String filePath, int durationSeconds);
    public native int lineinPlayback(String filePath);

    // 原有PCM接口
    public native int pcmOpen(int card, int device, int flags,
                              int channels, int rate, int format,
                              int periodSize, int periodCount);
    public native int pcmClose(int pcmHandle);
    public native int pcmWrite(int pcmHandle, byte[] data, int size);
    public native int pcmRead(int pcmHandle, byte[] data, int size);
    public native int pcmStart(int pcmHandle);
    public native int pcmStop(int pcmHandle);

    // Mixer接口
    public native int mixerOpen(int card);
    public native void mixerClose(int mixerHandle);
    public native int mixerSetValue(int mixerHandle, String ctlName, int value);
    public native int mixerGetValue(int mixerHandle, String ctlName);

    // 工具方法
    public native String getError();
    public native int getPcmBufferSize(int pcmHandle);

    // 回调管理
    private native void initCallback(AudioTestCallback callback);
    private native void cleanupCallback();

    // 回调接口
    public interface AudioTestCallback {
        void onTestProgress(int progress);
        void onTestComplete(boolean success, String message);
    }

    // 设置回调
    public void setAudioTestCallback(AudioTestCallback callback) {
        if (callback != null) {
            initCallback(callback);
        } else {
            cleanupCallback();
        }
    }

    // 音频参数枚举
    public static class AudioParams {
        public static final int CARD_DMIC = 1;
        public static final int CARD_LINEIN = 0;
        public static final int DEVICE_DEFAULT = 0;

        public static final int DEFAULT_CHANNELS = 2;
        public static final int DEFAULT_RATE_DMIC = 48000;
        public static final int DEFAULT_RATE_LINEIN = 44100;
        public static final int DEFAULT_BITS = 16;
        public static final int DEFAULT_PERIOD_SIZE = 1024;
        public static final int DEFAULT_PERIOD_COUNT = 4;
    }
}