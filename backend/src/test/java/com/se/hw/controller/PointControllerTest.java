package com.se.hw.controller;

import com.se.hw.common.Result;
import com.se.hw.entity.Map;
import com.se.hw.entity.Point;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;

/** 
* PointController Tester. 
* 
* @author 刘运淇
* @since <pre>5月 16, 2023</pre> 
* @version 1.0 
*/
@RunWith(SpringRunner.class)
@SpringBootTest
public class PointControllerTest {
    private Point point1;
    private Point point2;
    private Point point3;
    private Point point4;
    private Point point5;
    private Point point6;
    private int mapId;
    private int point1Id;
    private int point2Id;

    @Autowired
    private PointController pointController;
    @Autowired
    private MapController mapController;

@Before
public void before() throws Exception {
    // pointController = new PointController();

    mapController.save("map3","#FFFFFF");
    List<Map> maps = (List<Map>) mapController.findAll().getData();
    mapId = maps.get(0).getId();

    pointController.save(1.0F,1.0F,"point1",mapId,2);
    pointController.save(2.0F,2.0F,"point2",mapId,2);
    List<Point> points = (List<Point>) pointController.findAll().getData();
    point1Id = points.get(0).getId();
    point2Id = points.get(1).getId();

    point1 = new Point();
    point2 = new Point();
    point3 = new Point();
    point4 = new Point();
    point5 = new Point();
    point6 = new Point();
    point1.setName("point1");
    point1.setId(1);
    point1.setMapId(mapId);
    point1.setStatus(0);
    point1.setOriW(10.0F);
    point1.setOriX(10.0F);
    point1.setOriY(10.0F);
    point1.setOriZ(10.0F);
    point1.setXAxis(1.0F);
    point1.setYAxis(1.0F);
    point1.setZAxis(1.0F);

    point2.setName("point2");
    point2.setId(2);
    point2.setMapId(mapId);
    point2.setStatus(1);
    point2.setOriW(20.0F);
    point2.setOriX(20.0f);
    point2.setOriY(20.0f);
    point2.setOriZ(20.0f);
    point2.setXAxis(2.0f);
    point2.setYAxis(2.0f);
    point2.setZAxis(2.0f);

    // 构造无对应外键 航点
    point3.setName("error_point3");
    point3.setId(0x3f3f3f);
    point3.setMapId(10000);
    point3.setStatus(2);
    point3.setOriW(30.0f);
    point3.setOriX(30.0f);
    point3.setOriY(30.0f);
    point3.setOriZ(30.0f);
    point3.setXAxis(3.0f);
    point3.setYAxis(3.0f);
    point3.setZAxis(3.0f);

    // 构造正常 航点
    point4.setName("normal_point4");
    point4.setId(4);
    point4.setMapId(mapId);
    point4.setStatus(3);
    point4.setOriW(40.0f);
    point4.setOriX(40.0f);
    point4.setOriY(40.0f);
    point4.setOriZ(40.0f);
    point4.setXAxis(4.0f);
    point4.setYAxis(4.0f);
    point4.setZAxis(4.0f);

    // 构造重复id 航点
    point5.setName("duplicate_point5");
    point5.setId(point1Id);
    point5.setMapId(mapId);
    point5.setStatus(3);
    point5.setOriW(40.0f);
    point5.setOriX(40.0f);
    point5.setOriY(40.0f);
    point5.setOriZ(40.0f);
    point5.setXAxis(4.0f);
    point5.setYAxis(4.0f);
    point5.setZAxis(4.0f);

    // 构造重复id错误外键 航点
    point6.setName("duplicate_error_point5");
    point6.setId(point1Id);
    point6.setMapId(10000);
    point6.setStatus(3);
    point6.setOriW(40.0f);
    point6.setOriX(40.0f);
    point6.setOriY(40.0f);
    point6.setOriZ(40.0f);
    point6.setXAxis(4.0f);
    point6.setYAxis(4.0f);
    point6.setZAxis(4.0f);

} 

@After
public void after() throws Exception {
    List<Point> points = (List<Point>) pointController.findAll().getData();
    int size = points.size();
    for (int i = 0; i < size; i++) {
        pointController.delete(points.get(i).getId());
    }
    mapController.delete(mapId);
} 

/** 
* 
* Method: save(@RequestBody Point)
* 
*/ 
@Test
public void testSave() throws Exception { 
//TODO: Test goes here...
    Result result1 = pointController.save(1.0F,1.0F,"point1",mapId,2);
    assert result1.getCode() == 400 && result1.getMsg().equals("naming repetition!");
    Result result2 = pointController.save(1.0F,1.0F,"point4",mapId,2);
    assert result2.getCode() == 200;
    Result result3 = pointController.save(1.0F,1.0F,"point5",0x3f3f3f3f,2);
    assert result3.getCode() == 500 && result3.getMsg().equals("other error!");

}

    /**
     *
     * Method: mapToPoints(@RequestBody Integer mapId)
     *
     */
    @Test
public void testMapToPoints() throws Exception {
    Result result = pointController.mapToPoints(mapId);
    List<Point> points = (List<Point>) result.getData();
    assert result.getCode() == 100 &&
            points.size() == 2 &&
            points.get(0).getId() == point1Id &&
            points.get(1).getId() == point2Id;
}

/** 
* 
* Method: update(@RequestBody Point point) 
* 
*/ 
@Test
public void testUpdate() throws Exception { 
//TODO: Test goes here...
    Result result1 = pointController.update(PointController.point2req(point5));
    assert result1.getCode() == 100;
    Result result2 = pointController.findOne(point5.getId());
    assert result2.getCode() == 100 && ((Point) result2.getData()).getName().equals("duplicate_point5");

    Result result3 = pointController.update(PointController.point2req(point3));
    assert result3.getCode() == 404 && result3.getMsg().equals("can't find the point!");

    Result result4 = pointController.update(PointController.point2req(point6));
    assert result4.getCode() == 500 && result4.getMsg().equals("other error");
} 

/** 
* 
* Method: delete(@RequestParam Integer pointId) 
* 
*/ 
@Test
public void testDelete() throws Exception { 
//TODO: Test goes here...

    Result result1 = pointController.delete(point1Id);
    assert result1.getCode() == 100;
    Result result2 = pointController.findOne(point1Id);
    assert result2.getCode() == 404 && result2.getMsg().equals("can't find the point!");

    Result result3 = pointController.delete(0x3f3f);
    assert result3.getCode() == 404 && result3.getMsg().equals("can't find the point!");
} 

/** 
* 
* Method: findAll() 
* 
*/ 
@Test
public void testFindAll() throws Exception { 
//TODO: Test goes here...
    Result result = pointController.findAll();
    List<Point> points = (List<Point>) result.getData();
    assert result.getCode() == 100 &&
            points.size() == 2 &&
            points.get(0).getId() == point1Id &&
            points.get(1).getId() == point2Id;
} 

/** 
* 
* Method: findOne(@RequestParam Integer pointId) 
* 
*/ 
@Test
public void testFindOne() throws Exception { 
//TODO: Test goes here...
    Result result1 = pointController.findOne(point1Id);
    Point point = (Point) result1.getData();
    assert result1.getCode() == 100 && point.getId() == point1Id;
    Result result2 = pointController.findOne(10000);
    assert result2.getCode() == 404 && result2.getMsg().equals("can't find the point!");
} 

/** 
* 
* Method: findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize) 
* 
*/ 
@Test
public void testFindPage() throws Exception { 
//TODO: Test goes here...
    Result result = pointController.findPage(1,1);
    assert result.getCode() == 100;
}

} 
