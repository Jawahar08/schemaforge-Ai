package com.schemaforge.export.strategy;

import com.schemaforge.export.entity.ExportDialect;
import com.schemaforge.export.exception.ExportGenerationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExportStrategyFactory {

    private final List<ExportDialectStrategy> strategies;

    private Map<ExportDialect, ExportDialectStrategy> strategyMap;

    private Map<ExportDialect, ExportDialectStrategy> getStrategyMap() {
        if (strategyMap == null) {
            strategyMap = strategies.stream()
                    .collect(Collectors.toMap(
                            ExportDialectStrategy::getDialect,
                            Function.identity()
                    ));
        }
        return strategyMap;
    }

    public ExportDialectStrategy getStrategy(ExportDialect dialect) {
        if (dialect == null) {
            throw new ExportGenerationException("Export dialect must not be null");
        }
        ExportDialectStrategy strategy = getStrategyMap().get(dialect);
        if (strategy == null) {
            throw new ExportGenerationException("No SQL generation strategy available for dialect: " + dialect);
        }
        return strategy;
    }
}