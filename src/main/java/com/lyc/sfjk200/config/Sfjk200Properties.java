package com.lyc.sfjk200.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 类 Sfjk200Properties
 *
 * @author ChenQi
 * @date 2023/4/3
 */
@Data
@Component
@ConfigurationProperties(prefix = "sfjk200")
public class Sfjk200Properties {

    @Schema(name = "从站地址")
    private Integer address;

    @Schema(name = "功能码")
    private Integer function;

    @Schema(name = "回路数")
    private Integer loop;

    @Schema(name = "点位数")
    private Integer points;
}

