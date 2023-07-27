package com.app.test;

import pers.sweven.common.app.BaseApplication;
import pers.sweven.common.widget.SItemDecoration;

/**
 * Created by Sweven on 2023/7/27--13:15.
 * Email: sweventears@163.com
 */
public class App extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        new SItemDecoration(20,0,0,false);
    }


    public String a(String a){
        return null;
    }
}
