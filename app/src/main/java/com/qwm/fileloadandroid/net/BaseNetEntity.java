package com.qwm.fileloadandroid.net;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qiwenming
 * @date 2016/3/2 0002 下午 4:34
 * @ClassName: BaseNetEntity
 * @PackageName: com.qwm.fileloadandroid.net
 * @Description: 网络请求类
 */
public class BaseNetEntity {
//	public Context context = MyApplication.applicationContext;
//	public AsyncHttpResponseCallback hanlder;
	private static Object netEntityLock = new Object();
	private static Object requestQueueLock = new Object();
	private static RequestQueue mRequestQueue; //网络请求队列
	private static BaseNetEntity baseNetEntity;

	private BaseNetEntity(){} 
	
	public static BaseNetEntity getInstance(){
		if(baseNetEntity == null){
			synchronized (netEntityLock) {
				if (baseNetEntity == null){
					baseNetEntity = new BaseNetEntity();
				}
			}
		}
		
		return baseNetEntity;
	}
	
	
	public <T> Map<String,String>  getSendData(T beanClass) {
		if(beanClass==null){
			return null;
		}
		
		try{
//			T bean = beanClass;
			Class<? extends Object> clazz = beanClass.getClass();
			
			Map<String,String> paramsMap = new HashMap<String,String>();
			Field[] fields = clazz.getDeclaredFields();
			for(Field  field : fields){
				field.setAccessible(true);
				String name = field.getName();
				Object filedValue = field.get(beanClass);
				
				if(filedValue != null && !"".equals(filedValue)){
					paramsMap.put(name, filedValue.toString());
				}
			}
			
			return paramsMap;
		}catch(Exception e){
			e.printStackTrace();
		}
 
		return null;
	}

	/**
	 * 	文件和数据上传
	 * 在bean中不能定义 上传的文件的bean的对应得属性名
	 * 通过 filePartName 传递进来
	 * @param context
	 * @param message
	 * @param isShowDialog
	 * @param hanlder
	 * @param beanClass
	 * @param fileList
	 * @param url
	 * @param <T>
	 */
	public <T> void sendPostParamsFile(final Context context, final String message, boolean isShowDialog, final AsyncHttpResponseCallback hanlder,
			T beanClass,List<File> fileList ,String url){
		if(isShowDialog){
	    		ProgressDialog pd = ProgressDialog.show(context,"上传文件",message);
	        	hanlder.setLoadingDialog(pd);
    	}		

		HashMap<String,String> paramsMap = new HashMap<String,String>();
		if(beanClass!=null){
			paramsMap.put("data", new Gson().toJson(beanClass));
		}
    	RequestQueue mQueue = getRequestQueue(context); 
    	MultipartRequest request = new MultipartRequest(url, hanlder,  hanlder.new MyErrorListener(), fileList, paramsMap);
    	request.setRetryPolicy(new DefaultRetryPolicy(6000000, 0, 1.0f));
    	mQueue.add(request);
	}
	
	/**
	 * 文件和数据上传 单个文件
	 * 在bean中不能定义 上传的文件的bean的对应得属性名
	 * 通过 filePartName 传递进来
	 * @param context
	 * @param Message
	 * @param isShowDialog
	 * @param hanlder
	 * @param beanClass
	 * @param fileUrlList 文件
	 * @param url 
	 */
	/**
	 * 文件和数据上传 单个文件
	 * 在bean中不能定义 上传的文件的bean的对应得属性名
	 * 通过 filePartName 传递进来
	 * @param context
	 * @param Message
	 * @param isShowDialog
	 * @param hanlder
	 * @param beanClass
	 * @param itemBean
	 * @param url
	 * @param <T>
	 */
	public <T> void sendPostParamsOneFile(final Context context, final String Message, boolean isShowDialog, final AsyncHttpResponseCallback hanlder,
			T beanClass,File file ,String url){
		if(isShowDialog){
		    		ProgressDialog ld = ProgressDialog.show(context,"上传图片",Message);
		        	if(null == null){
		        		ld.setMessage("正在加载数据");
		        	}else{
		        		ld.setMessage("");
		        	}
		        	ld.show();
		        	hanlder.setLoadingDialog(ld);
    	}		

		HashMap<String,String> paramsMap = new HashMap<String,String>();
		if(beanClass!=null){
			paramsMap.put("data", new Gson().toJson(beanClass));
		}
    	RequestQueue mQueue = getRequestQueue(context); 
    	MultipartRequest request = new MultipartRequest(url, hanlder,  hanlder.new MyErrorListener(), file, paramsMap);
    	request.setRetryPolicy(new DefaultRetryPolicy(6000000, 0, 1.0f));
    	mQueue.add(request);
	}


	/**
	 * 获取请求队列，把接口返回的cookie缓存起来
	 * @param context
	 * @return
	 */
	public  RequestQueue getRequestQueue(Context context) {
		if(mRequestQueue == null){
			synchronized (requestQueueLock) {
				if (mRequestQueue == null) {
					mRequestQueue = Volley.newRequestQueue(context);
				}
			}
		}
		return mRequestQueue;
	}
	
}
