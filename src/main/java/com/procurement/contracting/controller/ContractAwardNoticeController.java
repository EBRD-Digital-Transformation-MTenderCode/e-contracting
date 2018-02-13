package com.procurement.contracting.controller;

import com.procurement.contracting.model.dto.bpe.ResponseDto;
import com.procurement.contracting.model.dto.createCAN.CreateCanRQ;
import com.procurement.contracting.service.CANServise;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/")
public class ContractAwardNoticeController {

    private final CANServise canServise;

    public ContractAwardNoticeController(final CANServise canServise) {
        this.canServise = canServise;
    }

    @PostMapping("createCAN")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<ResponseDto> createCAN(@Valid @RequestBody final CreateCanRQ contractRQDto,
                                                 @RequestParam(value = "owner")final String owner,
                                                 @RequestParam(value = "cpid")final String cpid) {



        ResponseDto responseDto = canServise.createCAN(cpid,owner,contractRQDto);
        return new ResponseEntity<>(responseDto,HttpStatus.OK);
    }
}
