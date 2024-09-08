package com.cesarmarcos.petshop.services;

import com.cesarmarcos.petshop.entities.Usuario;
import com.cesarmarcos.petshop.repository.UsuarioRepository;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements  UsuarioService {


    //https://github.com/axellageraldinc/reactive-web-api/blob/master/src/main/java/com/axell/reactive/service/book/BookServiceImpl.java

    private final UsuarioRepository usuarioRepo;

    @Override
    public Single<List<Usuario>> list(int limit, int page) {
        return Single.create(singleEmitter -> {
            List<Usuario> usuarios = usuarioRepo.findAll(PageRequest.of(page, limit)).getContent();
            singleEmitter.onSuccess(usuarios);
        });
    }

    @Override
    public Single<Usuario> addUser(Usuario usuario) {
        return Single.create(singleSubscriber ->{
            Optional<Usuario> usuarioBD = usuarioRepo.findById(usuario.getId());
            if(usuarioBD.isPresent()){
                singleSubscriber.onError(new EntityNotFoundException());
            }else{
                Usuario usuarioNew = usuarioRepo.save(addUsuario(usuario));
                singleSubscriber.onSuccess(usuarioNew);
            }
        });
    }

    private Usuario addUsuario(Usuario usuario){
        usuario.setId(UUID.randomUUID().toString());
        return usuario;
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
        //return usuarioRepo.findByUsuarioId(id)
        //            .switchIfEmpty(Single.error(new NoSuchElementException("Usuario no encontrado")));
        return Single.create(emmiter -> {
            Optional<Usuario> usuarioBd = usuarioRepo.findById(id);
            if(usuarioBd.isPresent()){
                emmiter.onSuccess(usuarioBd.get());
            }else{
                emmiter.onError(new EntityNotFoundException());
            }
        });
    }

    @Override
    public Completable update(String id, Usuario usuario) {
        return updateUsuario(id, usuario);
    }

    private Completable updateUsuario(String id, Usuario usuario){
        return Completable.create(completableEmitter -> {
            Usuario usuarioBD  = usuarioRepo.findById(id).orElseThrow(EntityNotFoundException::new);
            usuarioBD.setUsuario(usuario.getUsuario());
            usuarioBD.setRol(usuario.getRol());
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
