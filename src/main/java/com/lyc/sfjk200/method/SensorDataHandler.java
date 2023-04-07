package com.lyc.sfjk200.method;

import java.util.List;
import java.util.Map;

/**
 * 接口 SensorDataHandler
 *
 * @author ChenQi
 * @date 2023/4/6
 */
public interface SensorDataHandler {
    List<Map<String, Object>> readAttributeUnit();

    List<Map<String, Object>> readLowLimitAlarmValue();

    Map<String, Object> readDetectorStatus();

    List<Map<String, Object>> readDetectorConcentration();

    Map<String, Object> readPowerStatus();

    List<Map<String, Object>> readControllerStatus();

}
