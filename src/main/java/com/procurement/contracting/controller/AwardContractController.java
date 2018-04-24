package com.procurement.contracting.controller;

import com.procurement.contracting.model.dto.awardedContract.CreateACRQ;
import com.procurement.contracting.model.dto.bpe.ResponseDto;
import com.procurement.contracting.model.dto.changeStatus.ChangeStatusRQ;
import com.procurement.contracting.model.dto.updateAC.UpdateACRQ;
import com.procurement.contracting.service.ACService;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/")
public class AwardContractController {

    private final ACService acService;

    public AwardContractController(final ACService acService) {
        this.acService = acService;
    }

    @PostMapping("createAC")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<ResponseDto> createAC(@RequestParam(value = "identifier") final String cpid,
                                                @RequestParam(value = "token") final String token,
                                                @Valid @RequestBody final CreateACRQ createACRQ) {

        final ResponseDto responseDto = acService.createAC(cpid, token, createACRQ);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("updateAC")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public ResponseEntity<ResponseDto> updateAC(@RequestParam(value = "identifier") final String cpid,
                                                @RequestParam(value = "token") final String token,
                                                @RequestParam(value = "idPlatform") final String idPlatform,
                                                @Valid @RequestBody final UpdateACRQ updateContractRQ) {
        final ResponseDto responseDto = acService.updateAC(cpid, token, idPlatform, updateContractRQ);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("changeStatusAc")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public ResponseEntity<ResponseDto> changeStatus(@RequestParam(value = "identifier") final String cpid,
                                                    @RequestParam(value = "token") final String token,
                                                    @RequestParam(value = "idPlatform") final String idPlatform,
                                                    @Valid @RequestBody final ChangeStatusRQ changeStatusRQ) {

        final ResponseDto responseDto = acService.changeStatus(cpid, token, idPlatform, changeStatusRQ);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("checkStatusAc")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public ResponseEntity<ResponseDto> checkStatus(@RequestParam(value = "identifier") final String cpId,
                                                   @RequestParam(value = "token") final String token
    ) {
        final ResponseDto responseDto = acService.checkStatus(cpId, token);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
