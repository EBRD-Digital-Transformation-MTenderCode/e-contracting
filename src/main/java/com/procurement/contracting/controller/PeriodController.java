package com.procurement.contracting.controller;

import com.procurement.contracting.model.dto.bpe.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/enquiryperiod")
public class PeriodController {



    @PostMapping("/{cpid}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<ResponseDto> calculateAndSavePeriod() {


        return new ResponseEntity(true,null,null);
    }
}
