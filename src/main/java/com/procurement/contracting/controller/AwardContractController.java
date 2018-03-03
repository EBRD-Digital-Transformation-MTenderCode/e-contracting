package com.procurement.contracting.controller;

import com.procurement.contracting.model.dto.awardedContract.CreateACRQ;
import com.procurement.contracting.model.dto.bpe.ResponseDto;
import com.procurement.contracting.model.dto.changeStatus.ChangeStatusRQ;
import com.procurement.contracting.model.dto.updateAC.UpdateACRQ;
import com.procurement.contracting.service.ACServise;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

        final ResponseDto responseDto = acServise.createAC(cpid, token, createACRQ);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("updateAC")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public ResponseEntity<ResponseDto> updateAC(@Valid @RequestBody final UpdateACRQ updateContractRQ,
                                                @RequestParam(value = "token") final String token,
                                                @RequestParam(value = "cpid") final String cpid,
                                                @RequestParam(value = "idPlatform") final String idPlatform) {
        final ResponseDto responseDto = acServise.updateAC(cpid, token, idPlatform, updateContractRQ);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("changeStatusAc")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public ResponseEntity<ResponseDto> changeStatus(@Valid @RequestBody final ChangeStatusRQ changeStatusRQ,
                                                    @RequestParam(value = "token") final String token,
                                                    @RequestParam(value = "cpid") final String cpid,
                                                    @RequestParam(value = "idPlatform") final String idPlatform) {

        final ResponseDto responseDto = acServise.changeStatus(cpid, token, idPlatform, changeStatusRQ);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("checkStatusAc")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public ResponseEntity<ResponseDto> checkStatus(@Valid @RequestParam(value = "token") final String token,
                                                   @RequestParam(value = "cpid") final String cpId) {
        final ResponseDto responseDto = acServise.checkStatus(cpId, token);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
