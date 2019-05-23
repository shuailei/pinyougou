package util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import java.io.File;

public class QiniuUtil {
    /**
     * 设置七牛云存储区域
     * Zone.zone0() ：华东
     * Zone.zone1() ：华北
     * Zone.zone2() ：华南
     */
    private static Configuration cfg = new Configuration(Zone.zone1());
    /**
     * 上传控制器
     */
    private final static UploadManager uploadManager = new UploadManager(cfg);
    /**
     * 七牛云外链地址
     */
    private static String qiniu_outside_url = "http://ojwvupskq.bkt.clouddn.com/";

    /**
     * 七牛云账号对应的密钥
     */
    private static final Auth qiniu_auth = Auth.create("KFGPiYb1emLxkmlG5-v40gvSr_AHiUYWzGuaVe7j","ZaSXZftH3-ZUGn6aG9w41N5E2HRj64R9amaXbqnL");
    /**
     * 七牛云上传空间名
     */
    private static final String qiniu_bucket = "pictures";

    /**
     * 
     * @Description: 简单上传，使用默认策略
     * @author: tianpengw
     * @return
     */
    public static String getUpTokenDefault(){
        return qiniu_auth.uploadToken(qiniu_bucket);
    }

    /**
     * 
     * @Description: 覆盖上传
     * @author: tianpengw
     * @param key
     * @return
     */
    public static String getUpTokenCover(String key){
        return qiniu_auth.uploadToken(qiniu_bucket, key);
    }

    /**
     * 
     * @Description: 上传磁盘文件
     * @author: tianpengw
     * @param fileFullName 文件全路径
     * @param keyPath 上传文件的路径
     * @param upToken 七牛云上传token
     * 
     */
    private static String qiniuUploadFile(String fileFullName, String keyPath, String upToken){
        try {
            if(null != fileFullName && !"".equals(fileFullName) && new File(fileFullName).exists()){
                if(null == keyPath || "".equals(keyPath)){
                    keyPath = "image/";
                }
                String fileName = fileFullName.substring(fileFullName.lastIndexOf("/")+1);
                String key = keyPath + System.currentTimeMillis()+"_"+fileName;
                boolean result =upload(fileFullName, key, upToken);
                if(result){
                    return qiniu_outside_url + key;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 
     * @Description: 上传磁盘中的文件
     * @author: tianpengw
     * @param filePath
     * @param key
     * @param upToken
     * @return
     */
    private static boolean upload(String filePath, String key, String upToken){
        try {
            Response res = uploadManager.put(filePath, key, upToken);
          /*  //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(res.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);*/
          //  System.out.println(res.bodyString());
            if(res.isOK()){
                return true;
            }
        } catch (QiniuException e) {
            Response r = e.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
                System.out.println("上传磁盘文件异常！");
            } catch (QiniuException ex2) {
                //ignore
            }

        }
        return false;
    }

    /**
     * 
     * @Description: 上传文件
     * @author: tianpengw
     * @param fileFullName
     * @param keyPath
     * @param keyName 如果覆盖上传此值不为空，如果为空默认上传
     * @return
     */
    public static String uploadFile(String fileFullName, String keyPath, String keyName){

        if(null != keyName && !"".equals(keyName)){
            return qiniuUploadFile(fileFullName, keyPath, getUpTokenCover(keyName));
        }else{
            return qiniuUploadFile(fileFullName, keyPath, getUpTokenDefault());
        }
    }

    /*public static void main(String[] args) {
        System.out.println(uploadFile("/Users/wanglei/Downloads/entertainment/entertainment123.png",null,null));
    }*/
}
