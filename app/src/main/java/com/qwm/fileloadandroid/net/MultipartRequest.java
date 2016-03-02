package com.qwm.fileloadandroid.net;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author qiwenming
 * @date 2016-3-2 下午4:10:12
 * @ClassName: MultipartRequest
 * @Description: qwenming
 */
public class MultipartRequest extends Request<String> {

    private List<File> fileList = new ArrayList<File>();
    private MultipartEntity entity = new MultipartEntity();

    private final Response.Listener<String> mListener;

    private List<File> mFileParts = new ArrayList<File>();
    private Map<String, String> mParams;

    private File file;

    //==================================当个文件上传================================================
    public MultipartRequest(String url, AsyncHttpResponseCallback hanlder,AsyncHttpResponseCallback.MyErrorListener myErrorListener, File file, Map<String, String> params) {
        super(Method.POST, url, myErrorListener);
        mListener = hanlder;
        mParams = params;
        this.file = file;
        buildtEntity();
    }


    /**
     * 单个文件上传封装 头像
     */
    private void buildtEntity() {
        FileBody fileBody = new FileBody(file);
        entity.addPart("images", fileBody);
        //数据封装
        try {
            if (mParams != null && mParams.size() > 0) {
                for (Map.Entry<String, String> entry : mParams.entrySet()) {
                    entity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName("UTF-8")));
                }
            }
        } catch (UnsupportedEncodingException e) {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }


    //==================================多个文件上传==========================================
    public MultipartRequest(String url, AsyncHttpResponseCallback hanlder,
                            AsyncHttpResponseCallback.MyErrorListener myErrorListener, List<File> fileList, Map<String, String> params) {
        super(Method.POST, url, myErrorListener);
        mListener = hanlder;
        this.fileList = fileList;
        mParams = params;
        buildMultipartEntity();
    }

    /**
     * 文件处理
     */
    private void buildMultipartEntity() {
        int i = 0;
        //文件封装
        if (fileList != null && fileList.size() > 0) {
            for (File file : fileList) {
                entity.addPart("file_" + (i++), new FileBody(file));
            }
            long l = entity.getContentLength();
            Log.i("------", fileList.size() + "个，长度：" + l);
        }

        //数据封装
        try {
            if (mParams != null && mParams.size() > 0) {
                for (Map.Entry<String, String> entry : mParams.entrySet()) {
                    entity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName("UTF-8")));
                }
            }
        } catch (UnsupportedEncodingException e) {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {

        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return  Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }


    @Override
    protected void deliverResponse(String s) {
        mListener.onResponse(s);
    }
}