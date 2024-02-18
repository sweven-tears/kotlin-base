package pers.sweven.common.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import pers.sweven.common.utils.NumberUtils;
import pers.sweven.common.utils.ToastUtils;

/**
 * 页面跳转管理
 * <p>仿ARouter写法，便于后期更新换代
 */
public class PageManager {
    private final LinkedHashMap<String, Object> map = new LinkedHashMap<>();
    protected Class<?> clazz;
    protected String page;
    private int flags;
    private Bundle options;
    private boolean forbiddenRepeat;
    private boolean keepOnly;

    protected PageManager() {
    }

    public Navigation build(Class<?> clazz) {
        this.clazz = clazz;
        return new Navigation();
    }

    public Navigation getExtras() {
        return new Navigation();
    }

    private Bundle getBundle() {
        Bundle bundle = new Bundle();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                bundle.putString(entry.getKey(), (String) value);
            } else if (value instanceof Integer) {
                bundle.putInt(entry.getKey(), (Integer) value);
            } else if (value instanceof Double) {
                bundle.putDouble(entry.getKey(), (Double) value);
            } else if (value instanceof Float) {
                bundle.putFloat(entry.getKey(), (Float) value);
            } else if (value instanceof Boolean) {
                bundle.putBoolean(entry.getKey(), (Boolean) value);
            } else if (value instanceof Serializable) {
                bundle.putSerializable(entry.getKey(), (Serializable) value);
            }
        }
        return bundle;
    }

    private Intent getIntent() {
        Intent bundle = new Intent();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                bundle.putExtra(entry.getKey(), (String) value);
            } else if (value instanceof Integer) {
                bundle.putExtra(entry.getKey(), (Integer) value);
            } else if (value instanceof Double) {
                bundle.putExtra(entry.getKey(), (Double) value);
            } else if (value instanceof Float) {
                bundle.putExtra(entry.getKey(), (Float) value);
            } else if (value instanceof Boolean) {
                bundle.putExtra(entry.getKey(), (Boolean) value);
            } else if (value instanceof Serializable) {
                bundle.putExtra(entry.getKey(), (Serializable) value);
            }
        }
        return bundle;
    }

    /**
     * 启动activity
     */
    public void navigation(Context context) {
        if (clazz == null) {
            ToastUtils.showShort("页面" + page + "不存在");
            return;
        }
        if (keepOnly) {
            PageInit.getInstance().finishOtherActivity(clazz);
        }
        if (PageInit.getInstance().isHas(clazz) && forbiddenRepeat) {
            return;
        }
        if (context == null) {
            context = PageInit.getInstance().getContext();
        }
        Intent intent = new Intent(context, clazz);
        if (flags != 0) {
            intent.setFlags(flags);
        }
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtras(getBundle());
        context.startActivity(intent, options);
    }

    /**
     * 带requestCode启动activity
     */
    public void navigation(Activity activity, int requestCode) {
        if (clazz == null) {
            ToastUtils.showShort("页面" + page + "不存在");
            return;
        }
        if (keepOnly) {
            PageInit.getInstance().finishOtherActivity(clazz);
        }
        if (PageInit.getInstance().isHas(clazz) && forbiddenRepeat) {
            return;
        }
        try {
            Intent intent;
            intent = new Intent(activity, clazz);
            intent.putExtras(getBundle());
            intent.setFlags(flags);
            activity.startActivityForResult(intent, requestCode, options);
        } catch (Exception e) {
            ToastUtils.showShort(activity.getClass().getCanonicalName() + "页面跳转失败");
            e.printStackTrace();
        }
    }

    public class Navigation {
        /**
         * 仅支持 int,double,boolean,string
         *
         * @param url example: index=2&data=hello&is_first=true
         * @return {@link Map<String,Object>}=[{"index":2,"data":"hello","is_first":true}]
         */
        public Navigation withUrl(String url) {
            if (url != null) {
                String[] arrays = url.split("&");
                for (String s : arrays) {
                    String[] params = s.split("=");
                    if (params.length == 2) {
                        String key = params[0];
                        String value = params[1];
                        boolean isNumber = NumberUtils.isNumeric(value);
                        if (isNumber) {
                            if (value.contains(".")) {
                                double v = NumberUtils.parseDouble(value);
                                withDouble(key, v);
                            } else {
                                int anInt = NumberUtils.parseInt(value);
                                withInt(key, anInt);
                            }
                        } else if (value.equals("true")) {
                            withBoolean(key, true);
                        } else if (value.equals("false")) {
                            withBoolean(key, false);
                        } else {
                            withString(key, value);
                        }
                    }
                }
            }
            return this;
        }

        public Navigation withString(String key, String value) {
            map.put(key, value);
            return this;
        }

        public Navigation withInt(String key, int value) {
            map.put(key, value);
            return this;
        }

        public Navigation withBoolean(String key, boolean value) {
            map.put(key, value);
            return this;
        }

        public Navigation withFloat(String key, float value) {
            map.put(key, value);
            return this;
        }

        public Navigation withDouble(String key, Double value) {
            map.put(key, value);
            return this;
        }

        public Navigation withSerializable(String key, Serializable value) {
            map.put(key, value);
            return this;
        }


        public Navigation addFlags(int flag) {
            flags = flags | flag;
            return this;
        }

        public Navigation setFlags(int flag) {
            flags = flag;
            return this;
        }

        public Navigation setOptions(Bundle bundle) {
            options = bundle;
            return this;
        }

        /**
         * 禁止重复启动
         */
        public Navigation forbiddenRepeat(boolean forbidden) {
            forbiddenRepeat = forbidden;
            return this;
        }


        /**
         * 保留一个
         */
        public Navigation saveOne(boolean saveOne) {
            keepOnly = saveOne;
            return this;
        }

        public void navigation() {
            PageManager.this.navigation(null);
        }

        public void navigation(Context context) {
            PageManager.this.navigation(context);
        }

        public void navigation(Activity activity, int requestCode) {
            PageManager.this.navigation(activity, requestCode);
        }

    }
}