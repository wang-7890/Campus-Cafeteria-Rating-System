package com.spring.restaurantdishes.controller;

import com.spring.restaurantdishes.model.vo.AiDraft;
import com.spring.restaurantdishes.model.vo.AiRecommendResult;
import com.spring.restaurantdishes.model.vo.AiSummary;
import com.spring.restaurantdishes.model.RequestDetails.AiDraftRequest;
import com.spring.restaurantdishes.model.Result;
import com.spring.restaurantdishes.service.AiService;
import com.spring.restaurantdishes.utils.threadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AiController {
    @Autowired
    private AiService aiService;
    @PostMapping("/reviews/ai-draft")
    public ResponseEntity<Result<AiDraft>> getAiDraftReview(@Validated @RequestBody AiDraftRequest request){
        Result<AiDraft> result = aiService.aiDraft(request);
        if (result.getCode() == 0){
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @GetMapping("/dishes/{id}/ai-summary")
    public ResponseEntity<Result<AiSummary>> getAiSummary(@PathVariable Long id){
        Result<AiSummary> result = aiService.aiSummary(id);
        if (result.getCode() == 0){
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @GetMapping("/dishes/ai-recommend")
    public ResponseEntity<Result<AiRecommendResult>> getAiRecommend(){
        Map<String,Object> claims = threadLocalUtils.get();
        Long id = (Long) claims.get("userId");
        Result<AiRecommendResult> result = aiService.AiRecommend(id);
        if(result.getCode() == 0){
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(result.getCode()).body(result);
    }
}
