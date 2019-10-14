package me.stasiak;

import lombok.Getter;

import java.util.function.Function;

@Getter
enum FunctionStyle {
    JAVA((param) -> {
        String params = param.getParameters()
        .map(p -> p.getType() + " " + p.getName())
        .intersperse(", ")
        .fold("", String::concat);
        return param.getReturnType() + " " + param.getName() + "(" + params + ")";
    }),

    SCALA((param) -> {
        String params = param.getParameters()
        .map(p -> p.getName() + ": " + p.getType())
        .intersperse(", ")
        .fold("", String::concat);
        return param.getName() + ": " +  "(" + params + ") => " + param.getReturnType();
    }),

    KOTLIN((param) -> {
        String params = param.getParameters()
                .map(PresentableParameter::getType)
                .intersperse(", ")
                .fold("", String::concat);
        return param.getName() + ": " +  "(" + params + ") -> " + param.getReturnType();
    });

    private final Function<PresentableFunctionDto, String> function;

    FunctionStyle(Function<PresentableFunctionDto, String> function) {
        this.function = function;
    }
}
