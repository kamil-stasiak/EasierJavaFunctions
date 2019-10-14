package me.stasiak;

import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class PresentableFunctionDto {
    private final String name;
    private final List<PresentableParameter> parameters;
    private final String returnType;
}

