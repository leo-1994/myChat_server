package com.leo.chat.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class QRCodeUtils {

    public static void createQRCode(String filePath, String content) {
        //图片的宽度
        int width = 300;
        //图片的高度
        int height = 300;
        //图片的格式
        String format = "png";
        // 定义二维码的参数
        Map<EncodeHintType, Object> hints = new HashMap<>(3);
        //指定字符编码为“utf-8”
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //指定二维码的纠错等级为中级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        //设置图片的边距
        hints.put(EncodeHintType.MARGIN, 2);

        // 生成二维码
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            Path file = new File(filePath).toPath();
            MatrixToImageWriter.writeToPath(bitMatrix, format, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getContentFromQRCode(String filePath) {
        MultiFormatReader formatReader = new MultiFormatReader();
        File file = new File(filePath);
        BufferedImage image;
        try {
            image = ImageIO.read(file);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer
                    (new BufferedImageLuminanceSource(image)));
            Map<DecodeHintType, Object> hints = new HashMap<>(1);
            //指定字符编码为“utf-8”
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
            Result result = formatReader.decode(binaryBitmap, hints);
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
