package com.procurement.contracting.controller;

import com.procurement.contracting.model.dto.CreateCanRQ;
import com.procurement.contracting.model.dto.bpe.ResponseDto;
import com.procurement.contracting.service.CANService;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/")
public class CANController {

    private final CANService canService;

    public CANController(final CANService canService) {
        this.canService = canService;
    }

    @PostMapping("createCAN")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<ResponseDto> createCAN(@RequestParam(value = "identifier") final String cpid,
                                                 @RequestParam(value = "owner") final String owner,
                                                 @Valid @RequestBody final CreateCanRQ data) {

        return new ResponseEntity<>(canService.createCAN(cpid, owner, data), HttpStatus.OK);
    }

//    @GetMapping("checkCAN")
//    @ResponseStatus(value = HttpStatus.ACCEPTED)
//    public ResponseEntity<ResponseDto> checkCAN(@RequestParam(value = "identifier") final String cpId,
//                                                @RequestParam(value = "token") final String token,
//                                                @RequestParam(value = "idPlatform") final String idPlatform) {
//
//        final ResponseDto responseDto = canService.checkCAN(cpId, token, idPlatform);
//        return new ResponseEntity<>(responseDto, HttpStatus.OK);
//    }
//
//    @PostMapping("changeStatusCAN")
//    @ResponseStatus(value = HttpStatus.ACCEPTED)
//    public ResponseEntity<ResponseDto> changeStatus(@RequestParam(value = "identifier") final String cpId,
//                                                    @RequestParam(value = "awardId") final String awardId) {
//
//        final ResponseDto responseDto = canService.changeStatus(cpId, awardId);
//        return new ResponseEntity<>(responseDto, HttpStatus.OK);
//    }
}
