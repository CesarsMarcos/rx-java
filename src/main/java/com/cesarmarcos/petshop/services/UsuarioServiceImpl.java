package com.cesarmarcos.petshop.services;

import com.cesarmarcos.petshop.entities.Usuario;
import com.cesarmarcos.petshop.entities.dto.UsuarioDTO;
import com.cesarmarcos.petshop.repository.UsuarioRepository;;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements  UsuarioService {

    //https://github.com/axellageraldinc/reactive-web-api/blob/master/src/main/java/com/axell/reactive/service/book/BookServiceImpl.java

    private final UsuarioRepository usuarioRepo;

    @Override
    public Single<List<Usuario>> list(int limit, int page) {
        return Single.fromCallable(()->{
            return usuarioRepo.findAll(PageRequest.of(page, limit)).getContent();
        }).map(usuarios->{
            return usuarios.stream().collect(Collectors.toList());
        }).onErrorResumeNext(throwable -> {
            return Single.error(new RuntimeException(""));
        });
    }

    @Override
    public Single<UsuarioDTO> addUser(UsuarioDTO usuario) {
        return addUserRepo(usuario);
    }

    private Single<UsuarioDTO> addUserRepo(UsuarioDTO usuario){
        return Single.fromCallable(() -> {
            //if(Objects.nonNull(usuario.getUsername())){
                /*Boolean isUsuarioExiste = usuarioRepo.existsByUsuario(usuario.getUsername());
                if(isUsuarioExiste){
                    throw new EntityNotFoundException("Usuario existe");
                }*/
            //}else{
                usuario.setId(UUID.randomUUID().toString());
            //}
            usuario.setId(UUID.randomUUID().toString());
            Usuario usuarioNew = usuarioRepo.save(toUsuario(usuario));
            return toUsuarioDTO(usuarioNew);
        }).onErrorResumeNext(throwable -> {
            log.error ("Error inesperado: {}", throwable.getMessage(), throwable);
            return Single.error(new RuntimeException("Error interno al guardar el usuario"));
        });
    }

    private Single<Usuario> saveUsuarioToRepository(Usuario usuario){
        return Single.create(emmiter ->{
            Optional<Usuario> usuarioBD = usuarioRepo.findById(usuario.getId());
            if(usuarioBD.isPresent()){
                emmiter.onError(new EntityNotFoundException());
            }else{
                emmiter.onSuccess(usuarioRepo.save(usuario));
            }
        });
    }


    @Override
    public Single<UsuarioDTO> getDetails(String id) {
        /*return Single.create(singleSb ->{
            Optional<Usuario> usuarioOptional = usuarioRepo.findById(id);
            if(usuarioOptional.isPresent()){
                singleSb.onSuccess(usuarioOptional.get());
            }else{
                singleSb.onError(new EntityNotFoundException("Usuario no encontrado"));
            }
        });*/
        /*return Single.fromCallable(() -> usuarioRepo.findById(id)
                .map(this::toUsuarioDTO)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado")));*/

        return usuarioRepo.findById(id)
                //.filter(Optional::isPresent)
                //.map(Optional::get)
                .map(this::toUsuarioDTO)
                .map(Single::just)
                .orElseGet(() -> Single.error(new EntityNotFoundException("Usuario no encontrado con ID: " + id)));



    }

    @Override
    public Completable update(String id, Usuario usuario) {
        return updateUsuario(id, usuario);
    }

    private Completable updateUsuario(String id, Usuario usuario){
        return Completable.create(completableEmitter -> {
            Usuario usuarioBD  = usuarioRepo.findById(id).orElseThrow(EntityNotFoundException::new);
            usuarioBD.setUsuario(usuario.getUsuario());
            usuarioBD.setPassword(usuario.getPassword());
            usuarioBD.setEstado(usuario.getEstado());
            usuarioBD.setRol(usuario.getRol());
            usuarioRepo.save(usuarioBD);

            completableEmitter.onComplete();

            /*if(!usuarioBD.isPresent()){
                completableEmitter.onError(new EntityNotFoundException());
            }else{
                usuarioBD.get().setUsuario(usuario.getUsuario());
                usuarioBD.get().setRol(usuario.getRol());
                completableEmitter.onComplete();
            }*/
        });
    }

    @Override
    public Completable delete(String id) {
        return deleteUser(id);
    }

    private Completable deleteUser(String id) {
        return Completable.create(completableEmitter -> {
            Optional<Usuario> usuarioBD  = usuarioRepo.findById(id);
            if(usuarioBD.isEmpty()){
                 Completable.error(EntityNotFoundException::new);
            }else{
                usuarioRepo.delete(usuarioBD.get());
                 Completable.complete();
            }
        });
    }

    private UsuarioDTO toUsuarioDTO(Usuario usuario){
       return UsuarioDTO.builder()
               .id(usuario.getId())
                .username(usuario.getUsuario())
                .estado(usuario.getEstado())
                .rol(usuario.getRol())
                .password(usuario.getPassword())
                .build();
    }

    private Usuario toUsuario(UsuarioDTO usuario){
        return Usuario.builder()
                .id(usuario.getId())
                .usuario(usuario.getUsername())
                .estado(usuario.getEstado())
                .rol(usuario.getRol())
                .password(usuario.getPassword())
                .build();
    }

}
