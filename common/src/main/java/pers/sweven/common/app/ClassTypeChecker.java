package pers.sweven.common.app;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ClassTypeChecker {

    /**
     * 通用的检查方法，判断目标类是否为指定基类的子类
     * @param targetClass 目标类
     * @param baseClass 基类
     * @return 如果目标类是基类的子类返回 true，否则返回 false
     */
    public static boolean isSubclassOf(Class<?> targetClass, Class<?> baseClass) {
        return baseClass.isAssignableFrom(targetClass);
    }

    // 为了方便使用，保留之前特定类型的检查方法
    public static boolean isActivitySubclass(Class<?> clazz) {
        return isSubclassOf(clazz, Activity.class);
    }

    public static boolean isFragmentSubclass(Class<?> clazz) {
        return isSubclassOf(clazz, Fragment.class);
    }

    public static boolean isDialogFragmentSubclass(Class<?> clazz) {
        return isSubclassOf(clazz, DialogFragment.class);
    }

    public static boolean isDialogSubclass(Class<?> clazz) {
        return isSubclassOf(clazz, Dialog.class);
    }

    public static boolean isPopupWindowSubclass(Class<?> clazz) {
        return isSubclassOf(clazz, PopupWindow.class);
    }

    public static boolean isToastSubclass(Class<?> clazz) {
        return isSubclassOf(clazz, Toast.class);
    }

    public static boolean isBottomSheetDialogSubclass(Class<?> clazz) {
        return isSubclassOf(clazz, BottomSheetDialog.class);
    }

    public static boolean isBottomSheetDialogFragmentSubclass(Class<?> clazz) {
        return isSubclassOf(clazz, BottomSheetDialogFragment.class);
    }

    public static boolean isSearchDialogSubclass(Class<?> clazz) {
        return isSubclassOf(clazz, AlertDialog.class);
    }

    public static boolean isDatePickerDialogSubclass(Class<?> clazz) {
        return isSubclassOf(clazz, DatePickerDialog.class);
    }

    public static boolean isTimePickerDialogSubclass(Class<?> clazz) {
        return isSubclassOf(clazz, TimePickerDialog.class);
    }

    public static boolean isWindowViewRelatedClass(Class<?> clazz) {
        return isActivitySubclass(clazz) ||
                isFragmentSubclass(clazz) ||
                isDialogFragmentSubclass(clazz) ||
                isDialogSubclass(clazz) ||
                isPopupWindowSubclass(clazz) ||
                isToastSubclass(clazz) ||
                isBottomSheetDialogSubclass(clazz) ||
                isBottomSheetDialogFragmentSubclass(clazz) ||
                isSearchDialogSubclass(clazz) ||
                isDatePickerDialogSubclass(clazz) ||
                isTimePickerDialogSubclass(clazz);
    }
}