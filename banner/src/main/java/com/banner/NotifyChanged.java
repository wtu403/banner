package com.banner;

import java.util.List;

/**
 * 用于更新数据
 */
public interface NotifyChanged {
    void notifyDataSetChanged(List<String> title,int count);
}
