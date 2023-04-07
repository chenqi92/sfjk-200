package com.lyc.sfjk200.enums;

import lombok.Getter;

/**
 * 枚举 GasKeyWordEnum
 *
 * @author ChenQi
 * @date 2023/4/3
 */
@Getter
public enum GasKeyWordEnum {

    TIME("time"),

    ADDRESS("address"),

    FUNCTION("function"),

    LOOP("loop"),

    CONTROLLER_STATUS("controllerStatus"),

    PRE_CONTROLLER_STATUS("preControllerStatus"),

    ATTRIBUTE_UNITS("attributeUnits"),

    LOW_ALARM_VALUE("lowAlarmValue"),

    PROBE_STATUS("probeStatus"),

    DETECTOR_CONCENTRATION("detectorConcentration"),

    ECPS_STATUS("ecpsStatus"),

    DATA("data"),
    ;

    private final String name;

    GasKeyWordEnum(String name) {
        this.name = name;
    }
}
