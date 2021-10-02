package com.example.cakeappratings.Listner;

import com.example.cakeappratings.Modal.Rating;

import java.util.List;

public interface IRatingListner {
    void onReviewLoadSuccess(List<Rating> reviews);
    void onReviewLoadFail(String message);
}
