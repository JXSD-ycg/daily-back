package com.ycg.daily.pojo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class HolidayVO implements Serializable {

    /**
     * 忌事项
     */
    private String avoid;
    /**
     * 属相 例如：狗
     */
    private String chineseZodiac;
    /**
     * 星座
     */
    private String constellation;
    /**
     * 当前日期
     */
    private String date;
    /**
     * 这一年的第几天
     */
    private long dayOfYear;
    /**
     * 如果当前是工作日 则返回是当前月的第几个工作日，否则返回0 如果ignoreHoliday参数为true，这个字段不返回
     */
    private long indexWorkDayOfMonth;
    /**
     * 农历日期
     */
    private String lunarCalendar;
    /**
     * 节气描述 例如：小雪
     */
    private String solarTerms;
    /**
     * 宜事项
     */
    private String suit;
    /**
     * 0 工作日 1 假日 2 节假日 如果ignoreHoliday参数为true，这个字段不返回
     */
    private long type;
    /**
     * 类型描述 比如 国庆,休息日,工作日 如果ignoreHoliday参数为true，这个字段不返回
     */
    private String typeDes;
    /**
     * 当前周第几天 1-周一 2-周二 ... 7-周日
     */
    private long weekDay;
    /**
     * 这一年的第几周
     */
    private long weekOfYear;
    /**
     * 天干地支纪年法描述 例如：戊戌
     */
    private String yearTips;
}
