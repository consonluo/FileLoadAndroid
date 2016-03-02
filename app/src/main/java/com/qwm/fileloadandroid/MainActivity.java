package com.qwm.fileloadandroid;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qwm.fileloadandroid.net.AsyncHttpResponseCallback;
import com.qwm.fileloadandroid.net.BaseNetEntity;
import com.qwm.fileloadandroid.net.UpLoadUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MainActivity extends AppCompatActivity {

    private final String uploadUrl = "http://192.168.0.254:8080/FileLoadService/UploadServlet";
    private final int PHOTO = 50002;
    private String IMAGE_PHOTO = "image/*";
    private String MEDIA_AUDIO = "audio/*";
    private String MEDIA_VIDEO = "video/*";
    private List<String> filePathList = new ArrayList<String>();
    private TextView addressTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addressTv =(TextView)findViewById(R.id.tv_address);
    }

    /**
     * 选择图片
     * @param view
     */
    public void selectPic(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PHOTO);
        startActivityForResult(intent, PHOTO);
    }

    /**
     * 上传图片
     * @param view
     */
    public void uploadPic(View view){
        if(filePathList.size()<=0){
            Toast.makeText(this,"请选择文件",Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(filePathList.get(0));
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            byte[] data = output.toByteArray();
            output.close();
            input.close();
            UpLoadUtils.getInstance().upload(this, data, uploadUrl, "temp.jpg", new UpLoadUtils.FileUpLoadCallBack() {
                @Override
                public void onResponse(String requestStr) {
                    Toast.makeText(MainActivity.this,requestStr,Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * 上传图片
     * @param view
     */
    public void uploadPic2(View view){
        if(filePathList.size()<=0){
            Toast.makeText(this,"请选择文件",Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(filePathList.get(0));
        if(file.exists()){
            UpLoadUtils.getInstance().upload(this,file,uploadUrl,"tt.jpg",new UpLoadUtils.FileUpLoadCallBack() {
                @Override
                public void onResponse(String requestStr) {
                    Toast.makeText(MainActivity.this,requestStr,Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this,"文件不存在："+file.getAbsolutePath(),Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 上传图片
     * @param view
     */
    public void uploadMulFile(View view){
        if(filePathList.size()<=0){
            Toast.makeText(this,"请选择文件",Toast.LENGTH_SHORT).show();
            return;
        }
        List<File> fileList = new ArrayList<File>();
        for (int i = 0; i <filePathList.size(); i++) {
            File file = new File(filePathList.get(i));
            if(file.exists()){
               fileList.add(file);
            }else{
                Toast.makeText(this,"文件不存在:"+file.getAbsolutePath(),Toast.LENGTH_SHORT).show();
            }
        }
        if(fileList.size()>0){
            UpLoadUtils.getInstance().upload(this,fileList,uploadUrl,"tt.jpg",new UpLoadUtils.FileUpLoadCallBack() {
                @Override
                public void onResponse(String requestStr) {
                    Toast.makeText(MainActivity.this,requestStr,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    //================================volley上传========================================

    /**
     * 上传图片
     * @param view
     */
    public void uploadPic22(View view){
        if(filePathList.size()<=0){
            Toast.makeText(this,"请选择文件",Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(filePathList.get(0));
        if(file.exists()){
           BaseNetEntity.getInstance().sendPostParamsOneFile(this, "上传文件中。。。", true, new AsyncHttpResponseCallback() {
               @Override
               public void onSuccess(String resultStr) {
                   Toast.makeText(MainActivity.this,resultStr,Toast.LENGTH_SHORT).show();
               }
           }, null, file, uploadUrl);
        }else{
            Toast.makeText(this,"文件不存在："+file.getAbsolutePath(),Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 上传图片
     * @param view
     */
    public void uploadMulFile22(View view){
        if(filePathList.size()<=0){
            Toast.makeText(this,"请选择文件",Toast.LENGTH_SHORT).show();
            return;
        }
        List<File> fileList = new ArrayList<File>();
        for (int i = 0; i <filePathList.size(); i++) {
            File file = new File(filePathList.get(i));
            if(file.exists()){
                fileList.add(file);
            }else{
                Toast.makeText(this,"文件不存在:"+file.getAbsolutePath(),Toast.LENGTH_SHORT).show();
            }
        }
        if(fileList.size()>0){
            BaseNetEntity.getInstance().sendPostParamsFile(this, "上传中...", true, new AsyncHttpResponseCallback() {
                @Override
                public void onSuccess(String resultStr) {
                    Toast.makeText(MainActivity.this,resultStr,Toast.LENGTH_SHORT).show();
                }
            }, null, fileList, uploadUrl);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=RESULT_OK){
            return;
        }
        if (requestCode == PHOTO) {
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null,null);
            if(cursor==null){
                return;
            }
            cursor.moveToFirst();
            filePathList.add(cursor.getString(1));// 图片文件路径
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < filePathList.size(); i++) {
                sb.append(filePathList.get(i)+"");
            }
            addressTv.setText(sb.toString());
        }
    }
}
