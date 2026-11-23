package com.spring.restaurantdishes.service;

import com.spring.restaurantdishes.model.vo.AiDraft;
import com.spring.restaurantdishes.model.vo.AiRecommendResult;
import com.spring.restaurantdishes.model.vo.AiSummary;
import com.spring.restaurantdishes.model.RequestDetails.AiDraftRequest;
import com.spring.restaurantdishes.model.Result;

public interface AiService {
    Result<AiDraft> aiDraft(AiDraftRequest request);

    Result<AiSummary> aiSummary(Long id);

    Result<AiRecommendResult> AiRecommend(Long id);
}
