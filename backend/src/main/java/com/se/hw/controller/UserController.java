package com.se.hw.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import com.se.hw.service.IUserService;
import com.se.hw.entity.User;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author SE2304
 * @since 2023-04-27
 */
@RestController
@RequestMapping("/user")
    public class UserController {

    @Resource
    private IUserService userService;

    @PostMapping
    public Boolean save(@RequestBody User user){
        return userService.saveOrUpdate(user);
    }

    @DeleteMapping("/{id}")
     public Boolean delete(@PathVariable Integer id){
        return userService.removeById(id);
     }

     @GetMapping
     public List<User> findAll(){
        return userService.list();
     }

     @GetMapping("/{id}")
     public List<User> findOne(@PathVariable Integer id){
        return userService.list();
     }

     @GetMapping("/page")
     public Page<User> findPage(@RequestParam Integer pageNum,
     @RequestParam Integer pageSize){
        return userService.page(new Page<>(pageNum,pageSize));
     }

}

