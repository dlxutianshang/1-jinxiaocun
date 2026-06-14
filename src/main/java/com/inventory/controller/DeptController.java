package com.inventory.controller;

import com.inventory.model.Dept;
import com.inventory.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dept")
@CrossOrigin
public class DeptController {

    @Autowired
    private DeptService deptService;

    @GetMapping("/tree")
    public Map<String, Object> tree(@RequestParam(required = false) String name,
                                    @RequestParam(required = false) String status) {
        Map<String, Object> result = new java.util.HashMap<>();
        List<Dept> tree = deptService.findTree(name, status);
        result.put("success", true);
        result.put("data", tree);
        return result;
    }

    @GetMapping("/list")
    public Map<String, Object> list() {
        Map<String, Object> result = new java.util.HashMap<>();
        List<Dept> list = deptService.findAllFlat();
        result.put("success", true);
        result.put("data", list);
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Integer id) {
        Map<String, Object> result = new java.util.HashMap<>();
        Dept dept = deptService.findById(id);
        result.put("success", dept != null);
        result.put("data", dept);
        return result;
    }

    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody Dept dept) {
        return deptService.save(dept);
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody Dept dept) {
        return deptService.update(dept);
    }

    @PostMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable Integer id) {
        return deptService.delete(id);
    }

    @PostMapping("/saveOrder")
    public Map<String, Object> saveOrder(@RequestBody List<Dept> list) {
        return deptService.saveOrder(list);
    }
}
