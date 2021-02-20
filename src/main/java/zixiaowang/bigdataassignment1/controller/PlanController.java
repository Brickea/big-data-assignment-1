package zixiaowang.bigdataassignment1.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.mongodb.DBObject;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;
import zixiaowang.bigdataassignment1.service.PlanService;
import zixiaowang.bigdataassignment1.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

@Controller
public class PlanController {

    @Autowired
    PlanService planService;

    @PostMapping(value = "/plan")
    public ResponseEntity<Map<Object,Object>> createNewPlan(@RequestBody String newPlanJsonStr) throws ProcessingException {

        return planService.insert(newPlanJsonStr);
    }

    @GetMapping(value = "/plan/{planObjectId}")
    public ResponseEntity<JSONObject> findPlan(@PathVariable String planObjectId, @RequestHeader ("If-None-Match") String conditionalTag) {

        // conditional read
        System.out.println(conditionalTag);
        if(conditionalTag!=null){
           ResponseEntity<JSONObject> findResult =  planService.find(planObjectId);
            if(String.valueOf(findResult.getBody().toString().hashCode()).equals(conditionalTag)){
                return new ResponseEntity(findResult,HttpStatus.NOT_MODIFIED);
            }
        }

        return planService.find(planObjectId);
    }

    @DeleteMapping(value = "/plan/{planObjectId}")
    public ResponseEntity<Map<Object,Object>> deletePlan(@PathVariable String planObjectId){

        return planService.delete(planObjectId);
    }

//    @PutMapping(value = "/plan/{planObjectId}")
//    public ResponseEntity<Map<Object,Object>> putNewPlan(@RequestBody String putPlanJsonStr) throws ProcessingException {
//
//        return planService.put(putPlanJsonStr);
//    }
}
