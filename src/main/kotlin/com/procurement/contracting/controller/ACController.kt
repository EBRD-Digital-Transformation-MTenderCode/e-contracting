package com.procurement.contracting.controller

import com.procurement.contracting.model.dto.CreateContractRQ
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.service.ACService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.validation.Valid

@Validated
@RestController
@RequestMapping(path = ["/"])
class ACController(private val acService: ACService) {

    @PostMapping("createAC")
    fun createAC(@RequestParam(value = "identifier") cpid: String,
                 @RequestParam(value = "stage") stage: String,
                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                 @RequestParam("date") dateTime: LocalDateTime,
                 @Valid @RequestBody data: CreateContractRQ): ResponseEntity<ResponseDto> {

        return ResponseEntity(
                acService.createAC(cpId = cpid, stage = stage, dateTime = dateTime, dto = data),
                HttpStatus.OK)
    }

//    @PostMapping("updateAC")
//    @ResponseStatus(value = HttpStatus.ACCEPTED)
//    fun updateAC(@RequestParam(value = "identifier") cpid: String,
//                 @RequestParam(value = "token") token: String,
//                 @RequestParam(value = "idPlatform") idPlatform: String,
//                 @Valid @RequestBody updateContractRQ: UpdateACRQ): ResponseEntity<ResponseDto> {
//        val responseDto = acService.updateAC(cpid, token, idPlatform, updateContractRQ)
//        return ResponseEntity<T>(responseDto, HttpStatus.OK)
//    }
//
//    @PostMapping("changeStatusAc")
//    @ResponseStatus(value = HttpStatus.ACCEPTED)
//    fun changeStatus(@RequestParam(value = "identifier") cpid: String,
//                     @RequestParam(value = "token") token: String,
//                     @RequestParam(value = "idPlatform") idPlatform: String,
//                     @Valid @RequestBody changeStatusRQ: ChangeStatusRQ): ResponseEntity<ResponseDto> {
//
//        val responseDto = acService.changeStatus(cpid, token, idPlatform, changeStatusRQ)
//        return ResponseEntity<T>(responseDto, HttpStatus.OK)
//    }
//
//    @GetMapping("checkStatusAc")
//    @ResponseStatus(value = HttpStatus.ACCEPTED)
//    fun checkStatus(@RequestParam(value = "identifier") cpId: String,
//                    @RequestParam(value = "token") token: String
//    ): ResponseEntity<ResponseDto> {
//        val responseDto = acService.checkStatus(cpId, token)
//        return ResponseEntity<T>(responseDto, HttpStatus.OK)
//    }
}
