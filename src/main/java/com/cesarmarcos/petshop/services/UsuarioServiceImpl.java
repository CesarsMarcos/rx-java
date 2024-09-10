package com.cesarmarcos.petshop.services;

import com.cesarmarcos.petshop.entities.Usuario;
import com.cesarmarcos.petshop.repository.UsuarioRepository;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.internal.operators.maybe.MaybeFromCallable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements  UsuarioService {

    //https://github.com/axellageraldinc/reactive-web-api/blob/master/src/main/java/com/axell/reactive/service/book/BookServiceImpl.java

    private final UsuarioRepository usuarioRepo;

    @Override
    public Single<List<Usuario>> list(int limit, int page) {
        return Single.fromCallable(()->{
            return usuarioRepo.findAll(PageRequest.of(page, limit)).getContent();
        })
        .map(usuarios->{
            return usuarios.stream().collect(Collectors.toList());
        })
        .onErrorResumeNext(throwable -> {
            return Single.error(new RuntimeException(""));
        });
    }

    @Override
    public Single<Usuario> addUser(Usuario usuario) {
       return addUserRepo(usuario);
    }

    private Single<Usuario> addUserRepo(Usuario usuario){
        return Single.create(singleSubscriber ->{
            if(Objects.nonNull(usuario.getId())){
                Optional<Usuario> usuarioBD = usuarioRepo.findById(usuario.getId());
                if(!usuarioBD.isPresent()){
                    singleSubscriber.onError(new EntityNotFoundException());
                }
            }else{
                usuario.setId(UUID.randomUUID().toString());
                Usuario usuarioNew = usuarioRepo.save(usuario);
                singleSubscriber.onSuccess(usuarioNew);
            }
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
    public Single<Usuario> getDetails(String id) {
        return usuarioRepo.findByUsuarioId(id)
                   .switchIfEmpty(Single.error(new EntityNotFoundException("Usuario no encontrado")));
        /*return Single.create(emmiter -> {
            Optional<Usuario> usuarioBd = usuarioRepo.findById(id);
            if(usuarioBd.isPresent()){
                emmiter.onSuccess(usuarioBd.get());
            }else{
                emmiter.onError(new EntityNotFoundException());
            }
        });*/
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

    private Completable deleteUser(String id){
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
}
