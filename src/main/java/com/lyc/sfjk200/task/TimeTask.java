package com.lyc.sfjk200.task;

import cn.allbs.common.constant.DateConstant;
import cn.allbs.common.constant.StringPool;
import cn.allbs.common.utils.ObjectUtil;
import cn.allbs.exception.SFJK200Exception;
import cn.allbs.influx.InfluxTemplate;
import cn.allbs.utils.SFJK200.enums.KeyWordEnum;
import cn.allbs.utils.SFJK200.enums.OrderEnum;
import cn.allbs.utils.SFJK200.format.SFJK200Mapper;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.lyc.sfjk200.config.Sfjk200Properties;
import com.lyc.sfjk200.constant.CommonConstant;
import com.lyc.sfjk200.enums.GasKeyWordEnum;
import com.lyc.sfjk200.method.SensorDataAggregate;
import com.lyc.sfjk200.method.SensorDataHandlerImpl;
import com.lyc.sfjk200.utils.SerialPortManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static cn.allbs.utils.SFJK200.enums.GeneratorEnum.*;

/**
 * 类 TimeTask
 *
 * @author ChenQi
 * @date 2023/4/6
 */
@Slf4j
@Component
public class TimeTask {

    @Resource
    private Sfjk200Properties sfjk200Properties;

    @Resource
    private SerialPortManager serialPortManager;

    @Resource
    private InfluxTemplate influxTemplate;

    @Scheduled(cron = "0 */1 * * * ?")
    public void readData() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateConstant.NORM_DATETIME_PATTERN));
        log.info("=============================={}开始读取回路和对应的点位数==================================", now);
        // 组装为待发送的数据
        SFJK200Mapper sfjk200Mapper = new SFJK200Mapper();
        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put(ADDRESS.getName(), sfjk200Properties.getAddress());
        paramMap.put(FUNCTION.getName(), sfjk200Properties.getFunction());
        paramMap.put(START_ADDRESS.getName(), OrderEnum.NUMBER_OF_PLACEMENT_1_2.getStartAddress());
        paramMap.put(READ_ADDRESS.getName(), 2);
        try {
            byte[] bytes = sfjk200Mapper.writeDataAsByteArray(paramMap);
            byte[] readBytes = serialPortManager.writeAndRead(bytes);
            // 判断使用何种方式解析 需要将byte数组转为
            Map<String, Object> map = sfjk200Mapper.readValue(readBytes, OrderEnum.NUMBER_OF_PLACEMENT_1_2.getStartAddress(), Map.class);
            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get(KeyWordEnum.DATA.getKey());
            list.forEach(a -> {
                if (MapUtil.getInt(a, KeyWordEnum.POINT.getKey(), 0) != 0) {
                    // 当前回路的点位数
                    int points = MapUtil.getInt(a, KeyWordEnum.POINT.getKey());
                    // 当前回路数
                    int loop = MapUtil.getInt(a, KeyWordEnum.CIRCUIT.getKey());
                    sfjk200Properties.setLoop(loop);
                    sfjk200Properties.setPoints(points);
                    SensorDataHandlerImpl sensorDataHandler = new SensorDataHandlerImpl(serialPortManager, sfjk200Properties);
                    SensorDataAggregate sensorDataAggregate = new SensorDataAggregate(sensorDataHandler);
                    try {
                        Map<String, Object> properties = sensorDataAggregate.aggregate();
                        properties.put("dataTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateConstant.NORM_DATETIME_PATTERN)));
                        properties.put(GasKeyWordEnum.LOOP.getName(), loop);
                        // 保存至数据库
                        this.saveInfluxDb(properties);
                    } catch (Exception e) {
                        log.error("数据处理失败{}", e.getLocalizedMessage());
                    }
                }
            });
        } catch (SFJK200Exception | IOException e) {
            log.info("SFJK200解析失败{}", e.getLocalizedMessage());
        } catch (NullPointerException e) {
            log.info("未读到数据");
        }
    }

    @Async
    @SuppressWarnings("unchecked")
    public void saveInfluxDb(Map<String, Object> map) {
        // 存储数据
        // TODO 回路点位和真实检测位置关联
        Map<String, String> tags = new HashMap<>();
        List<Map<String, Object>> paramsList = new LinkedList<>();
        map.forEach((k, v) -> {
            if (GasKeyWordEnum.DATA.getName().equals(k)) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) v;
                for (int i = 1; i <= list.size(); i++) {
                    Map<String, Object> params = new HashMap<>();
                    Map<String, Object> listMap = list.get(i - 1);
                    int mulit;
                    if (listMap.containsKey(KeyWordEnum.GAS_UNIT_PRE.getKey()) && 0 == Convert.toInt(listMap.get(KeyWordEnum.GAS_UNIT_PRE.getKey()))) {
                        mulit = 10;
                    } else {
                        mulit = 1;
                    }
                    listMap.forEach((kk, vv) -> {
                        if (kk.equals(KeyWordEnum.GAS_NUM.getKey())) {
                            params.put("alarmNum", Convert.toLong(vv) * mulit);
                            return;
                        }
                        if (kk.equals(KeyWordEnum.DETECTOR_NUM.getKey())) {
                            params.put("value", Convert.toLong(vv) * mulit);
                            return;
                        }
                        if (kk.equals(KeyWordEnum.CONTROLLER_STATUS.getKey())) {
                            List<String> valueList = (List<String>) vv;
                            vv = StrUtil.join(StringPool.COMMA, valueList);
                        }
                        params.put(kk, vv);
                    });
                    paramsList.add(params);
                }
                return;
            }
            if (GasKeyWordEnum.LOOP.getName().equals(k)) {
                tags.put(k, Convert.toStr(v, ""));
                return;
            }
            if (ObjectUtil.isNotEmpty(v)) {
                tags.put(k, v.toString());
            }
        });
        log.info("tags:{}", tags);
        log.info("params: {}", paramsList);
        influxTemplate.batchInsert(CommonConstant.FIRE_ALARM_SFJK200, tags, paramsList);
    }
}
