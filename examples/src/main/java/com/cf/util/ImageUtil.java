package com.cf.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;   

public class ImageUtil {

    private static int MAX_CREATE_THUMBNAIL_TRY =  3;
   
    public static boolean createThumbnail(String urlParam, String imageSavePath){
        return createThumbnail(urlParam, imageSavePath, MAX_CREATE_THUMBNAIL_TRY);
    }
    
    private static boolean createThumbnail(String urlParam, String imageSavePath, int maxTry){
        if(maxTry > 0){
            try {
                URL url = new URL(urlParam);
                BufferedImage img = new BufferedImage(225, 225, BufferedImage.TYPE_INT_RGB);
                img.createGraphics().drawImage(ImageIO
                        .read(url)
                        .getScaledInstance(225, 225, Image.SCALE_SMOOTH),0,0,null);
                ImageIO.write(img, "jpg", new File(imageSavePath));
                return true;
            } catch (Exception e) {
                createThumbnail(urlParam, imageSavePath, --maxTry);
            }
        }
        return false;
    }
}