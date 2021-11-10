package com.ieye.model.mapper;

import com.ieye.model.ApiSpecification;
import com.ieye.model.dto.GetApiSpecResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetSpecificationMapper {

    public GetApiSpecResponseDTO mapApiSpecsToResponse(List<ApiSpecification> apiSpecificationList) {
        GetApiSpecResponseDTO getApiSpecResponseDTO = new GetApiSpecResponseDTO();
        getApiSpecResponseDTO.setApiSpec(apiSpecificationList);
        return getApiSpecResponseDTO;
    }

}
