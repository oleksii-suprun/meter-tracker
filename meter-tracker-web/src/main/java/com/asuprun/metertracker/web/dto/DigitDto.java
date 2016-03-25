package com.asuprun.metertracker.web.dto;

import com.asuprun.metertracker.web.domain.Digit;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class DigitDto {

    private long id;

    private byte[] image;

    @NotEmpty(message = "Digit value must not be empty")
    private String value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static DigitDto toDto(Digit digit) {
        if (digit == null) {
            return null;
        }
        DigitDto dto = new DigitDto();
        dto.setId(digit.getId());
        dto.setImage(digit.getImage());
        dto.setValue(digit.getValue());
        return dto;
    }

    public static Digit fromDto(DigitDto dto) {
        if (dto == null) {
            return null;
        }
        Digit digit = new Digit();
        digit.setId(dto.getId());
        digit.setImage(dto.getImage());
        digit.setValue(dto.getValue());
        return digit;
    }

    public static List<DigitDto> toDtos(List<Digit> digits) {
        return digits.stream().map(DigitDto::toDto).collect(Collectors.toList());
    }

    public static List<Digit> fromDtos(List<DigitDto> dtos) {
        return dtos.stream().map(DigitDto::fromDto).collect(Collectors.toList());
    }
}
