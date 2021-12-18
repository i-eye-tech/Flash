package com.ieye.model.dto;

import com.ieye.model.core.ApiSpecification;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GetApiSpecResponseDTO {

    private List<ApiSpecification> apiSpec;

}
