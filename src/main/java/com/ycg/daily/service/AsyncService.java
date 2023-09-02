package com.ycg.daily.service;


import com.ycg.daily.pojo.DailyInfo;

public interface AsyncService {

    void dailyInfoAsync();

    void updateViews(DailyInfo dailyInfo);

}
