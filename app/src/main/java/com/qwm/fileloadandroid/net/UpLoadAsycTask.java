package com.qwm.fileloadandroid.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * @author qiwenming
 * @date 2016/3/2 0002 下午 1:33
 * @ClassName: UpLoadAsycTask
 * @PackageName: com.qwm.fileloadandroid.net
 * @Description:  伤处文件的操作方法

    http请求：

    POST/logsys/home/uploadIspeedLog!doDefault.html HTTP/1.1
    　　Accept-Language: zh-cn
    　　Host: 192.168.24.56
    　　Content-Type:multipart/form-data;boundary=-----------------------------7db372eb000e2
    　　User-Agent: WinHttpClient
    　　Content-Length: 3693
    　　Connection: Keep-Alive
    　　-------------------------------7db372eb000e2
    　　Content-Disposition: form-data; name="file"; filename="kn.jpg"
    　　Content-Type: image/jpeg
    　　(此处省略文件二进制数据...）
    　　-------------------------------7db372eb000e2--

    主要说明：
    boundary=-----------------------------7db372eb000e2 :定义分割符

    -----------------------------7db372eb000e2        ：分隔符
    -------------------------------7db372eb000e2      ：Form每个部分用分隔符分割 (--boundary)
    -------------------------------7db372eb000e2--    ：结束分隔符 (--boundary--)
 *
 *
 */
public class UpLoadAsycTask extends AsyncTask {
    private static final int TIME_OUT = 10*10000000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码
    private final Context context;
    private ProgressDialog pdialog;

    public UpLoadAsycTask(Context context){
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
    }

    /**
     * 上传文件
     * @param data
     * @param requestPath
     * @param filename
     * @return
     */
    public static String upload(byte[] data,String requestPath,String filename){
        String boundary = "---------------"+ UUID.randomUUID().toString();//边界标识符
        String prefix = "--",line_end = "\r\n";
        String content_type = "multipart/form-data";//内容类型
        try {
            URL url = new URL(requestPath);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置http头部
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);//允许输入流
            conn.setDoOutput(true);//允许输出流
            conn.setUseCaches(false);//不使用缓存
            conn.setRequestMethod("POST");//使用post方式请求
            conn.setRequestProperty("Charset", CHARSET);//设置编码
            conn.setRequestProperty("connection", "keep-alive");//保持连接
            conn.setRequestProperty("Content-Type", content_type + ";boundary" + boundary);//设置内容类型和定义分隔符

            //开始写数据
            OutputStream out = conn.getOutputStream();//获取输出流
            out.write((prefix+boundary+line_end).getBytes());//form分割符
            out.write(("Content-Disposition: form-data;name=\"xiaoming\";filename=\""+filename+"\""+line_end).getBytes());//，描述
            out.write(("Content-Type: application/octet-stream; charset="+CHARSET+"\r\n").getBytes());//流方式发送数据
            out.write("Content-Transfer-Encoding: binary\r\n".getBytes());
            out.write(line_end.getBytes());
            out.write(data);//写入文件数据
            out.write(line_end.getBytes());
            out.write((prefix+boundary+prefix+line_end).getBytes());//form结束分隔符
            out.flush();
            out.close();

            //读取响应数据
            int code = conn.getResponseCode();
            StringBuilder sb = new StringBuilder();//存放响应结果
            String sCurrentLine = "";
            if(code==200){
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                while ((sCurrentLine=reader.readLine())!=null){
                    sb.append(sCurrentLine);
                }
            }else{
                sb.append("\"远程服务器连接失败,错误代码:\" + code");
            }
            return sb.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
