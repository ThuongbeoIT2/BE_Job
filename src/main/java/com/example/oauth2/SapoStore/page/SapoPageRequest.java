package com.example.oauth2.SapoStore.page;

import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class SapoPageRequest implements Pageable {
    Integer limit;
    Integer offset;
    private final Sort sort;

    public SapoPageRequest(Integer limit, Integer offset, Sort sort) {
        this.limit = limit;
        this.offset = offset;
        this.sort = sort; // truyền cách sắp xếp riêng
    }

    public SapoPageRequest(Integer limit, Integer offset) {
        this(limit, offset, Sort.unsorted()); // Không sắp xếp
    }

    @Override
    public int getPageNumber() {
        return offset / limit; // lấy trang hiện tại
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }
    @Override
    public Pageable next() {
        // Trang tiếp theo
        return new SapoPageRequest(getPageSize(), (int) (getOffset() + getPageSize()));
    }


    public Pageable previous() {
        // Trang phía trước
        return hasPrevious() ?
                new SapoPageRequest(getPageSize(), (int) (getOffset() - getPageSize())) : this;
    }

    @Override
    public Pageable previousOrFirst() {
        // Nếu trang hiện tại không lùi được nữa, lấy trang đầu
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new SapoPageRequest(getPageSize(), 0);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new SapoPageRequest(getPageSize(), pageNumber * getPageSize());
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }
}
