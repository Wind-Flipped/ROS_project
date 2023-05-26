package com.se.hw.controller;

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

/** 
* UserController Tester. 
* 
* @author lyq
* @since <pre>5ÔÂ 26, 2023</pre> 
* @version 1.0 
*/ 
public class UserControllerTest {
    @Autowired
    private UserController userController;

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: save(@RequestBody User user) 
* 
*/ 
@Test
public void testSave() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: delete(@PathVariable Integer id) 
* 
*/ 
@Test
public void testDelete() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: findAll() 
* 
*/ 
@Test
public void testFindAll() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: findOne(@PathVariable Integer id) 
* 
*/ 
@Test
public void testFindOne() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize) 
* 
*/ 
@Test
public void testFindPage() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: user_login(@RequestParam(value = "code", required = false) String code, @RequestParam(value = "rawData", required = false) String rawData
 * , @RequestParam(value = "signature", required = false) String signature, @RequestParam(value = "encrypteData", required = false) String encrypteData
 * , @RequestParam(value = "iv", required = false) String iv)
* 
*/ 
@Test
public void testUser_login() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: update(@RequestBody User user) 
* 
*/ 
@Test
public void testUpdate() throws Exception { 
//TODO: Test goes here... 
} 


} 
