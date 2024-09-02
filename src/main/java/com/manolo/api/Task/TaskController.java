package com.manolo.api.Task;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/tasks")
public class TaskController {


  @Autowired
  private ITaskRepository taskRepository;

  @GetMapping("/")
  public List<TaskModel> list(HttpServletRequest request) {
    var idUser = request.getAttribute("idUser");
    return this.taskRepository.findByIdUser((UUID) idUser);
  }

  @PostMapping("/")
  public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    var idUser = request.getAttribute("idUser");
    taskModel.setIdUser((UUID) idUser);

    var currentDate = LocalDateTime.now();
    if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndtAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início/témino deve ser maior que a data atual");
    }

    if(taskModel.getStartAt().isAfter(taskModel.getEndtAt())){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de término deve ser maior que a data de início ");
    }

    var task =  this.taskRepository.save(taskModel);
    return ResponseEntity.status(HttpStatus.OK).body(task);
  }

  @PutMapping("/{id}")
  public TaskModel update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id){
    var idUser = request.getAttribute("idUser");

    taskModel.setIdUser((UUID) idUser);
    taskModel.setId(id);

    return this.taskRepository.save(taskModel);
  }

}
