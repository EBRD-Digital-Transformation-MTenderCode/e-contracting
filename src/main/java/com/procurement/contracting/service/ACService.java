package com.procurement.contracting.service;

import com.procurement.contracting.model.dto.awardedContract.CreateACRQ;
import com.procurement.contracting.model.dto.bpe.ResponseDto;
import com.procurement.contracting.model.dto.changeStatus.ChangeStatusRQ;
import com.procurement.contracting.model.dto.updateAC.UpdateACRQ;
import org.springframework.stereotype.Service;

public interface ACService {

    ResponseDto createAC(String cpId, String token, CreateACRQ createACRQ);

    ResponseDto updateAC(String cpId, String token, String platformId, UpdateACRQ updateACRQ);

    ResponseDto changeStatus(String cpId, String token, String platformId, ChangeStatusRQ changeStatusRQ);

    ResponseDto checkStatus(String cpId, String token);
}
