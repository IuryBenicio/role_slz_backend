package com.example.roleslz_backend.Tables.business.services;

import com.example.roleslz_backend.Tables.business.DTO.BusinessDTO;
import com.example.roleslz_backend.Tables.business.DTO.CnpjAPI.CNPJDtoResponse;
import com.example.roleslz_backend.Tables.business.entity.BusinessEntity;
import com.example.roleslz_backend.Tables.business.exceptions.*;
import com.example.roleslz_backend.Tables.business.mappers.BusinessMapperClass;
import com.example.roleslz_backend.Tables.business.repository.BusinessRepository;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.exceptions.UserNotFounded;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import com.example.roleslz_backend.Tables.users.services.UserUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class BusinessServices {

    private final BusinessRepository businessRepository;
    private final BusinessMapperClass businessMapper;
    private final CNPJVerificationService cnpjVerificationService;
    private final UserRepository userRepository;
    private final UserUtils userUtils;

    public BusinessServices(BusinessRepository businessRepository, BusinessMapperClass businessMapper, CNPJVerificationService cnpjVerificationService, UserRepository userRepository, UserUtils userUtils) {
        this.businessRepository = businessRepository;
        this.businessMapper = businessMapper;
        this.cnpjVerificationService = cnpjVerificationService;
        this.userRepository = userRepository;
        this.userUtils = userUtils;
    }

    private UserEntity usuarioLogado(){
        return userRepository.findById(userUtils.getUserLogado().getId())
                .orElseThrow(() -> new UserNotFounded("Usuário não encontrado"));
    }

    // Resolve a empresa E confirma que ela pertence ao usuário do token. Mesma mensagem
    // de "não encontrada" no caso de empresa de outra pessoa, para não confirmar que ela existe.
    private BusinessEntity minhaEmpresa(long id){
        UserEntity logado = usuarioLogado();
        BusinessEntity minha = logado.getBusiness();

        if(minha == null || minha.getId() != id){
            throw new BusinessNotFounded("Empresa não encontrada");
        }

        return businessRepository.findById(id)
                .orElseThrow(() -> new BusinessNotFounded("Empresa não encontrada"));
    }

    @Transactional
    public BusinessDTO createBusiness(BusinessDTO businessDTO){
        UserEntity owner = usuarioLogado();

        if(owner.getBusiness() != null){
            throw new BusinessAlreadyCreated("Este usuário já possui uma empresa cadastrada");
        }

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

            BusinessEntity salvo = businessRepository.save(newBusiness);

            // Sem este vínculo a empresa nasce órfã — era por isso que editar/deletar
            // não tinha como checar dono.
            owner.setBusiness(salvo);
            userRepository.save(owner);

            return businessMapper.toDto(salvo);
        } catch (Exception e) {
            throw new BusinessNotCreated("Empresa não cadastrada");
        }
    }

    @Transactional
    public void deleteBusiness(long id){
        BusinessEntity business = minhaEmpresa(id);

        UserEntity owner = usuarioLogado();
        owner.setBusiness(null);
        userRepository.save(owner);

        try{
            businessRepository.delete(business);
        } catch (Exception e) {
            throw new BusinessNotDeleted("Business não deletado!");
        }
    }

    @Transactional
    public void editBusiness(long id, BusinessDTO businessDTO){
        BusinessEntity business = minhaEmpresa(id);

        businessMapper.updateEntityFromDto(businessDTO, business);

        try{
            businessRepository.save(business);
        } catch (Exception e) {
            throw new BusinessNotEdited("Business não editado!");
        }
    }
}
