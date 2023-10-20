package br.com.content4devs.controller;

import br.com.content4devs.domain.User;
import br.com.content4devs.repository.IUserRepository;
import br.com.content4devs.v1.user.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
public class UserController extends UserServiceGrpc.UserServiceImplBase {

    private final IUserRepository repository;

    public UserController(IUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void create(UserReq request, StreamObserver<UserRes> responseObserver) {
        User user = new User(request.getName(), request.getEmail());
        User saved = repository.save(user);
        UserRes userRes = UserRes.newBuilder()
                .setId(saved.getId())
                .setName(saved.getName())
                .setEmail(saved.getEmail())
                .build();
        responseObserver.onNext(userRes);
        responseObserver.onCompleted();
    }

    @Override
    public void getAll(EmptyReq request, StreamObserver<UserResList> responseObserver) {
        List<User> users = repository.findAll();
        List<UserRes> userRes = users.stream()
                .map(user -> UserRes.newBuilder()
                        .setId(user.getId())
                        .setName(user.getName())
                        .setEmail(user.getEmail())
                        .build())
                .toList();

        UserResList userResList = UserResList.newBuilder().addAllUsers(userRes).build();
        responseObserver.onNext(userResList);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllServerStream(EmptyReq request, StreamObserver<UserRes> responseObserver) {
        repository.findAll().forEach(user -> {
            UserRes userRes = UserRes.newBuilder()
                    .setId(user.getId())
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .build();
            responseObserver.onNext(userRes);
        });
        responseObserver.onCompleted();
    }
}
