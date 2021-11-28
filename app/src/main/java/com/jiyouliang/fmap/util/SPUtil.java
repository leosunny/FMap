package com.jiyouliang.fmap.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.amap.api.services.help.Tip;
import com.jiyouliang.fmap.MapApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SPUtil {
    private final static String PREFERENCE_NAME = "search_history";
    private final static String SEARCH_HISTORY="address_history";

    private final static String PREFERENCE_NAME_HOME = "perf_name_home";
    private final static String PREF_HOME_ADDRESS = "perf_home";
    private final static String PREFERENCE_NAME_OFFICE = "perf_name_office";
    private final static String PREF_OFFICE_ADDRESS = "perf_office";

    private final static String PREFERENCE_NAME_AIMLESS = "perf_name_aimless";
    private final static String AIMLESS_NORTH_VIEW="perf_north_view";

    private final static String PREFERENCE_NAME_FAVORITE = "perf_name_favorite";
    private final static String PREF_FAVORITE="perf_favorite";

    // 保存搜索记录
    public static void saveSearchHistory(String inputText) {
        SharedPreferences sp = MapApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (TextUtils.isEmpty(inputText)) {
            return;
        }
        String longHistory = sp.getString(SEARCH_HISTORY, "");  //获取之前保存的历史记录

        String[] tmpHistory = longHistory.split(";"); //逗号截取 保存在数组中
        List<String> historyList = new ArrayList<String>(Arrays.asList(tmpHistory)); //将改数组转换成ArrayList
        SharedPreferences.Editor editor = sp.edit();
        if (historyList.size() > 0) {
            //1.移除之前重复添加的元素
            for (int i = 0; i < historyList.size(); i++) {
                if (inputText.equals(historyList.get(i))) {
                    historyList.remove(i);
                    break;
                }
            }
            historyList.add(0, inputText); //将新输入的文字添加集合的第0位也就是最前面(2.倒序)
            if (historyList.size() > 10) {
                historyList.remove(historyList.size() - 1); //3.最多保存8条搜索记录 删除最早搜索的那一项
            }
            //逗号拼接
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < historyList.size(); i++) {
                sb.append(historyList.get(i) + ";");
            }
            //保存到sp
            editor.putString(SEARCH_HISTORY, sb.toString());
            editor.commit();
        } else {
            //之前未添加过
            editor.putString(SEARCH_HISTORY, inputText + ";");
            editor.commit();
        }
    }
    //获取搜索记录
    public static List<String> getSearchHistory(){
        SharedPreferences sp = MapApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String longHistory =sp.getString(SEARCH_HISTORY, "");
        String[] tmpHistory = longHistory.split(";"); //split后长度为1有一个空串对象
        List<String> historyList = new ArrayList<String>(Arrays.asList(tmpHistory));
        if (historyList.size() == 1 && historyList.get(0).equals("")) { //如果没有搜索记录，split之后第0位是个空串的情况下
            historyList.clear();  //清空集合，这个很关键
        }
        return historyList;
    }

    // 保存家和公司地址
    public static void saveHomeOfficeAddress(String inputText,AddressType type){
        if (TextUtils.isEmpty(inputText)) {
            return;
        }
        if (type.equals(AddressType.HOME)){
            SharedPreferences sp = MapApplication.getContext().getSharedPreferences(PREFERENCE_NAME_HOME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(PREF_HOME_ADDRESS, inputText);
            editor.commit();
        }else if(type.equals(AddressType.OFFICE)){
            SharedPreferences sp = MapApplication.getContext().getSharedPreferences(PREFERENCE_NAME_OFFICE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(PREF_OFFICE_ADDRESS, inputText);
            editor.commit();
        }

    }

    //获取家和公司的地址
    public static String getHomeOfficeAddress(AddressType type){
        String address = null;
        if (type.equals(AddressType.HOME)){
            SharedPreferences sp = MapApplication.getContext().getSharedPreferences(PREFERENCE_NAME_HOME, Context.MODE_PRIVATE);
            address = sp.getString(PREF_HOME_ADDRESS, "");
        }else if(type.equals(AddressType.OFFICE)){
            SharedPreferences sp = MapApplication.getContext().getSharedPreferences(PREFERENCE_NAME_OFFICE, Context.MODE_PRIVATE);
            address = sp.getString(PREF_OFFICE_ADDRESS, "");
        }

        return address;
    }

    //删除家和公司的地址
    public static void delHomeOfficeAddress(AddressType type){
        if (type.equals(AddressType.HOME)){
            SharedPreferences sp = MapApplication.getContext().getSharedPreferences(PREFERENCE_NAME_HOME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.commit();
        }else if(type.equals(AddressType.OFFICE)){
            SharedPreferences sp = MapApplication.getContext().getSharedPreferences(PREFERENCE_NAME_OFFICE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.commit();
        }
    }

    //保存巡航视角设置
    public static void saveAimlessNorthView(boolean isCarNorth){
        SharedPreferences sp = MapApplication.getContext().getSharedPreferences(PREFERENCE_NAME_AIMLESS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(AIMLESS_NORTH_VIEW, isCarNorth);
        editor.commit();
    }

    //获取续航视角
    public static boolean getAimlessNorthView(){
        SharedPreferences sp = MapApplication.getContext().getSharedPreferences(PREFERENCE_NAME_AIMLESS, Context.MODE_PRIVATE);
        boolean isCarNorth = sp.getBoolean(AIMLESS_NORTH_VIEW, true);
        return isCarNorth;
    }

    // 收藏POI
    public static void saveFavoriteAddress(String inputText) {
        SharedPreferences sp = MapApplication.getContext().getSharedPreferences(PREFERENCE_NAME_FAVORITE, Context.MODE_PRIVATE);
        if (TextUtils.isEmpty(inputText)) {
            return;
        }
        String longFavorite = sp.getString(PREF_FAVORITE, "");  //获取之前保存的历史记录

        String[] tmpFavorite = longFavorite.split(";"); //逗号截取 保存在数组中
        List<String> favoriteList = new ArrayList<String>(Arrays.asList(tmpFavorite)); //将改数组转换成ArrayList
        SharedPreferences.Editor editor = sp.edit();
        if (favoriteList.size() > 0) {

            favoriteList.add(0, inputText); //将新输入的文字添加集合的第0位也就是最前面(2.倒序)
            if (favoriteList.size() > 20) {
                favoriteList.remove(favoriteList.size() - 1); //3.最多保存记录 删除最早搜索的那一项
            }
            //逗号拼接
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < favoriteList.size(); i++) {
                sb.append(favoriteList.get(i) + ";");
            }
            //保存到sp
            editor.putString(PREF_FAVORITE, sb.toString());
            editor.commit();
        } else {
            //之前未添加过
            editor.putString(PREF_FAVORITE, inputText + ";");
            editor.commit();
        }
    }
    // 获取收藏POI列表
    public static List<String> getFavoriteAddress(){
        SharedPreferences sp = MapApplication.getContext().getSharedPreferences(PREFERENCE_NAME_FAVORITE, Context.MODE_PRIVATE);
        String longFavorite =sp.getString(PREF_FAVORITE, "");
        String[] tmpFavorite = longFavorite.split(";"); //split后长度为1有一个空串对象
        List<String> favoriteList = new ArrayList<String>(Arrays.asList(tmpFavorite));
        if (favoriteList.size() == 1 && favoriteList.get(0).equals("")) { //如果没有记录，split之后第0位是个空串的情况下
            favoriteList.clear();  //清空集合，这个很关键
        }
        return favoriteList;
    }

    // 检查POI收藏状态
    public static boolean isFavoriteAddress(String inputText) {
        SharedPreferences sp = MapApplication.getContext().getSharedPreferences(PREFERENCE_NAME_FAVORITE, Context.MODE_PRIVATE);
        if (TextUtils.isEmpty(inputText)) {
            return false;
        }
        String longFavorite = sp.getString(PREF_FAVORITE, "");  //获取之前保存的历史记录
        String[] tmpFavorite = longFavorite.split(";"); //逗号截取 保存在数组中
        List<String> favoriteList = new ArrayList<String>(Arrays.asList(tmpFavorite)); //将改数组转换成ArrayList
        if (favoriteList.size() > 0) {
            for (int i = 0; i < favoriteList.size(); i++) {
                if (inputText.equals(favoriteList.get(i))) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    // 取消收藏POI
    public static void delFavoriteAddress(String inputText) {
        SharedPreferences sp = MapApplication.getContext().getSharedPreferences(PREFERENCE_NAME_FAVORITE, Context.MODE_PRIVATE);
        if (TextUtils.isEmpty(inputText)) {
            return;
        }
        String longFavorite = sp.getString(PREF_FAVORITE, "");  //获取之前保存的历史记录

        String[] tmpFavorite = longFavorite.split(";"); //逗号截取 保存在数组中
        List<String> favoriteList = new ArrayList<String>(Arrays.asList(tmpFavorite)); //将改数组转换成ArrayList
        SharedPreferences.Editor editor = sp.edit();
        if (favoriteList.size() > 0) {
            //1.移除之前重复添加的元素
            for (int i = 0; i < favoriteList.size(); i++) {
                if (inputText.equals(favoriteList.get(i))) {
                    favoriteList.remove(i);
                    break;
                }
            }
            //逗号拼接
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < favoriteList.size(); i++) {
                sb.append(favoriteList.get(i) + ";");
            }
            //保存到sp
            editor.putString(PREF_FAVORITE, sb.toString());
            editor.commit();
        }
    }

    public enum AddressType{
        HOME,
        OFFICE
    }
}
