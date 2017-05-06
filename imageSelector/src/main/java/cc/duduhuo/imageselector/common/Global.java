package cc.duduhuo.imageselector.common;


import java.util.ArrayList;

import cc.duduhuo.imageselector.ImageSelectConfig;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 作者：https://github.com/smuyyh/ImageSelector
 * 日期：2017/4/9 18:16
 * 版本：1.0
 * 描述：全局静态变量
 * 备注：
 * =======================================================
 */
public class Global {
    /** 当前的图片文件夹 */
    public static String curFolderName;
    /** 图片选择器配置对象 */
    public static ImageSelectConfig config;
    /** 选择的图片List */
    public static ArrayList<String> imageList = new ArrayList<>();

}
