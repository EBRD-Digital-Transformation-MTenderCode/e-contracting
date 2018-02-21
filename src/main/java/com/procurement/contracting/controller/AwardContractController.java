package com.procurement.contracting.controller;

import com.procurement.contracting.model.dto.bpe.ResponseDto;
import com.procurement.contracting.model.dto.createAC.CreateACRQ;
import com.procurement.contracting.model.dto.updateAC.UpdateACRQ;
import com.procurement.contracting.service.ACServise;
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
public class AwardContractController {

    private final ACServise acServise;

    public AwardContractController(final ACServise acServise) {
        this.acServise = acServise;
    }

    @PostMapping("createAC")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<ResponseDto> createAC(@Valid @RequestBody final CreateACRQ createACRQ,
                                                 @RequestParam(value = "token") final String token,
                                                 @RequestParam(value = "cpid") final String cpid) {

        ResponseDto responseDto = acServise.createAC(cpid, token,createACRQ);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("updateAC")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public ResponseEntity<ResponseDto> updateAC(@Valid @RequestBody final UpdateACRQ updateContractRQ,
                                                @RequestParam(value = "token") final String token,
                                                @RequestParam(value = "cpid") final String cpid){
         ResponseDto responseDto = acServise.updateAC(cpid, token,updateContractRQ);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);

    }


}
