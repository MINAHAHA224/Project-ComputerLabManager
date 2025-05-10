package com.example.computerweb.controllers;

import com.example.computerweb.DTO.dto.subjectResponse.SubjectRpDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseFailure;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.creditClassRequest.CreditClassRqCreateDto;
import com.example.computerweb.DTO.requestBody.creditClassRequest.CreditClassRqUpdateDto;
import com.example.computerweb.services.ICreditClassService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CreditClassController {

    private final ICreditClassService iCreditClassService;


    @Operation(summary = "GVU can see list creditClass" , description = "The purpose is GVU can se what creditClass is practice , isn't practice")
    @GetMapping("/creditClassManagement" )
    public ResponseData<?> getCreditClassIndexPage  (){
        return this.iCreditClassService.handleGetDataForCreditIndexPage();
    }


    @GetMapping(value = "/creditClassManagement/subject")
    public ResponseData<?> getSubjectForCreditClass (){
       return  this.iCreditClassService.handleGetSubjectForCreditClass();
    }

    @GetMapping(value = "/creditClassManagement/class")
    public ResponseData<?> getClassForCreditClass (){
        return  this.iCreditClassService.handleGetClassForCreditClass();
    }


    @GetMapping(value = "/creditClassManagement/teacher")
    public ResponseData<?> getTeacherFollowSubject (@RequestParam( name = "codeSubject" , required = true) String codeSubject){

        return this.iCreditClassService.handleGetTeacherForCreditClass(codeSubject);
    }

    @Operation(summary = "GVU can create CreditClass after field full data" , description = "GVU can create CreditClass after field full data")
    @PostMapping("/creditClassManagement/create")
    public ResponseData<?> postCreateCreditClass (@Valid @RequestBody CreditClassRqCreateDto creditClassRqCreateDto){
        return this.iCreditClassService.handleCreateCreditClass(creditClassRqCreateDto);
    }

    @Operation(summary = "GVU can see detail  CreditClass to update" , description = "GVU can see detail  CreditClass to update")
    @GetMapping("/creditClassManagement/update/{creditClassId}")
    public ResponseData<?> getUpdateCreditClassPage (@PathVariable("creditClassId") Long creditClassId){
        return this.iCreditClassService.handleGetDataCreditClassDetails( creditClassId);
    }


    @Operation(summary = "GVU can update some field " , description = "GVU can update numberOfStudents, group , teacher  When change group combination will be change")
    @PostMapping("/creditClassManagement/update")
    public ResponseData<?> postUpdateCreditClassDetail (@Valid @RequestBody CreditClassRqUpdateDto creditClassRqUpdateDto){
        return this.iCreditClassService.handleUpdateCreditClassDetail(creditClassRqUpdateDto);
    }
    @Operation(summary = "GVU choose creditClassId to delete" , description = "GVU the creditClass don't have calendar can delete  ortherwise no delete")
    @PostMapping("/creditClassManagement/delete/{creditClassId}")
    public ResponseData<?> postDeleteCreditClass (@PathVariable("creditClassId") Long creditClassId){
        return  this.iCreditClassService.handleDeleteCreditClass(creditClassId);
    }
}
