package site.pyyf.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class processMd2Ebook {
    public static void main(String[] args) throws Exception {

        String filePath = "F:\\Projects\\Java\\tianti\\ebook\\src\\main\\resources\\容器.md";
        final String imgsPath = StringUtils.substringBeforeLast(filePath, "\\") + "\\imgs\\" +
                StringUtils.substringAfterLast(StringUtils.substringBeforeLast(filePath, "."), "\\") ;
        replacePath(filePath);
        uploadDir(imgsPath);
    }

    public static void uploadDir(String imgsPath){
        final UploadDir uploadDir = new UploadDir();
        final File file = new File(imgsPath);
        for (File listFile : file.listFiles()) {
            uploadDir.upload(listFile);
        }
    }

    public static void replacePath(String filePath) throws Exception {
        final md2oss md2oss = new md2oss();
        String fileName = filePath.split("\\\\")[filePath.split("\\\\").length - 1];
        String fileNameWithoutSuffix = fileName.split("\\.")[0];
        String src = ".\\imgs\\" + fileNameWithoutSuffix + "\\";
        String dst = "https://pyyf.oss-cn-hangzhou.aliyuncs.com/ebook/" + fileNameWithoutSuffix + "\\";
        md2oss.replace(filePath, src, dst);
    }
}
