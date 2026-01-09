package com.example.roleslz_backend.Tables.business.services;

import com.example.roleslz_backend.Tables.business.DTO.BusinessDTO;
import com.example.roleslz_backend.Tables.business.entity.BusinessEntity;
import com.example.roleslz_backend.Tables.business.exceptions.BusinessAlreadyCreated;
import com.example.roleslz_backend.Tables.business.exceptions.BusinessNotCreated;
import com.example.roleslz_backend.Tables.business.exceptions.BusinessNotFounded;
import com.example.roleslz_backend.Tables.business.mappers.BusinessMapper;
import com.example.roleslz_backend.Tables.business.repository.BusinessRepository;
import org.springframework.stereotype.Service;

@Service
public class BusinessServices {

    private final BusinessRepository businessRepository;
    private final BusinessMapper businessMapper;

    public BusinessServices(BusinessRepository businessRepository, BusinessMapper businessMapper) {
        this.businessRepository = businessRepository;
        this.businessMapper = businessMapper;
    }

    public BusinessDTO createBusiness(BusinessDTO businessDTO){
        businessRepository.findByNomeFantasia(businessDTO.nomeFantasia())
                .ifPresent(b -> { throw new BusinessAlreadyCreated("Empresa já cadastrada com este Nome Fantasia"); });

        businessRepository.findByCnpj(businessDTO.cnpj())
                .ifPresent(b -> { throw new BusinessAlreadyCreated("Empresa já cadastrada com este CNPJ"); });

        try{
            BusinessEntity newBusiness = new BusinessEntity();
            newBusiness.setCnpj(businessDTO.cnpj());
            newBusiness.setNomeFantasia(businessDTO.nomeFantasia());
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
