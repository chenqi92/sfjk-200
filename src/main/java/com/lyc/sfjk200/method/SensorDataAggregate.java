package com.lyc.sfjk200.method;

import cn.hutool.core.collection.CollUtil;
import com.lyc.sfjk200.enums.GasKeyWordEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 类 SensorDataAggregate
 *
 * @author ChenQi
 * @date 2023/4/6
 */
@Slf4j
public class SensorDataAggregate {
    private SensorDataHandler sensorDataHandler;

    public SensorDataAggregate(SensorDataHandler sensorDataHandler) {
        this.sensorDataHandler = sensorDataHandler;
    }

    public Map<String, Object> aggregate() throws InterruptedException {

        Map<String, Object> result = new HashMap<>();
        result.put(GasKeyWordEnum.DATA.getName(), new LinkedList<>());
        gatherData(sensorDataHandler.readAttributeUnit(), result);
        Thread.sleep(200);
        gatherData(sensorDataHandler.readLowLimitAlarmValue(), result);
        Thread.sleep(200);
        result.putAll(sensorDataHandler.readDetectorStatus());
        Thread.sleep(200);
        gatherData(sensorDataHandler.readDetectorConcentration(), result);
        Thread.sleep(200);
        result.putAll(sensorDataHandler.readPowerStatus());
        Thread.sleep(200);
        gatherData(sensorDataHandler.readControllerStatus(), result);
        Thread.sleep(200);
        return result;
    }

    @SuppressWarnings("uncheked")
    private Map<String, Object> gatherData(List<Map<String, Object>> list, Map<String, Object> properties) {
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) Optional.ofNullable(properties.get(GasKeyWordEnum.DATA.getName())).orElse(list);
        List<Map<String, Object>> resList = new LinkedList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> resMap = list.get(i);
            if (CollUtil.isNotEmpty(dataList) && i < dataList.size()) {
                resMap.putAll(dataList.get(i));
            }
            resList.add(resMap);
        }
        properties.put(GasKeyWordEnum.DATA.getName(), resList);
        return properties;
    }
}
