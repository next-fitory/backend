package org.fitory.brand.repository.impl.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.fitory.brand.domain.Brand;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface BrandMapper {
    Brand insert(Brand brand);
    Brand update(Brand brand);
    Optional<Brand> findById(Long id);
    List<Brand> findAll();
    Optional<Brand> findByName(String name);
    void softDeleteById(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
}
