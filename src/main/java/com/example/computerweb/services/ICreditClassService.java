package com.example.computerweb.services;

import com.example.computerweb.DTO.dto.subjectResponse.SubjectRpDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.requestBody.creditClassRequest.CreditClassRqCreateDto;
import com.example.computerweb.DTO.requestBody.creditClassRequest.CreditClassRqUpdateDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICreditClassService {

    ResponseData<?> handleGetDataForCreditIndexPage ();

    ResponseData<?> handleGetSubjectForCreditClass ();

    ResponseData<?> handleGetClassForCreditClass ();

    ResponseData<?> handleGetTeacherForCreditClass (String codeSubject);

    ResponseData<?> handleCreateCreditClass (CreditClassRqCreateDto creditClassRqCreateDto);

    ResponseData<?> handleGetDataCreditClassDetails (Long creditClassId);

    ResponseData<?> handleUpdateCreditClassDetail (CreditClassRqUpdateDto creditClassRqUpdateDto);

    ResponseData<?> handleDeleteCreditClass (Long creditClassId);
}
