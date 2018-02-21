package com.procurement.contracting.service;

import com.procurement.contracting.model.dto.bpe.ResponseDto;
import com.procurement.contracting.model.dto.createAC.CreateACRQ;
import com.procurement.contracting.model.dto.createCAN.CreateCanRQ;
import com.procurement.contracting.model.dto.updateAC.UpdateACRQ;
import org.springframework.stereotype.Service;

@Service
public interface ACServise {
    ResponseDto createAC(String cpId, String token, CreateACRQ createACRQ);
    ResponseDto updateAC(String cpId, String token, UpdateACRQ updateACRQ);
}
