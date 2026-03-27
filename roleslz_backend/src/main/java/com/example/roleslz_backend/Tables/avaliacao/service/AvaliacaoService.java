package com.example.roleslz_backend.Tables.avaliacao.service;

import com.example.roleslz_backend.Tables.avaliacao.exceptions.AvaliacaoAlreadyExists;
import com.example.roleslz_backend.Tables.avaliacao.exceptions.AvaliacaoDoesntExists;
import com.example.roleslz_backend.Tables.avaliacao.exceptions.AvaliacaoNotDeleted;
import com.example.roleslz_backend.Tables.avaliacao.exceptions.AvaliacaoNotEdited;
import com.example.roleslz_backend.Tables.avaliacao.DTO.AvaliacaoDTO;
import com.example.roleslz_backend.Tables.avaliacao.entity.AvaliacaoEntity;
import com.example.roleslz_backend.Tables.avaliacao.mapper.AvaliacaoMapperClass;
import com.example.roleslz_backend.Tables.avaliacao.repository.AvaliacaoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AvaliacaoService {

    private final AvaliacaoMapperClass avaliacaoMapperClass;
    private final AvaliacaoRepository avaliacaoRepository;

    public AvaliacaoService(AvaliacaoMapperClass avaliacaoMapperClass, AvaliacaoRepository avaliacaoRepository) {
        this.avaliacaoMapperClass = avaliacaoMapperClass;
        this.avaliacaoRepository = avaliacaoRepository;
    }

    //Service

    public AvaliacaoDTO addAvaliacao(AvaliacaoDTO avaliacaoDTO, long userId){
        Optional<AvaliacaoEntity> exists = avaliacaoRepository.findByUserId(userId);

        if(exists.isPresent()){
            throw new AvaliacaoAlreadyExists("Avaliação já existe!");
        }

        AvaliacaoEntity novaAvaliacao = avaliacaoMapperClass.toEntity(avaliacaoDTO);

        try{
            avaliacaoRepository.save(novaAvaliacao);
            return avaliacaoMapperClass.toDto(novaAvaliacao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAvaliacao(long avaliacaoId){
        AvaliacaoEntity avaliacao = avaliacaoRepository.findById(avaliacaoId).orElseThrow(()-> new AvaliacaoDoesntExists("Avaliação não encontrada para deleção"));

        try{
            avaliacaoRepository.delete(avaliacao);
        } catch (Exception e) {
            throw new AvaliacaoNotDeleted("Avaliação não deletada");
        }
    }

    public AvaliacaoDTO editAvaliacao(long id, float rate) {
        AvaliacaoEntity avaliacao = avaliacaoRepository.findById(id).orElseThrow(() -> new AvaliacaoDoesntExists("Avaliação não encontrada"));

        avaliacao.setNota(rate);

        try {
            avaliacaoRepository.save(avaliacao);
            return avaliacaoMapperClass.toDto(avaliacao);
        } catch (Exception e) {
            throw new AvaliacaoNotEdited("Avaliação não editada");
        }
    }
}