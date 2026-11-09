package com.spring.restaurantdishes.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.spring.restaurantdishes.Mapper.DishMapper;
import com.spring.restaurantdishes.Mapper.ReviewMapper;
import com.spring.restaurantdishes.model.DishDetail;
import com.spring.restaurantdishes.model.RequestDetails.DishDetailPage;
import com.spring.restaurantdishes.model.Result;
import com.spring.restaurantdishes.model.dataBasePojo.Dish;
import com.spring.restaurantdishes.model.DishListItem;
import com.spring.restaurantdishes.model.PageBean;
import com.spring.restaurantdishes.model.RequestDetails.DishPage;
import com.spring.restaurantdishes.model.dataBasePojo.review;
import com.spring.restaurantdishes.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;


@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public PageBean<DishListItem> pageDishes(DishPage dishPage) {
        Integer pageNum = dishPage.getPage();
        Integer pageSize = dishPage.getSize();

        QueryWrapper wrapper = QueryWrapper.create().where(Dish::getStatus).eq(1);

        if(StringUtils.hasText(dishPage.getKeyword())){
            wrapper.and(Dish::getName).eq(dishPage.getKeyword().trim());
        }
        if (StringUtils.hasText(dishPage.getCanteenName())){
            wrapper.and(Dish::getCanteenName).eq(dishPage.getCanteenName().trim());
        }
        if (StringUtils.hasText(dishPage.getCategory())){
            wrapper.and(Dish::getCategory).eq(dishPage.getCategory().trim());
        }

        String sort = dishPage.getSort();
        switch (sort) {
            case "rating":
                wrapper.orderBy(Dish::getAvgRating, false)
                        .orderBy(Dish::getReviewCount, false)
                        .orderBy(Dish::getId, false);
                break;
            case "reviewCount":
                wrapper.orderBy(Dish::getReviewCount,false)
                        .orderBy(Dish::getAvgRating,false)
                        .orderBy(Dish::getId,false);
                break;
            case "newest":
            default:
                wrapper.orderBy(Dish::getCreatedAt,false)
                        .orderBy(Dish::getId);
                break;
        }

        Page<DishListItem> flexPage = dishMapper.paginateAs(pageNum,pageSize,wrapper, DishListItem.class);

        PageBean<DishListItem> result = new PageBean<>();

        result.setList(flexPage.getRecords());
        result.setPage(flexPage.getPageNumber());
        result.setSize(flexPage.getPageSize());
        result.setTotal(flexPage.getTotalRow());
        result.setPages(flexPage.getTotalPage());

        return result;
    }

    @Override
    public List<DishListItem> Hotdishes(Number limit) {
        QueryWrapper queryWrapper = QueryWrapper.create().where(Dish::getStatus).eq(1);
        queryWrapper.orderBy(Dish::getReviewCount,false).orderBy(Dish::getAvgRating);
        return dishMapper.selectListByQueryAs(queryWrapper, DishListItem.class);
    }

    @Override
    public Result DishDetail(Number id) {
        QueryWrapper wrapper = QueryWrapper.create().where(Dish::getId).eq(id);
        DishDetail dishDetail = dishMapper.selectOneByQueryAs(wrapper, DishDetail.class);
        if(dishDetail == null || dishDetail.getId() == 0){
            return Result.error(404,"菜品不存在或已下架");
        }
        return Result.success(dishDetail);
    }

    @Override
    public Result getDishReviews(DishDetailPage dishDetailPage, Number id) {
        Number pageNum = dishDetailPage.getPage();
        Number pageSize = dishDetailPage.getSize();
        //查询条件
        QueryWrapper queryWrapper = QueryWrapper.create().where(Dish::getId).eq(id);
        Dish dish = dishMapper.selectOneByQueryAs(queryWrapper, Dish.class);
        if(dish == null || dish.getStatus() == 0){
            return Result.error(404,"菜品不存在或已下架");
        }
        QueryWrapper wrapper = QueryWrapper.create().where(review::getDishId).eq(id);
        wrapper.orderBy(review::getCreatedAt,false).orderBy(review::getId);
        //构建分页查询结果对象
        Page<review> reviewPage = reviewMapper.paginateAs(pageNum,pageSize,wrapper, review.class);

        //构建返回的PageBean
        PageBean<review> reviewPageBean = new PageBean<>();

        reviewPageBean.setList(reviewPage.getRecords());
        reviewPageBean.setSize(reviewPage.getPageSize());
        reviewPageBean.setTotal(reviewPage.getTotalRow());
        reviewPageBean.setPage(reviewPage.getPageNumber());
        reviewPageBean.setPages(reviewPage.getTotalPage());

        return Result.success(reviewPageBean);

    }


}
