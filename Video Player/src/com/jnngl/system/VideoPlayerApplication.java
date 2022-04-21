package com.jnngl.system;

import com.jnngl.totalcomputers.TotalComputers;
import com.jnngl.totalcomputers.system.TotalOS;
import com.jnngl.totalcomputers.system.desktop.ApplicationHandler;
import com.jnngl.totalcomputers.system.desktop.WindowApplication;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class VideoPlayerApplication extends WindowApplication {
    private FFmpegFrameGrabber grabber;
    private boolean playing = false;
    private BufferedImage buffer;
    private Thread loopThread;
    private Frame lastFrame = null;
    private int bW, bH, bX, bY;

    public static void main(String[] args) {
        ApplicationHandler.open(VideoPlayerApplication.class, args[0], args);
    }

    private void calcBounds() {
        if((float)getWidth()/getHeight() < grabber.getAspectRatio()) {
            bY = 0;
            bH = getHeight();
            float dif = (float)grabber.getImageHeight()/getHeight();
            bW = (int) (grabber.getImageWidth()/dif);
            bX = getWidth()/2-bW/2;
        }
        else {
            bX = 0;
            bW = getWidth();
            float dif = (float)grabber.getImageWidth()/getWidth();
            bH = (int) (grabber.getImageHeight()/dif);
            bY = getHeight()/2-bH/2;
        }
    }

    public VideoPlayerApplication(TotalOS os, String path, String[] args) {
        super(os, "Video Player", os.screenWidth/3*2, os.screenHeight/3*2, path);
        if(args.length < 2) return;
        loopThread = new Thread(() -> {
            grabber = new FFmpegFrameGrabber(os.fs.toFile(args[1]).getAbsoluteFile());
            try {
                grabber.start();
                double await = 1000d/grabber.getFrameRate();
                buffer = new BufferedImage(grabber.getImageWidth(), grabber.getImageHeight(), BufferedImage.TYPE_INT_RGB);
                calcBounds();

                addResizeEvent(new ResizeEvent() {
                    @Override
                    public void onResize(int width, int height) {
                        calcBounds();
                    }

                    @Override
                    public void onMaximize(int width, int height) {
                        calcBounds();
                    }

                    @Override
                    public void onUnmaximize(int width, int height) {
                        calcBounds();
                    }
                });

                long lastTime = System.currentTimeMillis();

                while(!loopThread.isInterrupted()) {
                    if(!playing) continue;
                    Frame frame = grabber.grab();
                    if(frame != null) {
                        lastFrame = frame;
                        renderCanvas();
                        while(System.currentTimeMillis()-lastTime < await);
                        lastTime = System.currentTimeMillis();
                    }
                }
            } catch(FrameGrabber.Exception e) {
                System.err.println(e.getMessage());
            }
        });
        loopThread.start();
        playing = true;
    }

    @Override
    protected void onStart() {
        os.fs.createAssociation("/rootfs/"+os.name+"/usr/Applications/Video Player.app",
                "3dostr", "3g2", "3gp", "4xm", "a64", "aa", "aac", "ac3", "acm", "act", "adf", "adp", "ads",
                "adts", "adx", "aea", "afc", "aiff", "aix", "alaw", "alias_pix", "amr", "amrnb", "amrwb", "anm", "apc",
                "ape", "apng", "aptx", "aptx_hd", "aqtitle", "asf", "asf_o", "asf_stream", "ast", "au", "avi",
                "avisynth", "avm2", "avr", "avs", "avs2", "bethsoftvid", "bfi", "bfstm", "bmp_pipe", "bmv", "boa",
                "brender_pix", "brstm", "c93", "caf", "cavsvideo", "cdg", "cdxl", "cine", "codec2", "codec2raw",
                "concat", "crc", "dcstr", "dds_pipe", "dhav", "dirac", "dnxhd", "dpx_pipe", "dsf", "dshow", "dsicin",
                "dts", "dtshd", "dv", "dvbsub", "dvbtxt", "dvd", "dxa", "ea", "ea_cdata", "eac3", "epaf", "exr_pipe",
                "f32be", "f32le", "f64be", "f64le", "ffmetadata", "fifo", "fifo_test", "film_cpk", "filmstrip", "fits",
                "flac", "flic", "flv", "frm", "fsb", "g722", "g723_1", "g726", "g726le", "g729", "gdigrab", "gdv",
                "genh", "gif", "gif_pipe", "gsm", "gxf", "h261", "h263", "h264", "hcom", "hds", "hevc", "hls", "hnm",
                "ico", "idcin", "idf", "iff", "ifv", "ilbc", "image2", "image2pipe", "ingenient", "ipmovie", "ipod",
                "ircam", "ismv", "iss", "iv8", "ivr", "j2k_pipe", "jacosub", "jpeg_pipe", "jpegls_pipe", "jv", "kux",
                "latm", "lavfi", "libopenmpt", "live_flv", "lmlm4", "loas", "lrc", "lvf", "lxf", "m4v", "matroska",
                "webm", "mgsts", "microdvd", "mjpeg", "mjpeg_2000", "mlp", "mlv", "mm", "mmf", "mov", "mp4", "m4a, mj2",
                "mp2", "mp3", "mpc", "mpc8", "mpeg", "mpeg1video", "mpeg2video", "mpegts", "mpegtsraw", "mpegvideo",
                "mpjpeg", "msf", "msnwctcp", "mtaf", "mtv", "mulaw", "musx", "mv", "mvi", "mxf", "mxf_d10",
                "mxf_opatom", "mxg", "nc", "nistsphere", "nsp", "nsv", "nut", "nuv", "oga", "ogg", "ogv", "oma", "opus",
                "paf", "pam_pipe", "pbm_pipe", "pcx_pipe", "pgm_pipe", "pgmyuv_pipe", "pictor_pipe", "pjs", "pmp",
                "png_pipe", "ppm_pipe", "psd_pipe", "psp", "psxstr", "pva", "pvf", "qcp", "qdraw_pipe", "r3d",
                "rawvideo", "redspark", "rl2", "rm", "roq", "rpl", "rsd", "rso", "rtp", "rtp_mpegts", "rtsp", "s16be",
                "s16le", "s24be", "s24le", "s32be", "s32le", "s337m", "s8", "sami", "sap", "sbc", "sbg", "scc", "sdl",
                "sdl2", "sdp", "sdr2", "sds", "sdx", "ser", "sgi_pipe", "shn", "siff", "singlejpeg", "sln", "smjpeg",
                "smk", "smoothstreaming", "smush", "sol", "sox", "spdif", "spx", "sunrast_pipe", "sup", "svag", "svcd",
                "svg_pipe", "swf", "tak", "thp", "tiertexseq", "tiff_pipe", "tmv", "truehd", "tta", "tty", "txd", "ty",
                "u16be", "u16le", "u24be", "u24le", "u32be", "u32le", "u8", "v210", "v210x", "vag", "vc1", "vcd",
                "vfwcap", "vidc", "vividas", "vivo", "vmd", "vob", "vobsub", "voc", "vpk", "vplayer", "vqf", "w64",
                "wav", "wc3movie", "webm", "webp", "webp_pipe", "webvtt", "wsvqa", "wtv", "wv", "wve", "xa", "xmv",
                "xpm_pipe", "xvag", "xwd_pipe", "xwma", "yop", "yuv4mpegpipe");
        os.fs.loadAssociations();
    }

    @Override
    protected boolean onClose() {
        if(loopThread != null)
            loopThread.interrupt();
        if(grabber == null) return true;
        try {
            grabber.stop();
        } catch (FrameGrabber.Exception e) {
            System.err.println(e.getMessage());
        }
        return true;
    }

    @Override
    protected void update() {
    }

    @Override
    protected void render(Graphics2D g2d) {
        if(grabber == null) return;
        if(!playing) return;
        if(lastFrame == null) return;
        if(lastFrame.imageWidth == 0 || lastFrame.imageHeight == 0) return;

        ByteBuffer image = (ByteBuffer) lastFrame.image[0];
        image.clear();

        for(int y = 0; y < lastFrame.imageHeight; y++) {
            for(int x = 0; x < lastFrame.imageWidth; x++) {
                int b = (image.get() & 0xff);
                int g = (image.get() & 0xff);
                int r = (image.get() & 0xff);
                if(lastFrame.imageChannels == 4) image.get();
                int argb = (255 << 24) | (r << 16) | (g << 8) | b;

                buffer.setRGB(x, y, argb);
            }
        }
        lastFrame = null;

        g2d.drawImage(buffer, bX, bY, bW, bH, null);
    }

    @Override
    public void processInput(int x, int y, TotalComputers.InputInfo.InteractType type) {
        if(grabber == null) return;
        playing = !playing;
    }
}
