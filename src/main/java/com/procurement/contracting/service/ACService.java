package com.procurement.contracting.service;

import com.procurement.contracting.model.dto.CreateContractRQ;
import com.procurement.contracting.model.dto.bpe.ResponseDto;

public interface ACService {

    ResponseDto createAC(String cpId, String stage, CreateContractRQ data);

//    ResponseDto updateAC(String cpId, String token, String platformId, UpdateACRQ updateACRQ);
//
//    ResponseDto changeStatus(String cpId, String token, String platformId, ChangeStatusRQ changeStatusRQ);
//
//    ResponseDto checkStatus(String cpId, String token);
}
