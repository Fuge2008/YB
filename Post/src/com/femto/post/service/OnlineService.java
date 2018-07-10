package com.femto.post.service;import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/** * @author  作者 Deep  @date 创建时间：2016年6月27日 下午4:02:43 * @version 1.0 * @parameter  * @since  * @return  */
public class OnlineService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
      
    @Override  
    public void onCreate() {  
        super.onCreate();  
    }  
      
    @Override  
    public void onStart(Intent intent, int startId) {  
       
        super.onStart(intent, startId);  
    }  
      
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        
        return super.onStartCommand(intent, flags, startId);  
    }  
}
