package com.procurement.contracting.service;

import com.procurement.contracting.model.dto.CreateCanRQ;
import com.procurement.contracting.model.dto.bpe.ResponseDto;

public interface CANService {

    ResponseDto createCAN(String cpId, String stage, String owner, CreateCanRQ data);

//    ResponseDto checkCAN(String cpId, String token, String idPlatform);
//
//    ResponseDto changeStatus(String cpId, String awardId);
}
