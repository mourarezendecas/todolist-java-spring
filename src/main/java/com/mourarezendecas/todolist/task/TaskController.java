package com.mourarezendecas.todolist.task;

import com.mourarezendecas.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository repository;

    @PostMapping("/")
    public ResponseEntity createTask(@RequestBody TaskModel bodyTask, HttpServletRequest request){
        bodyTask.setIdUser((UUID) request.getAttribute("idUser"));

        LocalDateTime currentDate = LocalDateTime.now();
        if(currentDate.isAfter(bodyTask.getStartAt()) || currentDate.isAfter(bodyTask.getEndAt())){
           return ResponseEntity.status(400).body("Date error, please check if the start date is greater then the currrent or the end date is less then current.");
        }

        if(bodyTask.getStartAt().isAfter(bodyTask.getEndAt())){
            return ResponseEntity.status(400).body("Date error, the start date must be less than the end date.");
        }

        TaskModel newTask = this.repository.save(bodyTask);

        return ResponseEntity.status(201).body(newTask);
    }

    @GetMapping("/")
    public ResponseEntity getAllTasks(HttpServletRequest request){
        List<TaskModel> foundTasks = repository.findByIdUser((UUID) request.getAttribute("idUser"));

        if(foundTasks.isEmpty()){
            return ResponseEntity.status(204).body("User doesn't have any saved tasks");
        }
        else{
            return ResponseEntity.status(200).body(foundTasks);
        }
    }

    @PutMapping("/{idTask}")
    public ResponseEntity updateTaskById(@RequestBody TaskModel updatedTask, @PathVariable UUID idTask, HttpServletRequest request){
        TaskModel task = this.repository.findById(idTask).orElse(null);

        if(task == null){
            return ResponseEntity.status(404).body("Task ID not found");
        }
        else {
            if(!task.getIdUser().equals(request.getAttribute("idUser"))){
                return ResponseEntity.status(401).body("Unauthorized user trying to update a task");
            }else{
                Utils.copyNonNullProperties(updatedTask, task);
                repository.save(task);
                return ResponseEntity.status(201).body(task);
            }
        }
    }
}
