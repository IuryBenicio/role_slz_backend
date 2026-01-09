package com.example.roleslz_backend.Tables.business.controllers;


import com.example.roleslz_backend.Tables.business.DTO.BusinessDTO;
import com.example.roleslz_backend.Tables.business.services.BusinessServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("owner")
public class BusinessController {

    private final BusinessServices businessServices;

    public BusinessController(BusinessServices businessServices) {
        this.businessServices = businessServices;
    }

    @PostMapping("register_business")
    public ResponseEntity<?> registerBusiness(@RequestBody BusinessDTO businessDTO){
        BusinessDTO business = businessServices.createBusiness(businessDTO);
        return ResponseEntity.ok(business);
    }

    @DeleteMapping("delete_business/{id}")
    public ResponseEntity<String> deleteBusiness(@PathVariable long id){
        businessServices.deleteBusiness(id);
        return ResponseEntity.ok("Empresa deletada");
    }

    @PatchMapping("edit_business/{id}")
    public ResponseEntity<String> deleteBusiness(@PathVariable long id, @RequestBody BusinessDTO businessDTO){
        businessServices.editBusiness(id, businessDTO);
        return ResponseEntity.ok("Empresa deletada");
    }
}
