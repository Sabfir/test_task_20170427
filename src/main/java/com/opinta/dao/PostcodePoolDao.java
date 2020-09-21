package com.opinta.dao;

import com.opinta.entity.PostcodePool;
import java.util.List;

public interface PostcodePoolDao {

    List<PostcodePool> getAll();

    PostcodePool getById(long id);

    PostcodePool save(PostcodePool postcodePool);

    void update(PostcodePool postcodePool);

    void delete(PostcodePool postcodePool);
}
