package com.qwm.fileloadandroid.net;

import android.app.ProgressDialog;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

/**
 * @author qiwenming
 * @date 2016/3/2 0002 下午 4:41
 * @ClassName: AsyncHttpResponseCallback
 * @PackageName: com.qwm.fileloadandroid.net
 * @Description:
 */
public abstract class AsyncHttpResponseCallback implements Listener
{
	/**
	 * 加载框
	 */
	protected ProgressDialog ld;
	
	public void setLoadingDialog(ProgressDialog ld){
		this.ld = ld;
	}


	@Override
	public void onResponse(Object responseObj) {
		if(ld != null && ld.isShowing()){
			ld.dismiss();
		}
		onSuccess(responseObj.toString());
	}  
	
	
	public class MyErrorListener implements ErrorListener{

		@Override
		public void onErrorResponse(VolleyError error) {
			if(ld != null && ld.isShowing()){ 
			  ld.dismiss();
		    }
			onFailure(error);
		}
	}

	public abstract void onSuccess(String resultStr);

	/**
	 * 网络异常或服务器异常
	 */
	public void onFailure(Throwable error) {
	}

}
