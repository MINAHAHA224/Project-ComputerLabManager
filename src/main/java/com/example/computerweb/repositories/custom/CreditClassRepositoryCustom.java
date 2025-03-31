package com.example.computerweb.repositories.custom;

import com.example.computerweb.DTO.dto.CreditClassEligibleDto;

import java.util.List;



public interface CreditClassRepositoryCustom {

    // dieu kien la SoTTH > 0 ,  ( SoTTH da duoc tao , SoTTH chua duoc tao ) duoc tim thong qua phan lich
    List<CreditClassEligibleDto> findAllCreditClassEligible ();
}
