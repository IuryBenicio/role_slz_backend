package com.example.roleslz_backend.Tables.categoria.services;

import com.example.roleslz_backend.Tables.categoria.DTO.CategoriaDTORequestClass;
import com.example.roleslz_backend.Tables.categoria.DTO.CategoriaDTOResponse;
import com.example.roleslz_backend.Tables.categoria.entity.CategoriaEntity;
import com.example.roleslz_backend.Tables.categoria.exceptions.CategoriaExists;
import com.example.roleslz_backend.Tables.categoria.exceptions.CategoriaNotDeleted;
import com.example.roleslz_backend.Tables.categoria.exceptions.CategoryNotFounded;
import com.example.roleslz_backend.Tables.categoria.mapper.CategoriaMapper;
import com.example.roleslz_backend.Tables.categoria.repository.CategoriaRepository;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServices {

    private final CategoriaRepository categoriaRepository;
    private final EventoRepository eventoRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaServices(CategoriaRepository categoriaRepository, EventoRepository eventoRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.eventoRepository = eventoRepository;
        this.categoriaMapper = categoriaMapper;
    }

    public CategoriaDTOResponse addCategoria(CategoriaDTORequestClass dto){
        Optional<CategoriaEntity> exists = categoriaRepository.findByName(dto.getName());

        if(exists.isPresent()){
            throw new CategoriaExists("Categoria já existe");
        }

        CategoriaEntity entity = new CategoriaEntity();
        entity.setName(dto.getName());
        if(dto.getEventos().size() > 0 || dto.getEventos() != null){
            List<Long> ids = dto.getEventos().stream().map(eventoDTOWithID -> eventoDTOWithID.id()).toList();

            List<EventoEntity> eventos = eventoRepository.findAllById(ids);

            entity.setEventos(new HashSet<EventoEntity>(eventos));
        }
        return categoriaMapper.toDTO(entity);
    }

    public void deleteCategoria(long id){
        categoriaRepository.findById(id).orElseThrow(()-> new CategoryNotFounded("Categoria não encontrada"));

        try{
            categoriaRepository.deleteById(id);
        } catch (Exception e) {
            throw new CategoriaNotDeleted("Categoria não deletada");
        }
    }
}
