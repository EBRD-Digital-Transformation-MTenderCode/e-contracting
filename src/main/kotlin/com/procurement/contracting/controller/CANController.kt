package com.procurement.contracting.controller

import com.procurement.contracting.model.dto.CreateCanRQ
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.service.CanService
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
class CANController(private val canService: CanService) {

    @PostMapping("createCAN")
    fun createCAN(@RequestParam(value = "identifier") cpid: String,
                  @RequestParam(value = "stage") stage: String,
                  @RequestParam(value = "owner") owner: String,
                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                  @RequestParam("date") dateTime: LocalDateTime,
                  @Valid @RequestBody data: CreateCanRQ): ResponseEntity<ResponseDto> {

        return ResponseEntity(
                canService.createCAN(cpId = cpid, stage = stage, owner = owner, dateTime = dateTime, dto = data),
                HttpStatus.OK)
    }

//    @GetMapping("checkCAN")
//    @ResponseStatus(value = HttpStatus.ACCEPTED)
//    fun checkCAN(@RequestParam(value = "identifier") cpId: String,
//                 @RequestParam(value = "token") token: String,
//                 @RequestParam(value = "idPlatform") idPlatform: String): ResponseEntity<ResponseDto> {
//
//        val responseDto = canService.checkCAN(cpId, token, idPlatform)
//        return ResponseEntity<T>(responseDto, HttpStatus.OK)
//    }
//
//    @PostMapping("changeStatusCAN")
//    @ResponseStatus(value = HttpStatus.ACCEPTED)
//    fun changeStatus(@RequestParam(value = "identifier") cpId: String,
//                     @RequestParam(value = "awardId") awardId: String): ResponseEntity<ResponseDto> {
//
//        val responseDto = canService.changeStatus(cpId, awardId)
//        return ResponseEntity<T>(responseDto, HttpStatus.OK)
//    }
}
