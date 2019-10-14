package me.stasiak;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class PresentableParameter {
    private final String name;
    private final String type;
    private final String genericType;
}
