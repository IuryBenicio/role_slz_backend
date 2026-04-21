package com.example.roleslz_backend.Tables.business.services;

import com.example.roleslz_backend.Tables.business.DTO.BusinessDTO;
import com.example.roleslz_backend.Tables.business.DTO.CnpjAPI.CNPJDtoResponse;
import com.example.roleslz_backend.Tables.business.entity.BusinessEntity;
import com.example.roleslz_backend.Tables.business.exceptions.BusinessAlreadyCreated;
import com.example.roleslz_backend.Tables.business.exceptions.BusinessNotCreated;
import com.example.roleslz_backend.Tables.business.exceptions.BusinessNotFounded;
import com.example.roleslz_backend.Tables.business.mappers.BusinessMapperClass;
import com.example.roleslz_backend.Tables.business.repository.BusinessRepository;
import org.springframework.stereotype.Service;

@Service
public class BusinessServices {

    private final BusinessRepository businessRepository;
    private final BusinessMapperClass businessMapper;
    private final CNPJVerificationService cnpjVerificationService;

    public BusinessServices(BusinessRepository businessRepository, BusinessMapperClass businessMapper, CNPJVerificationService cnpjVerificationService) {
        this.businessRepository = businessRepository;
        this.businessMapper = businessMapper;
        this.cnpjVerificationService = cnpjVerificationService;
    }

    public BusinessDTO createBusiness(BusinessDTO businessDTO){
        businessRepository.findByNomeFantasia(businessDTO.nomeFantasia())
                .ifPresent(b -> { throw new BusinessAlreadyCreated("Empresa já cadastrada com este Nome Fantasia"); });

        businessRepository.findByCnpj(businessDTO.cnpj())
                .ifPresent(b -> { throw new BusinessAlreadyCreated("Empresa já cadastrada com este CNPJ"); });

        CNPJDtoResponse cnpjDtoResponse = cnpjVerificationService.buscarCnpj(businessDTO.cnpj());

        try{
            BusinessEntity newBusiness = new BusinessEntity();
            newBusiness.setCnpj(cnpjDtoResponse.getCnpj());
            newBusiness.setNomeFantasia(cnpjDtoResponse.getNomeFantasia());
            newBusiness.setLogoTipoUrl(businessDTO.logoTipoUrl());
            newBusiness.setFuncionamento(businessDTO.funcionamentoClass());

            return businessMapper.toDto(businessRepository.save(newBusiness));
        } catch (Exception e) {
            throw new BusinessNotCreated("Empresa não cadastrada");
        }
    }

    public void deleteBusiness(long id){
        BusinessEntity business = businessRepository.findById(id).orElseThrow(()-> new BusinessNotFounded("Empresa não encontrada"));
        try{
            businessRepository.delete(business);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void editBusiness(long id, BusinessDTO businessDTO){
        BusinessEntity business = businessRepository.findById(id).orElseThrow(()-> new BusinessNotFounded("Empresa não encontrada"));

        businessMapper.updateEntityFromDto(businessDTO, business);

        try{
            businessRepository.save(business);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
