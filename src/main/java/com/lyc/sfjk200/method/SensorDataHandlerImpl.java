package com.lyc.sfjk200.method;

import cn.allbs.common.constant.StringPool;
import cn.allbs.utils.SFJK200.enums.KeyWordEnum;
import cn.allbs.utils.SFJK200.enums.OrderEnum;
import cn.allbs.utils.SFJK200.format.SFJK200Mapper;
import cn.hutool.core.util.StrUtil;
import com.lyc.sfjk200.config.Sfjk200Properties;
import com.lyc.sfjk200.enums.GasKeyWordEnum;
import com.lyc.sfjk200.utils.SerialPortManager;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.allbs.utils.SFJK200.enums.GeneratorEnum.*;

/**
 * 类 SensorDataHandlerImpl
 *
 * @author ChenQi
 * @date 2023/4/6
 */
@Slf4j
public class SensorDataHandlerImpl implements SensorDataHandler {
    private SerialPortManager serialPortManager;

    private Sfjk200Properties sfjk200Properties;

    public SensorDataHandlerImpl(SerialPortManager serialPortManager, Sfjk200Properties sfjk200Properties) {
        this.serialPortManager = serialPortManager;
        this.sfjk200Properties = sfjk200Properties;
    }

    // 读取点位的属性单位
    @Override
    public List<Map<String, Object>> readAttributeUnit() {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            int startAddress = OrderEnum.ATTRIBUTE_UNITS_1.getStartAddress() + (sfjk200Properties.getLoop() - 1) * (OrderEnum.ATTRIBUTE_UNITS_1.getEndAdress() + 1 - OrderEnum.ATTRIBUTE_UNITS_1.getStartAddress());
            // 组装为待发送的数据
            SFJK200Mapper sfjk200Mapper = new SFJK200Mapper();
            Map<String, Object> params = new HashMap<>(4);
            params.put(ADDRESS.getName(), sfjk200Properties.getAddress());
            params.put(FUNCTION.getName(), sfjk200Properties.getFunction());
            params.put(START_ADDRESS.getName(), startAddress);
            params.put(READ_ADDRESS.getName(), sfjk200Properties.getPoints());
            byte[] bytes = sfjk200Mapper.writeDataAsByteArray(params);

            byte[] readValue = serialPortManager.writeAndRead(bytes);
            Map<String, Object> resMap = sfjk200Mapper.readValue(readValue, startAddress, Map.class);
            list = (List<Map<String, Object>>) resMap.get(KeyWordEnum.DATA.getKey());
        } catch (Exception e) {
            log.error("回路{}读取点位属性数据发送失败", sfjk200Properties.getLoop());
        }
        return list;
    }

    // 读取点位的低限报警值
    @Override
    public List<Map<String, Object>> readLowLimitAlarmValue() {
        List<Map<String, Object>> list = new ArrayList<>();
        // 读取低限报警值，处理数据
        try {
            int startAddress = OrderEnum.LOW_ALARM_VALUE_1.getStartAddress() + (sfjk200Properties.getLoop() - 1) * (OrderEnum.LOW_ALARM_VALUE_1.getEndAdress() + 1 - OrderEnum.LOW_ALARM_VALUE_1.getStartAddress());
            // 组装为待发送的数据
            SFJK200Mapper sfjk200Mapper = new SFJK200Mapper();
            Map<String, Object> params = new HashMap<>(4);
            params.put(ADDRESS.getName(), sfjk200Properties.getAddress());
            params.put(FUNCTION.getName(), sfjk200Properties.getFunction());
            params.put(START_ADDRESS.getName(), startAddress);
            params.put(READ_ADDRESS.getName(), sfjk200Properties.getPoints());
            byte[] bytes = sfjk200Mapper.writeDataAsByteArray(params);

            byte[] readValue = serialPortManager.writeAndRead(bytes);

            Map<String, Object> resMap = sfjk200Mapper.readValue(readValue, startAddress, Map.class);
            list = (List<Map<String, Object>>) resMap.get(KeyWordEnum.DATA.getKey());
        } catch (Exception e) {
            log.error("回路{}读取点位的低限报警值失败", sfjk200Properties.getLoop());
        }
        return list;
    }

    // 读取回路探测器状态
    @Override
    public Map<String, Object> readDetectorStatus() {
        Map<String, Object> result = new HashMap<>();
        // 读取回路探测器状态，处理数据
        try {
            int startAddress = OrderEnum.PROBE_STATUS_1.getStartAddress() + (sfjk200Properties.getLoop() - 1) * (OrderEnum.PROBE_STATUS_1.getEndAdress() + 1 - OrderEnum.PROBE_STATUS_1.getStartAddress());
            // 组装为待发送的数据
            SFJK200Mapper sfjk200Mapper = new SFJK200Mapper();
            Map<String, Object> params = new HashMap<>(4);
            params.put(ADDRESS.getName(), sfjk200Properties.getAddress());
            params.put(FUNCTION.getName(), sfjk200Properties.getFunction());
            params.put(START_ADDRESS.getName(), startAddress);
            params.put(READ_ADDRESS.getName(), sfjk200Properties.getPoints());
            byte[] bytes = sfjk200Mapper.writeDataAsByteArray(params);

            byte[] readValue = serialPortManager.writeAndRead(bytes);

            Map<String, Object> resMap = sfjk200Mapper.readValue(readValue, startAddress, Map.class);
            List<Map<String, Object>> list = (List<Map<String, Object>>) resMap.get(KeyWordEnum.DATA.getKey());
            list.forEach(a -> {
                List<String> statusList = (List<String>) a.get(KeyWordEnum.CONTROLLER_STATUS.getKey());
                result.put(GasKeyWordEnum.PRE_CONTROLLER_STATUS.getName(), a.get(KeyWordEnum.CONTROLLER_STATUS_PRE.getKey()));
                result.put(GasKeyWordEnum.CONTROLLER_STATUS.getName(), StrUtil.join(StringPool.COMMA, statusList));
            });
        } catch (Exception e) {
            log.error("回路{}读取回路探测器状态失败", sfjk200Properties.getLoop());
        }
        return result;
    }

    // 读取回路探测器浓度
    @Override
    public List<Map<String, Object>> readDetectorConcentration() {
        List<Map<String, Object>> list = new ArrayList<>();
        // 读取回路探测器浓度，处理数据
        try {
            int startAddress = OrderEnum.DETECTOR_CONCENTRATION_1.getStartAddress() + (sfjk200Properties.getLoop() - 1) * (OrderEnum.DETECTOR_CONCENTRATION_1.getEndAdress() + 1 - OrderEnum.DETECTOR_CONCENTRATION_1.getStartAddress());
            // 组装为待发送的数据
            SFJK200Mapper sfjk200Mapper = new SFJK200Mapper();
            Map<String, Object> params = new HashMap<>(4);
            params.put(ADDRESS.getName(), sfjk200Properties.getAddress());
            params.put(FUNCTION.getName(), sfjk200Properties.getFunction());
            params.put(START_ADDRESS.getName(), startAddress);
            params.put(READ_ADDRESS.getName(), sfjk200Properties.getPoints());
            byte[] bytes = sfjk200Mapper.writeDataAsByteArray(params);

            byte[] readValue = serialPortManager.writeAndRead(bytes);

            Map<String, Object> resMap = sfjk200Mapper.readValue(readValue, startAddress, Map.class);
            list = (List<Map<String, Object>>) resMap.get(KeyWordEnum.DATA.getKey());
        } catch (Exception e) {
            log.error("回路{}读取读取回路探测器浓度数据发送失败", sfjk200Properties.getLoop());
        }
        return list;
    }

    // 读取回路外控电源状态
    @Override
    public Map<String, Object> readPowerStatus() {
        Map<String, Object> result = new HashMap<>();
        // 读取回路外控电源状态，处理数据
        try {
            int startAddress = OrderEnum.ECPS_STATUS_1.getStartAddress() + (sfjk200Properties.getLoop() - 1) * (OrderEnum.ECPS_STATUS_1.getEndAdress() + 1 - OrderEnum.ECPS_STATUS_1.getStartAddress());
            // 组装为待发送的数据
            SFJK200Mapper sfjk200Mapper = new SFJK200Mapper();
            Map<String, Object> params = new HashMap<>(4);
            params.put(ADDRESS.getName(), sfjk200Properties.getAddress());
            params.put(FUNCTION.getName(), sfjk200Properties.getFunction());
            params.put(START_ADDRESS.getName(), startAddress);
            params.put(READ_ADDRESS.getName(), 2);
            byte[] bytes = sfjk200Mapper.writeDataAsByteArray(params);

            byte[] readValue = serialPortManager.writeAndRead(bytes);

            Map<String, Object> resMap = sfjk200Mapper.readValue(readValue, startAddress, Map.class);
            List<Map<String, Object>> list = (List<Map<String, Object>>) resMap.get(KeyWordEnum.DATA.getKey());
            for (int i = 0; i < list.size(); i++) {
                int finalI = i + 1;
                list.get(i).forEach((k, v) -> {
                    result.put(k + "_" + finalI, v);
                });
            }
        } catch (Exception e) {
            log.error("回路{}读取回路外控电源状态失败", sfjk200Properties.getLoop());
        }
        return result;
    }

    // 读取控制器状态
    @Override
    public List<Map<String, Object>> readControllerStatus() {
        List<Map<String, Object>> list = new ArrayList<>();
        // 读取控制器状态，处理数据
        try {
            int startAddress = OrderEnum.CONTROL_STATUS.getStartAddress();
            // 组装为待发送的数据
            SFJK200Mapper sfjk200Mapper = new SFJK200Mapper();
            Map<String, Object> params = new HashMap<>(4);
            params.put(ADDRESS.getName(), sfjk200Properties.getAddress());
            params.put(FUNCTION.getName(), sfjk200Properties.getFunction());
            params.put(START_ADDRESS.getName(), startAddress);
            params.put(READ_ADDRESS.getName(), 1);
            byte[] bytes = sfjk200Mapper.writeDataAsByteArray(params);

            byte[] readValue = serialPortManager.writeAndRead(bytes);

            Map<String, Object> resMap = sfjk200Mapper.readValue(readValue, startAddress, Map.class);
            list = (List<Map<String, Object>>) resMap.get(KeyWordEnum.DATA.getKey());
        } catch (Exception e) {
            log.error("回路{}读取控制器状态失败", sfjk200Properties.getLoop());
        }
        return list;
    }
}
