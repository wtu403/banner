package com.banner;

import android.view.View;

import java.util.List;

public abstract class BannerAdapter{
    private NotifyChanged notifyChanged;

    public void setNotifyChanged(NotifyChanged notifyChanged) {
        this.notifyChanged = notifyChanged;
    }

    protected abstract View getView(int position, View convertView);
    protected abstract int getCount();
    /**
     * 设置title
     * @return
     */
    protected List<String> getTitles(){
        return null;
    }

    protected void notifyDataSetChanged(){
        if (notifyChanged != null){
            notifyChanged.notifyDataSetChanged(getTitles(),getCount());
        }
    }
}
