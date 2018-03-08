package org.yifeng.spring.boot.blogservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yifeng.spring.boot.blogservice.repositories.records.BlogRecord;

@Repository
public interface BlogRepository extends JpaRepository<BlogRecord, Long> {

}
