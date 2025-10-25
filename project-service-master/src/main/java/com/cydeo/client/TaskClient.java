package com.cydeo.client;

import com.cydeo.dto.wrapper.TaskResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "project-service", url = "http://localhost:8383/api/v1/task")
public interface TaskClient {

    @GetMapping("/count/project/{projectCode}")
    ResponseEntity<TaskResponse> getCountsByProject(@PathVariable("projectCode") String projectCode);

    @PutMapping("/complete/project/{projectCode}")
    ResponseEntity<TaskResponse> completeByProject(@PathVariable("projectCode") String projectCode);

    @DeleteMapping("/delete/project/{projectCode}")
    ResponseEntity<TaskResponse> deleteByProject(@PathVariable("projectCode") String projectCode);
}
