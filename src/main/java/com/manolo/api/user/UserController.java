package com.manolo.api.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;


@RestController()
@RequestMapping("/user")
public class UserController {

  @Autowired
  private IUserRepository userRepository;

  @PostMapping("/")
  public ResponseEntity create(@RequestBody UserModel userModel) {
    
    var user = this.userRepository.findByUsername(userModel.getUsername());

    if (user != null) {
      System.out.println(user);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe");
    }

    var passwordHashed = BCrypt.withDefaults().hashToString(8, userModel.getPassword().toCharArray());
    
    userModel.setPassword(passwordHashed);
    var userCreate = this.userRepository.save(userModel);
    return ResponseEntity.status(HttpStatus.CREATED).body(userCreate);
  }

}
