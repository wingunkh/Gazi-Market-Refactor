package capstone.capstone.repository;

import capstone.capstone.domain.VisitList;
import capstone.capstone.idclass.List_Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface VisitListRepository extends JpaRepository<VisitList, List_Post> {
    @Query(value = "SELECT V.post_num FROM Visit_list v WHERE v.user_num = :user_num", nativeQuery = true)
    List<Integer> getVisitList(@Param("user_num") int user_num);
}